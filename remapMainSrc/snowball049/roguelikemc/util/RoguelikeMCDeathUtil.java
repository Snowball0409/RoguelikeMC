package snowball049.roguelikemc.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;

import java.util.Random;

public class RoguelikeMCDeathUtil {

    public static ItemStack getDecayedItemStack() {
        int min = RoguelikeMCCommonConfig.INSTANCE.decayItemAmountMinMax.get(0);
        int max = RoguelikeMCCommonConfig.INSTANCE.decayItemAmountMinMax.get(1);
        ItemStack decayedItemStack = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(RoguelikeMCCommonConfig.INSTANCE.decayItem))
                .getDefaultInstance();
        decayedItemStack.setCount(new Random().nextInt(max-min+1)+min);
        return decayedItemStack;
    }

    public static void decayArmorAndWeapons(ServerPlayer serverPlayer) {
        Inventory inventory = serverPlayer.getInventory();

        for (int i = 0; i < inventory.armor.size(); i++) {
            ItemStack itemStack = inventory.armor.get(i);
            if (!itemStack.isEmpty()) {
                double rand = Math.random();
                if(rand < RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage) {
                    inventory.armor.set(i, getDecayedItemStack());
                }
            }
        }

        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack itemStack = inventory.items.get(i);
            if (isArmorOrWeapon(itemStack)) {
                double rand = Math.random();
                if(rand < RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage) {
                    inventory.items.set(i, getDecayedItemStack());
                }
            }
        }
    }

    public static void decayInventory(ServerPlayer serverPlayer) {
        Inventory inventory = serverPlayer.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack itemStack = inventory.items.get(i);
            if (!itemStack.isEmpty()) {
                double rand = Math.random();
                if(rand < RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage) {
                    ItemStack decayedItemStack = getDecayedItemStack();
                    decayedItemStack.setCount(Math.min(itemStack.getCount(), decayedItemStack.getCount()));
                    inventory.items.set(i, decayedItemStack);
                }
            }
        }
    }

    public static void clearArmorAndWeapons(ServerPlayer player) {
        Inventory inventory = player.getInventory();

        inventory.armor.clear();

        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack itemStack = inventory.items.get(i);
            if (isArmorOrWeapon(itemStack)) {
                inventory.items.set(i, ItemStack.EMPTY);
            }
        }

        if (isArmorOrWeapon(inventory.offhand.getFirst())) {
            inventory.offhand.set(0, ItemStack.EMPTY);
        }
    }

    public static boolean isArmorOrWeapon(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }

        Item item = itemStack.getItem();
        return item instanceof ArmorItem || item instanceof ProjectileWeaponItem || item instanceof TridentItem || item instanceof ShieldItem || item instanceof TieredItem;
    }
}
