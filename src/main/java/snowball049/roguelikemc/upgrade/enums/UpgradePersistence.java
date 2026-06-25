package snowball049.roguelikemc.upgrade.enums;

public enum UpgradePersistence {
    TEMPORARY(false),
    PERMANENT(true);

    private final boolean permanent;

    UpgradePersistence(boolean permanent) {
        this.permanent = permanent;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public static UpgradePersistence fromPermanentFlag(boolean isPermanent) {
        return isPermanent ? PERMANENT : TEMPORARY;
    }
}
