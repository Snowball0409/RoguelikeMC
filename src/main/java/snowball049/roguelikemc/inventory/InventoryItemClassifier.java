package snowball049.roguelikemc.inventory;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;

public final class InventoryItemClassifier {
    private InventoryItemClassifier() {
    }

    public static boolean isArmorOrWeapon(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }

        Item item = itemStack.getItem();
        return item instanceof ArmorItem
                || item instanceof RangedWeaponItem
                || item instanceof TridentItem
                || item instanceof ShieldItem
                || item instanceof ToolItem;
    }
}
