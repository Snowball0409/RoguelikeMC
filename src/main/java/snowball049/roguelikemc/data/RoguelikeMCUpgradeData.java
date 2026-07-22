package snowball049.roguelikemc.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;
import snowball049.roguelikemc.upgrade.enums.UpgradePersistence;
import snowball049.roguelikemc.upgrade.enums.UpgradeRarity;
import snowball049.roguelikemc.upgrade.enums.UpgradeStacking;

import java.util.ArrayList;
import java.util.List;

import static snowball049.roguelikemc.upgrade.schema.SchemaFields.JsonField;

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
    public static final Codec<JsonObject> JSON_OBJECT_CODEC = Codec.PASSTHROUGH.comapFlatMap(
            dynamic -> {
                JsonElement element = dynamic.convert(JsonOps.INSTANCE).getValue();
                if (!element.isJsonObject()) {
                    return DataResult.error(() -> "Expected JSON object payload");
                }
                return DataResult.success(element.getAsJsonObject());
            },
            json -> new Dynamic<>(JsonOps.INSTANCE, json.deepCopy())
    );

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
                            ActionData.CODEC.listOf().optionalFieldOf(JsonField.ACTIONS, new ArrayList<>()).forGetter(RoguelikeMCUpgradeData::actions)
                    ).apply(instance, RoguelikeMCUpgradeData::new)
            );
    public void write(PacketByteBuf buf) {
        JsonElement json = CODEC.encodeStart(JsonOps.INSTANCE, this)
                .getOrThrow(false, error -> { throw new IllegalStateException(error); });
        buf.writeString(json.toString());
    }

    public static RoguelikeMCUpgradeData read(PacketByteBuf buf) {
        JsonElement json = JsonParser.parseString(buf.readString());
        return CODEC.parse(JsonOps.INSTANCE, json)
                .getOrThrow(false, error -> { throw new IllegalStateException(error); });
    }

    public UpgradeRarity rarity() {
        return UpgradeRarity.fromString(tier);
    }

    public UpgradePersistence persistence() {
        return UpgradePersistence.fromPermanentFlag(isPermanent);
    }

    public UpgradeStacking stacking() {
        return UpgradeStacking.fromUniqueFlag(isUnique);
    }

    // Inner class to represent upgrade action data
    public record ActionData(
            String type,
            List<String> value,
            JsonObject payload) {

        public static final Codec<ActionData> CODEC = Codec.PASSTHROUGH.comapFlatMap(
                dynamic -> {
                    JsonElement element = dynamic.convert(JsonOps.INSTANCE).getValue();
                    if (!element.isJsonObject()) {
                        return DataResult.error(() -> "Expected action JSON object");
                    }

                    JsonObject json = element.getAsJsonObject();
                    JsonElement typeElement = json.get(JsonField.TYPE);
                    if (typeElement == null || typeElement.isJsonNull()) {
                        return DataResult.error(() -> "ActionData missing type");
                    }

                    List<String> value = List.of();
                    if (json.has(JsonField.VALUE)) {
                        DataResult<List<String>> valueResult = Codec.list(Codec.STRING).parse(JsonOps.INSTANCE, json.get(JsonField.VALUE));
                        if (valueResult.error().isPresent()) {
                            return DataResult.error(() -> valueResult.error().get().message());
                        }
                        value = valueResult.result().orElse(List.of());
                    }

                    JsonObject payload = emptyPayload();
                    if (json.has(JsonField.PAYLOAD)) {
                        JsonElement payloadElement = json.get(JsonField.PAYLOAD);
                        if (!payloadElement.isJsonObject()) {
                            return DataResult.error(() -> "ActionData payload must be an object");
                        }
                        payload = payloadElement.getAsJsonObject().deepCopy();
                    }

                    return validate(new ActionData(typeElement.getAsString(), value, payload));
                },
                action -> new Dynamic<>(JsonOps.INSTANCE, action.toJson())
        );

        public ActionData {
            value = value == null ? List.of() : List.copyOf(value);
            payload = payload == null ? emptyPayload() : payload.deepCopy();
        }

        public ActionData(String type, List<String> value) {
            this(type, value, emptyPayload());
        }

        public ActionData(String type, JsonObject payload) {
            this(type, List.of(), payload);
        }

        private static DataResult<ActionData> validate(ActionData action) {
            if (action.hasValue() || action.hasPayload()) {
                return DataResult.success(action);
            }
            return DataResult.error(() -> "ActionData requires either value[] or payload");
        }

        private static JsonObject emptyPayload() {
            return new JsonObject();
        }

        private JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty(JsonField.TYPE, type);
            if (hasValue()) {
                json.add(JsonField.VALUE, Codec.list(Codec.STRING).encodeStart(JsonOps.INSTANCE, value)
                        .getOrThrow(false, error -> { throw new IllegalStateException(error); }));
            }
            if (hasPayload()) {
                json.add(JsonField.PAYLOAD, payload.deepCopy());
            }
            return json;
        }

        /**
         * @deprecated Legacy runtime/packet payload shape retained for compatibility while authored
         * resource JSON migrates to semantic {@code payload} fields. Existing non-trigger handlers
         * still consume this path during the compatibility phase, but new parsing should prefer
         * {@code payload} when available.
         */
        @Deprecated(forRemoval = false, since = "2.0.0")
        @SuppressWarnings("java:S1133")
        public List<String> value() {
            return value;
        }

        public boolean hasValue() {
            return !value.isEmpty();
        }

        /**
         * Legacy event routing reads the first normalized value entry until packet/runtime schema
         * convergence replaces this with semantic payload fields.
         */
        public String legacyEventType() {
            return value.isEmpty() ? "" : value.get(0);
        }

        public boolean hasPayload() {
            return payload != null && !payload.entrySet().isEmpty();
        }

        public <T> DataResult<T> parsePayload(Codec<T> codec) {
            if (!hasPayload()) {
                return DataResult.error(() -> "ActionData has no payload for type " + type);
            }
            return codec.parse(JsonOps.INSTANCE, payload);
        }

        public UpgradeActionType actionType() {
            return UpgradeActionType.fromString(type);
        }
    }
}
