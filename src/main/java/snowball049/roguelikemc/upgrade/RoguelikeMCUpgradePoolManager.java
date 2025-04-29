package snowball049.roguelikemc.upgrade;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoguelikeMCUpgradePoolManager implements SimpleSynchronousResourceReloadListener {

    public static final Identifier ID = Identifier.of(RoguelikeMC.MOD_ID, "upgrade_pool");

    private static final Map<Identifier, List<Identifier>> POOLS = new HashMap<>();

    @Override
    public void reload(ResourceManager manager) {
        POOLS.clear();

        var resources = manager.findResources("upgrade_pool", id -> id.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier fileId = Identifier.of(RoguelikeMC.MOD_ID, entry.getKey().getPath().substring("upgrade_pool/".length(), entry.getKey().getPath().length() - ".json".length()));

            try (InputStream stream = entry.getValue().getInputStream()) {
                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                List<Identifier> upgrades = new ArrayList<>();
                for (JsonElement element : json.getAsJsonArray("upgrades")) {
                    upgrades.add(Identifier.tryParse(element.getAsString()));
                }

                POOLS.put(fileId, upgrades);

            } catch (Exception e) {
                RoguelikeMC.LOGGER.error("Failed to load upgrade pool: " + fileId, e);
            }
        }

        RoguelikeMC.LOGGER.info("Loaded {} upgrade pools", POOLS.size());
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    public static List<Identifier> getUpgradesFromPool(Identifier poolId) {
        return POOLS.getOrDefault(poolId, List.of());
    }

    public static List<Identifier> getUpgradePools() {
        return new ArrayList<>(POOLS.keySet());
    }
}
