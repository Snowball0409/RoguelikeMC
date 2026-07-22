package snowball049.roguelikemc;

import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.testutil.TestFixtures;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoguelikeMCStateSaverAndLoaderTest {
    @Test
    void migratesLegacyUpgradeSnapshotsToIdentifiers() {
        NbtElement legacyNbt = RoguelikeMCUpgradeData.CODEC.listOf()
                .encodeStart(
                        NbtOps.INSTANCE,
                        RoguelikeMCUpgradeData.CODEC.listOf()
                                .parse(JsonOps.INSTANCE, TestFixtures.array(TestFixtures.PERSISTENCE, "legacySnapshot"))
                                .getOrThrow(false, error -> { throw new IllegalStateException(error); })
                )
                .getOrThrow(false, error -> { throw new IllegalStateException(error); });

        List<Identifier> decoded = RoguelikeMCStateSaverAndLoader.decodeUpgradeIdList(legacyNbt);
        List<Identifier> expected = readIdentifierList(TestFixtures.array(TestFixtures.PERSISTENCE, "legacyIds"));

        assertEquals(expected, decoded);
    }

    @Test
    void roundTripsModernIdentifierList() {
        List<Identifier> original = readIdentifierList(TestFixtures.array(TestFixtures.PERSISTENCE, "modernIds"));

        NbtElement encoded = RoguelikeMCStateSaverAndLoader.encodeUpgradeIdList(original);
        List<Identifier> decoded = RoguelikeMCStateSaverAndLoader.decodeUpgradeIdList(encoded);

        assertEquals(original, decoded);
        assertInstanceOf(NbtList.class, encoded);
        assertTrue(encoded instanceof NbtList list && list.get(0) instanceof NbtString);
    }

    @Test
    void decodeEmptyOrMissingReturnsEmptyList() {
        assertTrue(RoguelikeMCStateSaverAndLoader.decodeUpgradeIdList(null).isEmpty());
    }

    private static List<Identifier> readIdentifierList(com.google.gson.JsonArray array) {
        List<Identifier> identifiers = new ArrayList<>();
        for (var element : array) {
            identifiers.add(new Identifier(element.getAsString()));
        }
        return identifiers;
    }
}
