package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RefreshUpgradeOptionHandler {
    public static void handle(ServerPlayNetworking.Context context) {
        // Shuffle Upgrade Options and Send to Client
        if(context.player().getWorld().isClient()) {
            return;
        }

        List<String> upgradeKeys = new ArrayList<>(RoguelikeMCUpgradeManager.getUpgrades().stream().map(RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade::id).toList());
        Collections.shuffle(upgradeKeys);
        List<RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade> currentUpgrades = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            currentUpgrades.add(RoguelikeMCUpgradeManager.getUpgrades().stream().filter(u -> u.id().equals(upgradeKeys.get(finalI))).findFirst().orElse(null));
        }
        for (RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade upgrade : currentUpgrades) {
            ServerPlayNetworking.send(context.player(), new UpgradeOptionS2CPayload(upgrade));
        }
    }
}
