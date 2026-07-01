package snowball049.roguelikemc.datagen;

import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultUpgradeDefinitionsTest {
    @Test
    void providesExpectedNumberOfDefaultUpgrades() {
        assertEquals(52, DefaultUpgradeDefinitions.defaultUpgrades().size());
    }

    @Test
    void defaultUpgradesHaveUniqueIds() {
        Set<String> ids = DefaultUpgradeDefinitions.defaultUpgrades().stream()
                .map(RoguelikeMCUpgradeData::id)
                .collect(Collectors.toSet());
        assertEquals(DefaultUpgradeDefinitions.defaultUpgrades().size(), ids.size());
    }

    @Test
    void mightyForceMatchesExpectedDefinition() {
        RoguelikeMCUpgradeData mightyForce = DefaultUpgradeDefinitions.defaultUpgrades().stream()
                .filter(upgrade -> "mighty_force".equals(upgrade.id()))
                .findFirst()
                .orElseThrow();

        assertEquals("common", mightyForce.tier());
        assertTrue(mightyForce.isPermanent());
        assertFalse(mightyForce.isUnique());
        assertEquals("attribute", mightyForce.actions().getFirst().type());
        assertEquals("minecraft:generic.attack_damage", mightyForce.actions().getFirst().value().getFirst());
    }
}
