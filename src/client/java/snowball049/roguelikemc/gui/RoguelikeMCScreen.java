package snowball049.roguelikemc.gui;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.data.RoguelikeMCClientData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;

import java.util.*;

@Environment(EnvType.CLIENT)
public class RoguelikeMCScreen extends Screen {

    // 自定義 GUI 背景圖
    private static final Identifier BACKGROUND_TEXTURE = Identifier.tryParse("roguelikemc", "textures/gui/upgrade_bg.png");

    // 選擇升級數量
    private static final int CHOOSE_POOL = 3;

    // GUI 元素
    private final ButtonWidget[] optionButtons = new ButtonWidget[CHOOSE_POOL];
    private ButtonWidget refreshButton;

    // GUI 尺寸
    private static final int GUI_WIDTH = 360;
    private static final int GUI_HEIGHT = 200;

    // 內容區域邊距
    private static final int CONTENT_PADDING = 15;
    private static final int SECTION_SPACING = 15;
    private static final int BUTTON_PADDING = 5;

    private static final int SECTION_WIDTH = (GUI_WIDTH - 4 * SECTION_SPACING) / 3;
    private static final int BUTTON_WIDTH = SECTION_WIDTH - 2 * BUTTON_PADDING;
    private static final int BUTTON_HEIGHT = (GUI_HEIGHT - 2 * CONTENT_PADDING - 2 * SECTION_SPACING) / CHOOSE_POOL;

    public RoguelikeMCScreen() {
        super(Text.literal("RoguelikeMC Upgrade"));
    }

    @Override
    protected void init() {
        super.init();
        // 計算GUI在屏幕中的實際位置
        int guiLeft = (width - GUI_WIDTH)/2;
        int guiTop = (height - GUI_HEIGHT)/2;

        // 右側按鈕起始位置（基於GUI右側區域）
        int rightPanelX = guiLeft + 2 * SECTION_WIDTH + 3 * SECTION_SPACING + BUTTON_PADDING; // 右側欄位內偏移
        int rightPanelY = guiTop + CONTENT_PADDING*2; // 從GUI頂部向下偏移

        for(int i=0; i<3; i++) {
            final int index = i;
            optionButtons[i] = ButtonWidget.builder(Text.empty(), button -> {
                        if (RoguelikeMCClientData.INSTANCE.currentOptions.size() > index) {
                            RoguelikeMCUpgradeData selected = RoguelikeMCClientData.INSTANCE.currentOptions.get(index);
                            ClientPlayNetworking.send(new SelectUpgradeOptionC2SPayload(selected));
                            RoguelikeMCClientData.INSTANCE.currentOptions.clear();
                            refreshOptionsDisplay();
                        }
                    })
                    .dimensions(
                            rightPanelX,
                            rightPanelY + i * (BUTTON_HEIGHT + BUTTON_PADDING),
                            BUTTON_WIDTH,
                            BUTTON_HEIGHT
                    )
                    .build();
            this.addDrawableChild(optionButtons[i]);
        }

        refreshButton = ButtonWidget.builder(Text.translatable("button.roguelikemc.draw_upgrades"), button -> {
                    if(RoguelikeMCClientData.INSTANCE.currentOptions.isEmpty()){
                        ClientPlayNetworking.send(new RefreshUpgradeOptionC2SPayload());
                        refreshOptionsDisplay();
                    }
                })
                .dimensions(
                        guiLeft + GUI_WIDTH / 2 - 50, // GUI水平居中
                        guiTop + GUI_HEIGHT - 25,   // GUI底部向上30像素
                        100,
                        20
                )
                .build();
        this.addDrawableChild(refreshButton);
    }

    @Override
    public boolean shouldPause() {
        return false; // 不暫停遊戲世界
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // 繪製自適應背景
        int guiLeft = ((width - GUI_WIDTH ) / 2);
        int guiTop = ((height - GUI_HEIGHT) / 2);

        context.getMatrices().push();

        // 繪製高清背景
        context.drawTexture(
                BACKGROUND_TEXTURE,
                guiLeft,
                guiTop,
                0, 0, GUI_WIDTH, GUI_WIDTH,
                GUI_WIDTH, GUI_WIDTH
        );

        // 渲染內容區域
        renderContent(context,
                guiLeft + CONTENT_PADDING,
                guiTop + CONTENT_PADDING,
                mouseX, mouseY);

        context.getMatrices().pop();
    }

    private void refreshOptionsDisplay(){
        for(int i=0; i<optionButtons.length; i++){
            if(i < RoguelikeMCClientData.INSTANCE.currentOptions.size()){
                RoguelikeMCUpgradeData effect = RoguelikeMCClientData.INSTANCE.currentOptions.get(i);
                optionButtons[i].setMessage(Text.translatable(effect.name()).formatted(getColorByRarity(effect.tier())));
                optionButtons[i].setTooltip(Tooltip.of(Text.translatable(effect.description()).formatted(Formatting.GRAY)));
            }else{
                optionButtons[i].setMessage(Text.empty());
            }
        }
    }

    private Formatting getColorByRarity(String rarity) {
        return switch (rarity) {
            case "common" -> Formatting.WHITE;
            case "rare" -> Formatting.BLUE;
            case "epic" -> Formatting.DARK_PURPLE;
            case "legendary" -> Formatting.GOLD;
            default -> Formatting.WHITE; // 默認顏色
        };
    }

    private void renderContent(DrawContext context, int x, int y, int mouseX, int mouseY) {

        // 暫時效果區域
        renderEffectsSection(context, x, y, Text.translatable("gui.roguelikemc.temporary_upgrade"), RoguelikeMCClientData.INSTANCE.temporaryUpgrades, mouseX, mouseY);

        // 永久效果區域
        renderEffectsSection(context, x + SECTION_WIDTH + SECTION_SPACING, y, Text.translatable("gui.roguelikemc.permanent_upgrade"), RoguelikeMCClientData.INSTANCE.permanentUpgrades, mouseX, mouseY);

        // 右側功能區域
        renderUtilitySection(context, x + 2 * (SECTION_WIDTH + SECTION_SPACING), y, mouseX, mouseY);

        // Upgrade Points area
        renderPointSection(context, x - CONTENT_PADDING + 5, y + GUI_HEIGHT - CONTENT_PADDING - 25, mouseX, mouseY);

        // Next Boss Hint
        renderBossHint(context, x + SECTION_WIDTH - 8, y + GUI_HEIGHT - CONTENT_PADDING - 25, mouseX, mouseY);

        // 刷新按鈕渲染
        refreshButton.render(context, mouseX, mouseY, 0);
    }

    private void renderBossHint(DrawContext context, int i, int i1, int mouseX, int mouseY) {
        context.fill(i, i1, i + 20, i1 + 20, 0x80303030);
        context.drawBorder(i, i1, 20, 20, 0xFF000000);
        context.drawTexture(
                Identifier.tryParse("roguelikemc", "textures/gui/boss_icon.png"),
                i, i1, 0, 0, 20, 20, 20, 20
        );
        // Render Tooltip
        if (isMouseOver(mouseX, mouseY, i, i1, 20, 20)) {
            Optional<EntityType<?>> nextBoss = Registries.ENTITY_TYPE.getOrEmpty(RoguelikeMCClientData.INSTANCE.nextBoss);
            List<MutableText> bossName = nextBoss.map(entityType ->
                    List.of(
                            Text.translatable("gui.roguelikemc.next_boss").formatted(Formatting.WHITE).append(Text.translatable(entityType.getTranslationKey())),
                            Text.literal(RoguelikeMCClientData.INSTANCE.nextBoss.toString()).formatted(Formatting.GRAY)))
                    .orElseGet(() ->
                        List.of(Text.translatable("gui.roguelikemc.next_boss").formatted(Formatting.WHITE),
                                Text.translatable("message.roguelikemc.boss_not_found").formatted(Formatting.GREEN)));
            context.drawTooltip(textRenderer, new ArrayList<>(bossName), mouseX, mouseY);
        }
    }

    private void renderPointSection(DrawContext context, int x, int y, int mouseX, int mouseY) {
        // Background color
         context.fill(x, y, x + SECTION_WIDTH, y + 20, 0x80303030);
         context.drawBorder(x, y, SECTION_WIDTH, 20, 0xFF000000);

        // Draw experience orb texture
        context.drawTexture(
                Identifier.tryParse("roguelikemc", "textures/item/upgrade_point_orb.png"),
                x + 2, y + 5, 0, 0, 10, 10, 10, 10
        );

        // Draw text
        context.getMatrices().scale(0.85f, 0.85f, 1.0f);
        context.drawText(
                textRenderer,
                Text.translatable("gui.roguelikemc.upgrade_points").append(Text.of(String.valueOf(RoguelikeMCClientData.INSTANCE.currentPoints))),
                Math.round((x + 13)/0.85f), Math.round((y + 7)/0.85f), 0xd397fe, true
        );
        context.getMatrices().scale(1.0f / 0.85f, 1.0f / 0.85f, 1.0f);
    }

    private void renderEffectsSection(DrawContext context, int x, int y, Text title, List<RoguelikeMCUpgradeData> effects, int mouseX, int mouseY) {
        // 標題文字
        context.drawCenteredTextWithShadow(textRenderer, title, x+SECTION_WIDTH/2, y, 0xFFFFFF);

        // 效果列表
        int itemHeight = 20;
        int itemWidth = 20; // 方塊寬度
        int itemPadding = 2;

        Map<String, Pair<RoguelikeMCUpgradeData, Integer>> collapsed = collapseUpgrades(effects);
        int i = 0;

        for (Pair<RoguelikeMCUpgradeData, Integer> pair : collapsed.values()) {
            RoguelikeMCUpgradeData effect = pair.getFirst();
            int count = pair.getSecond();

            int itemY = y + CONTENT_PADDING + Math.floorMod(i, 6) * (itemHeight + itemPadding);
            int itemX = x + Math.floorDiv(i, 6) * (itemWidth + itemPadding);

            context.fill(itemX, itemY, itemX + itemWidth, itemY + itemHeight, 0xFF545454);
            context.drawTexture(Identifier.tryParse(effect.icon()), itemX, itemY, 0, 0, itemWidth, itemHeight, itemWidth, itemHeight);
            context.drawBorder(itemX, itemY, itemWidth, itemHeight, 0xFFFFFFFF);

            // 如果疊加大於 1，顯示數量
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

            // Hover Tooltip
            if (isMouseOver(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight)) {
                List<Text> tooltip = List.of(
                        count>1?Text.translatable(effect.name()).formatted(getColorByRarity(effect.tier())).append(Text.literal(" x" + count).formatted(Formatting.WHITE)):
                                Text.translatable(effect.name()).formatted(getColorByRarity(effect.tier())),
                        Text.translatable(effect.description()).formatted(Formatting.GRAY)
                );
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }

            i++;
        }
    }

    private void renderUtilitySection(DrawContext context, int x, int y, int mouseX, int mouseY) {
        // 區域背景
        context.fill(x, y-5, x + SECTION_WIDTH, y + GUI_HEIGHT - 2*CONTENT_PADDING+5, 0x80303030);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("gui.roguelikemc.upgrade_pool"), x+SECTION_WIDTH/2, y, 0xFFFFFF);

        // 繪製三個按鈕的視覺元素
        int buttonX = x + BUTTON_PADDING;
        int buttonY = y + CONTENT_PADDING;
        for(int i=0; i<3; i++){
            optionButtons[i].render(context, mouseX, mouseY, i);

            if(i < RoguelikeMCClientData.INSTANCE.currentOptions.size()) {
                RoguelikeMCUpgradeData effect = RoguelikeMCClientData.INSTANCE.currentOptions.get(i);
                // Render icon
                context.drawTexture(
                        Identifier.tryParse(effect.icon()),
                        buttonX + BUTTON_PADDING,
                        buttonY + i * (BUTTON_HEIGHT + BUTTON_PADDING) + BUTTON_HEIGHT/2 - 10,
                        0, 0,
                        20, 20, 20, 20
                );

                // Render text
                context.drawTextWrapped(
                        textRenderer,
                        Text.translatable(effect.name()).formatted(getColorByRarity(effect.tier())).formatted(Formatting.BOLD),
                        buttonX + BUTTON_PADDING * 2 + 20, // 顏色方塊右側
                        buttonY + i * (BUTTON_HEIGHT + BUTTON_PADDING) + BUTTON_HEIGHT/2 - BUTTON_PADDING, // 垂直居中
                        BUTTON_WIDTH - BUTTON_PADDING * 3 - 20,
                        getColorByRarity(effect.tier()).getColorValue() // 文字顏色
                );
                // Render Tooltip
                if (optionButtons[i].isMouseOver(mouseX, mouseY)) {
                    context.drawTooltip(textRenderer,
                            List.of(
                                    Text.translatable(effect.name()).formatted(getColorByRarity(effect.tier())),
                                    Text.translatable(effect.description()).formatted(Formatting.GRAY)
                            ),
                            mouseX, mouseY
                    );
                }
            }
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
        if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}