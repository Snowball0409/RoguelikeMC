package snowball049.roguelikemc.upgrade.enums;

import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.upgrade.constants.UpgradeSchemaConstants.ActionType;

public enum UpgradeActionType {
    ATTRIBUTE(ActionType.ATTRIBUTE),
    EFFECT(ActionType.EFFECT),
    COMMAND(ActionType.COMMAND),
    EVENT(ActionType.EVENT),
    TRIGGER(ActionType.TRIGGER);

    private final String id;

    UpgradeActionType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static UpgradeActionType fromString(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }

        for (UpgradeActionType actionType : values()) {
            if (actionType.id.equalsIgnoreCase(type)) {
                return actionType;
            }
        }

        RoguelikeMC.LOGGER.warn("Unknown upgrade action type '{}'", type);
        return null;
    }
}
