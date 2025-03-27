package snowball049.roguelikemc.compat;

import net.fabricmc.loader.api.FabricLoader;

public class RoguelikeMCCompat {
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
