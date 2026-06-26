package snowball049.roguelikemc.data;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.testutil.TestFixtures;
import snowball049.roguelikemc.testutil.UpgradeTestRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoguelikeMCPlayerDataTest {
    @BeforeEach
    void setUp() {
        UpgradeTestRegistry.loadRegistrySections("shared");
    }

    @AfterEach
    void tearDown() {
        UpgradeTestRegistry.clear();
    }

    @Test
    void resolvesOwnedUpgradesFromRegistry() {
        JsonObject fixture = TestFixtures.object(TestFixtures.GAMEPLAY, "playerData", "resolveUpgrades");
        RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();

        UpgradeTestRegistry.readIdentifierList(fixture.getAsJsonArray("temporaryUpgradeIds"))
                .forEach(id -> playerData.temporaryUpgradeIds.add(id));
        UpgradeTestRegistry.readIdentifierList(fixture.getAsJsonArray("permanentUpgradeIds"))
                .forEach(id -> playerData.permanentUpgradeIds.add(id));
        UpgradeTestRegistry.readIdentifierList(fixture.getAsJsonArray("missingUpgradeIds"))
                .forEach(id -> playerData.temporaryUpgradeIds.add(id));

        assertEquals(fixture.get("expectedTemporaryCount").getAsInt(), playerData.getTemporaryUpgrades().size());
        assertEquals(fixture.get("expectedPermanentCount").getAsInt(), playerData.getPermanentUpgrades().size());
        assertEquals(fixture.get("expectedAllUpgradeIdCount").getAsInt(), playerData.getAllUpgradeIds().size());
    }

    @Test
    void resetClearsProgressFlags() {
        RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();
        playerData.currentKillHostile = 9;
        playerData.currentLevelGain = 3;
        playerData.currentGameStage = 2;
        playerData.currentAdvancementGain = 1;
        playerData.keepEquipmentAfterDeath = true;
        playerData.revive = true;

        playerData.reset();

        assertEquals(0, playerData.currentKillHostile);
        assertEquals(0, playerData.currentLevelGain);
        assertEquals(0, playerData.currentGameStage);
        assertEquals(0, playerData.currentAdvancementGain);
        assertFalse(playerData.keepEquipmentAfterDeath);
        assertFalse(playerData.revive);
    }
}
