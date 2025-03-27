package snowball049.roguelikemc.compat;

import net.fabricmc.loader.api.FabricLoader;
import snowball049.roguelikemc.RoguelikeMC;

public class RoguelikeMCCompat {
    public static boolean isAccessoriesLoaded;
    public static boolean isTrinketsLoaded;

    public static void load(){
        isAccessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");
        isTrinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");

        if(isAccessoriesLoaded) {
            RoguelikeMC.LOGGER.info("Accessories is loaded, compatability enabled");
        }
        if(isTrinketsLoaded) {
            RoguelikeMC.LOGGER.info("Trinkets is loaded, compatability enabled");
        }
    }
}
