package snowball049.roguelikemc.upgrade.runtime;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;
import snowball049.roguelikemc.upgrade.action.UpgradeActionHandler;
import snowball049.roguelikemc.upgrade.action.UpgradeActionHandlers;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("deprecation")
class UpgradeTriggerRuntimeServiceTest {
    private ServerPlayerEntity player;
    private HostileEntity hostileTarget;
    private LivingEntity passiveTarget;
    private ServerWorld serverWorld;
    private UpgradeActionHandler originalCommandHandler;
    private RecordingCommandHandler recordingCommandHandler;

    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @BeforeEach
    void setUp() {
        player = Mockito.mock(ServerPlayerEntity.class);
        hostileTarget = Mockito.mock(HostileEntity.class);
        passiveTarget = Mockito.mock(VillagerEntity.class);
        serverWorld = Mockito.mock(ServerWorld.class);

        Mockito.when(player.getUuid()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000123"));
        Mockito.when(player.getServerWorld()).thenReturn(serverWorld);

        originalCommandHandler = UpgradeActionHandlers.get(UpgradeActionType.COMMAND);
        recordingCommandHandler = new RecordingCommandHandler();
        UpgradeActionHandlers.register(recordingCommandHandler);
        UpgradeTriggerRuntimeService.clearAllForTesting();
    }

    @AfterEach
    void tearDown() {
        UpgradeActionHandlers.register(originalCommandHandler);
        UpgradeTriggerRuntimeService.clearAllForTesting();
    }

    @Test
    void dispatchesNestedActionAfterCountThreshold() {
        Mockito.when(serverWorld.getTime()).thenReturn(20L, 21L);

        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "threshold_trigger",
                triggerAction(2, 0, "threshold")
        );

        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(upgrade), hostileTarget);
        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(upgrade), hostileTarget);

        assertEquals(List.of("threshold"), recordingCommandHandler.commands);
    }

    @Test
    void respectsCooldownBeforeTriggerCanFireAgain() {
        Mockito.when(serverWorld.getTime()).thenReturn(100L, 102L, 105L);

        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "cooldown_trigger",
                triggerAction(1, 5, "cooldown")
        );

        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(upgrade), hostileTarget);
        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(upgrade), hostileTarget);
        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(upgrade), hostileTarget);

        assertEquals(List.of("cooldown", "cooldown"), recordingCommandHandler.commands);
    }

    @Test
    void isolatesStatePerTriggerActionIndex() {
        Mockito.when(serverWorld.getTime()).thenReturn(300L);

        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "isolated_trigger_actions",
                triggerAction(1, 5, "first"),
                triggerAction(1, 5, "second")
        );

        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(upgrade), hostileTarget);

        assertEquals(List.of("first", "second"), recordingCommandHandler.commands);
    }

    @Test
    void ignoresNonHostileTargetsForTargetHostileCondition() {
        Mockito.when(serverWorld.getTime()).thenReturn(400L);

        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "hostile_only_trigger",
                triggerAction(1, 0, "should_not_fire")
        );

        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(upgrade), passiveTarget);

        assertTrue(recordingCommandHandler.commands.isEmpty());
    }

    @Test
    void clearUpgradeOnlyClearsMatchingUpgradeProgress() {
        Mockito.when(serverWorld.getTime()).thenReturn(20L, 21L, 22L);

        RoguelikeMCUpgradeData firstUpgrade = upgradeWithActions("first_upgrade", triggerAction(1, 0, "first"));
        RoguelikeMCUpgradeData secondUpgrade = upgradeWithActions("second_upgrade", triggerAction(2, 0, "second"));

        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(secondUpgrade), hostileTarget);
        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(firstUpgrade), hostileTarget);
        UpgradeTriggerRuntimeService.clearUpgrade(player, Identifier.of("roguelikemc", "first_upgrade"));
        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(secondUpgrade), hostileTarget);

        assertEquals(List.of("first", "second"), recordingCommandHandler.commands);
    }

    @Test
    void clearPlayerRemovesAllTriggerProgress() {
        Mockito.when(serverWorld.getTime()).thenReturn(600L, 601L);

        RoguelikeMCUpgradeData upgrade = upgradeWithActions("reset_trigger", triggerAction(1, 0, "before_clear"));

        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(upgrade), hostileTarget);
        UpgradeTriggerRuntimeService.clearPlayer(player);
        UpgradeTriggerRuntimeService.processKillTriggers(player, List.of(upgrade), hostileTarget);

        assertEquals(List.of("before_clear", "before_clear"), recordingCommandHandler.commands);
    }

    @Test
    void attackEventDispatchesWhenHostileTargetMatches() {
        Mockito.when(serverWorld.getTime()).thenReturn(25L);

        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "attack_trigger",
                triggerAction("attack", 1, 0, false, "attacked", condition("target_hostile"))
        );

        UpgradeTriggerRuntimeService.processEvent(
                player,
                "attack",
                List.of(upgrade),
                TriggerEventContext.forTarget(hostileTarget)
        );

        assertEquals(List.of("attacked"), recordingCommandHandler.commands);
    }

    @Test
    void breakEventMatchesBlockIdCondition() {
        Mockito.when(serverWorld.getTime()).thenReturn(20L);
        BlockState ore = Blocks.DIAMOND_ORE.getDefaultState();
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "break_diamond",
                triggerAction("break", 1, 0, false, "mined", targetBlockId("minecraft:diamond_ore"))
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "break",
                List.of(upgrade),
                TriggerEventContext.forBlock(ore, World.OVERWORLD, BlockPos.ORIGIN)
        );
        assertEquals(List.of("mined"), recordingCommandHandler.commands);
    }

    @Test
    void breakEventIgnoresNonMatchingBlock() {
        BlockState dirt = Blocks.DIRT.getDefaultState();
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "break_diamond",
                triggerAction("break", 1, 0, false, "mined", targetBlockId("minecraft:diamond_ore"))
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "break",
                List.of(upgrade),
                TriggerEventContext.forBlock(dirt, World.OVERWORLD, BlockPos.ORIGIN)
        );
        assertTrue(recordingCommandHandler.commands.isEmpty());
    }

    @Test
    void tradeEventDispatchesForVillagerMerchant() {
        Mockito.when(serverWorld.getTime()).thenReturn(30L);
        VillagerEntity villager = Mockito.mock(VillagerEntity.class);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "trade_villager",
                triggerAction("trade", 1, 0, false, "traded", condition("trade_villager"))
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "trade",
                List.of(upgrade),
                TriggerEventContext.forMerchant(villager)
        );
        assertEquals(List.of("traded"), recordingCommandHandler.commands);
    }

    @Test
    void tradeEventDispatchesForWanderingTraderMerchant() {
        Mockito.when(serverWorld.getTime()).thenReturn(31L);
        WanderingTraderEntity trader = Mockito.mock(WanderingTraderEntity.class);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "trade_wandering",
                triggerAction("trade", 1, 0, false, "traded")
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "trade",
                List.of(upgrade),
                TriggerEventContext.forMerchant(trader)
        );
        assertEquals(List.of("traded"), recordingCommandHandler.commands);
    }

    @Test
    void levelupAddsMultipleLevelsToProgress() {
        Mockito.when(serverWorld.getTime()).thenReturn(40L, 41L);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "levelup",
                triggerAction("levelup", 5, 0, false, "leveled")
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "levelup",
                List.of(upgrade),
                TriggerEventContext.forLevelGain(5)
        );
        assertEquals(List.of("leveled"), recordingCommandHandler.commands);
    }

    @Test
    void levelupPartialProgressDoesNotFire() {
        Mockito.when(serverWorld.getTime()).thenReturn(42L);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "levelup",
                triggerAction("levelup", 5, 0, false, "leveled")
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "levelup",
                List.of(upgrade),
                TriggerEventContext.forLevelGain(3)
        );
        assertTrue(recordingCommandHandler.commands.isEmpty());
    }

    @Test
    void levelupPartialProgressAccumulatesAcrossCalls() {
        Mockito.when(serverWorld.getTime()).thenReturn(43L, 44L);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "levelup",
                triggerAction("levelup", 5, 0, false, "leveled")
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "levelup",
                List.of(upgrade),
                TriggerEventContext.forLevelGain(3)
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "levelup",
                List.of(upgrade),
                TriggerEventContext.forLevelGain(3)
        );
        assertEquals(List.of("leveled"), recordingCommandHandler.commands);
    }

    @Test
    void levelupFiresOncePerThresholdWhenGainingManyLevels() {
        Mockito.when(serverWorld.getTime()).thenReturn(45L);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "levelup_multi",
                triggerAction("levelup", 1, 0, false, "echo")
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "levelup",
                List.of(upgrade),
                TriggerEventContext.forLevelGain(5)
        );
        assertEquals(List.of("echo", "echo", "echo", "echo", "echo"), recordingCommandHandler.commands);
    }

    @Test
    void levelupCanFireMultipleTimesFromSingleGainWhenThresholdAllows() {
        Mockito.when(serverWorld.getTime()).thenReturn(46L);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "levelup_bundle",
                triggerAction("levelup", 5, 0, false, "temper")
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "levelup",
                List.of(upgrade),
                TriggerEventContext.forLevelGain(10)
        );
        assertEquals(List.of("temper", "temper"), recordingCommandHandler.commands);
    }

    @Test
    void damagedEventDispatchesNestedAction() {
        Mockito.when(serverWorld.getTime()).thenReturn(50L);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "damaged_trigger",
                triggerAction("damaged", 1, 0, false, "hurt")
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "damaged",
                List.of(upgrade),
                TriggerEventContext.empty()
        );
        assertEquals(List.of("hurt"), recordingCommandHandler.commands);
    }

    @Test
    void breakEventMatchesBlockTagCondition() {
        Mockito.when(serverWorld.getTime()).thenReturn(51L);
        // Bootstrap tests do not load datapack tags; stub isIn for tag matching.
        BlockState ore = Mockito.mock(BlockState.class);
        Mockito.when(ore.isIn(Mockito.<TagKey<Block>>any())).thenReturn(true);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "break_coal_tag",
                triggerAction("break", 1, 0, false, "mined", targetBlockTag("minecraft:coal_ores"))
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "break",
                List.of(upgrade),
                TriggerEventContext.forBlock(ore, World.OVERWORLD, BlockPos.ORIGIN)
        );
        assertEquals(List.of("mined"), recordingCommandHandler.commands);
    }

    @Test
    void targetBlockFailsClosedWhenBothBlockAndTagArePresent() {
        Mockito.when(serverWorld.getTime()).thenReturn(52L);
        BlockState ore = Blocks.COAL_ORE.getDefaultState();
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "target_block");
        JsonObject payload = new JsonObject();
        payload.addProperty("block", "minecraft:coal_ore");
        payload.addProperty("tag", "minecraft:coal_ores");
        condition.add("payload", payload);
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "break_invalid",
                triggerAction("break", 1, 0, false, "mined", condition)
        );
        UpgradeTriggerRuntimeService.processEvent(
                player,
                "break",
                List.of(upgrade),
                TriggerEventContext.forBlock(ore, World.OVERWORLD, BlockPos.ORIGIN)
        );
        assertTrue(recordingCommandHandler.commands.isEmpty());
    }

    @Test
    void clearPlayerAlsoClearsPlayerPlacedTracking() {
        Mockito.when(serverWorld.getTime()).thenReturn(53L, 54L);
        BlockPos pos = new BlockPos(8, 64, 8);
        BlockState ore = Blocks.DIAMOND_ORE.getDefaultState();
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "break_diamond",
                triggerAction("break", 1, 0, true, "mined", targetBlockId("minecraft:diamond_ore"))
        );

        UpgradeTriggerRuntimeService.onBlockPlacedByPlayer(player, World.OVERWORLD, pos, ore, List.of(upgrade));
        UpgradeTriggerRuntimeService.clearPlayer(player);
        UpgradeTriggerRuntimeService.onBlockBroken(player, World.OVERWORLD, pos, ore, List.of(upgrade));

        assertEquals(List.of("mined"), recordingCommandHandler.commands);
    }

    @Test
    void breakingPlayerPlacedBlockIsIgnoredWhenPreventPlaceBreakEnabled() {
        Mockito.when(serverWorld.getTime()).thenReturn(100L);
        BlockPos pos = new BlockPos(3, 64, 3);
        BlockState ore = Blocks.DIAMOND_ORE.getDefaultState();
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "break_diamond",
                triggerAction("break", 1, 0, true, "mined", targetBlockId("minecraft:diamond_ore"))
        );

        UpgradeTriggerRuntimeService.onBlockPlacedByPlayer(player, World.OVERWORLD, pos, ore, List.of(upgrade));
        UpgradeTriggerRuntimeService.onBlockBroken(player, World.OVERWORLD, pos, ore, List.of(upgrade));

        assertTrue(recordingCommandHandler.commands.isEmpty());
    }

    @Test
    void breakingPlayerPlacedBlockStillCountsWhenPreventPlaceBreakDisabled() {
        Mockito.when(serverWorld.getTime()).thenReturn(101L);
        BlockPos pos = new BlockPos(5, 64, 5);
        BlockState ore = Blocks.DIAMOND_ORE.getDefaultState();
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "break_diamond_open",
                triggerAction("break", 1, 0, false, "mined", targetBlockId("minecraft:diamond_ore"))
        );

        UpgradeTriggerRuntimeService.onBlockPlacedByPlayer(player, World.OVERWORLD, pos, ore, List.of(upgrade));
        UpgradeTriggerRuntimeService.onBlockBroken(player, World.OVERWORLD, pos, ore, List.of(upgrade));

        assertEquals(List.of("mined"), recordingCommandHandler.commands);
    }

    @Test
    void placingMatchingBlockDecrementsOnlyPreventPlaceBreakTriggers() {
        Mockito.when(serverWorld.getTime()).thenReturn(110L, 111L, 112L);
        BlockState ore = Blocks.DIAMOND_ORE.getDefaultState();
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "break_diamond",
                triggerAction("break", 2, 0, true, "mined", targetBlockId("minecraft:diamond_ore"))
        );
        UpgradeTriggerRuntimeService.onBlockBroken(player, World.OVERWORLD, new BlockPos(1, 1, 1), ore, List.of(upgrade));
        UpgradeTriggerRuntimeService.onBlockPlacedByPlayer(player, World.OVERWORLD, new BlockPos(2, 2, 2), ore, List.of(upgrade));
        UpgradeTriggerRuntimeService.onBlockBroken(player, World.OVERWORLD, new BlockPos(3, 3, 3), ore, List.of(upgrade));
        assertTrue(recordingCommandHandler.commands.isEmpty());
        UpgradeTriggerRuntimeService.onBlockBroken(player, World.OVERWORLD, new BlockPos(4, 4, 4), ore, List.of(upgrade));
        assertEquals(List.of("mined"), recordingCommandHandler.commands);
    }

    @Test
    void playerPlacedCapEvictsOldestEntry() {
        BlockState ore = Blocks.DIAMOND_ORE.getDefaultState();
        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "break_diamond",
                triggerAction("break", 1, 0, true, "mined", targetBlockId("minecraft:diamond_ore"))
        );
        BlockPos oldest = new BlockPos(0, 64, 0);
        BlockPos newest = new BlockPos(65, 64, 65);
        for (int i = 0; i < 64; i++) {
            UpgradeTriggerRuntimeService.onBlockPlacedByPlayer(
                    player, World.OVERWORLD, new BlockPos(i, 64, i), ore, List.of(upgrade));
        }
        UpgradeTriggerRuntimeService.onBlockPlacedByPlayer(
                player, World.OVERWORLD, newest, ore, List.of(upgrade));
        Mockito.when(serverWorld.getTime()).thenReturn(200L);
        UpgradeTriggerRuntimeService.onBlockBroken(player, World.OVERWORLD, oldest, ore, List.of(upgrade));
        assertEquals(List.of("mined"), recordingCommandHandler.commands);
    }

    private static RoguelikeMCUpgradeData upgradeWithActions(String id, RoguelikeMCUpgradeData.ActionData... actions) {
        return new RoguelikeMCUpgradeData(
                id,
                "upgrade.roguelikemc.name." + id,
                "",
                "common",
                false,
                false,
                "",
                List.of(actions)
        );
    }

    private static RoguelikeMCUpgradeData.ActionData triggerAction(int count, int cooldown, String commandName) {
        return triggerAction("kill", count, cooldown, false, commandName, condition("target_hostile"));
    }

    private static RoguelikeMCUpgradeData.ActionData triggerAction(
            String eventType,
            int count,
            int cooldown,
            boolean preventPlaceBreak,
            String commandName,
            JsonObject... conditionObjects
    ) {
        JsonObject nestedAction = new JsonObject();
        nestedAction.addProperty("type", "command");
        JsonArray nestedValue = new JsonArray();
        nestedValue.add(commandName);
        nestedAction.add("value", nestedValue);

        JsonObject triggerPayload = new JsonObject();
        triggerPayload.addProperty("eventType", eventType);
        triggerPayload.addProperty("count", count);
        triggerPayload.addProperty("cooldown", cooldown);
        if (preventPlaceBreak) {
            triggerPayload.addProperty("preventPlaceBreak", true);
        }

        JsonArray conditions = new JsonArray();
        for (JsonObject conditionObject : conditionObjects) {
            conditions.add(conditionObject);
        }
        triggerPayload.add("conditions", conditions);
        triggerPayload.add("action", nestedAction);

        return new RoguelikeMCUpgradeData.ActionData("trigger", triggerPayload);
    }

    private static JsonObject condition(String type) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", type);
        condition.add("payload", new JsonObject());
        return condition;
    }

    private static JsonObject targetBlockId(String blockId) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "target_block");
        JsonObject payload = new JsonObject();
        payload.addProperty("block", blockId);
        condition.add("payload", payload);
        return condition;
    }

    private static JsonObject targetBlockTag(String tagId) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "target_block");
        JsonObject payload = new JsonObject();
        payload.addProperty("tag", tagId);
        condition.add("payload", payload);
        return condition;
    }

    private static final class RecordingCommandHandler implements UpgradeActionHandler {
        private final List<String> commands = new ArrayList<>();

        @Override
        public UpgradeActionType type() {
            return UpgradeActionType.COMMAND;
        }

        @Override
        public void apply(UpgradeActionContext context) {
            commands.add(context.action().value().getFirst());
        }
    }
}
