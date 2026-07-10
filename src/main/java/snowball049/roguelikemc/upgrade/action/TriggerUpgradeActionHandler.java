package snowball049.roguelikemc.upgrade.action;

import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

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
        // Trigger upgrades activate through gameplay hooks rather than immediate apply-time effects.
    }
}
