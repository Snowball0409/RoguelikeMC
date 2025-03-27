package snowball049.roguelikemc.util;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.Accessory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.compat.RoguelikeMCCompat;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;

import java.util.Random;

public class RoguelikeMCDeathUtil {

    public static ItemStack getDecayedItemStack() {
        int min = RoguelikeMCCommonConfig.INSTANCE.decayItemAmountMinMax.get(0);
        int max = RoguelikeMCCommonConfig.INSTANCE.decayItemAmountMinMax.get(1);
        ItemStack decayedItemStack = Registries.ITEM.get(Identifier.tryParse(RoguelikeMCCommonConfig.INSTANCE.decayItem))
                .getDefaultStack();
        decayedItemStack.setCount(new Random().nextInt(max-min+1)+min);
        return decayedItemStack;
    }

    public static void decayArmorAndWeapons(ServerPlayerEntity serverPlayer) {
        PlayerInventory inventory = serverPlayer.getInventory();

        for (int i = 0; i < inventory.armor.size(); i++) {
            ItemStack itemStack = inventory.armor.get(i);
            if (!itemStack.isEmpty()) {
                double rand = Math.random();
                if(rand < RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage) {
                    inventory.armor.set(i, getDecayedItemStack());
                }
            }
        }

        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack itemStack = inventory.main.get(i);
            if (isArmorOrWeapon(itemStack)) {
                double rand = Math.random();
                if(rand < RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage) {
                    inventory.main.set(i, getDecayedItemStack());
                }
            }
        }

        // accessories compatibility
        if(RoguelikeMCCompat.isAccessoriesLoaded) {
            if(serverPlayer instanceof AccessoriesCapability accessoriedPlayer) {
                accessoriedPlayer.getAllEquipped().forEach((slot) -> {
                    double rand = Math.random();
                    if(rand < RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage) {
                        slot.reference().setStack(getDecayedItemStack());
                    }
                });
            }
        }

        // trinkets compatibility
        if(RoguelikeMCCompat.isTrinketsLoaded) {
            TrinketsApi.getTrinketComponent(serverPlayer).ifPresent(trinketComponent -> trinketComponent.getAllEquipped().forEach((slot) -> {
                double rand = Math.random();
                if (rand < RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage) {
                    slot.getLeft().inventory().setStack(slot.getLeft().index(), getDecayedItemStack());
                }
            }));

        }
    }

    public static void decayInventory(ServerPlayerEntity serverPlayer) {
        PlayerInventory inventory = serverPlayer.getInventory();

        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack itemStack = inventory.main.get(i);
            if (!itemStack.isEmpty()) {
                double rand = Math.random();
                if(rand < RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage) {
                    ItemStack decayedItemStack = getDecayedItemStack();
                    decayedItemStack.setCount(Math.min(itemStack.getCount(), decayedItemStack.getCount()));
                    inventory.main.set(i, decayedItemStack);
                }
            }
        }
    }

    public static void clearArmorAndWeapons(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();

        inventory.armor.clear();

        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack itemStack = inventory.main.get(i);
            if (isArmorOrWeapon(itemStack)) {
                inventory.main.set(i, ItemStack.EMPTY);
            }
        }

        if (isArmorOrWeapon(inventory.offHand.getFirst())) {
            inventory.offHand.set(0, ItemStack.EMPTY);
        }

        // accessories compatibility
        if(RoguelikeMCCompat.isAccessoriesLoaded) {
            if(player instanceof AccessoriesCapability accessoriedPlayer) {
                accessoriedPlayer.getAllEquipped().forEach((slot) -> {
                    slot.reference().setStack(ItemStack.EMPTY);
                });
            }
        }

        // trinkets compatibility
        if(RoguelikeMCCompat.isTrinketsLoaded) {
            TrinketsApi.getTrinketComponent(player).ifPresent(trinketComponent -> trinketComponent.getAllEquipped().forEach((slot) -> {
                slot.getLeft().inventory().setStack(slot.getLeft().index(), ItemStack.EMPTY);
            }));
        }
    }

    public static boolean isArmorOrWeapon(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }

        Item item = itemStack.getItem();
        return item instanceof ArmorItem ||
                item instanceof RangedWeaponItem ||
                item instanceof TridentItem ||
                item instanceof ShieldItem ||
                item instanceof ToolItem ||
                (RoguelikeMCCompat.isAccessoriesLoaded && item instanceof Accessory);
    }
}
