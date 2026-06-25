package snowball049.roguelikemc.upgrade.apply;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.enums.UpgradePersistence;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;
import snowball049.roguelikemc.upgrade.action.UpgradeActionHandlers;

public final class UpgradeApplier {
    private UpgradeApplier() {
    }

    public static void addUpgrade(Identifier upgradeId, ServerPlayerEntity player) {
        RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeManager.getUpgrade(upgradeId);
        if (upgrade == null) {
            RoguelikeMC.LOGGER.warn("Attempted to add unknown upgrade: {}", upgradeId);
            return;
        }

        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        if (upgrade.persistence() == UpgradePersistence.PERMANENT) {
            playerData.permanentUpgradeIds.add(upgradeId);
        } else {
            playerData.temporaryUpgradeIds.add(upgradeId);
        }

        applyUpgrade(player, upgrade);
        syncOwnedUpgrades(player, playerData);

        if (!player.getWorld().isClient()) {
            player.getWorld().playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.ENTITY_PLAYER_LEVELUP,
                    player.getSoundCategory(),
                    1.0F,
                    1.0F
            );
        }
    }

    public static void addUpgrade(RoguelikeMCUpgradeData upgrade, ServerPlayerEntity player) {
        addUpgrade(RoguelikeMCUpgradeManager.idFor(upgrade), player);
    }

    public static void applyUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade) {
        forEachAction(player, upgrade, UpgradeActionHandlers::apply);
    }

    public static void applyJoinUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade) {
        forEachAction(player, upgrade, UpgradeActionHandlers::onJoin);
    }

    public static void removeUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade, RoguelikeMCUpgradeData.ActionData action) {
        UpgradeActionHandlers.remove(new UpgradeActionContext(player, upgrade, action));
    }

    public static void syncOwnedUpgrades(ServerPlayerEntity player, RoguelikeMCPlayerData playerData) {
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(true, playerData.getPermanentUpgrades()));
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(false, playerData.getTemporaryUpgrades()));
    }

    private static void forEachAction(
            ServerPlayerEntity player,
            RoguelikeMCUpgradeData upgrade,
            UpgradeActionDispatcher dispatcher
    ) {
        upgrade.actions().forEach(action ->
                dispatcher.dispatch(new UpgradeActionContext(player, upgrade, action))
        );
    }

    @FunctionalInterface
    private interface UpgradeActionDispatcher {
        void dispatch(UpgradeActionContext context);
    }
}
