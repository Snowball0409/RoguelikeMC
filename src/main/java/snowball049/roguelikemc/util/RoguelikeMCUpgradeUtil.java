package snowball049.roguelikemc.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradePoolManager;

import java.util.*;
import java.util.stream.Collectors;

public class RoguelikeMCUpgradeUtil {
    public static void addUpgrade(RoguelikeMCUpgradeData upgrade, ServerPlayerEntity player) {
        RoguelikeMCPlayerData serverState = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
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
                case "attribute" -> RoguelikeMCUpgradeUtil.addUpgradeAttribute(player, upgrade.id(), action.value(), upgrade.isPermanent());
                case "effect" -> RoguelikeMCUpgradeUtil.applyUpgradeEffect(player, action.value(), upgrade.isPermanent());
                case "command" -> RoguelikeMCUpgradeUtil.applyCommandEffect(player, action.value(), upgrade.isPermanent());
                case "event" -> RoguelikeMCUpgradeUtil.applyUpgradeEvent(player, action.value(), upgrade.isPermanent());
                default -> {
                    RoguelikeMC.LOGGER.warn("Unknown action type: " + action.type());
                }
            }
        });
    }

    public static void applyJoinUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade) {
        upgrade.actions().forEach(action -> {
            switch (action.type()) {
                case "attribute" -> RoguelikeMCUpgradeUtil.addUpgradeAttribute(player, upgrade.id(), action.value(), upgrade.isPermanent());
                case "effect" -> RoguelikeMCUpgradeUtil.applyUpgradeEffect(player, action.value(), upgrade.isPermanent());
                case "event" -> RoguelikeMCUpgradeUtil.applyUpgradeEvent(player, action.value(), upgrade.isPermanent());
                case "command" -> {// Do nothing}
                }
                default -> {
                    RoguelikeMC.LOGGER.warn("Unknown action type: " + action.type());
                }
            }
        });
    }

    private static void applyCommandEffect(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        String command = value.getFirst();
        MinecraftServer server = player.getServer();

        if (server != null) {
            server.getCommandManager().executeWithPrefix(player.getCommandSource().withLevel(4).withSilent(), command);
        } else {
            RoguelikeMC.LOGGER.warn("Server is null");
        }
    }

    public static void addUpgradeAttribute(ServerPlayerEntity player, String id, List<String> value, boolean isPermanent) {
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
        EntityAttributeModifier attributeModifier;
        attributeModifier = new EntityAttributeModifier(Identifier.of(RoguelikeMC.MOD_ID + ":" + id + "/" + UUID.randomUUID()), amount, operation);

        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(attributeEntry, attributeModifier);

        player.getAttributes().addTemporaryModifiers(modifiers);
    }

    public static void applyUpgradeEffect(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        Identifier effectIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectIdentifier)
                .orElseThrow();//()->new IllegalStateException("Effect not found: "+effectIdentifier));


        if(!player.getWorld().isClient()) {
            player.addStatusEffect(new StatusEffectInstance(effectEntry, Integer.parseInt(value.get(1)), Integer.parseInt(value.get(2)), false, false, true));
        }
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
    public static void removeUpgradeAttribute(ServerPlayerEntity player, Identifier id, List<String> value) {
        Identifier attributeIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<EntityAttribute> attributeEntry = Registries.ATTRIBUTE.getEntry(attributeIdentifier)
                .orElseThrow();//(() -> new IllegalStateException("Attribute not found: " + attributeIdentifier));

        for (EntityAttributeModifier modifier : Objects.requireNonNull(player.getAttributeInstance(attributeEntry)).getModifiers()) {
            RoguelikeMC.LOGGER.debug("Removing attribute " + modifier.id() + " from upgrade effect");
            if (modifier.id().toString().startsWith(id.toString())) {
                Objects.requireNonNull(player.getAttributeInstance(attributeEntry)).removeModifier(modifier);
            }
        }
    }

    public static List<RoguelikeMCUpgradeData> getRandomUpgrades(RoguelikeMCPlayerData playerData) {
        List<RoguelikeMCUpgradeData> allUpgrades = playerData.activeUpgradePools.stream()
                .flatMap(poolId -> RoguelikeMCUpgradePoolManager.getUpgradesFromPool(poolId).stream())
                .distinct()
                .map(RoguelikeMCUpgradeManager::getUpgrade)
                .filter(Objects::nonNull)
                .toList();
        if (allUpgrades.isEmpty()) allUpgrades = RoguelikeMCUpgradeManager.getUpgrades().stream().toList();

        // Filter Unique Upgrades which player already owns
        Set<String> ownedUniqueIds = playerData.getAllUpgrades().stream()
                .filter(RoguelikeMCUpgradeData::isUnique)
                .map(RoguelikeMCUpgradeData::id)
                .collect(Collectors.toSet());

        List<RoguelikeMCUpgradeData> available = allUpgrades.stream()
                .filter(data -> !(data.isUnique() && ownedUniqueIds.contains(data.id())))
                .toList();

        if (available.isEmpty()) return List.of();

        // Randomly select by tier rate
        Map<String, Integer> tierWeights = Map.of(
                "legendary", 5,
                "epic", 15,
                "rare", 30,
                "common", 50
        );

        List<RoguelikeMCUpgradeData> weightedPool = new ArrayList<>();
        for (RoguelikeMCUpgradeData upgrade : available) {
            int weight = tierWeights.getOrDefault(upgrade.tier().toLowerCase(), 0);
            for (int i = 0; i < weight; i++) {
                weightedPool.add(upgrade);
            }
        }

        // Pick 3 random upgrades from the weighted pool with at least one non-unique
        List<RoguelikeMCUpgradeData> chosen = new ArrayList<>();
        Collections.shuffle(weightedPool);
        Set<String> selectedIds = new HashSet<>();

        int tries = 0;
        while (chosen.size() < 3 && tries < 1000) {
            tries++;
            RoguelikeMCUpgradeData candidate = weightedPool.get(new Random().nextInt(weightedPool.size()));
            if (selectedIds.contains(candidate.id())) continue;
            chosen.add(candidate);
            selectedIds.add(candidate.id());
        }

        if (chosen.stream().allMatch(RoguelikeMCUpgradeData::isUnique)) {
            // Replace one unique upgrade with a non-unique one
            List<RoguelikeMCUpgradeData> nonUniquePool = available.stream()
                    .filter(upg -> !upg.isUnique() && !selectedIds.contains(upg.id()))
                    .toList();

            if (!nonUniquePool.isEmpty()) {
                RoguelikeMCUpgradeData replacement = nonUniquePool.get(new Random().nextInt(nonUniquePool.size()));
                chosen.set(0, replacement);
            }
        }

        return chosen;
    }

    public static void removeUpgrade(ServerPlayerEntity player, Identifier id, RoguelikeMCUpgradeData.ActionData upgradeAction) {
        switch(upgradeAction.type()){
            case "attribute" -> RoguelikeMCUpgradeUtil.removeUpgradeAttribute(player, id, upgradeAction.value());
            case "effect" -> RoguelikeMCUpgradeUtil.removeUpgradeEffect(player, upgradeAction.value());
            case "command" -> {
            }
            case "event" -> RoguelikeMCUpgradeUtil.removeUpgradeEvent(player, upgradeAction.value());
            default -> {
                RoguelikeMC.LOGGER.warn("Unexpected value: " + upgradeAction.type());
            }
        }
    }

    private static void applyUpgradeEvent(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        String eventType = value.getFirst();
        switch(eventType){
            case "allow_creative_flying" -> {
                player.getAbilities().allowFlying = true;
                player.sendAbilitiesUpdate();
            }
            case "keep_equipment_after_death" -> {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                playerData.keepEquipmentAfterDeath = true;
            }
            case "one_last_chance" -> {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                playerData.revive = true;
            }
            case "set_equipment" -> {
                try {
                    int slotIndex = Integer.parseInt(value.get(1));
                    String nbtString = value.get(2);
                    NbtCompound nbt = !nbtString.isEmpty()?StringNbtReader.parse(nbtString):new NbtCompound();
                    if (!player.getInventory().armor.get(slotIndex).isEmpty()) {
                        if(!player.getInventory().armor.get(slotIndex).getItem().equals(ItemStack.fromNbtOrEmpty(player.getWorld().getRegistryManager(), nbt).getItem())){
                            player.dropItem(player.getInventory().armor.get(slotIndex), false);
                            player.sendMessage(Text.literal("You have been dropped your equipment!"), false);
                        }
                    }
                    player.getInventory().armor.set(slotIndex, ItemStack.fromNbtOrEmpty(player.getWorld().getRegistryManager(), nbt));
                } catch (CommandSyntaxException e) {
                    RoguelikeMC.LOGGER.warn(e.getClass() + ":" + e.getMessage());
                }
            }
            case "effect_mobs" -> {
                try {
                    Identifier effectIdentifier = Identifier.tryParse(value.get(1));
                    RegistryEntry.Reference<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectIdentifier)
                            .orElseThrow();
                    World world = player.getWorld();
                    if (world.isClient()) return;
                    Box area = new Box(player.getBlockPos()).expand(Double.parseDouble(value.get(3)));
                    for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, area, e -> !e.isPlayer() && e instanceof HostileEntity)) {
                        entity.addStatusEffect(new StatusEffectInstance(effectEntry, 40, Integer.parseInt(value.get(2)), false, true, false));
                    }
                } catch (Exception e) {
                    RoguelikeMC.LOGGER.warn(e.getClass() + ":" + e.getMessage());
                }
            }
            case "add_loot_table", "provoked" -> {
            }
            default -> {
                RoguelikeMC.LOGGER.warn("Unexpected eventType value: " + eventType);
            }
        }
    }

    private static void removeUpgradeEvent(ServerPlayerEntity player, List<String> value) {
        String eventType = value.getFirst();
        switch(eventType){
            case "allow_creative_flying" -> {
                player.getAbilities().allowFlying = false;
                player.sendAbilitiesUpdate();
            }
            case "keep_equipment_after_death" -> {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                playerData.keepEquipmentAfterDeath = false;
            }
            case "one_last_chance" -> {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                playerData.revive = false;
            }
            case "set_equipment" -> {
                int slotIndex = Integer.parseInt(value.get(1));
                player.getInventory().armor.set(slotIndex, ItemStack.EMPTY);
            }
            case "effect_mobs", "add_loot_table", "provoked" -> {
            }
            default -> {
                RoguelikeMC.LOGGER.warn("Unexpected eventType value: " + eventType);
            }
        }
    }

    public static void tickSetEquipment(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.temporaryUpgrades.forEach(upgrade -> {
                upgrade.actions().forEach(action -> {
                    if (action.type().equals("event") && action.value().get(0).equals("set_equipment") && action.value().get(2).isEmpty()) {
                        RoguelikeMCUpgradeUtil.applyUpgradeEvent(player, action.value(), false);
                    }
                });
            });
            playerData.permanentUpgrades.forEach(upgrade -> {
                upgrade.actions().forEach(action -> {
                    if (action.type().equals("event") && action.value().get(0).equals("set_equipment") && action.value().get(2).isEmpty()) {
                        RoguelikeMCUpgradeUtil.applyUpgradeEvent(player, action.value(), true);
                    }
                });
            });
        });
    }

    public static void tickEffectToMobEntity(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.temporaryUpgrades.forEach(upgrade -> {
                upgrade.actions().forEach(action -> {
                    if (action.type().equals("event") && action.value().getFirst().equals("effect_mobs")) {
                        RoguelikeMCUpgradeUtil.applyUpgradeEvent(player, action.value(), false);
                    }
                });
            });
            playerData.permanentUpgrades.forEach(upgrade -> {
                upgrade.actions().forEach(action -> {
                    if (action.type().equals("event") && action.value().getFirst().equals("effect_mobs")) {
                        RoguelikeMCUpgradeUtil.applyUpgradeEvent(player, action.value(), true);
                    }
                });
            });
        });
    }

    public static void tickEnableCreativeFly(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.temporaryUpgrades.forEach(upgrade -> {
                upgrade.actions().forEach(action -> {
                    if (action.type().equals("event") && action.value().getFirst().equals("allow_creative_flying")) {
                        RoguelikeMCUpgradeUtil.applyUpgradeEvent(player, action.value(), false);
                    }
                });
            });
            playerData.permanentUpgrades.forEach(upgrade -> {
                upgrade.actions().forEach(action -> {
                    if (action.type().equals("event") && action.value().getFirst().equals("allow_creative_flying")) {
                        RoguelikeMCUpgradeUtil.applyUpgradeEvent(player, action.value(), true);
                    }
                });
            });
        });
    }
}
