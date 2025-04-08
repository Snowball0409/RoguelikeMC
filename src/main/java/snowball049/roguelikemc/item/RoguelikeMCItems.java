package snowball049.roguelikemc.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;

public class RoguelikeMCItems {
    public static final Item UPGRADE_POINT_ORB = new UpgradePointOrbItem();

    public static Item register(Item item, String id) {
        Identifier itemID = Identifier.tryParse(RoguelikeMC.MOD_ID, id);
        return Registry.register(Registries.ITEM, itemID, item);
    }

    public static void initailize() {
        register(UPGRADE_POINT_ORB, "upgrade_point_orb");
        RoguelikeMC.LOGGER.info("RoguelikeMC Items Initialized");
    }
}
