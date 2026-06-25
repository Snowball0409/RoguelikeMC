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
import snowball049.roguelikemc.upgrade.roll.UpgradeRollService;
import snowball049.roguelikemc.util.RoguelikeMCPointUtil;

import java.util.List;

public class RefreshUpgradeOptionHandler {
    public static void handle(RefreshUpgradeOptionC2SPayload ignored, ServerPlayNetworking.Context context) {
        // Shuffle Upgrade Options and Send to Client
        if(context.player().getWorld().isClient()) return;
        if (!RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem) return;
        if (RoguelikeMCUpgradeManager.getUpgrades().size() < 3) {
            context.player().sendMessage(Text.translatable("message.roguelikemc.warn_no_upgrade"));
            return;
        }

        boolean isRemove = RoguelikeMCPointUtil.removeUpgradePoints(context.player(), 1);
        if (!isRemove) return;

        List<RoguelikeMCUpgradeData> currentUpgrades = UpgradeRollService
                .rollOptions(RoguelikeMCStateSaverAndLoader.getPlayerState(context.player()));
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(context.player());
        for (RoguelikeMCUpgradeData upgrade : currentUpgrades) {
            playerData.currentOptionIds.add(RoguelikeMCUpgradeManager.idFor(upgrade));
            ServerPlayNetworking.send(context.player(), new UpgradeOptionS2CPayload(upgrade));
        }
    }
}
