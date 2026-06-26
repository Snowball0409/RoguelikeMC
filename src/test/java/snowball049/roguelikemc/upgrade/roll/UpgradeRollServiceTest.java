package snowball049.roguelikemc.upgrade.roll;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.testutil.TestFixtures;
import snowball049.roguelikemc.testutil.UpgradeTestRegistry;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradePoolManager;
import snowball049.roguelikemc.upgrade.enums.UpgradeStacking;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpgradeRollServiceTest {
    @BeforeEach
    void setUp() {
        UpgradeTestRegistry.loadRollRegistry();
    }

    @AfterEach
    void tearDown() {
        UpgradeTestRegistry.clear();
    }

    @Test
    void excludesOwnedUniqueUpgrades() {
        JsonObject scenario = TestFixtures.object(TestFixtures.GAMEPLAY, "roll", "excludeOwnedUnique");
        RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();
        UpgradeTestRegistry.readIdentifierList(scenario.getAsJsonArray("ownedPermanentUpgradeIds"))
                .forEach(id -> playerData.permanentUpgradeIds.add(id));

        Set<String> excludedIds = UpgradeTestRegistry.readIdentifierList(scenario.getAsJsonArray("excludedUpgradeIds"))
                .stream()
                .map(id -> id.getPath())
                .collect(Collectors.toSet());

        for (int attempt = 0; attempt < 50; attempt++) {
            List<RoguelikeMCUpgradeData> rolled = UpgradeRollService.rollOptions(playerData, new Random(attempt));
            assertFalse(rolled.isEmpty());
            assertTrue(rolled.stream().noneMatch(upgrade -> excludedIds.contains(upgrade.id())));
        }
    }

    @Test
    void returnsEmptyWhenCandidatePoolIsEmpty() {
        UpgradeTestRegistry.clear();
        RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();
        assertTrue(UpgradeRollService.rollOptions(playerData).isEmpty());
    }

    @Test
    void returnsDistinctOptionsUpToThree() {
        RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();
        List<RoguelikeMCUpgradeData> rolled = UpgradeRollService.rollOptions(playerData, new Random(7));

        assertEquals(3, rolled.size());
        assertEquals(3, rolled.stream().map(RoguelikeMCUpgradeData::id).collect(Collectors.toSet()).size());
    }

    @Test
    void ensuresAtLeastOneStackableOption() {
        RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();
        List<RoguelikeMCUpgradeData> rolled = UpgradeRollService.rollOptions(playerData, new Random(42));

        assertTrue(rolled.stream().anyMatch(upgrade -> upgrade.stacking() == UpgradeStacking.STACKABLE));
    }

    @Test
    void seededRollMatchesFixture() {
        JsonObject scenario = TestFixtures.object(TestFixtures.GAMEPLAY, "roll", "seeded");
        RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();

        List<RoguelikeMCUpgradeData> rolled = UpgradeRollService.rollOptions(
                playerData,
                new Random(scenario.get("seed").getAsLong())
        );

        List<String> actualIds = rolled.stream().map(RoguelikeMCUpgradeData::id).toList();
        List<String> expectedIds = TestFixtures.readStringList(scenario.getAsJsonArray("expectedUpgradeIds"));
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void usesActiveUpgradePoolWhenPresent() {
        JsonObject scenario = TestFixtures.object(TestFixtures.GAMEPLAY, "roll", "activePool");
        UpgradeTestRegistry.loadPools();

        RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();
        UpgradeTestRegistry.readIdentifierList(scenario.getAsJsonArray("activeUpgradePools"))
                .forEach(id -> playerData.activeUpgradePools.add(id));

        List<String> expectedPoolIds = TestFixtures.readStringList(scenario.getAsJsonArray("expectedUpgradeIds"));
        List<String> poolUpgradeIds = RoguelikeMCUpgradePoolManager
                .getUpgradesFromPool(playerData.activeUpgradePools.getFirst())
                .stream()
                .map(id -> id.toString())
                .toList();
        assertEquals(expectedPoolIds, poolUpgradeIds);

        for (int attempt = 0; attempt < 30; attempt++) {
            List<RoguelikeMCUpgradeData> rolled = UpgradeRollService.rollOptions(playerData, new Random(attempt));
            assertTrue(rolled.stream().allMatch(upgrade -> expectedPoolIds.contains("roguelikemc:" + upgrade.id())));
        }
    }
}
