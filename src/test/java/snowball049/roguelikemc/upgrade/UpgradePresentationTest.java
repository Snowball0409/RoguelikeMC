package snowball049.roguelikemc.upgrade;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.testutil.TestFixtures;
import snowball049.roguelikemc.testutil.UpgradeTestRegistry;

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
}
