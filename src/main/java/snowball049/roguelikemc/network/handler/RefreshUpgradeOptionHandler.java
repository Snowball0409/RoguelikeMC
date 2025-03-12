package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.ActionResult;
import snowball049.roguelikemc.config.RoguelikeMCConfig;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RefreshUpgradeOptionHandler {
    public static void handle(RoguelikeMCConfig config, ServerPlayNetworking.Context context) {
        // Shuffle Upgrade Options and Send to Client
        if(context.player().getWorld().isClient()) {
            return;
        }
        Collections.shuffle(config.upgrades);
        List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> currentUpgrades = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            currentUpgrades.add(config.upgrades.get(i));
        }
        for (RoguelikeMCConfig.RogueLikeMCUpgradeConfig upgrade : currentUpgrades) {
            ServerPlayNetworking.send(context.player(), new UpgradeOptionS2CPayload(upgrade));
        }
    }
}
