package snowball049.roguelikemc.gui;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMCClient;
import snowball049.roguelikemc.data.RoguelikeMCClientData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class RoguelikeMCScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.tryParse("roguelikemc", "textures/gui/upgrade_bg_2.png");

    private static final int GUI_WIDTH = 360;
    private static final int GUI_HEIGHT = 210;
    private static final int CONTENT_PADDING = 15;
    private static final int BOTTOM_ROW_HEIGHT = 20;
    private static final int SECTION_SPACING = 15;
    private static final int SECTION_WIDTH = (GUI_WIDTH - 3 * SECTION_SPACING) / 2;

    private ButtonWidget openDrawScreenButton;

    public RoguelikeMCScreen() {
        super(Text.literal("RoguelikeMC Upgrade"));
    }

    @Override
    protected void init() {
        super.init();
        int guiLeft = (width - GUI_WIDTH) / 2;
        int guiTop = (height - GUI_HEIGHT) / 2;

        openDrawScreenButton = ButtonWidget.builder(
                        Text.translatable("button.roguelikemc.draw_upgrades"),
                        button -> client.setScreen(new RoguelikeMCDrawScreen(this))
                )
                .dimensions(
                        guiLeft + CONTENT_PADDING,
                        getBottomRowY(guiTop),
                        SECTION_WIDTH,
                        BOTTOM_ROW_HEIGHT
                )
                .build();
        addDrawableChild(openDrawScreenButton);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int guiLeft = (width - GUI_WIDTH) / 2;
        int guiTop = (height - GUI_HEIGHT) / 2;

        context.getMatrices().push();
        context.drawTexture(
                BACKGROUND_TEXTURE,
                guiLeft,
                guiTop,
                0,
                0,
                GUI_WIDTH,
                GUI_HEIGHT,
                GUI_WIDTH,
                GUI_WIDTH
        );
        renderContent(context, guiLeft + CONTENT_PADDING, guiTop + CONTENT_PADDING, mouseX, mouseY);
        context.getMatrices().pop();
    }

    private void renderContent(DrawContext context, int x, int y, int mouseX, int mouseY) {
        renderEffectsSection(context, x + SECTION_SPACING - 2, y, Text.translatable("gui.roguelikemc.temporary_upgrade"), RoguelikeMCClientData.INSTANCE.temporaryUpgrades, mouseX, mouseY);
        renderEffectsSection(context, x + SECTION_WIDTH + SECTION_SPACING + 1, y, Text.translatable("gui.roguelikemc.permanent_upgrade"), RoguelikeMCClientData.INSTANCE.permanentUpgrades, mouseX, mouseY);
        renderBossHint(context, x + SECTION_WIDTH + SECTION_SPACING, getBottomRowY(y - CONTENT_PADDING), mouseX, mouseY);
        openDrawScreenButton.render(context, mouseX, mouseY, 0);
    }

    private void renderBossHint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        context.fill(x, y, x + SECTION_WIDTH, y + 20, 0x80303030);
        context.drawBorder(x, y, SECTION_WIDTH, 20, 0xFF000000);
        context.drawTexture(
                Identifier.tryParse("roguelikemc", "textures/gui/boss_icon.png"),
                x + 3,
                y,
                0,
                0,
                20,
                20,
                20,
                20
        );
        context.getMatrices().scale(0.85f, 0.85f, 1.0f);
        context.drawText(
                textRenderer,
                bossHintText(),
                Math.round((x + 26) / 0.85f),
                Math.round((y + 7) / 0.85f),
                0xFFFFFF,
                true
        );
        context.getMatrices().scale(1.0f / 0.85f, 1.0f / 0.85f, 1.0f);

        if (isMouseOver(mouseX, mouseY, x, y, SECTION_WIDTH, 20)) {
            Optional<EntityType<?>> nextBoss = Registries.ENTITY_TYPE.getOrEmpty(RoguelikeMCClientData.INSTANCE.nextBoss);
            List<MutableText> bossName = nextBoss.map(entityType ->
                            List.of(
                                    Text.translatable("gui.roguelikemc.next_boss").formatted(Formatting.WHITE).append(Text.translatable(entityType.getTranslationKey())),
                                    Text.literal(RoguelikeMCClientData.INSTANCE.nextBoss.toString()).formatted(Formatting.GRAY)))
                    .orElseGet(() ->
                            List.of(
                                    Text.translatable("gui.roguelikemc.next_boss").formatted(Formatting.WHITE),
                                    Text.translatable("message.roguelikemc.boss_not_found").formatted(Formatting.GREEN)
                            ));
            context.drawTooltip(textRenderer, new ArrayList<>(bossName), mouseX, mouseY);
        }
    }

    private Text bossHintText() {
        Optional<EntityType<?>> nextBoss = Registries.ENTITY_TYPE.getOrEmpty(RoguelikeMCClientData.INSTANCE.nextBoss);
        if (nextBoss.isPresent()) {
            return Text.translatable("gui.roguelikemc.next_boss")
                    .append(Text.translatable(nextBoss.get().getTranslationKey()).formatted(Formatting.WHITE));
        }
        return Text.translatable("message.roguelikemc.boss_not_found").formatted(Formatting.GREEN);
    }

    private void renderEffectsSection(DrawContext context, int x, int y, Text title, List<RoguelikeMCUpgradeData> effects, int mouseX, int mouseY) {
//        context.drawCenteredTextWithShadow(textRenderer, title, x + SECTION_WIDTH / 2, y, 0xFFFFFF);

        int itemHeight = 20;
        int itemWidth = 20;
        int itemPaddingY = 2;
        int itemPaddingX = 4;
        int itemsPerRow = 5;
        int offset = itemHeight - 4;

        Map<String, Pair<RoguelikeMCUpgradeData, Integer>> collapsed = collapseUpgrades(effects);
        int i = 0;

        for (Pair<RoguelikeMCUpgradeData, Integer> pair : collapsed.values()) {
            RoguelikeMCUpgradeData effect = pair.getFirst();
            int count = pair.getSecond();

            int itemX = x + Math.floorMod(i, itemsPerRow) * (itemWidth + itemPaddingX);
            int itemY = y + CONTENT_PADDING + Math.floorDiv(i, itemsPerRow) * (itemHeight + itemPaddingY) + offset;

            context.fill(itemX, itemY, itemX + itemWidth, itemY + itemHeight, 0x503A2414);
            context.drawTexture(Identifier.tryParse(effect.icon()), itemX, itemY, 0, 0, itemWidth, itemHeight, itemWidth, itemHeight);

            if (count > 1) {
                context.drawText(
                        textRenderer,
                        Text.literal(String.valueOf(count)).formatted(Formatting.WHITE),
                        itemX + itemWidth - 7,
                        itemY + itemHeight - 8,
                        0xFFFFFF,
                        true
                );
            }

            Text isUnique = effect.isUnique() ? Text.literal("[U]").formatted(Formatting.LIGHT_PURPLE) : Text.empty();
            if (isMouseOver(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight)) {
                List<Text> tooltip = List.of(
                        count > 1
                                ? Text.translatable(effect.name()).formatted(getColorByRarity(effect.tier())).append(Text.literal(" x" + count).formatted(Formatting.WHITE)).append(isUnique)
                                : Text.translatable(effect.name()).formatted(getColorByRarity(effect.tier())).append(isUnique),
                        Text.translatable(effect.description()).formatted(Formatting.GRAY)
                );
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }

            i++;
        }
    }

    private Formatting getColorByRarity(String rarity) {
        return switch (rarity) {
            case "common" -> Formatting.WHITE;
            case "rare" -> Formatting.BLUE;
            case "epic" -> Formatting.DARK_PURPLE;
            case "legendary" -> Formatting.GOLD;
            default -> Formatting.WHITE;
        };
    }

    private Map<String, Pair<RoguelikeMCUpgradeData, Integer>> collapseUpgrades(List<RoguelikeMCUpgradeData> upgrades) {
        Map<String, Pair<RoguelikeMCUpgradeData, Integer>> collapsed = new HashMap<>();
        for (RoguelikeMCUpgradeData upgrade : upgrades) {
            String id = upgrade.id();
            if (collapsed.containsKey(id)) {
                collapsed.computeIfPresent(id, (k, entry) -> Pair.of(entry.getFirst(), entry.getSecond() + 1));
            } else {
                collapsed.put(id, Pair.of(upgrade, 1));
            }
        }
        return collapsed;
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private int getBottomRowY(int guiTop) {
        return guiTop + GUI_HEIGHT - CONTENT_PADDING - BOTTOM_ROW_HEIGHT;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)
                || RoguelikeMCClient.getOpenGuiKey().matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
