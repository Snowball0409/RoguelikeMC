package snowball049.roguelikemc.upgrade.runtime;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;
import snowball049.roguelikemc.upgrade.action.UpgradeActionHandlers;
import snowball049.roguelikemc.upgrade.action.trigger.TriggerActionPayload;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

import static snowball049.roguelikemc.upgrade.schema.SchemaFields.Trigger;

public final class UpgradeTriggerRuntimeService {
    private static final int MAX_PLAYER_PLACED = 64;

    private static final Map<UUID, Map<TriggerStateKey, TriggerProgress>> TRIGGER_STATE = new HashMap<>();
    private static final Map<UUID, LinkedHashSet<PlacedBlockKey>> PLAYER_PLACED = new HashMap<>();

    private UpgradeTriggerRuntimeService() {
    }

    public static void onEntityKilledByPlayer(ServerPlayerEntity player, LivingEntity target) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        processKillTriggers(player, playerData.getAllUpgrades(), target);
    }

    public static void onPlayerAttack(ServerPlayerEntity player, LivingEntity target) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        processEvent(player, Trigger.EVENT_ATTACK, playerData.getAllUpgrades(), TriggerEventContext.forTarget(target));
    }

    public static void onPlayerDamaged(ServerPlayerEntity player) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        processEvent(player, Trigger.EVENT_DAMAGED, playerData.getAllUpgrades(), TriggerEventContext.empty());
    }

    public static void onMerchantTrade(ServerPlayerEntity player, MerchantEntity merchant) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        processEvent(player, Trigger.EVENT_TRADE, playerData.getAllUpgrades(), TriggerEventContext.forMerchant(merchant));
    }

    public static void onLevelUp(ServerPlayerEntity player, int levelsGained) {
        if (levelsGained <= 0) {
            return;
        }
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        processEvent(
                player,
                Trigger.EVENT_LEVELUP,
                playerData.getAllUpgrades(),
                TriggerEventContext.forLevelGain(levelsGained)
        );
    }

    public static void onBlockPlacedByPlayer(
            ServerPlayerEntity player,
            RegistryKey<World> worldKey,
            BlockPos pos,
            BlockState state
    ) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        onBlockPlacedByPlayer(player, worldKey, pos, state, playerData.getAllUpgrades());
    }

    public static void onBlockBroken(
            ServerPlayerEntity player,
            RegistryKey<World> worldKey,
            BlockPos pos,
            BlockState state
    ) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        onBlockBroken(player, worldKey, pos, state, playerData.getAllUpgrades());
    }

    public static void clearPlayer(ServerPlayerEntity player) {
        if (player != null) {
            UUID uuid = player.getUuid();
            TRIGGER_STATE.remove(uuid);
            PLAYER_PLACED.remove(uuid);
        }
    }

    public static void clearUpgrade(ServerPlayerEntity player, Identifier upgradeId) {
        if (player == null || upgradeId == null) {
            return;
        }

        Map<TriggerStateKey, TriggerProgress> playerState = TRIGGER_STATE.get(player.getUuid());
        if (playerState == null || playerState.isEmpty()) {
            return;
        }

        playerState.keySet().removeIf(key -> upgradeId.equals(key.upgradeId()));
        if (playerState.isEmpty()) {
            TRIGGER_STATE.remove(player.getUuid());
        }
    }

    static void processKillTriggers(
            ServerPlayerEntity player,
            Collection<RoguelikeMCUpgradeData> upgrades,
            LivingEntity target
    ) {
        processEvent(player, Trigger.EVENT_KILL, upgrades, TriggerEventContext.forTarget(target));
    }

    static void processEvent(
            ServerPlayerEntity player,
            String eventType,
            Collection<RoguelikeMCUpgradeData> upgrades,
            TriggerEventContext context
    ) {
        long currentTick = player.getServerWorld().getTime();
        UUID playerUuid = player.getUuid();
        for (RoguelikeMCUpgradeData upgrade : upgrades) {
            Identifier upgradeId = RoguelikeMCUpgradeManager.idFor(upgrade);
            for (int actionIndex = 0; actionIndex < upgrade.actions().size(); actionIndex++) {
                tryFireTrigger(player, upgrade, upgradeId, actionIndex, eventType, context, playerUuid, currentTick);
            }
        }
    }

    static void onBlockPlacedByPlayer(
            ServerPlayerEntity player,
            RegistryKey<World> worldKey,
            BlockPos pos,
            BlockState state,
            Collection<RoguelikeMCUpgradeData> upgrades
    ) {
        LinkedHashSet<PlacedBlockKey> placed = PLAYER_PLACED.computeIfAbsent(player.getUuid(), ignored -> new LinkedHashSet<>());
        while (placed.size() >= MAX_PLAYER_PLACED) {
            Iterator<PlacedBlockKey> iterator = placed.iterator();
            if (!iterator.hasNext()) {
                break;
            }
            iterator.next();
            iterator.remove();
        }
        placed.add(new PlacedBlockKey(worldKey, pos.toImmutable()));
        decrementMatchingBreakProgress(player, upgrades, state);
    }

    static void onBlockBroken(
            ServerPlayerEntity player,
            RegistryKey<World> worldKey,
            BlockPos pos,
            BlockState state,
            Collection<RoguelikeMCUpgradeData> upgrades
    ) {
        PlacedBlockKey key = new PlacedBlockKey(worldKey, pos.toImmutable());
        LinkedHashSet<PlacedBlockKey> placed = PLAYER_PLACED.get(player.getUuid());
        boolean wasPlayerPlaced = placed != null && placed.contains(key);
        processEvent(
                player,
                Trigger.EVENT_BREAK,
                upgrades,
                TriggerEventContext.forBlock(state, worldKey, pos, wasPlayerPlaced)
        );
        if (placed != null) {
            placed.remove(key);
            if (placed.isEmpty()) {
                PLAYER_PLACED.remove(player.getUuid());
            }
        }
    }

    private static void tryFireTrigger(
            ServerPlayerEntity player,
            RoguelikeMCUpgradeData upgrade,
            Identifier upgradeId,
            int actionIndex,
            String eventType,
            TriggerEventContext context,
            UUID playerUuid,
            long currentTick
    ) {
        RoguelikeMCUpgradeData.ActionData action = upgrade.actions().get(actionIndex);
        if (action.actionType() != UpgradeActionType.TRIGGER) {
            return;
        }

        TriggerActionPayload payload = parseTriggerPayload(action).resultOrPartial(RoguelikeMC.LOGGER::warn).orElse(null);
        if (payload == null || !eventType.equalsIgnoreCase(payload.eventType())) {
            return;
        }
        if (Trigger.EVENT_BREAK.equalsIgnoreCase(eventType)
                && payload.preventPlaceBreak()
                && context.wasPlayerPlaced()) {
            return;
        }
        if (!matchesConditions(payload, context)) {
            return;
        }

        TriggerProgress progress = progress(playerUuid, upgradeId, actionIndex);
        if (currentTick < progress.nextAvailableTick()) {
            return;
        }

        if (Trigger.EVENT_LEVELUP.equalsIgnoreCase(eventType)) {
            progress.addLevels(context.levelsGained());
        } else {
            progress.increment();
        }

        // Consume thresholds in a loop so multi-level gains (and surplus kill/break progress)
        // can fire more than once. Cooldown, if any, stops the loop after the first fire.
        while (progress.count() >= payload.count()) {
            progress.consume(payload.count());
            for (RoguelikeMCUpgradeData.ActionData nestedAction : payload.actions()) {
                UpgradeActionHandlers.apply(new UpgradeActionContext(player, upgrade, nestedAction));
            }
            if (payload.cooldown() > 0) {
                progress.setCooldown(currentTick + payload.cooldown());
                break;
            }
        }
    }

    private static void decrementMatchingBreakProgress(
            ServerPlayerEntity player,
            Collection<RoguelikeMCUpgradeData> upgrades,
            BlockState state
    ) {
        TriggerEventContext context = TriggerEventContext.forBlock(state, null, null, false);
        UUID playerUuid = player.getUuid();
        for (RoguelikeMCUpgradeData upgrade : upgrades) {
            Identifier upgradeId = RoguelikeMCUpgradeManager.idFor(upgrade);
            for (int actionIndex = 0; actionIndex < upgrade.actions().size(); actionIndex++) {
                RoguelikeMCUpgradeData.ActionData action = upgrade.actions().get(actionIndex);
                if (action.actionType() != UpgradeActionType.TRIGGER) {
                    continue;
                }
                TriggerActionPayload payload = parseTriggerPayload(action).resultOrPartial(RoguelikeMC.LOGGER::warn).orElse(null);
                if (payload == null
                        || !Trigger.EVENT_BREAK.equalsIgnoreCase(payload.eventType())
                        || !payload.preventPlaceBreak()
                        || !matchesConditions(payload, context)) {
                    continue;
                }
                progress(playerUuid, upgradeId, actionIndex).decrement();
            }
        }
    }

    private static DataResult<TriggerActionPayload> parseTriggerPayload(RoguelikeMCUpgradeData.ActionData action) {
        return TriggerActionPayload.fromAction(action);
    }

    private static boolean matchesConditions(TriggerActionPayload payload, TriggerEventContext context) {
        for (TriggerActionPayload.TriggerConditionData condition : payload.conditions()) {
            if (!matchesCondition(condition, context)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesCondition(TriggerActionPayload.TriggerConditionData condition, TriggerEventContext context) {
        return switch (condition.type()) {
            case Trigger.CONDITION_TARGET_HOSTILE -> {
                LivingEntity target = context.target();
                yield target instanceof HostileEntity && !target.isPlayer();
            }
            case Trigger.CONDITION_TRADE_VILLAGER -> matchesTradeVillager(context.merchant());
            case Trigger.CONDITION_TARGET_BLOCK -> matchesTargetBlock(condition.payload(), context.blockState());
            default -> {
                RoguelikeMC.LOGGER.warn("Unsupported trigger condition type '{}'", condition.type());
                yield false;
            }
        };
    }

    private static boolean matchesTradeVillager(MerchantEntity merchant) {
        return merchant instanceof VillagerEntity || merchant instanceof WanderingTraderEntity;
    }

    private static boolean matchesTargetBlock(JsonObject payload, BlockState state) {
        if (state == null || payload == null) {
            return false;
        }
        boolean hasBlock = payload.has(Trigger.PAYLOAD_BLOCK);
        boolean hasTag = payload.has(Trigger.PAYLOAD_TAG);
        if (hasBlock == hasTag) {
            RoguelikeMC.LOGGER.warn("target_block requires exactly one of 'block' or 'tag'");
            return false;
        }
        if (hasBlock) {
            Identifier id = Identifier.tryParse(payload.get(Trigger.PAYLOAD_BLOCK).getAsString());
            if (id == null) {
                return false;
            }
            Block block = Registries.BLOCK.get(id);
            return block != Blocks.AIR && state.isOf(block);
        }
        Identifier tagId = Identifier.tryParse(payload.get(Trigger.PAYLOAD_TAG).getAsString());
        if (tagId == null) {
            return false;
        }
        return state.isIn(TagKey.of(RegistryKeys.BLOCK, tagId));
    }

    private static TriggerProgress progress(UUID playerUuid, Identifier upgradeId, int actionIndex) {
        Map<TriggerStateKey, TriggerProgress> playerState = TRIGGER_STATE.computeIfAbsent(playerUuid, ignored -> new HashMap<>());
        TriggerStateKey key = new TriggerStateKey(upgradeId, actionIndex);
        return playerState.computeIfAbsent(key, ignored -> new TriggerProgress());
    }

    static void clearAllForTesting() {
        TRIGGER_STATE.clear();
        PLAYER_PLACED.clear();
    }

    private record TriggerStateKey(Identifier upgradeId, int actionIndex) {
    }

    private record PlacedBlockKey(RegistryKey<World> worldKey, BlockPos pos) {
    }

    private static final class TriggerProgress {
        private int count;
        private long nextAvailableTick;

        private int count() {
            return count;
        }

        private long nextAvailableTick() {
            return nextAvailableTick;
        }

        private void increment() {
            count++;
        }

        private void addLevels(int levels) {
            if (levels > 0) {
                count += levels;
            }
        }

        private void decrement() {
            if (count > 0) {
                count--;
            }
        }

        private void consume(int amount) {
            if (amount <= 0) {
                return;
            }
            count = Math.max(0, count - amount);
        }

        private void setCooldown(long nextAvailableTick) {
            this.nextAvailableTick = Math.max(0L, nextAvailableTick);
        }
    }
}
