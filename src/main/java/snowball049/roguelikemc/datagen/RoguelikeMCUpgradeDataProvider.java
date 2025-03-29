package snowball049.roguelikemc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoguelikeMCUpgradeDataProvider implements DataProvider {
    public static final RoguelikeMCUpgradeDataProvider INSTANCE = new RoguelikeMCUpgradeDataProvider(null);
    private final FabricDataOutput output;
    public final List<RoguelikeMCUpgradeData> upgrades = new ArrayList<>();

    public RoguelikeMCUpgradeDataProvider(FabricDataOutput output) {
        this.output = output;
    }

    // Method to add upgrades
    public void addUpgrade(RoguelikeMCUpgradeData upgrade) {
        upgrades.add(upgrade);
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (RoguelikeMCUpgradeData upgrade : upgrades) {

            Path path = output.getPath()
                    .resolve(FabricLoader.getInstance().getGameDir())
                    .resolve("data")
                    .resolve(RoguelikeMC.MOD_ID)
                    .resolve("upgrades")
                    .resolve(upgrade.id() + ".json");

            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    Files.createDirectories(path.getParent());
                    Files.writeString(path, gson.toJson(upgrade), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to write upgrade data to file: " + path, e);
                }
            }));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return "RoguelikeMC Upgrade Data Provider";
    }
}