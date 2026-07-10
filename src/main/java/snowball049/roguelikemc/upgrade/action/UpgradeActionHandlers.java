package snowball049.roguelikemc.upgrade.action;

import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.upgrade.action.trigger.TriggerUpgradeActionHandler;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import java.util.EnumMap;
import java.util.Map;

public final class UpgradeActionHandlers {
    private static final Map<UpgradeActionType, UpgradeActionHandler> HANDLERS = new EnumMap<>(UpgradeActionType.class);

    static {
        register(AttributeUpgradeActionHandler.INSTANCE);
        register(EffectUpgradeActionHandler.INSTANCE);
        register(CommandUpgradeActionHandler.INSTANCE);
        register(EventUpgradeActionHandler.INSTANCE);
        register(TriggerUpgradeActionHandler.INSTANCE);
    }

    private UpgradeActionHandlers() {
    }

    public static void register(UpgradeActionHandler handler) {
        HANDLERS.put(handler.type(), handler);
    }

    public static UpgradeActionHandler get(UpgradeActionType type) {
        return HANDLERS.get(type);
    }

    public static void apply(UpgradeActionContext context) {
        dispatch(context, UpgradeActionHandler::apply);
    }

    public static void remove(UpgradeActionContext context) {
        dispatch(context, UpgradeActionHandler::remove);
    }

    public static void onJoin(UpgradeActionContext context) {
        dispatch(context, UpgradeActionHandler::onJoin);
    }

    public static void tick(UpgradeActionContext context) {
        UpgradeActionHandler handler = getHandler(context);
        if (handler != null && handler.matchesTick(context.action())) {
            handler.tick(context);
        }
    }

    private static void dispatch(UpgradeActionContext context, HandlerConsumer consumer) {
        UpgradeActionHandler handler = getHandler(context);
        if (handler == null) {
            RoguelikeMC.LOGGER.warn("Unknown action type: {}", context.action().type());
            return;
        }
        consumer.accept(handler, context);
    }

    private static UpgradeActionHandler getHandler(UpgradeActionContext context) {
        UpgradeActionType actionType = context.action().actionType();
        if (actionType == null) {
            return null;
        }
        return HANDLERS.get(actionType);
    }

    @FunctionalInterface
    private interface HandlerConsumer {
        void accept(UpgradeActionHandler handler, UpgradeActionContext context);
    }
}
