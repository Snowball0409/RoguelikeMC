package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.accessor.PlayerEntityAccessor;
import snowball049.roguelikemc.config.RoguelikeMCConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

public class SelectUpgradeOptionHandler {
    public static void handle(SelectUpgradeOptionC2SPayload packet, ServerPlayNetworking.Context context) {
        if(context.player().getWorld().isClient()) {
            return;
        }

        RoguelikeMCConfig.RogueLikeMCUpgradeConfig selected = packet.option();
        ServerPlayerEntity player = context.player();
        RoguelikeMCPlayerData serverState = RoguelikeMCStateSaverAndLoader.getPlayerState(player);

        if (selected.is_permanent()) {
            serverState.permanentUpgrades.add(selected);
        }else{
            serverState.temporaryUpgrades.add(selected);
        }
        RoguelikeMCUpgradeUtil.applyUpgrade(player, selected);
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(serverState.permanentUpgrades));
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(serverState.temporaryUpgrades));
    }
}
