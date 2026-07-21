package snowball049.roguelikemc.upgrade;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.testutil.TestFixtures;
import snowball049.roguelikemc.testutil.UpgradeTestRegistry;
import snowball049.roguelikemc.upgrade.enums.UpgradeRarity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpgradePresentationTest {
    @Test
    void rendersTemporaryUniqueClassificationTags() {
        JsonObject fixture = TestFixtures.object(TestFixtures.GAMEPLAY, "presentation", "temporaryUnique");
        var upgrade = UpgradeTestRegistry.registryUpgrade(fixture.get("registryKey").getAsString());

        String tags = UpgradePresentation.classificationTags(upgrade).getString();
        assertTrue(tags.contains(fixture.get("persistenceTag").getAsString()));
        assertTrue(tags.contains(fixture.get("uniqueTag").getAsString()));
    }

    @Test
    void rendersPermanentStackableClassificationTags() {
        JsonObject fixture = TestFixtures.object(TestFixtures.GAMEPLAY, "presentation", "permanentStackable");
        var upgrade = UpgradeTestRegistry.registryUpgrade(fixture.get("registryKey").getAsString());

        String tags = UpgradePresentation.classificationTags(upgrade).getString();
        assertEquals(fixture.get("tags").getAsString(), tags);
        assertEquals(fixture.get("uniqueTag").getAsString(), UpgradePresentation.uniqueTag(upgrade).getString());
    }

    @Test
    void rarityArgb_hasOpaqueAlphaAndMatchesFormattingColor() {
        for (UpgradeRarity rarity : UpgradeRarity.values()) {
            int argb = UpgradePresentation.rarityArgb(rarity);
            assertEquals(0xFF, (argb >>> 24) & 0xFF, "alpha must be opaque for " + rarity);
            Integer rgb = rarity.color().getColorValue();
            assertEquals(rgb == null ? 0xFFFFFF : rgb.intValue(), argb & 0xFFFFFF,
                    "rgb must match Formatting color for " + rarity);
        }
    }

    @Test
    void rarityArgb_commonIsWhite() {
        assertEquals(0xFFFFFFFF, UpgradePresentation.rarityArgb(UpgradeRarity.COMMON));
    }
}
