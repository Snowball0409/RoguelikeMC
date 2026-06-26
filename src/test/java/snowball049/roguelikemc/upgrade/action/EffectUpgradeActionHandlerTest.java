package snowball049.roguelikemc.upgrade.action;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.testutil.TestFixtures;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EffectUpgradeActionHandlerTest {
    @Test
    void matchesInfiniteEffectTicksFromFixture() {
        for (JsonObject testCase : TestFixtures.cases(TestFixtures.HANDLERS, "effectMatchesTick").toList()) {
            RoguelikeMCUpgradeData.ActionData action = new RoguelikeMCUpgradeData.ActionData(
                    "effect",
                    TestFixtures.readStringList(testCase.getAsJsonArray("values"))
            );

            boolean matches = EffectUpgradeActionHandler.INSTANCE.matchesTick(action);
            assertEquals(testCase.get("matches").getAsBoolean(), matches, testCase.get("name").getAsString());
        }
    }
}
