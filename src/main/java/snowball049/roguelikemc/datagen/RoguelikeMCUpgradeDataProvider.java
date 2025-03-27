package snowball049.roguelikemc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import snowball049.roguelikemc.RoguelikeMC;

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
    public final List<RoguelikeMCUpgrade> upgrades = new ArrayList<>();

    public RoguelikeMCUpgradeDataProvider(FabricDataOutput output) {
        this.output = output;
    }

    // Nested class to represent upgrade data
    public record RoguelikeMCUpgrade(
        String id,
        String name,
        String description,
        String tier,
        boolean isPermanent,
        boolean isUnique,
        String icon,
        List<ActionData> actions){

        public static final Codec<RoguelikeMCUpgrade> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                Codec.STRING.fieldOf("id").forGetter(RoguelikeMCUpgrade::id),
                                Codec.STRING.fieldOf("name").forGetter(RoguelikeMCUpgrade::name),
                                Codec.STRING.optionalFieldOf("description", "").forGetter(RoguelikeMCUpgrade::description),
                                Codec.STRING.optionalFieldOf("tier", "common").forGetter(RoguelikeMCUpgrade::tier),
                                Codec.BOOL.optionalFieldOf("isPermanent", false).forGetter(RoguelikeMCUpgrade::isPermanent),
                                Codec.BOOL.optionalFieldOf("isUnique", false).forGetter(RoguelikeMCUpgrade::isUnique),
                                Codec.STRING.optionalFieldOf("icon", "").forGetter(RoguelikeMCUpgrade::icon),
                                ActionData.CODEC.listOf().optionalFieldOf("actions", new ArrayList<>()).forGetter(RoguelikeMCUpgrade::actions)
                        ).apply(instance, RoguelikeMCUpgrade::new)
                );
        public static final PacketCodec<RegistryByteBuf, RoguelikeMCUpgrade> PACKET_CODEC = PacketCodecs.registryCodec(RoguelikeMCUpgrade.CODEC);
        

        // Inner class for action data
        public record ActionData (
            String type,
            List<String> value){
            public static final Codec<ActionData> CODEC = RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.STRING.fieldOf("type").forGetter(ActionData::type),
                            Codec.list(Codec.STRING).fieldOf("value").forGetter(ActionData::value)
                    ).apply(instance, ActionData::new)
            );
        }
    }

    // Method to add upgrades
    public void addUpgrade(RoguelikeMCUpgrade upgrade) {
        upgrades.add(upgrade);
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (RoguelikeMCUpgrade upgrade : upgrades) {

            Path path = output.getPath()
                    .resolve(FabricLoader.getInstance().getGameDir())
                    .resolve("data")
                    .resolve(RoguelikeMC.MOD_ID)
                    .resolve("upgrades")
                    .resolve(upgrade.id + ".json");

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