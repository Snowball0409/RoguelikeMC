package snowball049.roguelikemc.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RefreshUpgradeOptionHandler {
    public static void handle(RefreshUpgradeOptionC2SPayload payload, ServerPlayNetworking.Context context) {
        // Shuffle Upgrade Options and Send to Client
        if(context.player().getWorld().isClient()) return;
        if (!RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem) return;
        if (RoguelikeMCUpgradeManager.getUpgrades().size() < 3) {
            context.player().sendMessage(Text.literal("No enough upgrades available!"));
            return;
        }

        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(context.player());

        if (playerData.upgradePoints <= 0) {
            context.player().sendMessage(Text.literal("You don't have enough upgrade points!"));
            return;
        }

        List<RoguelikeMCUpgradeData> currentUpgrades = RoguelikeMCUpgradeUtil.getRandomUpgrades(playerData);
        for (RoguelikeMCUpgradeData upgrade : currentUpgrades) {
            ServerPlayNetworking.send(context.player(), new UpgradeOptionS2CPayload(upgrade));
        }
    }
}
