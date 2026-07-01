package snowball049.roguelikemc.upgrade.point;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.network.packet.SendUpgradePointsS2CPayload;

public final class UpgradePointService {
    private UpgradePointService() {
    }

    public static int applyAdd(int currentPoints, int delta) {
        return clampNonNegative(currentPoints + delta);
    }

    public static boolean canSpend(int currentPoints) {
        return currentPoints > 0;
    }

    public static int applySpend(int currentPoints, int cost) {
        return Math.max(currentPoints - cost, 0);
    }

    public static int clampNonNegative(int points) {
        return Math.max(points, 0);
    }

    public static KillProgressResult applyKillProgress(int currentKills, int killsToAdd, int requirement) {
        int remaining = currentKills + killsToAdd;
        int pointsEarned = 0;
        while (remaining >= requirement) {
            remaining -= requirement;
            pointsEarned++;
        }
        return new KillProgressResult(remaining, pointsEarned);
    }

    public static void addUpgradePoints(ServerPlayerEntity player, int point) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        playerData.upgradePoints = applyAdd(playerData.upgradePoints, point);
        player.sendMessage(Text.translatable("message.roguelikemc.grant_upgrade_point", point), false);
        syncPoints(player, playerData.upgradePoints);
    }

    public static boolean removeUpgradePoints(ServerPlayerEntity player, int point) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);

        if (!canSpend(playerData.upgradePoints)) {
            player.sendMessage(Text.translatable("message.roguelikemc.not_enough_upgrade_point"), false);
            return false;
        }

        playerData.upgradePoints = applySpend(playerData.upgradePoints, point);
        syncPoints(player, playerData.upgradePoints);
        return true;
    }

    public static void getUpgradePoints(ServerCommandSource source, ServerPlayerEntity player) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        source.sendMessage(Text.literal(player.getName().getString() + " have " + playerData.upgradePoints + " upgrade points!"));
        syncPoints(player, playerData.upgradePoints);
    }

    public static void setUpgradePoints(ServerPlayerEntity player, int amount) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        playerData.upgradePoints = clampNonNegative(amount);
        syncPoints(player, playerData.upgradePoints);
        player.sendMessage(Text.literal("You have been set to " + amount + " upgrade points!"), false);
    }

    private static void syncPoints(ServerPlayerEntity player, int points) {
        ServerPlayNetworking.send(player, new SendUpgradePointsS2CPayload(points));
    }

    public record KillProgressResult(int remainingKills, int pointsEarned) {
    }
}
