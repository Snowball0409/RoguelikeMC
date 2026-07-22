package snowball049.roguelikemc.inventory;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import snowball049.roguelikemc.testutil.TestFixtures;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InventoryItemClassifierTest {
    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    static Stream<String> armorOrWeaponCases() {
        return TestFixtures.cases(TestFixtures.ITEMS, "armorOrWeapon")
                .map(testCase -> testCase.get("name").getAsString());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("armorOrWeaponCases")
    void classifiesArmorOrWeaponFromFixture(String caseName) {
        var testCase = TestFixtures.cases(TestFixtures.ITEMS, "armorOrWeapon")
                .filter(entry -> caseName.equals(entry.get("name").getAsString()))
                .findFirst()
                .orElseThrow();

        ItemStack stack = new ItemStack(
                net.minecraft.registry.Registries.ITEM.get(new net.minecraft.util.Identifier(testCase.get("itemId").getAsString()))
        );
        assertEquals(testCase.get("isArmorOrWeapon").getAsBoolean(), InventoryItemClassifier.isArmorOrWeapon(stack), caseName);
    }

    @org.junit.jupiter.api.Test
    void emptyStackIsNotArmorOrWeapon() {
        assertFalse(InventoryItemClassifier.isArmorOrWeapon(ItemStack.EMPTY));
    }
}
