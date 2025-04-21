package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

public class SelectUpgradeOptionHandler {
    public static void handle(SelectUpgradeOptionC2SPayload packet, ServerPlayNetworking.Context context) {
        if(context.player().getWorld().isClient()) {
            return;
        }

        RoguelikeMCUpgradeData selected = packet.option();
        ServerPlayerEntity player = context.player();
        RoguelikeMCStateSaverAndLoader.getPlayerState(player).currentOptions.clear();
        RoguelikeMCUpgradeUtil.handleUpgrade(selected, player);
    }
}
