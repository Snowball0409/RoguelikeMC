package snowball049.roguelikemc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCAttribute;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.util.RoguelikeMCDeathUtil;
import snowball049.roguelikemc.util.RoguelikeMCPointUtil;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

import java.util.ArrayList;
import java.util.List;

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
                    RoguelikeMCPointUtil.addUpgradePoints(serverPlayer, 1);
                }
            }
        }
    }

    @Inject(method="dropInventory", at=@At("HEAD"), cancellable=true)
    private void onPlayerDeath(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player.isSpectator()) return;
        if (player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) return;

        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        List<ItemStack> armors = new ArrayList<>();
        if (playerData.keepEquipmentAfterDeath) {
            armors.addAll(player.getInventory().armor.stream().toList());
        }

        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (RoguelikeMCCommonConfig.INSTANCE.enableClearInventoryAfterDeath) {
                serverPlayer.getInventory().clear();
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

        if(!armors.isEmpty() && playerData.keepEquipmentAfterDeath){
            for (int i = 0;i < armors.size();i++){
                player.getInventory().armor.set(i,armors.get(i));
            }
            ci.cancel();
        }
    }

    @Inject(method="damage", at=@At("HEAD"), cancellable=true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (!(player instanceof ServerPlayerEntity)) return;
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);

        // One last chance
        float health = player.getHealth();
        if (amount < health) return;

        if (playerData.revive){
            playerData.revive = false;
            cir.setReturnValue(false);
            // Undying Totem Effect
            World world = player.getWorld();
            player.setHealth(1);
            player.clearStatusEffects();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1
            );
        }
    }

    @ModifyArg(method="damage", at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    private float onDamage(float amount) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!(player instanceof ServerPlayerEntity)) return amount;

        // Damage Ratio Attribute
        double damageRatio = player.getAttributeValue(RoguelikeMCAttribute.DAMAGE_RATIO);
        return (float) (amount * (1.0D + damageRatio));
    }

    @ModifyVariable(
            method = "attack",
            at = @At("STORE"),
            ordinal = 2
    )
    private boolean modifyCriticalFlag(boolean original) {
        if (original) return true;

        PlayerEntity player = (PlayerEntity)(Object) this;
        double critChance = player.getAttributeValue(RoguelikeMCAttribute.CRITICAL_CHANCE);
        return player.getRandom().nextFloat() < critChance;
    }



    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.5F, ordinal = 0))
    private float modifyCritDamage(float critDamage) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!(player instanceof ServerPlayerEntity)) return critDamage;

        // Critical Damage Attribute
        double criticalDamage = player.getAttributeValue(RoguelikeMCAttribute.CRITICAL_DAMAGE);
        return (float) (1.0D + criticalDamage);
    }
}
