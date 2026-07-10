package snowball049.roguelikemc.upgrade.runtime;

import com.mojang.serialization.DataResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
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
import java.util.Map;
import java.util.UUID;

import static snowball049.roguelikemc.upgrade.schema.SchemaFields.Trigger;

public final class UpgradeTriggerRuntimeService {
    private static final Map<UUID, Map<TriggerStateKey, TriggerProgress>> TRIGGER_STATE = new HashMap<>();

    private UpgradeTriggerRuntimeService() {
    }

    public static void onEntityKilledByPlayer(ServerPlayerEntity player, LivingEntity target) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        processKillTriggers(player, playerData.getAllUpgrades(), target);
    }

    public static void clearPlayer(ServerPlayerEntity player) {
        if (player != null) {
            TRIGGER_STATE.remove(player.getUuid());
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
        long currentTick = player.getServerWorld().getTime();
        UUID playerUuid = player.getUuid();
        for (RoguelikeMCUpgradeData upgrade : upgrades) {
            Identifier upgradeId = RoguelikeMCUpgradeManager.idFor(upgrade);
            for (int actionIndex = 0; actionIndex < upgrade.actions().size(); actionIndex++) {
                tryFireKillTrigger(player, upgrade, upgradeId, actionIndex, target, playerUuid, currentTick);
            }
        }
    }

    private static void tryFireKillTrigger(
            ServerPlayerEntity player,
            RoguelikeMCUpgradeData upgrade,
            Identifier upgradeId,
            int actionIndex,
            LivingEntity target,
            UUID playerUuid,
            long currentTick
    ) {
        RoguelikeMCUpgradeData.ActionData action = upgrade.actions().get(actionIndex);
        if (action.actionType() != UpgradeActionType.TRIGGER) {
            return;
        }

        TriggerActionPayload payload = parseTriggerPayload(action).resultOrPartial(RoguelikeMC.LOGGER::warn).orElse(null);
        if (payload == null || !Trigger.EVENT_KILL.equalsIgnoreCase(payload.eventType())) {
            return;
        }
        if (!matchesConditions(payload, target)) {
            return;
        }

        TriggerProgress progress = progress(playerUuid, upgradeId, actionIndex);
        if (currentTick < progress.nextAvailableTick()) {
            return;
        }

        progress.increment();
        if (progress.count() < payload.count()) {
            return;
        }

        progress.reset(currentTick + payload.cooldown());
        UpgradeActionHandlers.apply(new UpgradeActionContext(player, upgrade, payload.action()));
    }

    private static DataResult<TriggerActionPayload> parseTriggerPayload(RoguelikeMCUpgradeData.ActionData action) {
        return TriggerActionPayload.fromAction(action);
    }

    private static boolean matchesConditions(TriggerActionPayload payload, LivingEntity target) {
        for (TriggerActionPayload.TriggerConditionData condition : payload.conditions()) {
            if (!matchesCondition(condition, target)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesCondition(TriggerActionPayload.TriggerConditionData condition, LivingEntity target) {
        return switch (condition.type()) {
            case Trigger.CONDITION_TARGET_HOSTILE -> target instanceof HostileEntity && !target.isPlayer();
            default -> {
                RoguelikeMC.LOGGER.warn("Unsupported trigger condition type '{}'", condition.type());
                yield false;
            }
        };
    }

    private static TriggerProgress progress(UUID playerUuid, Identifier upgradeId, int actionIndex) {
        Map<TriggerStateKey, TriggerProgress> playerState = TRIGGER_STATE.computeIfAbsent(playerUuid, ignored -> new HashMap<>());
        TriggerStateKey key = new TriggerStateKey(upgradeId, actionIndex);
        return playerState.computeIfAbsent(key, ignored -> new TriggerProgress());
    }

    static void clearAllForTesting() {
        TRIGGER_STATE.clear();
    }

    private record TriggerStateKey(Identifier upgradeId, int actionIndex) {
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

        private void reset(long nextAvailableTick) {
            count = 0;
            this.nextAvailableTick = Math.max(0L, nextAvailableTick);
        }
    }
}
