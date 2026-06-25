package snowball049.roguelikemc.upgrade.enums;

import snowball049.roguelikemc.RoguelikeMC;

public enum UpgradeActionType {
    ATTRIBUTE("attribute"),
    EFFECT("effect"),
    COMMAND("command"),
    EVENT("event");

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
