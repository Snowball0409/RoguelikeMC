package snowball049.roguelikemc.upgrade.gameplay;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

@SuppressWarnings("deprecation")
class UpgradeTriggerGameplayServiceTest {
    private ServerPlayerEntity player;
    private HostileEntity hostileTarget;
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
        serverWorld = Mockito.mock(ServerWorld.class);

        Mockito.when(player.getUuid()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000123"));
        Mockito.when(player.getServerWorld()).thenReturn(serverWorld);

        originalCommandHandler = UpgradeActionHandlers.get(UpgradeActionType.COMMAND);
        recordingCommandHandler = new RecordingCommandHandler();
        UpgradeActionHandlers.register(recordingCommandHandler);
        UpgradeTriggerGameplayService.clearAllForTesting();
    }

    @AfterEach
    void tearDown() {
        UpgradeActionHandlers.register(originalCommandHandler);
        UpgradeTriggerGameplayService.clearAllForTesting();
    }

    @Test
    void dispatchesNestedActionAfterCountThreshold() {
        Mockito.when(serverWorld.getTime()).thenReturn(20L, 21L);

        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "threshold_trigger",
                triggerAction(2, 0, "threshold")
        );

        UpgradeTriggerGameplayService.processKillTriggers(player, List.of(upgrade), hostileTarget);
        UpgradeTriggerGameplayService.processKillTriggers(player, List.of(upgrade), hostileTarget);

        assertEquals(List.of("threshold"), recordingCommandHandler.commands);
    }

    @Test
    void respectsCooldownBeforeTriggerCanFireAgain() {
        Mockito.when(serverWorld.getTime()).thenReturn(100L, 102L, 105L);

        RoguelikeMCUpgradeData upgrade = upgradeWithActions(
                "cooldown_trigger",
                triggerAction(1, 5, "cooldown")
        );

        UpgradeTriggerGameplayService.processKillTriggers(player, List.of(upgrade), hostileTarget);
        UpgradeTriggerGameplayService.processKillTriggers(player, List.of(upgrade), hostileTarget);
        UpgradeTriggerGameplayService.processKillTriggers(player, List.of(upgrade), hostileTarget);

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

        UpgradeTriggerGameplayService.processKillTriggers(player, List.of(upgrade), hostileTarget);

        assertEquals(List.of("first", "second"), recordingCommandHandler.commands);
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
        JsonObject nestedAction = new JsonObject();
        nestedAction.addProperty("type", "command");
        JsonArray nestedValue = new JsonArray();
        nestedValue.add(commandName);
        nestedAction.add("value", nestedValue);

        JsonObject triggerPayload = new JsonObject();
        triggerPayload.addProperty("eventType", "kill");
        triggerPayload.addProperty("count", count);
        triggerPayload.addProperty("cooldown", cooldown);

        JsonArray conditions = new JsonArray();
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "target_hostile");
        condition.add("payload", new JsonObject());
        conditions.add(condition);
        triggerPayload.add("conditions", conditions);
        triggerPayload.add("action", nestedAction);

        return new RoguelikeMCUpgradeData.ActionData("trigger", triggerPayload);
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
