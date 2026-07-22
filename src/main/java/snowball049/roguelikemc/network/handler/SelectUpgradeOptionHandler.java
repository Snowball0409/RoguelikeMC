package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.apply.UpgradeApplier;

public final class SelectUpgradeOptionHandler {
    private SelectUpgradeOptionHandler() {
    }

    @SuppressWarnings("java:S1172")
    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        SelectUpgradeOptionC2SPayload packet = SelectUpgradeOptionC2SPayload.read(buf);
        server.execute(() -> handleOnServerThread(player, packet));
    }

    private static void handleOnServerThread(ServerPlayerEntity player, SelectUpgradeOptionC2SPayload packet) {
        if (player.getWorld().isClient()) {
            return;
        }

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
