package snowball049.roguelikemc.upgrade.action;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class AttributeUpgradeActionHandler implements UpgradeActionHandler {
    public static final AttributeUpgradeActionHandler INSTANCE = new AttributeUpgradeActionHandler();

    private AttributeUpgradeActionHandler() {
    }

    @Override
    public UpgradeActionType type() {
        return UpgradeActionType.ATTRIBUTE;
    }

    @Override
    public void apply(UpgradeActionContext context) {
        // Legacy normalized runtime payload. When packet/runtime schema converges with
        // authored `type + payload`, migrate this call site away from direct value[] reads.
        addAttribute(context.player(), context.upgrade().id(), context.action().value());
    }

    @Override
    public void remove(UpgradeActionContext context) {
        removeAttribute(context.player(), context.upgradeId(), context.action().value());
    }

    @Override
    public void onJoin(UpgradeActionContext context) {
        apply(context);
    }

    private static void addAttribute(net.minecraft.server.network.ServerPlayerEntity player, String id, List<String> value) {
        Identifier attributeIdentifier = Identifier.tryParse(value.get(0));
        EntityAttribute attribute = Objects.requireNonNull(Registries.ATTRIBUTE.get(attributeIdentifier));
        double amount = Double.parseDouble(value.get(1));
        EntityAttributeModifier.Operation operation = switch (value.get(2)) {
            case "add_value" -> EntityAttributeModifier.Operation.ADDITION;
            case "add_multiplied_base" -> EntityAttributeModifier.Operation.MULTIPLY_BASE;
            case "add_multiplied_total" -> EntityAttributeModifier.Operation.MULTIPLY_TOTAL;
            default -> EntityAttributeModifier.Operation.ADDITION;
        };

        EntityAttributeInstance instance = Objects.requireNonNull(player.getAttributeInstance(attribute));
        int stackIndex = nextStackIndex(instance, id, attributeIdentifier);
        String modifierName = RoguelikeMC.MOD_ID + ":" + id + "/" + attributePath(attributeIdentifier) + "/" + stackIndex;

        EntityAttributeModifier attributeModifier = new EntityAttributeModifier(
                UUID.nameUUIDFromBytes(modifierName.getBytes(StandardCharsets.UTF_8)),
                modifierName,
                amount,
                operation
        );

        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(attribute, attributeModifier);
        player.getAttributes().addTemporaryModifiers(modifiers);
    }

    private static void removeAttribute(net.minecraft.server.network.ServerPlayerEntity player, Identifier id, List<String> value) {
        Identifier attributeIdentifier = Identifier.tryParse(value.get(0));
        EntityAttribute attribute = Objects.requireNonNull(Registries.ATTRIBUTE.get(attributeIdentifier));

        EntityAttributeInstance instance = Objects.requireNonNull(player.getAttributeInstance(attribute));
        String prefix = id.toString() + "/";
        for (EntityAttributeModifier modifier : List.copyOf(instance.getModifiers())) {
            RoguelikeMC.LOGGER.debug("Removing attribute {} from upgrade effect", modifier.getName());
            if (modifier.getName().startsWith(prefix)) {
                instance.removeModifier(modifier);
            }
        }
    }

    private static int nextStackIndex(
            EntityAttributeInstance instance,
            String upgradeId,
            Identifier attributeIdentifier
    ) {
        String prefix = RoguelikeMC.MOD_ID + ":" + upgradeId + "/" + attributePath(attributeIdentifier) + "/";
        int maxIndex = -1;
        for (EntityAttributeModifier modifier : instance.getModifiers()) {
            String modifierName = modifier.getName();
            if (!modifierName.startsWith(prefix)) {
                continue;
            }
            try {
                maxIndex = Math.max(maxIndex, Integer.parseInt(modifierName.substring(prefix.length())));
            } catch (NumberFormatException ignored) {
                // Legacy random-UUID modifiers still remove via upgradeId prefix.
            }
        }
        return maxIndex + 1;
    }

    private static String attributePath(Identifier attributeIdentifier) {
        return attributeIdentifier.getNamespace() + "." + attributeIdentifier.getPath();
    }
}
