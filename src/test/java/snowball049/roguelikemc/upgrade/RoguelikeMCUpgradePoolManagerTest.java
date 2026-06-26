package snowball049.roguelikemc.upgrade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.testutil.TestFixtures;
import snowball049.roguelikemc.testutil.UpgradeTestRegistry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoguelikeMCUpgradePoolManagerTest {
    @BeforeEach
    void setUp() {
        UpgradeTestRegistry.loadRegistry();
        UpgradeTestRegistry.loadPools();
    }

    @AfterEach
    void tearDown() {
        UpgradeTestRegistry.clear();
    }

    @Test
    void exposesConfiguredPoolUpgrades() {
        var pool = TestFixtures.object(TestFixtures.GAMEPLAY, "pools", "test_pool");
        List<String> expected = TestFixtures.readStringList(pool.getAsJsonArray("upgradeIds"));
        List<String> actual = RoguelikeMCUpgradePoolManager
                .getUpgradesFromPool(net.minecraft.util.Identifier.of(pool.get("id").getAsString()))
                .stream()
                .map(net.minecraft.util.Identifier::toString)
                .toList();

        assertEquals(expected, actual);
    }
}
