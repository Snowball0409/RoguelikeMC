package snowball049.roguelikemc.upgrade.action.event;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.testutil.TestFixtures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpgradeEventHandlersTest {
    @Test
    void registersExpectedEventHandlersFromFixture() {
        for (JsonObject expected : TestFixtures.cases(TestFixtures.HANDLERS, "eventHandlers").toList()) {
            String eventType = expected.get("eventType").getAsString();
            int tickInterval = expected.get("tickInterval").getAsInt();

            UpgradeEventHandler handler = UpgradeEventHandlers.get(eventType);
            assertNotNull(handler, "Missing handler for " + eventType);
            assertEquals(eventType, handler.eventType());
            assertEquals(tickInterval, handler.tickInterval());
        }
    }
}
