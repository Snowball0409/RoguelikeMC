package snowball049.roguelikemc.upgrade.gameplay;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;
import snowball049.roguelikemc.upgrade.action.event.UpgradeEventHandler;
import snowball049.roguelikemc.upgrade.action.event.UpgradeEventHandlers;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import java.util.Collection;

public final class UpgradeEventGameplayService {
    private UpgradeEventGameplayService() {
    }

    public static void tickAtInterval(MinecraftServer server, int interval) {
        forEachEventAction(server, (context, handler) -> {
            if (handler.tickInterval() == interval && handler.matchesPeriodicTick(context)) {
                handler.tick(context);
            }
        });
    }

    public static void onEntityKilledByPlayer(ServerPlayerEntity player, LivingEntity target, DamageSource source) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        for (RoguelikeMCUpgradeData upgrade : playerData.getAllUpgrades()) {
            for (RoguelikeMCUpgradeData.ActionData action : upgrade.actions()) {
                if (action.actionType() != UpgradeActionType.EVENT) {
                    continue;
                }

                UpgradeEventHandler handler = UpgradeEventHandlers.get(eventType(action));
                if (handler == null) {
                    continue;
                }

                UpgradeActionContext context = new UpgradeActionContext(player, upgrade, action);
                if (handler.matchesEntityKill(context, target)) {
                    handler.onEntityKill(context, target, source);
                }
            }
        }
    }

    private static void forEachEventAction(MinecraftServer server, EventActionConsumer consumer) {
        server.getPlayerManager().getPlayerList().forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            forEachUpgradeEvents(player, playerData.getTemporaryUpgrades(), consumer);
            forEachUpgradeEvents(player, playerData.getPermanentUpgrades(), consumer);
        });
    }

    private static void forEachUpgradeEvents(
            ServerPlayerEntity player,
            Collection<RoguelikeMCUpgradeData> upgrades,
            EventActionConsumer consumer
    ) {
        upgrades.forEach(upgrade -> upgrade.actions().forEach(action -> {
            if (action.actionType() != UpgradeActionType.EVENT) {
                return;
            }

            UpgradeEventHandler handler = UpgradeEventHandlers.get(eventType(action));
            if (handler == null) {
                return;
            }

            consumer.accept(new UpgradeActionContext(player, upgrade, action), handler);
        }));
    }

    private static String eventType(RoguelikeMCUpgradeData.ActionData action) {
        // Legacy normalized runtime payload: gameplay event routing still reads value[0]
        // until the packet/runtime schema catches up with authored `type + payload`.
        return action.value().getFirst();
    }

    @FunctionalInterface
    private interface EventActionConsumer {
        void accept(UpgradeActionContext context, UpgradeEventHandler handler);
    }
}
