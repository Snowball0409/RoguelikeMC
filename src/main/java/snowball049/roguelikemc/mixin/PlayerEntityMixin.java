package snowball049.roguelikemc.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// MixinPlayerEntity.java
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Unique
    private NbtCompound roguelikeMCData = new NbtCompound();

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    protected void writeCustomData(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("roguelikemc_upgrade", roguelikeMCData);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    protected void readCustomData(NbtCompound nbt, CallbackInfo ci) {
        roguelikeMCData = nbt.getCompound("roguelikemc_upgrade");
    }

    @Unique
    public NbtCompound getRoguelikeData() {
        return this.roguelikeMCData;
    }
}
