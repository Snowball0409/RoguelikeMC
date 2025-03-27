package snowball049.roguelikemc.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayer owner;

    @Inject(method = "grantCriterion", at = @At("TAIL"))
    private void onAdvancementGrant(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = this.owner;
        AdvancementProgress progress = ((PlayerAdvancements) (Object) this).getOrStartProgress(advancement);

        if (player != null
                && RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem
                && RoguelikeMCCommonConfig.INSTANCE.enableAdvancementUpgrade
                && !player.level().isClientSide()
                && advancement.value().display().isPresent()
                && !advancement.value().isRoot()
                && progress.isDone()
                ) {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.upgradePoints++;
            RoguelikeMCUpgradeUtil.sendPointMessage(player, 1);
        }
    }
}
