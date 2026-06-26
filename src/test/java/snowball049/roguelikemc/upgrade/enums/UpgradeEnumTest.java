package snowball049.roguelikemc.upgrade.enums;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.testutil.TestFixtures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpgradeEnumTest {
    @Test
    void upgradeRarityFromStringIsCaseInsensitive() {
        assertEquals(UpgradeRarity.LEGENDARY, UpgradeRarity.fromString("Legendary"));
        assertEquals(UpgradeRarity.COMMON, UpgradeRarity.fromString("common"));
    }

    @Test
    void upgradeRarityBlankDefaultsToCommon() {
        assertEquals(UpgradeRarity.COMMON, UpgradeRarity.fromString(null));
        assertEquals(UpgradeRarity.COMMON, UpgradeRarity.fromString("  "));
    }

    @Test
    void upgradePersistenceFromPermanentFlag() {
        assertEquals(UpgradePersistence.PERMANENT, UpgradePersistence.fromPermanentFlag(true));
        assertEquals(UpgradePersistence.TEMPORARY, UpgradePersistence.fromPermanentFlag(false));
    }

    @Test
    void upgradeStackingFromUniqueFlag() {
        assertEquals(UpgradeStacking.UNIQUE, UpgradeStacking.fromUniqueFlag(true));
        assertEquals(UpgradeStacking.STACKABLE, UpgradeStacking.fromUniqueFlag(false));
    }

    @Test
    void upgradeActionTypeFromString() {
        assertEquals(UpgradeActionType.EVENT, UpgradeActionType.fromString("event"));
        assertEquals(UpgradeActionType.ATTRIBUTE, UpgradeActionType.fromString("ATTRIBUTE"));
        assertNull(UpgradeActionType.fromString("unknown_action"));
        assertNull(UpgradeActionType.fromString("  "));
    }

    @Test
    void upgradeRarityRollWeightsMatchFixture() {
        for (JsonObject testCase : TestFixtures.cases(TestFixtures.HANDLERS, "rarityWeights").toList()) {
            UpgradeRarity rarity = UpgradeRarity.fromString(testCase.get("tier").getAsString());
            assertEquals(testCase.get("rollWeight").getAsInt(), rarity.rollWeight());
        }
    }
}
