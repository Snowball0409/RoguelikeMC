package snowball049.roguelikemc.upgrade.action;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import net.minecraft.entity.attribute.EntityAttributeInstance;

import java.util.List;
import java.util.Objects;

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
        Identifier attributeIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<EntityAttribute> attributeEntry = Registries.ATTRIBUTE.getEntry(attributeIdentifier)
                .orElseThrow();
        double amount = Double.parseDouble(value.get(1));
        EntityAttributeModifier.Operation operation = switch (value.get(2)) {
            case "add_value" -> EntityAttributeModifier.Operation.ADD_VALUE;
            case "add_multiplied_base" -> EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case "add_multiplied_total" -> EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            default -> EntityAttributeModifier.Operation.ADD_VALUE;
        };

        EntityAttributeInstance instance = Objects.requireNonNull(player.getAttributeInstance(attributeEntry));
        int stackIndex = nextStackIndex(instance, id, attributeIdentifier);
        Identifier modifierId = Identifier.of(
                RoguelikeMC.MOD_ID,
                id + "/" + attributePath(attributeIdentifier) + "/" + stackIndex
        );

        EntityAttributeModifier attributeModifier = new EntityAttributeModifier(
                modifierId,
                amount,
                operation
        );

        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(attributeEntry, attributeModifier);
        player.getAttributes().addTemporaryModifiers(modifiers);
    }

    private static void removeAttribute(net.minecraft.server.network.ServerPlayerEntity player, Identifier id, List<String> value) {
        Identifier attributeIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<EntityAttribute> attributeEntry = Registries.ATTRIBUTE.getEntry(attributeIdentifier)
                .orElseThrow();

        EntityAttributeInstance instance = Objects.requireNonNull(player.getAttributeInstance(attributeEntry));
        String prefix = id.toString() + "/";
        for (EntityAttributeModifier modifier : List.copyOf(instance.getModifiers())) {
            RoguelikeMC.LOGGER.debug("Removing attribute {} from upgrade effect", modifier.id());
            if (modifier.id().toString().startsWith(prefix)) {
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
            String modifierId = modifier.id().toString();
            if (!modifierId.startsWith(prefix)) {
                continue;
            }
            try {
                maxIndex = Math.max(maxIndex, Integer.parseInt(modifierId.substring(prefix.length())));
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
