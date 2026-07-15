package snowball049.roguelikemc.upgrade.action.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import java.util.List;

import static snowball049.roguelikemc.upgrade.schema.SchemaFields.JsonField;

public record TriggerActionPayload(
        String eventType,
        int count,
        int cooldown,
        boolean preventPlaceBreak,
        List<TriggerConditionData> conditions,
        List<RoguelikeMCUpgradeData.ActionData> actions
) {
    public static final Codec<TriggerActionPayload> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf(JsonField.EVENT_TYPE).forGetter(TriggerActionPayload::eventType),
                    Codec.INT.optionalFieldOf(JsonField.COUNT, 1).forGetter(TriggerActionPayload::count),
                    Codec.INT.optionalFieldOf(JsonField.COOLDOWN, 0).forGetter(TriggerActionPayload::cooldown),
                    Codec.BOOL.optionalFieldOf(JsonField.PREVENT_PLACE_BREAK, false).forGetter(TriggerActionPayload::preventPlaceBreak),
                    TriggerConditionData.CODEC.listOf().optionalFieldOf(JsonField.CONDITIONS, List.of()).forGetter(TriggerActionPayload::conditions),
                    RoguelikeMCUpgradeData.ActionData.CODEC.listOf().fieldOf(JsonField.ACTIONS).forGetter(TriggerActionPayload::actions)
            ).apply(instance, TriggerActionPayload::new)
    );

    public TriggerActionPayload {
        count = Math.max(1, count);
        cooldown = Math.max(0, cooldown);
        conditions = conditions == null ? List.of() : List.copyOf(conditions);
        actions = actions == null ? List.of() : List.copyOf(actions);
    }

    public static DataResult<TriggerActionPayload> fromAction(RoguelikeMCUpgradeData.ActionData action) {
        if (action == null) {
            return DataResult.error(() -> "Trigger action cannot be null");
        }
        if (!action.hasPayload()) {
            return DataResult.error(() -> "Trigger action has no structured payload");
        }
        return promoteNestedActions(action.payload().deepCopy())
                .flatMap(payload -> CODEC.parse(JsonOps.INSTANCE, payload))
                .flatMap(TriggerActionPayload::validate);
    }

    public static DataResult<JsonObject> promoteNestedActions(JsonObject payload) {
        if (payload == null) {
            return DataResult.error(() -> "Trigger payload cannot be null");
        }

        JsonObject normalized = payload.deepCopy();
        boolean hasActions = normalized.has(JsonField.ACTIONS) && !normalized.get(JsonField.ACTIONS).isJsonNull();
        boolean hasAction = normalized.has(JsonField.ACTION) && !normalized.get(JsonField.ACTION).isJsonNull();

        if (hasActions) {
            JsonElement actionsElement = normalized.get(JsonField.ACTIONS);
            if (!actionsElement.isJsonArray() || actionsElement.getAsJsonArray().isEmpty()) {
                return DataResult.error(() -> "Trigger payload 'actions' must be a non-empty array");
            }
            normalized.remove(JsonField.ACTION);
            return DataResult.success(normalized);
        }

        if (hasAction) {
            JsonElement actionElement = normalized.get(JsonField.ACTION);
            if (!actionElement.isJsonObject()) {
                return DataResult.error(() -> "Trigger payload legacy 'action' must be an object");
            }
            JsonArray actions = new JsonArray();
            actions.add(actionElement.getAsJsonObject().deepCopy());
            normalized.add(JsonField.ACTIONS, actions);
            normalized.remove(JsonField.ACTION);
            return DataResult.success(normalized);
        }

        return DataResult.error(() -> "Trigger payload requires 'actions' or legacy 'action'");
    }

    private static DataResult<TriggerActionPayload> validate(TriggerActionPayload payload) {
        if (payload.actions().isEmpty()) {
            return DataResult.error(() -> "Trigger payload requires at least one nested action");
        }
        for (RoguelikeMCUpgradeData.ActionData nested : payload.actions()) {
            if (nested.actionType() == UpgradeActionType.TRIGGER) {
                return DataResult.error(() -> "Nested trigger actions are not supported");
            }
        }
        return DataResult.success(payload);
    }

    public record TriggerConditionData(String type, JsonObject payload) {
        public static final Codec<TriggerConditionData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf(JsonField.TYPE).forGetter(TriggerConditionData::type),
                        RoguelikeMCUpgradeData.JSON_OBJECT_CODEC.optionalFieldOf(JsonField.PAYLOAD, emptyPayload()).forGetter(TriggerConditionData::payload)
                ).apply(instance, TriggerConditionData::new)
        );

        public TriggerConditionData {
            payload = payload == null ? emptyPayload() : payload.deepCopy();
        }

        private static JsonObject emptyPayload() {
            return new JsonObject();
        }
    }
}
