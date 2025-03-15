package snowball049.roguelikemc.command;

public class RoguelikeMCCommands {
//    private static final String[] LEVEL = {"common", "rare", "epic", "legendary", "fate"};
//
//    public static int executeCommandUpgrade(CommandContext<ServerCommandSource> context) {
//        String tier = context.getArgument("tier", String.class);
//        RoguelikeMCConfig.UpgradeConfig config = RoguelikeMCConfig.loadConfig();
//
//        if (config != null) {
//            List<RoguelikeMCConfig.UpgradeOption> selectedUpgrades;
//            switch (tier) {
//                case "common":
//                    Collections.shuffle(config.commonUpgrade);
//                    selectedUpgrades = config.commonUpgrade.subList(0, Math.min(3, config.commonUpgrade.size()));
//                    break;
//                case "rare":
//                    Collections.shuffle(config.rareUpgrade);
//                    selectedUpgrades = config.rareUpgrade.subList(0, Math.min(3, config.rareUpgrade.size()));
//                    break;
//                case "legendary":
//                    Collections.shuffle(config.legendaryUpgrade);
//                    selectedUpgrades = config.legendaryUpgrade.subList(0, Math.min(3, config.legendaryUpgrade.size()));
//                    break;
//                default:
//                    context.getSource().sendFeedback(() -> Text.literal("Invalid Tier: " + tier), false);
//                    return 1;
//            }
//            ServerPlayerEntity player = context.getSource().getPlayer();
//            if (player != null) {
//                player.sendMessage(Text.literal("§aChoose an Upgrade："), false);
//                for (RoguelikeMCConfig.UpgradeOption upgrade : selectedUpgrades) {
//                    Text upgradeText = applyUpgradeMessage(player, upgrade);
//                    player.sendMessage(upgradeText, false);
//                }
//            }
//        } else {
//            context.getSource().sendFeedback(() -> Text.literal("Config not found"), false);
//            return 0;
//        }
//        return 1;
//    }
//
//    public static CompletableFuture<Suggestions> suggestUpgrades(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
//        for (String level : LEVEL) {
//            builder.suggest(level);
//        }
//        return builder.buildFuture();
//    }
//
//    public static Text applyUpgradeMessage(ServerPlayerEntity player, RoguelikeMCConfig.UpgradeOption upgrade) {
//        List<String> commands = new ArrayList<>();
//        Text text = Text.literal("§6> " + upgrade.name);
//        for (RoguelikeMCConfig.UpgradeAction action : upgrade.action) {
//            switch (action.type) {
//                case "attribute":
//                    commands.addAll(generateAttributeCommand(player, action.value));
//                    break;
//                case "effect":
//                    commands.add(generateEffectCommand(player, action.value));
//                    break;
//                default:
//                    player.sendMessage(Text.literal("§cUnknown action type: " + action.type), false);
//            }
//        }
//        // 將所有命令用分號合併為一個字符串
//        for (String command : commands) {
//            text = text.copy().styled(style -> style
//                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
//                    .withBold(true));
//        }
//        return text;
//    }
//
//    private static List<String> generateAttributeCommand(ServerPlayerEntity player, List<String> value) {
//        // 檢查輸入值有效性
//        if (value.size() < 3) {
//            LOGGER.error("Invalid attribute command parameters: {}", value);
//            return Collections.emptyList();
//        }
//
//        Identifier attributeId = Identifier.tryParse(value.get(0));
//        if (attributeId == null) {
//            LOGGER.error("Invalid attribute ID: {}", value.get(0));
//            return Collections.emptyList();
//        }
//
//        double amount;
//        try {
//            amount = Double.parseDouble(value.get(1));
//        } catch (NumberFormatException e) {
//            LOGGER.error("Invalid amount value: {}", value.get(1));
//            return Collections.emptyList();
//        }
//
//        String attributeModifierAugment = value.get(2);
//        RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attribute = Registries.ATTRIBUTE.getEntry(attributeId).orElse(null);
//        if (attribute == null) {
//            LOGGER.error("Attribute not found: {}", attributeId);
//            return Collections.emptyList();
//        }
//
//        // 生成 modifier ID
//        String modifierId = MOD_ID + ":tmp_" + UUID.randomUUID();
//
//        // 檢查現有 modifier
////        Set<EntityAttributeModifier> modifiers = Objects.requireNonNull(player.getAttributeInstance(attribute)).getModifiers();
////        boolean hasExistingModifier = modifiers.stream()
////                .anyMatch(modifier -> modifier.id().toString().equals(modifierId));
//
//        List<String> commandList = new ArrayList<>();
////        if (hasExistingModifier) {
////            commandList.add(String.format(
////                    "/attribute %s %s modifier remove %s",
////                    player.getName().getString(),
////                    attributeId,
////                    modifierId
////            ));
////        }
//
//        commandList.add(String.format(
//                "/attribute %s %s modifier add %s %.2f %s",
//                player.getName().getString(),
//                attributeId,
//                modifierId,
//                amount,
//                attributeModifierAugment
//        ));
//
//        LOGGER.info("Generated commands: {}", commandList);
//        return commandList;
//    }
//
//    private static String generateEffectCommand(ServerPlayerEntity player, List<String> value) {
//        if (value.size() < 2) {
//            LOGGER.error("Invalid effect command parameters: {}", value);
//            return "";
//        }
//
//        String effectId = value.get(0);
//        int duration;
//        try {
//            duration = Integer.parseInt(value.get(1));
//        } catch (NumberFormatException e) {
//            LOGGER.error("Invalid duration value: {}", value.get(1));
//            return "";
//        }
//        if (duration <= 0) duration = 999999; // 使用數字表示無限
//
//        int amplifier = value.size() > 2 ? Integer.parseInt(value.get(2)) : 0;
//        return String.format("/effect give %s %s %d %d", player.getName().getString(), effectId, duration, amplifier);
//    }
}