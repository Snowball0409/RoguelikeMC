package snowball049.roguelikemc.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.util.RoguelikeMCPointUtil;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At("TAIL"))
    private void onAdvancementGrant(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = this.owner;
        AdvancementProgress progress = ((PlayerAdvancementTracker) (Object) this).getProgress(advancement);

        if (player != null
                && RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem
                && RoguelikeMCCommonConfig.INSTANCE.enableAdvancementUpgrade
                && !player.getWorld().isClient()
                && advancement.value().display().isPresent()
                && !advancement.value().isRoot()
                && progress.isDone()
                ) {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.currentAdvancementGain ++;
            if (playerData.currentAdvancementGain >= RoguelikeMCCommonConfig.INSTANCE.amountOfAdvancementUpgrade) {
                RoguelikeMCPointUtil.addUpgradePoints(player, 1);
                playerData.currentAdvancementGain -= RoguelikeMCCommonConfig.INSTANCE.amountOfAdvancementUpgrade;
            }

        }
    }
}
