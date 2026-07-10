package snowball049.roguelikemc.upgrade.action.trigger;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

import java.util.List;

import static snowball049.roguelikemc.upgrade.schema.SchemaFields.JsonField;

public record TriggerActionPayload(
        String eventType,
        int count,
        int cooldown,
        List<TriggerConditionData> conditions,
        RoguelikeMCUpgradeData.ActionData action
) {
    public static final Codec<TriggerActionPayload> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf(JsonField.EVENT_TYPE).forGetter(TriggerActionPayload::eventType),
                    Codec.INT.optionalFieldOf(JsonField.COUNT, 1).forGetter(TriggerActionPayload::count),
                    Codec.INT.optionalFieldOf(JsonField.COOLDOWN, 0).forGetter(TriggerActionPayload::cooldown),
                    TriggerConditionData.CODEC.listOf().optionalFieldOf(JsonField.CONDITIONS, List.of()).forGetter(TriggerActionPayload::conditions),
                    RoguelikeMCUpgradeData.ActionData.CODEC.fieldOf(JsonField.ACTION).forGetter(TriggerActionPayload::action)
            ).apply(instance, TriggerActionPayload::new)
    );

    public TriggerActionPayload {
        count = Math.max(1, count);
        cooldown = Math.max(0, cooldown);
        conditions = conditions == null ? List.of() : List.copyOf(conditions);
    }

    public static DataResult<TriggerActionPayload> fromAction(RoguelikeMCUpgradeData.ActionData action) {
        if (action == null) {
            return DataResult.error(() -> "Trigger action cannot be null");
        }
        if (!action.hasPayload()) {
            return DataResult.error(() -> "Trigger action has no structured payload");
        }
        return CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, action.payload());
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
