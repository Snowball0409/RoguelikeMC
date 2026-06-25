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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class RoguelikeMCScreen extends Screen {
    private Layout layout;

    public RoguelikeMCScreen() {
        super(Text.literal("RoguelikeMC Upgrade"));
    }

    @Override
    protected void init() {
        super.init();
        layout = Layout.compute(width, height);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderScreen(context, ensureLayout(), mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.inventoryKey.matchesKey(keyCode, scanCode)
                || RoguelikeMCClient.getOpenGuiKey().matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Layout currentLayout = ensureLayout();
        if (button == 0 && currentLayout.drawButton().contains((int) mouseX, (int) mouseY) && client != null) {
            client.setScreen(new RoguelikeMCDrawScreen(this));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Layout ensureLayout() {
        if (layout == null) {
            layout = Layout.compute(width, height);
        }
        return layout;
    }

    private void renderScreen(DrawContext context, Layout currentLayout, int mouseX, int mouseY) {
        context.getMatrices().push();
        drawBackground(context, currentLayout);
        drawUpgradeGrid(context, currentLayout.temporaryUpgrades(), RoguelikeMCClientData.INSTANCE.temporaryUpgrades, mouseX, mouseY);
        drawUpgradeGrid(context, currentLayout.permanentUpgrades(), RoguelikeMCClientData.INSTANCE.permanentUpgrades, mouseX, mouseY);
        drawIconButton(context, Assets.BOSS_ICON, currentLayout.bossHint(), mouseX, mouseY, 0x50303030, 0x70403030, this::buildBossTooltip);
        drawIconButton(context, Assets.DRAW_BUTTON, currentLayout.drawButton(), mouseX, mouseY, 0x30000000, 0x50FFFFFF,
                () -> List.of(Text.translatable("button.roguelikemc.draw_upgrades")));
        context.getMatrices().pop();
    }

    private void drawBackground(DrawContext context, Layout currentLayout) {
        context.drawTexture(
                Assets.BACKGROUND,
                currentLayout.guiLeft(),
                currentLayout.guiTop(),
                0,
                0,
                Assets.GUI_WIDTH,
                Assets.GUI_HEIGHT,
                Assets.BACKGROUND_TEXTURE_WIDTH,
                Assets.BACKGROUND_TEXTURE_HEIGHT
        );
    }

    private void drawIconButton(
            DrawContext context,
            Identifier texture,
            Rect bounds,
            int mouseX,
            int mouseY,
            int normalColor,
            int hoveredColor,
            TooltipSupplier tooltipSupplier
    ) {
        boolean hovered = bounds.contains(mouseX, mouseY);
        context.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), hovered ? hoveredColor : normalColor);
        context.drawTexture(
                texture,
                bounds.x(),
                bounds.y(),
                0,
                0,
                bounds.width(),
                bounds.height(),
                Assets.TOP_ICON_TEXTURE_SIZE,
                Assets.TOP_ICON_TEXTURE_SIZE
        );

        if (hovered) {
            context.drawTooltip(textRenderer, new ArrayList<>(tooltipSupplier.get()), mouseX, mouseY);
        }
    }

    private List<MutableText> buildBossTooltip() {
        Optional<EntityType<?>> nextBoss = Registries.ENTITY_TYPE.getOrEmpty(RoguelikeMCClientData.INSTANCE.nextBoss);
        if (nextBoss.isPresent()) {
            return List.of(
                    Text.translatable("gui.roguelikemc.next_boss")
                            .formatted(Formatting.WHITE)
                            .append(Text.translatable(nextBoss.get().getTranslationKey())),
                    Text.literal(RoguelikeMCClientData.INSTANCE.nextBoss.toString()).formatted(Formatting.GRAY)
            );
        }

        return List.of(
                Text.translatable("gui.roguelikemc.next_boss").formatted(Formatting.WHITE),
                Text.translatable("message.roguelikemc.boss_not_found").formatted(Formatting.GREEN)
        );
    }

    private void drawUpgradeGrid(
            DrawContext context,
            Rect bounds,
            List<RoguelikeMCUpgradeData> upgrades,
            int mouseX,
            int mouseY
    ) {
        int index = 0;
        for (Map.Entry<String, Pair<RoguelikeMCUpgradeData, Integer>> entry : collapseUpgrades(upgrades).entrySet()) {
            RoguelikeMCUpgradeData upgrade = entry.getValue().getFirst();
            int count = entry.getValue().getSecond();

            int itemX = bounds.x() + Math.floorMod(index, Grid.ITEMS_PER_ROW) * (Grid.ITEM_SIZE + Grid.ITEM_PADDING_X);
            int itemY = bounds.y() + Grid.SECTION_TOP_PADDING
                    + Math.floorDiv(index, Grid.ITEMS_PER_ROW) * (Grid.ITEM_SIZE + Grid.ITEM_PADDING_Y)
                    + Grid.ROW_OFFSET;

            drawUpgradeIcon(context, upgrade, count, itemX, itemY, mouseX, mouseY);
            index++;
        }
    }

    private void drawUpgradeIcon(
            DrawContext context,
            RoguelikeMCUpgradeData upgrade,
            int count,
            int itemX,
            int itemY,
            int mouseX,
            int mouseY
    ) {
        context.fill(itemX, itemY, itemX + Grid.ITEM_SIZE, itemY + Grid.ITEM_SIZE, 0x503A2414);

        Identifier icon = Identifier.tryParse(upgrade.icon());
        if (icon != null) {
            context.drawTexture(icon, itemX, itemY, 0, 0, Grid.ITEM_SIZE, Grid.ITEM_SIZE, Grid.ITEM_SIZE, Grid.ITEM_SIZE);
        }

        if (count > 1) {
            context.drawText(
                    textRenderer,
                    Text.literal(String.valueOf(count)).formatted(Formatting.WHITE),
                    itemX + Grid.ITEM_SIZE - 7,
                    itemY + Grid.ITEM_SIZE - 8,
                    0xFFFFFF,
                    true
            );
        }

        if (!contains(mouseX, mouseY, itemX, itemY, Grid.ITEM_SIZE)) {
            return;
        }

        Text uniqueTag = UpgradePresentation.uniqueTag(upgrade);
        Formatting rarityColor = UpgradePresentation.rarityColor(upgrade);
        Text nameLine = count > 1
                ? Text.translatable(upgrade.name()).formatted(rarityColor)
                .append(Text.literal(" x" + count).formatted(Formatting.WHITE))
                .append(uniqueTag)
                : Text.translatable(upgrade.name()).formatted(rarityColor).append(uniqueTag);

        context.drawTooltip(
                textRenderer,
                List.of(nameLine, Text.translatable(upgrade.description()).formatted(Formatting.GRAY)),
                mouseX,
                mouseY
        );
    }

    private static Map<String, Pair<RoguelikeMCUpgradeData, Integer>> collapseUpgrades(List<RoguelikeMCUpgradeData> upgrades) {
        Map<String, Pair<RoguelikeMCUpgradeData, Integer>> collapsed = new LinkedHashMap<>();
        for (RoguelikeMCUpgradeData upgrade : upgrades) {
            collapsed.merge(upgrade.id(), Pair.of(upgrade, 1), (existing, ignored) ->
                    Pair.of(existing.getFirst(), existing.getSecond() + 1));
        }
        return collapsed;
    }

    private static boolean contains(int mouseX, int mouseY, int x, int y, int size) {
        return mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size;
    }

    @FunctionalInterface
    private interface TooltipSupplier {
        List<? extends Text> get();
    }

    private record Rect(int x, int y, int width, int height) {
        boolean contains(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }

        int right() {
            return x + width;
        }

        int bottom() {
            return y + height;
        }
    }

    private record Layout(
            int guiLeft,
            int guiTop,
            Rect temporaryUpgrades,
            Rect permanentUpgrades,
            Rect bossHint,
            Rect drawButton
    ) {
        static Layout compute(int screenWidth, int screenHeight) {
            int guiLeft = (screenWidth - Assets.GUI_WIDTH) / 2;
            int guiTop = (screenHeight - Assets.GUI_HEIGHT) / 2;
            int contentX = guiLeft + Assets.CONTENT_PADDING;
            int contentY = guiTop + Assets.CONTENT_PADDING;

            int drawButtonX = guiLeft + Assets.GUI_WIDTH - Assets.CONTENT_PADDING - Assets.TOP_ICON_SIZE - 30;
            int drawButtonY = guiTop + Assets.CONTENT_PADDING - 4;
            int bossHintX = drawButtonX - Assets.TOP_ICON_SPACING - Assets.TOP_ICON_SIZE;
            int iconSize = Assets.TOP_ICON_SIZE;

            int temporaryX = contentX + Assets.SECTION_SPACING - 2;
            int permanentX = contentX + Assets.SECTION_WIDTH + Assets.SECTION_SPACING + 1;
            int sectionHeight = Assets.GUI_HEIGHT - Assets.CONTENT_PADDING * 2;

            return new Layout(
                    guiLeft,
                    guiTop,
                    new Rect(temporaryX, contentY, Assets.SECTION_WIDTH, sectionHeight),
                    new Rect(permanentX, contentY, Assets.SECTION_WIDTH, sectionHeight),
                    new Rect(bossHintX, drawButtonY, iconSize, iconSize),
                    new Rect(drawButtonX, drawButtonY, iconSize, iconSize)
            );
        }
    }

    private static final class Assets {
        static final Identifier BACKGROUND = Identifier.of("roguelikemc", "textures/gui/upgrade_bg.png");
        static final Identifier DRAW_BUTTON = Identifier.of("roguelikemc", "textures/gui/draw.png");
        static final Identifier BOSS_ICON = Identifier.of("roguelikemc", "textures/gui/boss_icon.png");

        static final int GUI_WIDTH = 360;
        static final int GUI_HEIGHT = 210;
        static final int BACKGROUND_TEXTURE_WIDTH = GUI_WIDTH;
        static final int BACKGROUND_TEXTURE_HEIGHT = GUI_WIDTH;

        static final int CONTENT_PADDING = 15;
        static final int SECTION_SPACING = 15;
        static final int SECTION_WIDTH = (GUI_WIDTH - 3 * SECTION_SPACING) / 2;

        static final int TOP_ICON_SIZE = 20;
        static final int TOP_ICON_TEXTURE_SIZE = 20;
        static final int TOP_ICON_SPACING = 4;

        private Assets() {
        }
    }

    private static final class Grid {
        static final int ITEM_SIZE = 20;
        static final int ITEM_PADDING_X = 4;
        static final int ITEM_PADDING_Y = 2;
        static final int ITEMS_PER_ROW = 5;
        static final int ROW_OFFSET = ITEM_SIZE - 4;
        static final int SECTION_TOP_PADDING = Assets.CONTENT_PADDING;

        private Grid() {
        }
    }
}
