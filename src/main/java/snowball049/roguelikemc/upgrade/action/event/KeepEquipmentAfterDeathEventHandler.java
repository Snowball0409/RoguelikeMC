package snowball049.roguelikemc.upgrade.action.event;

import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

public final class KeepEquipmentAfterDeathEventHandler implements UpgradeEventHandler {
    @Override
    public String eventType() {
        return "keep_equipment_after_death";
    }

    @Override
    public void apply(UpgradeActionContext context) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(context.player());
        playerData.keepEquipmentAfterDeath = true;
    }

    @Override
    public void remove(UpgradeActionContext context) {
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(context.player());
        playerData.keepEquipmentAfterDeath = false;
    }
}
