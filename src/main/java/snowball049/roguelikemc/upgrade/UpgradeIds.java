package snowball049.roguelikemc.upgrade;

import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

public final class UpgradeIds {
    private UpgradeIds() {
    }

    public static Identifier fromUpgradeData(RoguelikeMCUpgradeData upgrade) {
        String rawId = upgrade.id();
        if (rawId == null || rawId.isBlank()) {
            return Identifier.of(RoguelikeMC.MOD_ID, "unknown");
        }

        if (rawId.contains(":")) {
            Identifier parsed = Identifier.tryParse(rawId);
            if (parsed != null) {
                return parsed;
            }
        }

        return Identifier.of(RoguelikeMC.MOD_ID, rawId);
    }
}
