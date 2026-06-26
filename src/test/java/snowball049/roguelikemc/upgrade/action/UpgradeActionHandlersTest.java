package snowball049.roguelikemc.upgrade.action;

import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.testutil.TestFixtures;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpgradeActionHandlersTest {
    @Test
    void registersAllActionTypesFromFixture() {
        for (String actionTypeId : TestFixtures.readStringList(TestFixtures.array(TestFixtures.HANDLERS, "actionTypes"))) {
            UpgradeActionType actionType = UpgradeActionType.fromString(actionTypeId);
            assertNotNull(actionType);
            assertEquals(actionType, UpgradeActionHandlers.get(actionType).type());
        }
    }
}
