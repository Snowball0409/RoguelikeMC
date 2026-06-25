package snowball049.roguelikemc.upgrade.action.event;

import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

import java.util.HashMap;
import java.util.Map;

public final class UpgradeEventHandlers {
    private static final Map<String, UpgradeEventHandler> HANDLERS = new HashMap<>();

    static {
        register(new AllowCreativeFlyingEventHandler());
        register(new KeepEquipmentAfterDeathEventHandler());
        register(new OneLastChanceEventHandler());
        register(new SetEquipmentEventHandler());
        register(new EffectMobsEventHandler());
        register(new ProvokedEventHandler());
        register(new AddLootTableEventHandler());
    }

    private UpgradeEventHandlers() {
    }

    public static void register(UpgradeEventHandler handler) {
        HANDLERS.put(handler.eventType(), handler);
    }

    public static UpgradeEventHandler get(String eventType) {
        return HANDLERS.get(eventType);
    }

    public static void apply(UpgradeActionContext context) {
        dispatch(context, UpgradeEventHandler::apply);
    }

    public static void remove(UpgradeActionContext context) {
        dispatch(context, UpgradeEventHandler::remove);
    }

    public static void onJoin(UpgradeActionContext context) {
        dispatch(context, UpgradeEventHandler::onJoin);
    }

    private static void dispatch(UpgradeActionContext context, EventConsumer consumer) {
        UpgradeEventHandler handler = getHandler(context);
        if (handler == null) {
            RoguelikeMC.LOGGER.warn("Unexpected eventType value: {}", eventType(context));
            return;
        }
        consumer.accept(handler, context);
    }

    private static UpgradeEventHandler getHandler(UpgradeActionContext context) {
        return HANDLERS.get(eventType(context));
    }

    private static String eventType(UpgradeActionContext context) {
        return context.action().value().getFirst();
    }

    @FunctionalInterface
    private interface EventConsumer {
        void accept(UpgradeEventHandler handler, UpgradeActionContext context);
    }
}
