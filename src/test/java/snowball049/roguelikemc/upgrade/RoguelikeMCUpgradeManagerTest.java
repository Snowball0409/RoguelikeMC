package snowball049.roguelikemc.upgrade;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.testutil.UpgradeTestRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoguelikeMCUpgradeManagerTest {
    @BeforeEach
    void setUp() {
        UpgradeTestRegistry.loadRegistrySections("shared");
    }

    @AfterEach
    void tearDown() {
        UpgradeTestRegistry.clear();
    }

    @Test
    void resolvesRegisteredUpgradeByIdentifier() {
        var upgrade = RoguelikeMCUpgradeManager.getUpgrade(Identifier.of("roguelikemc", "one_last_chance"));
        assertEquals("one_last_chance", upgrade.id());
        assertEquals(UpgradeIds.fromUpgradeData(upgrade), RoguelikeMCUpgradeManager.idFor(upgrade));
    }
}
