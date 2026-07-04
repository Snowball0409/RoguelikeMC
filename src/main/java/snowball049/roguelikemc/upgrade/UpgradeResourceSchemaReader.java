package snowball049.roguelikemc.upgrade;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

import java.util.ArrayList;
import java.util.List;

public final class UpgradeResourceSchemaReader {
    private UpgradeResourceSchemaReader() {
    }

    public static DataResult<RoguelikeMCUpgradeData> parse(JsonObject resourceJson) {
        try {
            JsonObject normalized = normalizeUpgradeJson(resourceJson);
            return RoguelikeMCUpgradeData.CODEC.parse(JsonOps.INSTANCE, normalized);
        } catch (RuntimeException exception) {
            return DataResult.error(exception::getMessage);
        }
    }

    static JsonObject normalizeUpgradeJson(JsonObject resourceJson) {
        JsonObject normalized = resourceJson.deepCopy();
        if (!normalized.has("actions")) {
            return normalized;
        }

        JsonArray actions = normalized.getAsJsonArray("actions");
        JsonArray normalizedActions = new JsonArray();
        for (JsonElement actionElement : actions) {
            normalizedActions.add(normalizeAction(actionElement.getAsJsonObject()));
        }
        normalized.add("actions", normalizedActions);
        return normalized;
    }

    private static JsonObject normalizeAction(JsonObject actionJson) {
        if (actionJson.has("value")) {
            return normalizeLegacyValueAction(actionJson);
        }

        if (!actionJson.has("payload")) {
            return actionJson.deepCopy();
        }

        String type = requiredString(actionJson, "type");
        JsonObject payload = requiredObject(actionJson, "payload");

        JsonObject normalized = new JsonObject();
        normalized.addProperty("type", type);
        normalized.add("value", switch (type) {
            case "attribute" -> values(
                    requiredString(payload, "attribute"),
                    requiredString(payload, "amount"),
                    requiredString(payload, "operation")
            );
            case "effect" -> values(
                    requiredString(payload, "effect"),
                    requiredString(payload, "duration"),
                    requiredString(payload, "amplifier")
            );
            case "command" -> commandValues(payload);
            case "event" -> eventValues(payload);
            default -> throw new IllegalArgumentException("Unsupported authored action type: " + type);
        });
        return normalized;
    }

    /**
     * @deprecated Legacy authored resource support retained temporarily for compatibility with
     * existing datapacks. New authored upgrade JSON should use the semantic {@code type + payload}
     * schema instead of {@code value[]}.
     */
    @Deprecated(forRemoval = false, since = "2.0.0")
    private static JsonObject normalizeLegacyValueAction(JsonObject actionJson) {
        return actionJson.deepCopy();
    }

    private static JsonArray commandValues(JsonObject payload) {
        List<String> values = new ArrayList<>();
        values.add(requiredString(payload, "command"));
        String needsOwner = optionalString(payload, "needsOwner");
        if ("true".equalsIgnoreCase(needsOwner)) {
            values.add("true");
        }
        return values(values);
    }

    private static JsonArray eventValues(JsonObject payload) {
        String eventType = requiredString(payload, "eventType");
        return switch (eventType) {
            case "allow_creative_flying", "keep_equipment_after_death", "one_last_chance" ->
                    values(eventType);
            case "set_equipment" -> values(
                    eventType,
                    requiredString(payload, "slot"),
                    optionalString(payload, "itemNbt", "")
            );
            case "effect_mobs" -> values(
                    eventType,
                    requiredString(payload, "effect"),
                    requiredString(payload, "amplifier"),
                    requiredString(payload, "radius")
            );
            case "provoked" -> values(
                    eventType,
                    requiredString(payload, "entityType")
            );
            case "add_loot_table" -> values(
                    eventType,
                    requiredString(payload, "entityType"),
                    requiredString(payload, "lootTable")
            );
            default -> throw new IllegalArgumentException("Unsupported authored event type: " + eventType);
        };
    }

    private static JsonArray values(String... values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }

    private static JsonArray values(List<String> values) {
        JsonArray array = new JsonArray();
        values.forEach(array::add);
        return array;
    }

    private static JsonObject requiredObject(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null || !element.isJsonObject()) {
            throw new IllegalArgumentException("Missing object field: " + key);
        }
        return element.getAsJsonObject();
    }

    private static String requiredString(JsonObject json, String key) {
        String value = optionalString(json, key, null);
        if (value == null) {
            throw new IllegalArgumentException("Missing field: " + key);
        }
        return value;
    }

    private static String optionalString(JsonObject json, String key) {
        return optionalString(json, key, null);
    }

    private static String optionalString(JsonObject json, String key, String defaultValue) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            return defaultValue;
        }
        return element.getAsString();
    }
}
