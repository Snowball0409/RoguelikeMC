package snowball049.roguelikemc.upgrade.constants;

public final class UpgradeSchemaConstants {
    private UpgradeSchemaConstants() {
    }

    public static final class ActionType {
        public static final String ATTRIBUTE = "attribute";
        public static final String EFFECT = "effect";
        public static final String COMMAND = "command";
        public static final String EVENT = "event";
        public static final String TRIGGER = "trigger";

        private ActionType() {
        }
    }

    public static final class JsonField {
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String PAYLOAD = "payload";
        public static final String ACTIONS = "actions";
        public static final String EVENT_TYPE = "eventType";
        public static final String COUNT = "count";
        public static final String COOLDOWN = "cooldown";
        public static final String CONDITIONS = "conditions";
        public static final String ACTION = "action";

        private JsonField() {
        }
    }

    public static final class PayloadField {
        public static final String ATTRIBUTE = "attribute";
        public static final String AMOUNT = "amount";
        public static final String OPERATION = "operation";
        public static final String EFFECT = "effect";
        public static final String DURATION = "duration";
        public static final String AMPLIFIER = "amplifier";
        public static final String COMMAND = "command";
        public static final String NEEDS_OWNER = "needsOwner";
        public static final String SLOT = "slot";
        public static final String ITEM_NBT = "itemNbt";
        public static final String ENTITY_TYPE = "entityType";
        public static final String LOOT_TABLE = "lootTable";
        public static final String RADIUS = "radius";

        private PayloadField() {
        }
    }

    public static final class Trigger {
        public static final String EVENT_KILL = "kill";
        public static final String CONDITION_TARGET_HOSTILE = "target_hostile";

        private Trigger() {
        }
    }
}
