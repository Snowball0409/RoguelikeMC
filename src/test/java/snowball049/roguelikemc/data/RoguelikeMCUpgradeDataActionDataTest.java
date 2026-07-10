package snowball049.roguelikemc.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoguelikeMCUpgradeDataActionDataTest {
    @Test
    void legacyEventTypeReturnsEmptyStringWhenValueMissing() {
        RoguelikeMCUpgradeData.ActionData action = new RoguelikeMCUpgradeData.ActionData("event", List.of());

        assertEquals("", action.legacyEventType());
    }

    @Test
    void legacyEventTypeReturnsFirstValueWhenPresent() {
        RoguelikeMCUpgradeData.ActionData action = new RoguelikeMCUpgradeData.ActionData(
                "event",
                List.of("allow_creative_flying")
        );

        assertEquals("allow_creative_flying", action.legacyEventType());
    }
}
