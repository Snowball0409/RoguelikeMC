package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.command.RoguelikeMCCommands;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

public class SelectUpgradeOptionHandler {
    public static void handle(SelectUpgradeOptionC2SPayload packet, ServerPlayNetworking.Context context) {
        if(context.player().getWorld().isClient()) {
            return;
        }

        RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade selected = packet.option();
        ServerPlayerEntity player = context.player();
        RoguelikeMCUpgradeUtil.handleUpgrade(selected, player);
    }
}
