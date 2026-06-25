package snowball049.roguelikemc.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.apply.UpgradeApplier;
import snowball049.roguelikemc.upgrade.roll.UpgradeRollService;
import snowball049.roguelikemc.upgrade.tick.UpgradeTickService;

import java.util.List;

/**
 * @deprecated Use {@link UpgradeApplier}, {@link UpgradeRollService}, and {@link UpgradeTickService} directly.
 */
@Deprecated
public final class RoguelikeMCUpgradeUtil {
    private RoguelikeMCUpgradeUtil() {
    }

    public static void addUpgrade(RoguelikeMCUpgradeData upgrade, ServerPlayerEntity player) {
        UpgradeApplier.addUpgrade(upgrade, player);
    }

    public static void applyUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade) {
        UpgradeApplier.applyUpgrade(player, upgrade);
    }

    public static void applyJoinUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade) {
        UpgradeApplier.applyJoinUpgrade(player, upgrade);
    }

    public static List<RoguelikeMCUpgradeData> getRandomUpgrades(RoguelikeMCPlayerData playerData) {
        return UpgradeRollService.rollOptions(playerData);
    }

    public static void removeUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade, RoguelikeMCUpgradeData.ActionData upgradeAction) {
        UpgradeApplier.removeUpgrade(player, upgrade, upgradeAction);
    }

    public static void tickInfiniteEffects(MinecraftServer minecraftServer) {
        UpgradeTickService.tickInfiniteEffects(minecraftServer);
    }

    public static void tickRecurringEvents(MinecraftServer minecraftServer) {
        UpgradeTickService.tickEvents(minecraftServer, 20);
    }

    public static void tickSetEquipment(MinecraftServer minecraftServer) {
        UpgradeTickService.tickEvents(minecraftServer, 20);
    }

    public static void tickEffectToMobEntity(MinecraftServer minecraftServer) {
        UpgradeTickService.tickEvents(minecraftServer, 20);
    }

    public static void tickEnableCreativeFly(MinecraftServer minecraftServer) {
        UpgradeTickService.tickEvents(minecraftServer, 20);
    }

    public static void tickProvokedEvents(MinecraftServer minecraftServer) {
        UpgradeTickService.tickEvents(minecraftServer, 100);
    }
}
