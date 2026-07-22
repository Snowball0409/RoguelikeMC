package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.roll.UpgradeRollService;
import snowball049.roguelikemc.upgrade.point.UpgradePointService;

import java.util.List;

public final class RefreshUpgradeOptionHandler {
    private RefreshUpgradeOptionHandler() {
    }

    @SuppressWarnings("java:S1172")
    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        RefreshUpgradeOptionC2SPayload.read(buf);
        server.execute(() -> handleOnServerThread(player));
    }

    private static void handleOnServerThread(ServerPlayerEntity player) {
        // Shuffle Upgrade Options and Send to Client
        if (player.getWorld().isClient()) return;
        if (!RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem) return;
        if (RoguelikeMCUpgradeManager.getUpgrades().size() < 3) {
            player.sendMessage(Text.translatable("message.roguelikemc.warn_no_upgrade"));
            return;
        }

        boolean isRemove = UpgradePointService.removeUpgradePoints(player, 1);
        if (!isRemove) return;

        List<RoguelikeMCUpgradeData> currentUpgrades = UpgradeRollService
                .rollOptions(RoguelikeMCStateSaverAndLoader.getPlayerState(player));
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        for (RoguelikeMCUpgradeData upgrade : currentUpgrades) {
            playerData.currentOptionIds.add(RoguelikeMCUpgradeManager.idFor(upgrade));
            PacketByteBuf out = PacketByteBufs.create();
            new UpgradeOptionS2CPayload(upgrade).write(out);
            ServerPlayNetworking.send(player, UpgradeOptionS2CPayload.ID, out);
        }
    }
}
