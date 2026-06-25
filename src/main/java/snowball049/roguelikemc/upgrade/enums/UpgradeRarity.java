package snowball049.roguelikemc.upgrade.enums;

import net.minecraft.util.Formatting;
import snowball049.roguelikemc.RoguelikeMC;

public enum UpgradeRarity {
    COMMON("common", 50, Formatting.WHITE),
    RARE("rare", 30, Formatting.BLUE),
    EPIC("epic", 15, Formatting.DARK_PURPLE),
    LEGENDARY("legendary", 5, Formatting.GOLD);

    private final String id;
    private final int rollWeight;
    private final Formatting color;

    UpgradeRarity(String id, int rollWeight, Formatting color) {
        this.id = id;
        this.rollWeight = rollWeight;
        this.color = color;
    }

    public String id() {
        return id;
    }

    public int rollWeight() {
        return rollWeight;
    }

    public Formatting color() {
        return color;
    }

    public static UpgradeRarity fromString(String tier) {
        if (tier == null || tier.isBlank()) {
            return COMMON;
        }

        for (UpgradeRarity rarity : values()) {
            if (rarity.id.equalsIgnoreCase(tier)) {
                return rarity;
            }
        }

        RoguelikeMC.LOGGER.warn("Unknown upgrade tier '{}', defaulting to COMMON", tier);
        return COMMON;
    }
}
