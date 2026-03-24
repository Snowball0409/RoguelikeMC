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
import net.minecraft.entity.passive.CatVariant;
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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RoguelikeMCUpgradeUtil {
    public static void addUpgrade(RoguelikeMCUpgradeData upgrade, ServerPlayerEntity player) {
        RoguelikeMCPlayerData serverState = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        if (upgrade.isPermanent()) {
            serverState.permanentUpgrades.add(upgrade);
        } else {
            serverState.temporaryUpgrades.add(upgrade);
        }
        applyUpgrade(player, upgrade);
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(true, serverState.permanentUpgrades));
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(false, serverState.temporaryUpgrades));
        if (!player.getWorld().isClient()) {
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), 1.0F, 1.0F);
        }
    }

    public static void applyUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade) {
        forEachUpgradeAction(upgrade, action -> applyUpgradeAction(player, upgrade, action));
    }

    public static void applyJoinUpgrade(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade) {
        forEachUpgradeAction(upgrade, action -> applyJoinUpgradeAction(player, upgrade, action));
    }

    private static int[] uuidToIntArray(UUID uuid) {
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        return new int[]{
                (int) (most >> 32),
                (int) most,
                (int) (least >> 32),
                (int) least
        };
    }

    private static void applyCommandEffect(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        String command = value.getFirst();
        MinecraftServer server = player.getServer();

        if (server == null) {
            RoguelikeMC.LOGGER.warn("Server is null");
            return;
        }

        if (Boolean.parseBoolean(value.getLast()) && command.startsWith("summon")) {
            int[] uuidInts = uuidToIntArray(player.getUuid());
            int nbtStart = command.indexOf('{');
            if (nbtStart != -1) {
                String beforeNBT = command.substring(0, nbtStart + 1);
                String afterNBT = command.substring(nbtStart + 1);
                String ownerTag = String.format("Owner:[I;%d,%d,%d,%d],", uuidInts[0], uuidInts[1], uuidInts[2], uuidInts[3]);
                command = beforeNBT + ownerTag + afterNBT;
            } else {
                String ownerTag = String.format("{Owner:[I;%d,%d,%d,%d]}", uuidInts[0], uuidInts[1], uuidInts[2], uuidInts[3]);
                command = command + " " + ownerTag;
            }
        }

        String summonedEntityType = getSummonedEntityType(command);
        if ("cat".equals(summonedEntityType)) {
            command = applyCatVariant(command, server);
        } else if ("horse".equals(summonedEntityType)) {
            command = applyHorseVariant(command, player);
        }

        server.getCommandManager().executeWithPrefix(player.getCommandSource().withLevel(4).withSilent(), command);
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

        EntityAttributeModifier attributeModifier = new EntityAttributeModifier(
                Identifier.of(RoguelikeMC.MOD_ID + ":" + id + "/" + UUID.randomUUID()),
                amount,
                operation
        );

        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(attributeEntry, attributeModifier);
        player.getAttributes().addTemporaryModifiers(modifiers);
    }

    public static void applyUpgradeEffect(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        Identifier effectIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectIdentifier)
                .orElseThrow();

        if (!player.getWorld().isClient()) {
            player.addStatusEffect(new StatusEffectInstance(effectEntry, Integer.parseInt(value.get(1)), Integer.parseInt(value.get(2)), false, false, true));
        }
    }

    public static void tickInfiniteEffects(MinecraftServer minecraftServer) {
        forEachPlayerAction(
                minecraftServer,
                action -> action.type().equals("effect") && action.value().get(1).equals("-1"),
                (player, upgrade, action) -> applyUpgradeEffect(player, action.value(), upgrade.isPermanent())
        );
    }

    public static void removeUpgradeEffect(ServerPlayerEntity player, List<String> value) {
        player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(value.getFirst())).orElseThrow());
    }

    public static void removeUpgradeAttribute(ServerPlayerEntity player, Identifier id, List<String> value) {
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

    public static List<RoguelikeMCUpgradeData> getRandomUpgrades(RoguelikeMCPlayerData playerData) {
        List<RoguelikeMCUpgradeData> allUpgrades = playerData.activeUpgradePools.stream()
                .flatMap(poolId -> RoguelikeMCUpgradePoolManager.getUpgradesFromPool(poolId).stream())
                .distinct()
                .map(RoguelikeMCUpgradeManager::getUpgrade)
                .filter(Objects::nonNull)
                .toList();
        if (allUpgrades.isEmpty()) {
            allUpgrades = RoguelikeMCUpgradeManager.getUpgrades().stream().toList();
        }

        Set<String> ownedUniqueIds = playerData.getAllUpgrades().stream()
                .filter(RoguelikeMCUpgradeData::isUnique)
                .map(RoguelikeMCUpgradeData::id)
                .collect(Collectors.toSet());

        List<RoguelikeMCUpgradeData> available = allUpgrades.stream()
                .filter(data -> !(data.isUnique() && ownedUniqueIds.contains(data.id())))
                .toList();

        if (available.isEmpty()) {
            return List.of();
        }

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

        List<RoguelikeMCUpgradeData> chosen = new ArrayList<>();
        Set<String> selectedIds = new HashSet<>();
        Random random = new Random();

        int tries = 0;
        while (chosen.size() < 3 && tries < 1000) {
            tries++;
            RoguelikeMCUpgradeData candidate = weightedPool.get(random.nextInt(weightedPool.size()));
            if (selectedIds.contains(candidate.id())) {
                continue;
            }
            chosen.add(candidate);
            selectedIds.add(candidate.id());
        }

        if (chosen.stream().allMatch(RoguelikeMCUpgradeData::isUnique)) {
            List<RoguelikeMCUpgradeData> nonUniquePool = available.stream()
                    .filter(upg -> !upg.isUnique() && !selectedIds.contains(upg.id()))
                    .toList();

            if (!nonUniquePool.isEmpty()) {
                chosen.set(0, nonUniquePool.get(random.nextInt(nonUniquePool.size())));
            }
        }

        return chosen;
    }

    public static void removeUpgrade(ServerPlayerEntity player, Identifier id, RoguelikeMCUpgradeData.ActionData upgradeAction) {
        switch (upgradeAction.type()) {
            case "attribute" -> removeUpgradeAttribute(player, id, upgradeAction.value());
            case "effect" -> removeUpgradeEffect(player, upgradeAction.value());
            case "command" -> {
            }
            case "event" -> removeUpgradeEvent(player, upgradeAction.value());
            default -> RoguelikeMC.LOGGER.warn("Unexpected value: {}", upgradeAction.type());
        }
    }

    private static void applyUpgradeEvent(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        String eventType = value.getFirst();
        switch (eventType) {
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
                    NbtCompound nbt = !nbtString.isEmpty() ? StringNbtReader.parse(nbtString) : new NbtCompound();
                    if (!player.getInventory().armor.get(slotIndex).isEmpty()
                            && !player.getInventory().armor.get(slotIndex).getItem().equals(ItemStack.fromNbtOrEmpty(player.getWorld().getRegistryManager(), nbt).getItem())) {
                        player.dropItem(player.getInventory().armor.get(slotIndex), false);
                        player.sendMessage(Text.translatable("message.roguelikemc.drop_equipment"), false);
                    }
                    player.getInventory().armor.set(slotIndex, ItemStack.fromNbtOrEmpty(player.getWorld().getRegistryManager(), nbt));
                } catch (CommandSyntaxException e) {
                    RoguelikeMC.LOGGER.warn("{}:{}", e.getClass(), e.getMessage());
                }
            }
            case "effect_mobs" -> {
                try {
                    Identifier effectIdentifier = Identifier.tryParse(value.get(1));
                    RegistryEntry.Reference<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectIdentifier)
                            .orElseThrow();
                    World world = player.getWorld();
                    if (world.isClient()) {
                        return;
                    }
                    Box area = new Box(player.getBlockPos()).expand(Double.parseDouble(value.get(3)));
                    for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, area, e -> !e.isPlayer() && e instanceof HostileEntity)) {
                        entity.addStatusEffect(new StatusEffectInstance(effectEntry, 40, Integer.parseInt(value.get(2)), false, true, false));
                    }
                } catch (Exception e) {
                    RoguelikeMC.LOGGER.warn("{}:{}", e.getClass(), e.getMessage());
                }
            }
            case "add_loot_table", "provoked" -> {
            }
            default -> RoguelikeMC.LOGGER.warn("Unexpected eventType value: {}", eventType);
        }
    }

    private static void removeUpgradeEvent(ServerPlayerEntity player, List<String> value) {
        String eventType = value.getFirst();
        switch (eventType) {
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
            default -> RoguelikeMC.LOGGER.warn("Unexpected eventType value: {}", eventType);
        }
    }

    public static void tickSetEquipment(MinecraftServer minecraftServer) {
        forEachPlayerAction(
                minecraftServer,
                action -> action.type().equals("event")
                        && action.value().getFirst().equals("set_equipment")
                        && action.value().get(2).isEmpty(),
                (player, upgrade, action) -> applyUpgradeEvent(player, action.value(), upgrade.isPermanent())
        );
    }

    public static void tickEffectToMobEntity(MinecraftServer minecraftServer) {
        forEachPlayerAction(
                minecraftServer,
                action -> action.type().equals("event") && action.value().getFirst().equals("effect_mobs"),
                (player, upgrade, action) -> applyUpgradeEvent(player, action.value(), upgrade.isPermanent())
        );
    }

    public static void tickEnableCreativeFly(MinecraftServer minecraftServer) {
        forEachPlayerAction(
                minecraftServer,
                action -> action.type().equals("event") && action.value().getFirst().equals("allow_creative_flying"),
                (player, upgrade, action) -> applyUpgradeEvent(player, action.value(), upgrade.isPermanent())
        );
    }

    private static void applyUpgradeAction(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade, RoguelikeMCUpgradeData.ActionData action) {
        switch (action.type()) {
            case "attribute" -> addUpgradeAttribute(player, upgrade.id(), action.value(), upgrade.isPermanent());
            case "effect" -> applyUpgradeEffect(player, action.value(), upgrade.isPermanent());
            case "command" -> applyCommandEffect(player, action.value(), upgrade.isPermanent());
            case "event" -> applyUpgradeEvent(player, action.value(), upgrade.isPermanent());
            default -> RoguelikeMC.LOGGER.warn("Unknown action type: {}", action.type());
        }
    }

    private static void applyJoinUpgradeAction(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade, RoguelikeMCUpgradeData.ActionData action) {
        switch (action.type()) {
            case "attribute" -> addUpgradeAttribute(player, upgrade.id(), action.value(), upgrade.isPermanent());
            case "event" -> applyJoinUpgradeEvent(player, action.value(), upgrade.isPermanent());
            case "effect", "command" -> {
            }
            default -> RoguelikeMC.LOGGER.warn("Unknown action type: {}", action.type());
        }
    }

    private static void applyJoinUpgradeEvent(ServerPlayerEntity player, List<String> value, boolean isPermanent) {
        if (value.isEmpty()) {
            return;
        }

        if ("allow_creative_flying".equals(value.getFirst())) {
            player.getAbilities().allowFlying = true;
            if (!player.isOnGround()) {
                player.getAbilities().flying = true;
            }
            player.sendAbilitiesUpdate();
        }
    }

    private static void forEachUpgradeAction(RoguelikeMCUpgradeData upgrade, Consumer<RoguelikeMCUpgradeData.ActionData> consumer) {
        upgrade.actions().forEach(consumer);
    }

    private static void forEachPlayerAction(
            MinecraftServer minecraftServer,
            Predicate<RoguelikeMCUpgradeData.ActionData> filter,
            PlayerUpgradeActionConsumer consumer
    ) {
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            forEachUpgradeCollection(player, playerData.temporaryUpgrades, filter, consumer);
            forEachUpgradeCollection(player, playerData.permanentUpgrades, filter, consumer);
        });
    }

    private static void forEachUpgradeCollection(
            ServerPlayerEntity player,
            Collection<RoguelikeMCUpgradeData> upgrades,
            Predicate<RoguelikeMCUpgradeData.ActionData> filter,
            PlayerUpgradeActionConsumer consumer
    ) {
        upgrades.forEach(upgrade -> forEachUpgradeAction(upgrade, action -> {
            if (filter.test(action)) {
                consumer.accept(player, upgrade, action);
            }
        }));
    }

    private static String getSummonedEntityType(String command) {
        String[] parts = command.split(" ");
        return parts.length > 1 ? parts[1] : "";
    }

    private static String applyCatVariant(String command, MinecraftServer server) {
        Optional<RegistryEntry.Reference<CatVariant>> variantEntry = Registries.CAT_VARIANT.getRandom(server.getOverworld().getRandom());
        if (variantEntry.isEmpty()) {
            return command;
        }

        String variant = Objects.requireNonNull(Registries.CAT_VARIANT.getId(variantEntry.get().value())).toString();
        return appendNbtTag(command, String.format("variant:\"%s\"", variant));
    }

    private static String applyHorseVariant(String command, ServerPlayerEntity player) {
        int color = player.getRandom().nextInt(7);
        int style = player.getRandom().nextInt(5);
        int variant = color | (style << 8);
        return appendNbtTag(command, String.format("Variant:%d", variant));
    }

    private static String appendNbtTag(String command, String tag) {
        int nbtStart = command.indexOf('{');
        if (nbtStart != -1) {
            String beforeNBT = command.substring(0, nbtStart + 1);
            String afterNBT = command.substring(nbtStart + 1);
            return afterNBT.isEmpty() ? beforeNBT + tag + "}" : beforeNBT + tag + "," + afterNBT;
        }

        return command + " {" + tag + "}";
    }

    @FunctionalInterface
    private interface PlayerUpgradeActionConsumer {
        void accept(ServerPlayerEntity player, RoguelikeMCUpgradeData upgrade, RoguelikeMCUpgradeData.ActionData action);
    }
}
