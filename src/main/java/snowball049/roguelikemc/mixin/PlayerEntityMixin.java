package snowball049.roguelikemc.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

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
}
