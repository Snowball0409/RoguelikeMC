package snowball049.roguelikemc.upgrade.action.event;

import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

public final class OneLastChanceEventHandler implements UpgradeEventHandler {
    @Override
    public String eventType() {
        return "one_last_chance";
    }

    @Override
    public void apply(UpgradeActionContext context) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(context.player());
        playerData.revive = true;
    }

    @Override
    public void remove(UpgradeActionContext context) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(context.player());
        playerData.revive = false;
    }
}
