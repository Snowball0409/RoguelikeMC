package snowball049.roguelikemc.upgrade.runtime;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.action.EffectUpgradeActionHandler;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;
import snowball049.roguelikemc.upgrade.action.UpgradeActionHandlers;

import java.util.Collection;
import java.util.function.Predicate;

public final class UpgradeTickRuntimeService {
    private UpgradeTickRuntimeService() {
    }

    public static void tickInfiniteEffects(MinecraftServer server) {
        forEachMatchingAction(
                server,
                EffectUpgradeActionHandler.INSTANCE::matchesTick,
                UpgradeActionHandlers::tick
        );
    }

    public static void tickEvents(MinecraftServer server, int interval) {
        UpgradeEventRuntimeService.tickAtInterval(server, interval);
    }

    private static void forEachMatchingAction(
            MinecraftServer server,
            Predicate<RoguelikeMCUpgradeData.ActionData> filter,
            UpgradeActionTickConsumer consumer
    ) {
        server.getPlayerManager().getPlayerList().forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            forEachUpgradeCollection(player, playerData.getTemporaryUpgrades(), filter, consumer);
            forEachUpgradeCollection(player, playerData.getPermanentUpgrades(), filter, consumer);
        });
    }

    private static void forEachUpgradeCollection(
            ServerPlayerEntity player,
            Collection<RoguelikeMCUpgradeData> upgrades,
            Predicate<RoguelikeMCUpgradeData.ActionData> filter,
            UpgradeActionTickConsumer consumer
    ) {
        upgrades.forEach(upgrade -> upgrade.actions().forEach(action -> {
            if (filter.test(action)) {
                consumer.tick(new UpgradeActionContext(player, upgrade, action));
            }
        }));
    }

    @FunctionalInterface
    private interface UpgradeActionTickConsumer {
        void tick(UpgradeActionContext context);
    }
}
