package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.apply.UpgradeApplier;

public class SelectUpgradeOptionHandler {
    public static void handle(SelectUpgradeOptionC2SPayload packet, ServerPlayNetworking.Context context) {
        if (context.player().getWorld().isClient()) {
            return;
        }

        ServerPlayerEntity player = context.player();
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        int optionIndex = packet.optionIndex();
        if (optionIndex < 0 || optionIndex >= playerData.currentOptionIds.size()) {
            return;
        }

        Identifier selectedId = playerData.currentOptionIds.get(optionIndex);
        RoguelikeMCUpgradeData selected = RoguelikeMCUpgradeManager.getUpgrade(selectedId);
        if (selected == null) {
            return;
        }

        playerData.currentOptionIds.clear();
        UpgradeApplier.addUpgrade(selectedId, player);
    }
}
