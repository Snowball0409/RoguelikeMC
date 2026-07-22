package snowball049.roguelikemc.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import snowball049.roguelikemc.testutil.TestFixtures;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;
import snowball049.roguelikemc.upgrade.enums.UpgradePersistence;
import snowball049.roguelikemc.upgrade.enums.UpgradeRarity;
import snowball049.roguelikemc.upgrade.enums.UpgradeStacking;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("deprecation")
class RoguelikeMCUpgradeDataCodecTest {
    static Stream<String> upgradeCodecCases() {
        return TestFixtures.cases(TestFixtures.UPGRADES, "codec")
                .map(testCase -> testCase.get("name").getAsString());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("upgradeCodecCases")
    void parsesUpgradeJsonFixture(String caseName) {
        JsonObject testCase = findCase("codec", caseName);
        JsonObject input = testCase.getAsJsonObject("input");
        JsonObject expected = testCase.getAsJsonObject("expected");

        RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeData.CODEC
                .parse(JsonOps.INSTANCE, input)
                .getOrThrow(false, error -> { throw new IllegalStateException(error); });

        assertEquals(expected.get("id").getAsString(), upgrade.id());
        assertEquals(expected.get("name").getAsString(), upgrade.name());
        assertEquals(expected.get("description").getAsString(), upgrade.description());
        assertEquals(UpgradeRarity.fromString(expected.get("tier").getAsString()), upgrade.rarity());
        assertEquals(
                UpgradePersistence.fromPermanentFlag(expected.get("isPermanent").getAsBoolean()),
                upgrade.persistence()
        );
        assertEquals(
                UpgradeStacking.fromUniqueFlag(expected.get("isUnique").getAsBoolean()),
                upgrade.stacking()
        );
        assertEquals(expected.get("icon").getAsString(), upgrade.icon());

        JsonArray expectedActionTypes = expected.getAsJsonArray("actionTypes");
        JsonArray expectedActionPayloads = expected.has("actionPayloads")
                ? expected.getAsJsonArray("actionPayloads")
                : null;
        assertEquals(expectedActionTypes.size(), upgrade.actions().size());
        for (int i = 0; i < expectedActionTypes.size(); i++) {
            assertEquals(
                    UpgradeActionType.fromString(expectedActionTypes.get(i).getAsString()),
                    upgrade.actions().get(i).actionType()
            );

            if (expectedActionPayloads != null) {
                assertEquals(expectedActionPayloads.get(i).getAsJsonObject(), upgrade.actions().get(i).payload());
            }
        }

        if (expected.has("firstActionValues")) {
            JsonArray expectedValues = expected.getAsJsonArray("firstActionValues");
            List<String> actualValues = upgrade.actions().get(0).value();
            assertEquals(expectedValues.size(), actualValues.size());
            for (int i = 0; i < expectedValues.size(); i++) {
                assertEquals(expectedValues.get(i).getAsString(), actualValues.get(i));
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("upgradeCodecCases")
    void roundTripsUpgradeJsonFixture(String caseName) {
        JsonObject input = findCase("codec", caseName).getAsJsonObject("input");

        RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeData.CODEC
                .parse(JsonOps.INSTANCE, input)
                .getOrThrow(false, error -> { throw new IllegalStateException(error); });
        JsonObject encoded = RoguelikeMCUpgradeData.CODEC
                .encodeStart(JsonOps.INSTANCE, upgrade)
                .getOrThrow(false, error -> { throw new IllegalStateException(error); })
                .getAsJsonObject();
        RoguelikeMCUpgradeData roundTripped = RoguelikeMCUpgradeData.CODEC
                .parse(JsonOps.INSTANCE, encoded)
                .getOrThrow(false, error -> { throw new IllegalStateException(error); });

        assertNotNull(roundTripped);
        assertEquals(upgrade, roundTripped);
    }

    private static JsonObject findCase(String group, String caseName) {
        return TestFixtures.cases(TestFixtures.UPGRADES, group)
                .filter(testCase -> caseName.equals(testCase.get("name").getAsString()))
                .findFirst()
                .orElseThrow();
    }
}
