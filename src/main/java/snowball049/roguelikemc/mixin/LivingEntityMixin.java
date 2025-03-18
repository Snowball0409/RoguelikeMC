package snowball049.roguelikemc.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onBossKilled(DamageSource source, CallbackInfo ci) {
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        int stageIndex = RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.indexOf(Registries.ENTITY_TYPE.getId(entity.getType()).toString());

        if (stageIndex >= 0) {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.currentGameStage = Math.max(playerData.currentGameStage, stageIndex + 1);
            player.sendMessage(Text.literal("§aYou have complete the stage " + stageIndex), false);
        }
    }


    @Inject(method = "modifyAppliedDamage", at = @At("HEAD"), cancellable = true)
    private void reduceBossDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        int bossStage = RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.indexOf(Registries.ENTITY_TYPE.getId(entity.getType()).toString());

        if (bossStage > 0) {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            int playerStage = playerData.currentGameStage;

            if (playerStage < bossStage) {
                double reduction = RoguelikeMCCommonConfig.INSTANCE.gameStageDecayPercentage;
                float reducedDamage = (float) (amount * (1 - reduction));
                cir.setReturnValue(reducedDamage);
                player.sendMessage(Text.literal("§cYour damage is reduced by " + (reduction * 100) + "%.§cYou haven't defeated the previous boss!"), true);

            }
        }
    }
}
