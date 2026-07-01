package snowball049.roguelikemc.gameplay.death;

import com.google.gson.JsonObject;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import snowball049.roguelikemc.testutil.TestFixtures;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecayItemStacksTest {
    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    static Stream<JsonObject> shouldDecayCases() {
        return TestFixtures.cases(TestFixtures.DEATH, "shouldDecay");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("shouldDecayCases")
    void shouldDecayFromFixture(JsonObject testCase) {
        boolean expected = testCase.get("expected").getAsBoolean();
        double chance = testCase.get("chance").getAsDouble();
        double roll = testCase.get("roll").getAsDouble();
        assertEquals(expected, DecayItemStacks.shouldDecay(chance, roll), testCase.get("name").getAsString());
    }

    @Test
    void randomCountUsesInclusiveRange() {
        assertEquals(4, DecayItemStacks.randomCount(4, 4, new Random()));
    }

    @Test
    void randomCountRejectsInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> DecayItemStacks.randomCount(5, 2, new Random()));
    }

    @Test
    void createUsesConfiguredItemAndCount() {
        ItemStack stack = DecayItemStacks.create(Items.BONE, 2, 2, new Random(0));
        assertEquals(Items.BONE, stack.getItem());
        assertEquals(2, stack.getCount());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("capCountCases")
    void capCountToSourceFromFixture(JsonObject testCase) {
        ItemStack source = new ItemStack(Items.DIAMOND, testCase.get("sourceCount").getAsInt());
        ItemStack decayed = new ItemStack(Items.ROTTEN_FLESH, testCase.get("decayedCount").getAsInt());
        DecayItemStacks.capCountToSource(source, decayed);
        assertEquals(testCase.get("expected").getAsInt(), decayed.getCount(), testCase.get("name").getAsString());
    }

    static Stream<JsonObject> capCountCases() {
        return TestFixtures.cases(TestFixtures.DEATH, "capCountToSource");
    }
}
