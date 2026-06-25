package snowball049.roguelikemc.upgrade.action;

import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;
import snowball049.roguelikemc.upgrade.action.event.UpgradeEventHandlers;

public final class EventUpgradeActionHandler implements UpgradeActionHandler {
    public static final EventUpgradeActionHandler INSTANCE = new EventUpgradeActionHandler();

    private EventUpgradeActionHandler() {
    }

    @Override
    public UpgradeActionType type() {
        return UpgradeActionType.EVENT;
    }

    @Override
    public void apply(UpgradeActionContext context) {
        UpgradeEventHandlers.apply(context);
    }

    @Override
    public void remove(UpgradeActionContext context) {
        UpgradeEventHandlers.remove(context);
    }

    @Override
    public void onJoin(UpgradeActionContext context) {
        UpgradeEventHandlers.onJoin(context);
    }
}
