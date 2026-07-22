package snowball049.roguelikemc.upgrade;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Identifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.testutil.TestFixtures;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpgradeIdsTest {
    static Stream<String> upgradeIdCases() {
        return TestFixtures.cases(TestFixtures.UPGRADES, "identifiers")
                .map(testCase -> testCase.get("name").getAsString());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("upgradeIdCases")
    void resolvesIdentifierFromUpgradeFixture(String caseName) {
        JsonObject testCase = TestFixtures.cases(TestFixtures.UPGRADES, "identifiers")
                .filter(entry -> caseName.equals(entry.get("name").getAsString()))
                .findFirst()
                .orElseThrow();

        RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeData.CODEC
                .parse(JsonOps.INSTANCE, testCase.getAsJsonObject("input"))
                .getOrThrow(false, error -> { throw new IllegalStateException(error); });

        Identifier identifier = UpgradeIds.fromUpgradeData(upgrade);
        assertEquals(new Identifier(testCase.get("identifier").getAsString()), identifier);
    }
}
