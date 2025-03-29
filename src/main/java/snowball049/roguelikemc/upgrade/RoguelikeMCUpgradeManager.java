package snowball049.roguelikemc.upgrade;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RoguelikeMCUpgradeManager implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final Map<String, RoguelikeMCUpgradeData> UPGRADES = new HashMap<>();

    public static void loadUpgrades(ResourceManager resourceManager) {
        Map<String, RoguelikeMCUpgradeData> allUpgrades = new HashMap<>();
        String dataType = "upgrades";

        for (Identifier id: resourceManager.findAllResources(dataType, path -> path.getPath().endsWith(".json")).keySet()) {
            try (InputStreamReader reader = new InputStreamReader(resourceManager.getResource(id).orElseThrow().getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeData.CODEC
                        .parse(JsonOps.INSTANCE, json)
                        .resultOrPartial(RoguelikeMC.LOGGER::error)
                        .orElse(null);

                if (upgrade != null) {
                    allUpgrades.put(upgrade.id(), upgrade); // Add upgrade to the map
                } else {
                    RoguelikeMC.LOGGER.error("Failed to parse upgrade: {}", id);
                }
            } catch (Exception e) {
                RoguelikeMC.LOGGER.error("Error reading resource: {}", id, e);
            }
        }
        UPGRADES.putAll(allUpgrades);
        RoguelikeMC.LOGGER.info("Loaded {} upgrades", allUpgrades.size());
    }


    public static Collection<RoguelikeMCUpgradeData> getUpgrades() {
        return UPGRADES.values();
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of("roguelikemc", "upgrades");
    }

    @Override
    public void reload(ResourceManager manager) {
        loadUpgrades(manager);
    }
}
