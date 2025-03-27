package snowball049.roguelikemc.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;

import java.util.*;

public class RoguelikeMCUpgradeUtil {
    public static void handleUpgrade(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade, ServerPlayer player) {
        RoguelikeMCPlayerData serverState = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        if (upgrade.is_permanent()) {
            serverState.permanentUpgrades.add(upgrade);
        }else{
            serverState.temporaryUpgrades.add(upgrade);
        }
        RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrade);
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(serverState.permanentUpgrades));
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(serverState.temporaryUpgrades));
        if(!player.level().isClientSide()){
            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, player.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public static void applyUpgrade(ServerPlayer player, RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade) {
        upgrade.action().forEach(action -> {
            if (action.type().equals("attribute")) {
                RoguelikeMCUpgradeUtil.addUpgradeAttribute(player, action.value(), upgrade.is_permanent());
            } else if (action.type().equals("effect")) {
                RoguelikeMCUpgradeUtil.applyUpgradeEffect(player, action.value(), upgrade.is_permanent());
            }
        });
    }
    public static void addUpgradeAttribute(ServerPlayer player, List<String> value, boolean isPermanent) {
        RoguelikeMC.LOGGER.info("value: "+value);
        ResourceLocation attributeIdentifier = ResourceLocation.tryParse(value.getFirst());
        Holder.Reference<Attribute> attributeEntry = BuiltInRegistries.ATTRIBUTE.getHolder(attributeIdentifier)
                .orElseThrow(() -> new IllegalStateException("Attribute not found: " + attributeIdentifier));
        double amount = Double.parseDouble(value.get(1));
        AttributeModifier.Operation operation = switch (value.get(2)) {
            case "add_value" -> AttributeModifier.Operation.ADD_VALUE;
            case "add_multiplied_base" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case "add_multiplied_total" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            default -> throw new IllegalStateException("Unexpected value: " + value.get(2));
        };
        AttributeModifier attributeModifier;
        if (isPermanent) {
            attributeModifier = new AttributeModifier(ResourceLocation.parse(RoguelikeMC.MOD_ID + ":tmp/" + UUID.randomUUID()), amount, operation);
        }else{
            attributeModifier = new AttributeModifier(ResourceLocation.parse(RoguelikeMC.MOD_ID + ":" + UUID.randomUUID()), amount, operation);
        }

        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(attributeEntry, attributeModifier);

        player.getAttributes().addTransientAttributeModifiers(modifiers);
    }
    public static void removeUpgradeAttribute(ServerPlayer player, List<String> value) {
        ResourceLocation attributeIdentifier = ResourceLocation.tryParse(value.getFirst());
        Holder.Reference<Attribute> attributeEntry = BuiltInRegistries.ATTRIBUTE.getHolder(attributeIdentifier)
                .orElseThrow(() -> new IllegalStateException("Attribute not found: " + attributeIdentifier));

        for (AttributeModifier modifier : Objects.requireNonNull(player.getAttribute(attributeEntry)).getModifiers()) {
            if (modifier.id().toString().startsWith(RoguelikeMC.MOD_ID+":tmp/")) {
                Objects.requireNonNull(player.getAttribute(attributeEntry)).removeModifier(modifier);
            }
        }
    }
    public static void applyUpgradeEffect(ServerPlayer player, List<String> value, boolean isPermanent) {
        ResourceLocation effectIdentifier = ResourceLocation.tryParse(value.getFirst());
        Holder.Reference<MobEffect> effectEntry = BuiltInRegistries.MOB_EFFECT.getHolder(effectIdentifier)
                .orElseThrow(()->new IllegalStateException("Effect not found: "+effectIdentifier));


        if(!player.level().isClientSide()) {
            player.addEffect(new MobEffectInstance(effectEntry, Integer.parseInt(value.get(1)), Integer.parseInt(value.get(2)), false, false, true));
        }
    }
    public static void sendPointMessage(ServerPlayer player, int amount) {
        player.sendSystemMessage(Component.nullToEmpty("You have been granted "+ amount +" upgrade point!"));
    }
}
