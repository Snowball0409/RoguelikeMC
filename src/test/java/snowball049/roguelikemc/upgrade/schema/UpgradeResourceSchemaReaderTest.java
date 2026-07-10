package snowball049.roguelikemc.upgrade.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.testutil.TestFixtures;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;
import snowball049.roguelikemc.upgrade.enums.UpgradePersistence;
import snowball049.roguelikemc.upgrade.enums.UpgradeRarity;
import snowball049.roguelikemc.upgrade.enums.UpgradeStacking;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation")
class UpgradeResourceSchemaReaderTest {
    static Stream<String> resourceSchemaCases() {
        return TestFixtures.cases(TestFixtures.UPGRADES, "resource_schema")
                .map(testCase -> testCase.get("name").getAsString());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("resourceSchemaCases")
    void parsesLegacyAndAuthoredResourceFixtures(String caseName) {
        JsonObject testCase = findCase("resource_schema", caseName);
        JsonObject input = testCase.getAsJsonObject("input");
        JsonObject expected = testCase.getAsJsonObject("expected");

        RoguelikeMCUpgradeData upgrade = UpgradeResourceSchemaReader.parse(input).getOrThrow();

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
        JsonArray expectedActionValues = expected.getAsJsonArray("actionValues");
        JsonArray expectedActionPayloads = expected.has("actionPayloads")
                ? expected.getAsJsonArray("actionPayloads")
                : null;
        assertEquals(expectedActionTypes.size(), upgrade.actions().size());
        assertEquals(expectedActionValues.size(), upgrade.actions().size());

        for (int i = 0; i < upgrade.actions().size(); i++) {
            RoguelikeMCUpgradeData.ActionData action = upgrade.actions().get(i);
            assertEquals(
                    UpgradeActionType.fromString(expectedActionTypes.get(i).getAsString()),
                    action.actionType()
            );

            List<String> actualValues = action.value();
            JsonArray expectedValues = expectedActionValues.get(i).getAsJsonArray();
            assertEquals(expectedValues.size(), actualValues.size());
            for (int valueIndex = 0; valueIndex < expectedValues.size(); valueIndex++) {
                assertEquals(expectedValues.get(valueIndex).getAsString(), actualValues.get(valueIndex));
            }

            if (expectedActionPayloads != null) {
                assertEquals(expectedActionPayloads.get(i).getAsJsonObject(), action.payload());
            }
        }
    }

    private static JsonObject findCase(String group, String caseName) {
        return TestFixtures.cases(TestFixtures.UPGRADES, group)
                .filter(testCase -> caseName.equals(testCase.get("name").getAsString()))
                .findFirst()
                .orElseThrow();
    }
}
