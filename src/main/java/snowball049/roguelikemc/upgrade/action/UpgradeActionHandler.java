package snowball049.roguelikemc.upgrade.action;

import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

public interface UpgradeActionHandler {
    UpgradeActionType type();

    void apply(UpgradeActionContext context);

    default void remove(UpgradeActionContext context) {
    }

    default void onJoin(UpgradeActionContext context) {
    }

    default boolean matchesTick(RoguelikeMCUpgradeData.ActionData action) {
        return false;
    }

    default void tick(UpgradeActionContext context) {
        apply(context);
    }
}
