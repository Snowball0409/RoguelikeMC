package snowball049.roguelikemc.upgrade;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

import java.util.ArrayList;
import java.util.List;

import static snowball049.roguelikemc.upgrade.constants.UpgradeSchemaConstants.ActionType;
import static snowball049.roguelikemc.upgrade.constants.UpgradeSchemaConstants.JsonField;
import static snowball049.roguelikemc.upgrade.constants.UpgradeSchemaConstants.PayloadField;

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
        if (!normalized.has(JsonField.ACTIONS)) {
            return normalized;
        }

        JsonArray actions = normalized.getAsJsonArray(JsonField.ACTIONS);
        JsonArray normalizedActions = new JsonArray();
        for (JsonElement actionElement : actions) {
            normalizedActions.add(normalizeAction(actionElement.getAsJsonObject()));
        }
        normalized.add(JsonField.ACTIONS, normalizedActions);
        return normalized;
    }

    private static JsonObject normalizeAction(JsonObject actionJson) {
        if (actionJson.has(JsonField.PAYLOAD)) {
            String type = requiredString(actionJson, JsonField.TYPE);
            JsonObject payload = requiredObject(actionJson, JsonField.PAYLOAD);
            return switch (type) {
                case ActionType.TRIGGER -> normalizeTriggerAction(type, payload);
                case ActionType.ATTRIBUTE, ActionType.EFFECT, ActionType.COMMAND, ActionType.EVENT ->
                        normalizePayloadBackedAction(type, payload);
                default -> throw new IllegalArgumentException("Unsupported authored action type: " + type);
            };
        }

        if (actionJson.has(JsonField.VALUE)) {
            return actionJson.deepCopy();
        }

        return actionJson.deepCopy();
    }

    private static JsonObject normalizePayloadBackedAction(String type, JsonObject payload) {
        JsonObject normalized = new JsonObject();
        normalized.addProperty(JsonField.TYPE, type);
        normalized.add(JsonField.VALUE, switch (type) {
            case ActionType.ATTRIBUTE -> values(
                    requiredString(payload, PayloadField.ATTRIBUTE),
                    requiredString(payload, PayloadField.AMOUNT),
                    requiredString(payload, PayloadField.OPERATION)
            );
            case ActionType.EFFECT -> values(
                    requiredString(payload, PayloadField.EFFECT),
                    requiredString(payload, PayloadField.DURATION),
                    requiredString(payload, PayloadField.AMPLIFIER)
            );
            case ActionType.COMMAND -> commandValues(payload);
            case ActionType.EVENT -> eventValues(payload);
            default -> throw new IllegalArgumentException("Unsupported authored action type: " + type);
        });
        return normalized;
    }

    private static JsonObject normalizeTriggerAction(String type, JsonObject payload) {
        JsonObject normalized = new JsonObject();
        normalized.addProperty(JsonField.TYPE, type);

        JsonObject runtimePayload = new JsonObject();
        runtimePayload.addProperty(JsonField.EVENT_TYPE, requiredString(payload, JsonField.EVENT_TYPE));
        runtimePayload.addProperty(JsonField.COUNT, requiredInt(payload, JsonField.COUNT));
        runtimePayload.addProperty(JsonField.COOLDOWN, optionalInt(payload, JsonField.COOLDOWN, 0));

        JsonArray conditions = optionalArray(payload, JsonField.CONDITIONS);
        JsonArray normalizedConditions = new JsonArray();
        for (JsonElement conditionElement : conditions) {
            normalizedConditions.add(normalizeCondition(conditionElement.getAsJsonObject()));
        }
        runtimePayload.add(JsonField.CONDITIONS, normalizedConditions);

        JsonObject nestedAction = normalizeAction(requiredObject(payload, JsonField.ACTION));
        if (ActionType.TRIGGER.equalsIgnoreCase(requiredString(nestedAction, JsonField.TYPE))) {
            throw new IllegalArgumentException("Nested trigger actions are not supported");
        }
        runtimePayload.add(JsonField.ACTION, nestedAction);

        normalized.add(JsonField.PAYLOAD, runtimePayload);
        return normalized;
    }

    private static JsonObject normalizeCondition(JsonObject conditionJson) {
        JsonObject normalized = new JsonObject();
        normalized.addProperty(JsonField.TYPE, requiredString(conditionJson, JsonField.TYPE));
        JsonObject payload = conditionJson.has(JsonField.PAYLOAD)
                ? requiredObject(conditionJson, JsonField.PAYLOAD).deepCopy()
                : new JsonObject();
        normalized.add(JsonField.PAYLOAD, payload);
        return normalized;
    }

    private static JsonArray commandValues(JsonObject payload) {
        List<String> values = new ArrayList<>();
        values.add(requiredString(payload, PayloadField.COMMAND));
        String needsOwner = optionalString(payload, PayloadField.NEEDS_OWNER);
        if ("true".equalsIgnoreCase(needsOwner)) {
            values.add("true");
        }
        return values(values);
    }

    private static JsonArray eventValues(JsonObject payload) {
        String eventType = requiredString(payload, JsonField.EVENT_TYPE);
        return switch (eventType) {
            case "allow_creative_flying", "keep_equipment_after_death", "one_last_chance" ->
                    values(eventType);
            case "set_equipment" -> values(
                    eventType,
                    requiredString(payload, PayloadField.SLOT),
                    optionalString(payload, PayloadField.ITEM_NBT, "")
            );
            case "effect_mobs" -> values(
                    eventType,
                    requiredString(payload, PayloadField.EFFECT),
                    requiredString(payload, PayloadField.AMPLIFIER),
                    requiredString(payload, PayloadField.RADIUS)
            );
            case "provoked" -> values(
                    eventType,
                    requiredString(payload, PayloadField.ENTITY_TYPE)
            );
            case "add_loot_table" -> values(
                    eventType,
                    requiredString(payload, PayloadField.ENTITY_TYPE),
                    requiredString(payload, PayloadField.LOOT_TABLE)
            );
            default -> throw new IllegalArgumentException("Unsupported authored event type: " + eventType);
        };
    }

    private static JsonArray optionalArray(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            return new JsonArray();
        }
        if (!element.isJsonArray()) {
            throw new IllegalArgumentException("Expected array field: " + key);
        }
        return element.getAsJsonArray();
    }

    private static int requiredInt(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            throw new IllegalArgumentException("Missing field: " + key);
        }
        return element.getAsInt();
    }

    private static int optionalInt(JsonObject json, String key, int defaultValue) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            return defaultValue;
        }
        return element.getAsInt();
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
