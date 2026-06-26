package snowball049.roguelikemc.upgrade.action.event;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.testutil.TestFixtures;
import snowball049.roguelikemc.testutil.UpgradeTestRegistry;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

class SetEquipmentEventHandlerTest {
    @Test
    void matchesPeriodicTickFromFixture() {
        SetEquipmentEventHandler handler = new SetEquipmentEventHandler();
        var upgrade = UpgradeTestRegistry.registryUpgrade("one_last_chance");

        for (JsonObject testCase : TestFixtures.cases(TestFixtures.HANDLERS, "setEquipmentMatchesTick").toList()) {
            RoguelikeMCUpgradeData.ActionData action = new RoguelikeMCUpgradeData.ActionData(
                    "event",
                    TestFixtures.readStringList(testCase.getAsJsonArray("values"))
            );
            UpgradeActionContext context = new UpgradeActionContext(null, upgrade, action);

            org.junit.jupiter.api.Assertions.assertEquals(
                    testCase.get("matches").getAsBoolean(),
                    handler.matchesPeriodicTick(context),
                    testCase.get("name").getAsString()
            );
        }
    }
}
