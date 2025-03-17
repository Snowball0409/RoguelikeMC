package snowball049.roguelikemc.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

import javax.tools.Tool;
import java.util.Random;

// MixinPlayerEntity.java
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method="addExperienceLevels", at=@At("TAIL"))
    private void addExperienceLevel(int level, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem && RoguelikeMCCommonConfig.INSTANCE.enableLevelUpgrade) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(serverPlayer);
                playerData.currentLevelGain += level;
                if(playerData.currentLevelGain >= RoguelikeMCCommonConfig.INSTANCE.amountOfLevelUpgrade) {
                    playerData.currentLevelGain -= RoguelikeMCCommonConfig.INSTANCE.amountOfLevelUpgrade;
                    playerData.upgradePoints++;
                    RoguelikeMCUpgradeUtil.sendPointMessage(serverPlayer, 1);
                }
            }
        }
    }

    @Inject(method="dropInventory", at=@At("HEAD"), cancellable=true)
    private void onPlayerDeath(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (RoguelikeMCCommonConfig.INSTANCE.enableClearInventoryAfterDeath) {
                serverPlayer.getInventory().clear();
                ci.cancel();
                return;
            }else if (RoguelikeMCCommonConfig.INSTANCE.enableDecayInventoryAfterDeath) {
                decayInventory(serverPlayer);
            }

            if (RoguelikeMCCommonConfig.INSTANCE.enableClearEquipmentAfterDeath) {
                clearArmorAndWeapons(serverPlayer);
            }
            else if (RoguelikeMCCommonConfig.INSTANCE.enableDecayEquipmentAfterDeath) {
                decayArmorAndWeapons(serverPlayer);
            }
        }
    }

    @Unique
    private ItemStack getDecayedItemStack() {
        int min = RoguelikeMCCommonConfig.INSTANCE.decayItemAmountMinMax.get(0);
        int max = RoguelikeMCCommonConfig.INSTANCE.decayItemAmountMinMax.get(1);
        ItemStack decayedItemStack = Registries.ITEM.get(Identifier.tryParse(RoguelikeMCCommonConfig.INSTANCE.decayItem))
                .getDefaultStack();
        decayedItemStack.setCount(new Random().nextInt(max-min+1)+min);
        return decayedItemStack;
    }

    @Unique
    private void decayArmorAndWeapons(ServerPlayerEntity serverPlayer) {
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
    }

    @Unique
    private void decayInventory(ServerPlayerEntity serverPlayer) {
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

    @Unique
    private void clearArmorAndWeapons(ServerPlayerEntity player) {
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
    }

    @Unique
    private boolean isArmorOrWeapon(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }

        Item item = itemStack.getItem();
        return item instanceof ArmorItem || item instanceof RangedWeaponItem || item instanceof TridentItem || item instanceof ShieldItem || item instanceof ToolItem;
    }
}
