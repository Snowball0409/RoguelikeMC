package snowball049.roguelikemc.upgrade;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.enums.UpgradePersistence;
import snowball049.roguelikemc.upgrade.enums.UpgradeRarity;
import snowball049.roguelikemc.upgrade.enums.UpgradeStacking;

public final class UpgradePresentation {
    private UpgradePresentation() {
    }

    public static Formatting rarityColor(RoguelikeMCUpgradeData upgrade) {
        return rarityColor(upgrade.rarity());
    }

    public static Formatting rarityColor(UpgradeRarity rarity) {
        return rarity.color();
    }

    public static Text classificationTags(RoguelikeMCUpgradeData upgrade) {
        MutableText persistenceTag = upgrade.persistence() == UpgradePersistence.PERMANENT
                ? Text.literal("[P]").formatted(Formatting.GOLD)
                : Text.literal("[T]").formatted(Formatting.DARK_GRAY);

        if (upgrade.stacking() == UpgradeStacking.UNIQUE) {
            return persistenceTag.append(Text.literal(" [U]").formatted(Formatting.LIGHT_PURPLE));
        }

        return persistenceTag;
    }

    public static Text uniqueTag(RoguelikeMCUpgradeData upgrade) {
        if (upgrade.stacking() == UpgradeStacking.UNIQUE) {
            return Text.literal("[U]").formatted(Formatting.LIGHT_PURPLE);
        }

        return Text.empty();
    }

    public static int rarityArgb(RoguelikeMCUpgradeData upgrade) {
        return rarityArgb(upgrade.rarity());
    }

    public static int rarityArgb(UpgradeRarity rarity) {
        Integer rgb = rarity.color().getColorValue();
        return rgb != null ? (0xFF000000 | rgb) : 0xFFFFFFFF;
    }
}
