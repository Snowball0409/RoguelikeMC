package snowball049.roguelikemc.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.network.packet.SendUpgradePointsS2CPayload;

public class RoguelikeMCPointUtil {
    public static void addUpgradePoints(ServerPlayerEntity player, int point){
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        playerData.upgradePoints += point;

        if (playerData.upgradePoints < 0) {
            playerData.upgradePoints = 0;
        }
        player.sendMessage(Text.literal("You have been granted " + point + " upgrade points!"), false);
        ServerPlayNetworking.send(player, new SendUpgradePointsS2CPayload(playerData.upgradePoints));
    }
    public static boolean removeUpgradePoints(ServerPlayerEntity player, int point){
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);

        if (playerData.upgradePoints <= 0) {
            player.sendMessage(Text.literal("You don't have enough upgrade points!"), false);
            return false;
        }
        playerData.upgradePoints = Math.max(playerData.upgradePoints - point, 0);

        ServerPlayNetworking.send(player, new SendUpgradePointsS2CPayload(playerData.upgradePoints));
        return true;
    }
    public static void getUpgradePoints(ServerPlayerEntity player, int point){
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        ServerPlayNetworking.send(player, new SendUpgradePointsS2CPayload(playerData.upgradePoints));
    }
}
