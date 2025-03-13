package snowball049.roguelikemc.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snowball049.roguelikemc.accessor.PlayerEntityAccessor;
import snowball049.roguelikemc.config.RoguelikeMCConfig;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method="copyFrom", at=@At("TAIL"))
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        PlayerEntityAccessor player = (PlayerEntityAccessor) oldPlayer;
        List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> permanentUpgrades = player.getPermanentUpgrades();
        PlayerEntityAccessor newPlayer = (PlayerEntityAccessor) this;
        newPlayer.setPermanentUpgrades(permanentUpgrades);
        permanentUpgrades.forEach(upgrade -> {
            RoguelikeMCUpgradeUtil.applyUpgrade((ServerPlayerEntity) (Object) this, upgrade);
        });

        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, new RefreshCurrentUpgradeS2CPayload(newPlayer.getTemporaryUpgrades()));
    }
}
