package snowball049.roguelikemc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;
import snowball049.roguelikemc.upgrade.enums.UpgradePersistence;
import snowball049.roguelikemc.upgrade.enums.UpgradeRarity;
import snowball049.roguelikemc.upgrade.enums.UpgradeStacking;

import java.util.ArrayList;
import java.util.List;

// Nested class to represent upgrade data
public record RoguelikeMCUpgradeData(
        String id,
        String name,
        String description,
        String tier,
        boolean isPermanent,
        boolean isUnique,
        String icon,
        List<ActionData> actions) {

    public static final Codec<RoguelikeMCUpgradeData> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.STRING.fieldOf("id").forGetter(RoguelikeMCUpgradeData::id),
                            Codec.STRING.fieldOf("name").forGetter(RoguelikeMCUpgradeData::name),
                            Codec.STRING.optionalFieldOf("description", "").forGetter(RoguelikeMCUpgradeData::description),
                            Codec.STRING.optionalFieldOf("tier", "common").forGetter(RoguelikeMCUpgradeData::tier),
                            Codec.BOOL.optionalFieldOf("isPermanent", false).forGetter(RoguelikeMCUpgradeData::isPermanent),
                            Codec.BOOL.optionalFieldOf("isUnique", false).forGetter(RoguelikeMCUpgradeData::isUnique),
                            Codec.STRING.optionalFieldOf("icon", "").forGetter(RoguelikeMCUpgradeData::icon),
                            ActionData.CODEC.listOf().optionalFieldOf("actions", new ArrayList<>()).forGetter(RoguelikeMCUpgradeData::actions)
                    ).apply(instance, RoguelikeMCUpgradeData::new)
            );
    public static final PacketCodec<RegistryByteBuf, RoguelikeMCUpgradeData> PACKET_CODEC = PacketCodecs.registryCodec(RoguelikeMCUpgradeData.CODEC);

    public UpgradeRarity rarity() {
        return UpgradeRarity.fromString(tier);
    }

    public UpgradePersistence persistence() {
        return UpgradePersistence.fromPermanentFlag(isPermanent);
    }

    public UpgradeStacking stacking() {
        return UpgradeStacking.fromUniqueFlag(isUnique);
    }

    // Inner class for action data
    public record ActionData(
            String type,
            List<String> value) {
        public static final Codec<ActionData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("type").forGetter(ActionData::type),
                        Codec.list(Codec.STRING).fieldOf("value").forGetter(ActionData::value)
                ).apply(instance, ActionData::new)
        );

        /**
         * @deprecated Legacy runtime/packet payload shape retained for compatibility while authored
         * resource JSON migrates to semantic {@code payload} fields. Prefer the authored
         * {@code type + payload} schema at the resource layer and normalize through
         * {@code UpgradeResourceSchemaReader}.
         */
        @Deprecated(forRemoval = false, since = "2.0.0")
        public List<String> value() {
            return value;
        }

        public UpgradeActionType actionType() {
            return UpgradeActionType.fromString(type);
        }
    }
}
