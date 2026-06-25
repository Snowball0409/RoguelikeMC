package snowball049.roguelikemc.gui;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMCClient;
import snowball049.roguelikemc.data.RoguelikeMCClientData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.UpgradePresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class RoguelikeMCScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.tryParse("roguelikemc", "textures/gui/upgrade_bg.png");
    private static final Identifier DRAW_BUTTON_TEXTURE = Identifier.tryParse("roguelikemc", "textures/gui/draw.png");
    private static final Identifier BOSS_ICON_TEXTURE = Identifier.tryParse("roguelikemc", "textures/gui/boss_icon.png");

    private static final int GUI_WIDTH = 360;
    private static final int GUI_HEIGHT = 210;
    private static final int BACKGROUND_TEXTURE_WIDTH = GUI_WIDTH;
    private static final int BACKGROUND_TEXTURE_HEIGHT = GUI_WIDTH;
    private static final int CONTENT_PADDING = 15;
    private static final int SECTION_SPACING = 15;
    private static final int SECTION_WIDTH = (GUI_WIDTH - 3 * SECTION_SPACING) / 2;
    private static final int TOP_ICON_SIZE = 20;
    private static final int TOP_ICON_TEXTURE_SIZE = 20;
    private static final int TOP_ICON_SPACING = 4;

    private static final int TOP_ICON_HITBOX_SIZE = TOP_ICON_SIZE;

    private int drawButtonX;
    private int drawButtonY;
    private int bossHintX;
    private int bossHintY;

    public RoguelikeMCScreen() {
        super(Text.literal("RoguelikeMC Upgrade"));
    }

    @Override
    protected void init() {
        super.init();
        int guiLeft = (width - GUI_WIDTH) / 2;
        int guiTop = (height - GUI_HEIGHT) / 2;

        drawButtonX = guiLeft + GUI_WIDTH - CONTENT_PADDING - TOP_ICON_SIZE - 30;
        drawButtonY = guiTop + CONTENT_PADDING - 4;
        bossHintX = drawButtonX - TOP_ICON_SPACING - TOP_ICON_SIZE;
        bossHintY = drawButtonY;
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
                BACKGROUND_TEXTURE_WIDTH,
                BACKGROUND_TEXTURE_HEIGHT
        );
        renderContent(context, guiLeft + CONTENT_PADDING, guiTop + CONTENT_PADDING, mouseX, mouseY);
        context.getMatrices().pop();
    }

    private void renderContent(DrawContext context, int x, int y, int mouseX, int mouseY) {
        renderEffectsSection(context, x + SECTION_SPACING - 2, y, RoguelikeMCClientData.INSTANCE.temporaryUpgrades, mouseX, mouseY);
        renderEffectsSection(context, x + SECTION_WIDTH + SECTION_SPACING + 1, y, RoguelikeMCClientData.INSTANCE.permanentUpgrades, mouseX, mouseY);
        renderBossHint(context, mouseX, mouseY);
        renderDrawButton(context, mouseX, mouseY);
    }

    private void renderBossHint(DrawContext context, int mouseX, int mouseY) {
        boolean hovered = isMouseOver(mouseX, mouseY, bossHintX, bossHintY, TOP_ICON_HITBOX_SIZE, TOP_ICON_HITBOX_SIZE);
        context.fill(bossHintX, bossHintY, bossHintX + TOP_ICON_SIZE, bossHintY + TOP_ICON_SIZE, hovered ? 0x70403030 : 0x50303030);
        context.drawTexture(BOSS_ICON_TEXTURE, bossHintX, bossHintY, 0, 0, TOP_ICON_SIZE, TOP_ICON_SIZE, TOP_ICON_TEXTURE_SIZE, TOP_ICON_TEXTURE_SIZE);

        if (hovered) {
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

    private void renderDrawButton(DrawContext context, int mouseX, int mouseY) {
        boolean hovered = isMouseOver(mouseX, mouseY, drawButtonX, drawButtonY, TOP_ICON_HITBOX_SIZE, TOP_ICON_HITBOX_SIZE);
        context.fill(drawButtonX, drawButtonY, drawButtonX + TOP_ICON_SIZE, drawButtonY + TOP_ICON_SIZE, hovered ? 0x50FFFFFF : 0x30000000);
        context.drawTexture(DRAW_BUTTON_TEXTURE, drawButtonX, drawButtonY, 0, 0, TOP_ICON_SIZE, TOP_ICON_SIZE, TOP_ICON_TEXTURE_SIZE, TOP_ICON_TEXTURE_SIZE);

        if (hovered) {
            context.drawTooltip(textRenderer, Text.translatable("button.roguelikemc.draw_upgrades"), mouseX, mouseY);
        }
    }

    private void renderEffectsSection(DrawContext context, int x, int y, List<RoguelikeMCUpgradeData> effects, int mouseX, int mouseY) {
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

            Text isUnique = UpgradePresentation.uniqueTag(effect);
            if (isMouseOver(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight)) {
                Formatting rarityColor = UpgradePresentation.rarityColor(effect);
                List<Text> tooltip = List.of(
                        count > 1
                                ? Text.translatable(effect.name()).formatted(rarityColor).append(Text.literal(" x" + count).formatted(Formatting.WHITE)).append(isUnique)
                                : Text.translatable(effect.name()).formatted(rarityColor).append(isUnique),
                        Text.translatable(effect.description()).formatted(Formatting.GRAY)
                );
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }

            i++;
        }
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)
                || RoguelikeMCClient.getOpenGuiKey().matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver((int) mouseX, (int) mouseY, drawButtonX, drawButtonY, TOP_ICON_HITBOX_SIZE, TOP_ICON_HITBOX_SIZE)) {
            if (client != null) {
                client.setScreen(new RoguelikeMCDrawScreen(this));
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
