package snowball049.roguelikemc.upgrade;

import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

public final class UpgradeIds {
    private UpgradeIds() {
    }

    public static Identifier fromUpgradeData(RoguelikeMCUpgradeData upgrade) {
        Identifier parsed = Identifier.tryParse(upgrade.id());
        if (parsed != null) {
            return parsed;
        }
        return Identifier.of(RoguelikeMC.MOD_ID, upgrade.id());
    }
}
