package snowball049.roguelikemc.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method="copyFrom", at=@At("TAIL"))
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
//        PlayerEntityAccessor player = (PlayerEntityAccessor) oldPlayer;
//        List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> permanentUpgrades = player.getPermanentUpgrades();
//        PlayerEntityAccessor newPlayer = (PlayerEntityAccessor) this;
//        newPlayer.setPermanentUpgrades(permanentUpgrades);
//        permanentUpgrades.forEach(upgrade -> {
//            RoguelikeMCUpgradeUtil.applyUpgrade((ServerPlayerEntity) (Object) this, upgrade);
//        });
//
//        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, new RefreshCurrentUpgradeS2CPayload(newPlayer.getTemporaryUpgrades()));
//        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(oldPlayer);
//        playerData.temporaryUpgrades.clear();
//        playerData.permanentUpgrades.forEach(upgrade -> {
//            RoguelikeMCUpgradeUtil.applyUpgrade((ServerPlayerEntity) (Object) this, upgrade);
//        });
//        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, new RefreshCurrentUpgradeS2CPayload(playerData.permanentUpgrades));
//        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, new RefreshCurrentUpgradeS2CPayload(playerData.temporaryUpgrades));
    }
}
