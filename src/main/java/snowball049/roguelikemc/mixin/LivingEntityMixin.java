package snowball049.roguelikemc.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.network.packet.RefreshCurrentBossStageS2CPayload;
import snowball049.roguelikemc.upgrade.gameplay.UpgradeEventGameplayService;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onBossKilled(DamageSource source, CallbackInfo ci) {
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;
        if (!RoguelikeMCCommonConfig.INSTANCE.enableLinearGameStage) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        int stageIndex = RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.indexOf(Registries.ENTITY_TYPE.getId(entity.getType()).toString());
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);

        if (stageIndex >= 0 && playerData.currentGameStage < stageIndex + 1) {
            playerData.currentGameStage = stageIndex + 1;
            if (playerData.currentGameStage >= RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.size()) {
                ServerPlayNetworking.send(player, new RefreshCurrentBossStageS2CPayload("none"));
            }else{
                player.sendMessage(Text.translatable("message.roguelikemc.pass_game_stage").append(entity.getType().getName()), false);
                ServerPlayNetworking.send(player, new RefreshCurrentBossStageS2CPayload(RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.get(playerData.currentGameStage)));
            }
        }
    }


    @Inject(method = "modifyAppliedDamage", at = @At("HEAD"), cancellable = true)
    private void reduceBossDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;
        if (!RoguelikeMCCommonConfig.INSTANCE.enableLinearGameStage) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        int bossStage = RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.indexOf(Registries.ENTITY_TYPE.getId(entity.getType()).toString());

        if (bossStage > 0) {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            int playerStage = playerData.currentGameStage;

            if (playerStage < bossStage) {
                double reduction = RoguelikeMCCommonConfig.INSTANCE.gameStageDecayPercentage;
                float reducedDamage = (float) (amount * (1 - reduction));
                cir.setReturnValue(reducedDamage);
                player.sendMessage(Text.translatable("message.roguelikemc.damage_reduce", String.valueOf(reduction * 100)).formatted(Formatting.RED), true);
            }
        }
    }

    @Inject(method = "dropLoot", at = @At("TAIL"))
    private void roguelike$onDropLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        if (!causedByPlayer) return;
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;

        UpgradeEventGameplayService.onEntityKilledByPlayer(player, (LivingEntity) (Object) this, source);
    }
}
