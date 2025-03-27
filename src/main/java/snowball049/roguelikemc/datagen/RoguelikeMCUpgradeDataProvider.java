package snowball049.roguelikemc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import snowball049.roguelikemc.RoguelikeMC;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoguelikeMCUpgradeDataProvider implements DataProvider {
    private final FabricDataOutput output;
    private final List<RoguelikeMCUpgrade> upgrades = new ArrayList<>();

    public RoguelikeMCUpgradeDataProvider(FabricDataOutput output) {
        this.output = output;
    }

    // Nested class to represent upgrade data
    public static class RoguelikeMCUpgrade {
        private final String id;
        private final String name;
        private String description = "";
        private String tier = "common";
        private boolean isPermanent = false;
        private boolean isUnique = false;
        private String icon = "";
        private final List<ActionData> actions = new ArrayList<>();

        public RoguelikeMCUpgrade(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public RoguelikeMCUpgrade description(String description) {
            this.description = description;
            return this;
        }

        public RoguelikeMCUpgrade tier(String tier) {
            this.tier = tier;
            return this;
        }

        public RoguelikeMCUpgrade isPermanent(boolean isPermanent) {
            this.isPermanent = isPermanent;
            return this;
        }

        public RoguelikeMCUpgrade isUnique(boolean isUnique) {
            this.isUnique = isUnique;
            return this;
        }

        public RoguelikeMCUpgrade icon(String icon) {
            this.icon = icon;
            return this;
        }

        public RoguelikeMCUpgrade addEffectAction(String type, String effect, String duration, String amplifier) {
            actions.add(new ActionData(type, effect, duration, amplifier));
            return this;
        }

        // Inner class for action data
        public static class ActionData {
            private final String type;
            private final List<String> value;

            public ActionData(String type, String effect, String duration, String amplifier) {
                this.type = type;
                this.value = List.of(effect, duration, amplifier);
            }
        }

        // Convert to JsonObject for serialization
        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("id", id);
            json.addProperty("name", name);
            json.addProperty("description", description);
            json.addProperty("tier", tier);
            json.addProperty("is_permanent", isPermanent);
            json.addProperty("is_unique", isUnique);
            json.addProperty("icon", icon);

            JsonArray actionArray = new JsonArray();
            for (ActionData action : actions) {
                JsonObject actionObj = new JsonObject();
                actionObj.addProperty("type", action.type);

                JsonArray valueArray = new JsonArray();
                action.value.forEach(valueArray::add);

                actionObj.add("value", valueArray);
                actionArray.add(actionObj);
            }
            json.add("action", actionArray);

            return json;
        }
    }

    // Method to add upgrades
    public void addUpgrade(RoguelikeMCUpgrade upgrade) {
        upgrades.add(upgrade);
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (RoguelikeMCUpgrade upgrade : upgrades) {
            JsonObject upgradeJson = upgrade.toJson();

            Path path = output.getPath()
                    .resolve(FabricLoader.getInstance().getGameDir())
                    .resolve("data")
                    .resolve(RoguelikeMC.MOD_ID)
                    .resolve("upgrades")
                    .resolve(upgrade.id + ".json");

            futures.add(DataProvider.writeToPath(writer, upgradeJson, path));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return "RoguelikeMC Upgrade Data Provider";
    }
}