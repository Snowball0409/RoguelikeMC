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

        EntityAttributeModifier attributeModifier = new EntityAttributeModifier(
                Identifier.of(RoguelikeMC.MOD_ID + ":" + id + "/" + UUID.randomUUID()),
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

        for (EntityAttributeModifier modifier : Objects.requireNonNull(player.getAttributeInstance(attributeEntry)).getModifiers()) {
            RoguelikeMC.LOGGER.debug("Removing attribute {} from upgrade effect", modifier.id());
            if (modifier.id().toString().startsWith(id.toString())) {
                Objects.requireNonNull(player.getAttributeInstance(attributeEntry)).removeModifier(modifier);
            }
        }
    }
}
