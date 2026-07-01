package snowball049.roguelikemc.upgrade.point;

import com.google.gson.JsonObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import snowball049.roguelikemc.testutil.TestFixtures;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpgradePointServiceTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("applyAddCases")
    void applyAddFromFixture(JsonObject testCase) {
        int current = testCase.get("current").getAsInt();
        int delta = testCase.get("delta").getAsInt();
        int expected = testCase.get("expected").getAsInt();
        assertEquals(expected, UpgradePointService.applyAdd(current, delta), testCase.get("name").getAsString());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("applySpendCases")
    void applySpendFromFixture(JsonObject testCase) {
        int current = testCase.get("current").getAsInt();
        int cost = testCase.get("cost").getAsInt();
        int expected = testCase.get("expected").getAsInt();
        assertEquals(expected, UpgradePointService.applySpend(current, cost), testCase.get("name").getAsString());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("canSpendCases")
    void canSpendFromFixture(JsonObject testCase) {
        int current = testCase.get("current").getAsInt();
        boolean expected = testCase.get("expected").getAsBoolean();
        assertEquals(expected, UpgradePointService.canSpend(current), testCase.get("name").getAsString());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("applyKillProgressCases")
    void applyKillProgressFromFixture(JsonObject testCase) {
        UpgradePointService.KillProgressResult result = UpgradePointService.applyKillProgress(
                testCase.get("currentKills").getAsInt(),
                testCase.get("killsToAdd").getAsInt(),
                testCase.get("requirement").getAsInt()
        );

        assertEquals(testCase.get("remaining").getAsInt(), result.remainingKills(), testCase.get("name").getAsString());
        assertEquals(testCase.get("pointsEarned").getAsInt(), result.pointsEarned(), testCase.get("name").getAsString());
    }

    static Stream<JsonObject> applyAddCases() {
        return TestFixtures.cases(TestFixtures.UPGRADE_POINTS, "applyAdd");
    }

    static Stream<JsonObject> applySpendCases() {
        return TestFixtures.cases(TestFixtures.UPGRADE_POINTS, "applySpend");
    }

    static Stream<JsonObject> canSpendCases() {
        return TestFixtures.cases(TestFixtures.UPGRADE_POINTS, "canSpend");
    }

    static Stream<JsonObject> applyKillProgressCases() {
        return TestFixtures.cases(TestFixtures.UPGRADE_POINTS, "applyKillProgress");
    }
}
