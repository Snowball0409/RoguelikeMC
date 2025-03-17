package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RefreshUpgradeOptionHandler {
    public static void handle(ServerPlayNetworking.Context context) {
        // Shuffle Upgrade Options and Send to Client
        if(context.player().getWorld().isClient()) {
            return;
        }

        Collections.shuffle(RoguelikeMCUpgradesConfig.INSTANCE.upgrades);
        List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> currentUpgrades = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            currentUpgrades.add(RoguelikeMCUpgradesConfig.INSTANCE.upgrades.get(i));
        }
        for (RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade : currentUpgrades) {
            ServerPlayNetworking.send(context.player(), new UpgradeOptionS2CPayload(upgrade));
        }
    }
}
