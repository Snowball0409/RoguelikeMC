package snowball049.roguelikemc.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.config.RoguelikeMCConfig;

import java.util.*;

public class RoguelikeMCUpgradeUtil {
    public static void applyUpgrade(ServerPlayerEntity player, RoguelikeMCConfig.RogueLikeMCUpgradeConfig upgrades) {
        upgrades.action().forEach(action -> {
            if (action.type().equals("attribute")) {
                RoguelikeMCUpgradeUtil.addUpgradeAttribute(player, action.value(), upgrades.is_permanent());
            } else if (action.type().equals("effect")) {
                RoguelikeMCUpgradeUtil.applyUpgradeEffect(player, action.value(), upgrades.is_permanent());
            }
        });
    }
    public static void addUpgradeAttribute(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        RoguelikeMC.LOGGER.info("value: "+value);
        Identifier attributeIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<EntityAttribute> attributeEntry = Registries.ATTRIBUTE.getEntry(attributeIdentifier)
                .orElseThrow(() -> new IllegalStateException("Attribute not found: " + attributeIdentifier));
        double amount = Double.parseDouble(value.get(1));
        EntityAttributeModifier.Operation operation = switch (value.get(2)) {
            case "add_value" -> EntityAttributeModifier.Operation.ADD_VALUE;
            case "add_multiplied_base" -> EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case "add_multiplied_total" -> EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            default -> throw new IllegalStateException("Unexpected value: " + value.get(2));
        };
        EntityAttributeModifier attributeModifier;
        if (isPermanent) {
            attributeModifier = new EntityAttributeModifier(Identifier.of(RoguelikeMC.MOD_ID + ":tmp/" + UUID.randomUUID()), amount, operation);
        }else{
            attributeModifier = new EntityAttributeModifier(Identifier.of(RoguelikeMC.MOD_ID + ":" + UUID.randomUUID()), amount, operation);
        }

        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(attributeEntry, attributeModifier);

        player.getAttributes().addTemporaryModifiers(modifiers);
    }
    public static void removeUpgradeAttribute(ServerPlayerEntity player, List<String> value) {
        Identifier attributeIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<EntityAttribute> attributeEntry = Registries.ATTRIBUTE.getEntry(attributeIdentifier)
                .orElseThrow(() -> new IllegalStateException("Attribute not found: " + attributeIdentifier));

        for (EntityAttributeModifier modifier : Objects.requireNonNull(player.getAttributeInstance(attributeEntry)).getModifiers()) {
            if (modifier.id().toString().startsWith(RoguelikeMC.MOD_ID+":tmp/")) {
                Objects.requireNonNull(player.getAttributeInstance(attributeEntry)).removeModifier(modifier);
            }
        }
    }
    public static void applyUpgradeEffect(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        Identifier effectIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectIdentifier)
                .orElseThrow(()->new IllegalStateException("Effect not found: "+effectIdentifier));


        if(!player.getWorld().isClient()) {
            player.addStatusEffect(new StatusEffectInstance(effectEntry, Integer.parseInt(value.get(1)), Integer.parseInt(value.get(2)), false, false, true));
        }
    }
}
