package snowball049.roguelikemc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.testutil.TestFixtures;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoguelikeMCCommonConfigTest {
    private static final Gson GSON = new GsonBuilder().create();

    @Test
    void deserializesConfigFixture() {
        RoguelikeMCCommonConfig actual = GSON.fromJson(
                TestFixtures.object(TestFixtures.CONFIG, "custom"),
                RoguelikeMCCommonConfig.class
        );

        assertEquals(true, actual.enableUpgradeSystem);
        assertEquals(false, actual.enableKillHostileEntityUpgrade);
        assertEquals(25, actual.killHostileEntityRequirement);
        assertEquals(0.75, actual.decayInventoryPercentage);
        assertEquals("minecraft:bone", actual.decayItem);
        assertEquals(2, actual.decayItemAmountMinMax.get(0));
        assertEquals(5, actual.decayItemAmountMinMax.get(1));
        assertEquals("roguelikemc:test_ban", actual.bannedUpgrades.getFirst());
    }

    @Test
    void defaultConfigMatchesFixtureDefaults() {
        RoguelikeMCCommonConfig defaults = new RoguelikeMCCommonConfig();
        assertEquals(true, defaults.enableUpgradeSystem);
        assertEquals(10, defaults.killHostileEntityRequirement);
        assertEquals(0.6, defaults.decayInventoryPercentage);
        assertEquals("minecraft:rotten_flesh", defaults.decayItem);
    }
}
