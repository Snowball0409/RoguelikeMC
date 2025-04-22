package snowball049.roguelikemc.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.data.RoguelikeMCAttribute;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {
    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void modifyXpPickup(PlayerEntity player, CallbackInfo ci) {
        if (player.getWorld().isClient) return;

        ExperienceOrbEntity orb = (ExperienceOrbEntity)(Object)this;
        int base = orb.getExperienceAmount();

        double gain = player.getAttributeValue(RoguelikeMCAttribute.EXPERIENCE_GAIN);
        int modified = (int)(base * (1.0D + gain));

        player.addExperience(modified);
        orb.discard();
        ci.cancel();
    }
}
