package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import snowball049.roguelikemc.accessor.PlayerEntityAccessor;
import snowball049.roguelikemc.config.RoguelikeMCConfig;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;

public class SelectUpgradeOptionHandler {
    public static void handle(SelectUpgradeOptionC2SPayload packet, ServerPlayNetworking.Context context) {
        if(context.player().getWorld().isClient()) {
            return;
        }

        RoguelikeMCConfig.RogueLikeMCUpgradeConfig selected = packet.option();
        PlayerEntity player = context.player();

        if(player instanceof PlayerEntityAccessor accessor) {
            if (selected.is_permanent()) {
                accessor.addPermanentUpgrades(selected);
            }else{
                accessor.addTemporaryUpgrades(selected);
            }
        }
    }
}
