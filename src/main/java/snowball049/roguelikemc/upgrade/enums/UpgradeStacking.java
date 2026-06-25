package snowball049.roguelikemc.upgrade.enums;

public enum UpgradeStacking {
    STACKABLE(false),
    UNIQUE(true);

    private final boolean unique;

    UpgradeStacking(boolean unique) {
        this.unique = unique;
    }

    public boolean isUnique() {
        return unique;
    }

    public static UpgradeStacking fromUniqueFlag(boolean isUnique) {
        return isUnique ? UNIQUE : STACKABLE;
    }
}
