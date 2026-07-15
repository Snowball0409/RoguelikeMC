package snowball049.roguelikemc.mixin;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snowball049.roguelikemc.upgrade.runtime.UpgradeTriggerRuntimeService;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin {
    @Inject(method = "trade", at = @At("TAIL"))
    private void roguelike$onTrade(TradeOffer offer, CallbackInfo ci) {
        MerchantEntity self = (MerchantEntity) (Object) this;
        if (!(self instanceof VillagerEntity) && !(self instanceof WanderingTraderEntity)) {
            return;
        }
        PlayerEntity customer = self.getCustomer();
        if (!(customer instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        UpgradeTriggerRuntimeService.onMerchantTrade(serverPlayer, self);
    }
}
