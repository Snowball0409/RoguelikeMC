package snowball049.roguelikemc.gameplay.death;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.compat.RoguelikeMCCompat;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.inventory.InventoryItemClassifier;

import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public final class DeathInventoryService {
    private static final Random RANDOM = new Random();
    private static DoubleSupplier randomSupplier = RANDOM::nextDouble;
    private static Supplier<ItemStack> decayedItemSupplier = DeathInventoryService::createConfiguredDecayStack;

    private DeathInventoryService() {
    }

    static void setRandomSupplierForTesting(DoubleSupplier supplier) {
        randomSupplier = supplier;
    }

    static void setDecayedItemSupplierForTesting(Supplier<ItemStack> supplier) {
        decayedItemSupplier = supplier;
    }

    static void resetSuppliersForTesting() {
        randomSupplier = RANDOM::nextDouble;
        decayedItemSupplier = DeathInventoryService::createConfiguredDecayStack;
    }

    public static ItemStack createConfiguredDecayStack() {
        int min = RoguelikeMCCommonConfig.INSTANCE.decayItemAmountMinMax.get(0);
        int max = RoguelikeMCCommonConfig.INSTANCE.decayItemAmountMinMax.get(1);
        return DecayItemStacks.create(
                Registries.ITEM.get(Identifier.tryParse(RoguelikeMCCommonConfig.INSTANCE.decayItem)),
                min,
                max,
                RANDOM
        );
    }

    public static void decayArmorAndWeapons(ServerPlayerEntity serverPlayer) {
        PlayerInventory inventory = serverPlayer.getInventory();
        double decayChance = RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage;

        for (int i = 0; i < inventory.armor.size(); i++) {
            ItemStack itemStack = inventory.armor.get(i);
            if (!itemStack.isEmpty() && DecayItemStacks.shouldDecay(decayChance, randomSupplier.getAsDouble())) {
                inventory.armor.set(i, decayedItemSupplier.get().copy());
            }
        }

        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack itemStack = inventory.main.get(i);
            if (InventoryItemClassifier.isArmorOrWeapon(itemStack)
                    && DecayItemStacks.shouldDecay(decayChance, randomSupplier.getAsDouble())) {
                inventory.main.set(i, decayedItemSupplier.get().copy());
            }
        }

        if (RoguelikeMCCompat.isTrinketsLoaded) {
            TrinketsApi.getTrinketComponent(serverPlayer).ifPresent(trinketComponent -> trinketComponent.getAllEquipped().forEach((slot) -> {
                if (DecayItemStacks.shouldDecay(decayChance, randomSupplier.getAsDouble())) {
                    slot.getLeft().inventory().setStack(slot.getLeft().index(), decayedItemSupplier.get().copy());
                }
            }));
        }
    }

    public static void decayInventory(ServerPlayerEntity serverPlayer) {
        PlayerInventory inventory = serverPlayer.getInventory();
        double decayChance = RoguelikeMCCommonConfig.INSTANCE.decayInventoryPercentage;

        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack itemStack = inventory.main.get(i);
            if (!itemStack.isEmpty() && DecayItemStacks.shouldDecay(decayChance, randomSupplier.getAsDouble())) {
                ItemStack decayedItemStack = DecayItemStacks.capCountToSource(itemStack, decayedItemSupplier.get().copy());
                inventory.main.set(i, decayedItemStack);
            }
        }
    }

    public static void clearArmorAndWeapons(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();

        inventory.armor.clear();

        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack itemStack = inventory.main.get(i);
            if (InventoryItemClassifier.isArmorOrWeapon(itemStack)) {
                inventory.main.set(i, ItemStack.EMPTY);
            }
        }

        if (InventoryItemClassifier.isArmorOrWeapon(inventory.offHand.getFirst())) {
            inventory.offHand.set(0, ItemStack.EMPTY);
        }

        if (RoguelikeMCCompat.isTrinketsLoaded) {
            TrinketsApi.getTrinketComponent(player).ifPresent(trinketComponent ->
                    trinketComponent.getAllEquipped().forEach((slot) ->
                            slot.getLeft().inventory().setStack(slot.getLeft().index(), ItemStack.EMPTY)));
        }
    }
}
