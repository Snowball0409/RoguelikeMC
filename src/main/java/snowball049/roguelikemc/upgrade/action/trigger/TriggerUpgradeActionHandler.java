package snowball049.roguelikemc.upgrade.action.trigger;

import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;
import snowball049.roguelikemc.upgrade.action.UpgradeActionHandler;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

@SuppressWarnings("java:S6548")
public final class TriggerUpgradeActionHandler implements UpgradeActionHandler {
    public static final TriggerUpgradeActionHandler INSTANCE = new TriggerUpgradeActionHandler();

    private TriggerUpgradeActionHandler() {
    }

    @Override
    public UpgradeActionType type() {
        return UpgradeActionType.TRIGGER;
    }

    @Override
    public void apply(UpgradeActionContext context) {
        // Trigger upgrades activate through runtime hooks rather than immediate apply-time effects.
    }
}
