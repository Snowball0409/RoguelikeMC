package snowball049.roguelikemc.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;

import java.util.*;

public class RoguelikeMCUpgradeUtil {
    public static void handleUpgrade(RoguelikeMCUpgradeData upgrade, ServerPlayerEntity player) {
        RoguelikeMCPlayerData serverState = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        serverState.upgradePoints --;
        if (upgrade.isPermanent()) {
            serverState.permanentUpgrades.add(upgrade);
        }else{
            serverState.temporaryUpgrades.add(upgrade);
        }
        RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrade);
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(true, serverState.permanentUpgrades));
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(false, serverState.temporaryUpgrades));
        if(!player.getWorld().isClient()){
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), 1.0F, 1.0F);
        }
    }

    public static void applyUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade) {
        upgrade.actions().forEach(action -> {
            switch (action.type()) {
                case "attribute" -> RoguelikeMCUpgradeUtil.addUpgradeAttribute(player, action.value(), upgrade.isPermanent());
                case "effect" -> RoguelikeMCUpgradeUtil.applyUpgradeEffect(player, action.value(), upgrade.isPermanent());
                case "command" -> RoguelikeMCUpgradeUtil.applyCommandEffect(player, action.value(), upgrade.isPermanent());
                default -> throw new IllegalStateException("Unexpected value: " + action.type());
            }
        });
    }

    private static void applyCommandEffect(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        String command = value.getFirst();
        MinecraftServer server = player.getServer();

        if (server != null) {
            server.getCommandManager().executeWithPrefix(player.getCommandSource().withLevel(4), command);
        } else {
            throw new IllegalStateException("Server is null");
        }
    }

    public static void addUpgradeAttribute(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
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

    public static void applyUpgradeEffect(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        Identifier effectIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectIdentifier)
                .orElseThrow(()->new IllegalStateException("Effect not found: "+effectIdentifier));


        if(!player.getWorld().isClient()) {
            player.addStatusEffect(new StatusEffectInstance(effectEntry, Integer.parseInt(value.get(1)), Integer.parseInt(value.get(2)), false, false, true));
        }
    }
    public static void sendPointMessage(ServerPlayerEntity player, int amount) {
        player.sendMessage(Text.of("You have been granted "+ amount +" upgrade point!"));
    }

    public static void tickInfiniteEffects(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.temporaryUpgrades.forEach(upgrade -> {
                upgrade.actions().forEach(action -> {
                    if (action.type().equals("effect") && action.value().get(1).equals("-1")) {
                        RoguelikeMCUpgradeUtil.applyUpgradeEffect(player, action.value(), false);
                    }
                });
            });
            playerData.permanentUpgrades.forEach(upgrade -> {
                upgrade.actions().forEach(action -> {
                    if (action.type().equals("effect") && action.value().get(1).equals("-1")) {
                        RoguelikeMCUpgradeUtil.applyUpgradeEffect(player, action.value(), true);
                    }
                });
            });
        });
    }

    public static void removeUpgradeEffect(ServerPlayerEntity player, List<String> value) {
        player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(value.getFirst())).orElseThrow());
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
}
