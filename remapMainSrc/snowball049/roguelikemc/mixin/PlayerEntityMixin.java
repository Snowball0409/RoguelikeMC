package snowball049.roguelikemc.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.util.RoguelikeMCDeathUtil;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

// MixinPlayerEntity.java
@Mixin(Player.class)
public abstract class PlayerEntityMixin {
    @Inject(method="addExperienceLevels", at=@At("TAIL"))
    private void addExperienceLevel(int level, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem && RoguelikeMCCommonConfig.INSTANCE.enableLevelUpgrade) {
            if (player instanceof ServerPlayer serverPlayer) {
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
        Player player = (Player) (Object) this;

        if (player.isSpectator()) return;
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

        if (player instanceof ServerPlayer serverPlayer) {
            if (RoguelikeMCCommonConfig.INSTANCE.enableClearInventoryAfterDeath) {
                serverPlayer.getInventory().clearContent();
                ci.cancel();
                return;
            }else if (RoguelikeMCCommonConfig.INSTANCE.enableDecayInventoryAfterDeath) {
                RoguelikeMCDeathUtil.decayInventory(serverPlayer);
            }

            if (RoguelikeMCCommonConfig.INSTANCE.enableClearEquipmentAfterDeath) {
                RoguelikeMCDeathUtil.clearArmorAndWeapons(serverPlayer);
            }
            else if (RoguelikeMCCommonConfig.INSTANCE.enableDecayEquipmentAfterDeath) {
                RoguelikeMCDeathUtil.decayArmorAndWeapons(serverPlayer);
            }
        }
    }


}
