package snowball049.roguelikemc.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.network.SendPacketToServer;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RoguelikeMCScreen extends Screen {

    private List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> TEMPORARY_EFFECTS = new ArrayList<>();
    private List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> PERMANENT_EFFECTS = new ArrayList<>();
    public final List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> currentOptions = new ArrayList<>(3);

    // 自定義 GUI 背景圖
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.tryBuild("roguelikemc", "textures/gui/upgrade_bg.png");

    // 選擇升級數量
    private static final int CHOOSE_POOL = 3;

    // GUI 元素
    private final Button[] optionButtons = new Button[CHOOSE_POOL];
    private Button refreshButton;

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
        super(Component.literal("RoguelikeMC Upgrade"));
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
            optionButtons[i] = Button.builder(Component.empty(), button -> {
                        if (currentOptions.size() > index) {
                            RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig selected = currentOptions.get(index);
                            SendPacketToServer.send(new SelectUpgradeOptionC2SPayload(selected));
                            currentOptions.clear();
                            refreshOptionsDisplay();
                        }
                    })
                    // 調整按鈕位置計算
                    .bounds(
                            rightPanelX,
                            rightPanelY + i * (BUTTON_HEIGHT + BUTTON_PADDING),
                            BUTTON_WIDTH,  // 按鈕寬度配合右側區域
                            BUTTON_HEIGHT    // 按鈕高度
                    )
                    .build();
            this.addRenderableWidget(optionButtons[i]);
        }

        refreshButton = Button.builder(Component.literal("Draw Upgrades"), button -> {
                    if(currentOptions.isEmpty()){
                        SendPacketToServer.send(new RefreshUpgradeOptionC2SPayload());
                        refreshOptionsDisplay();
                    }
                })
                .bounds(
                        guiLeft + GUI_WIDTH / 2 - 50, // GUI水平居中
                        guiTop + GUI_HEIGHT - 30,   // GUI底部向上30像素
                        100,
                        20
                )
                .build();
        this.addRenderableWidget(refreshButton);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 不暫停遊戲世界
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // 繪製自適應背景
        int guiLeft = ((width - GUI_WIDTH ) / 2);
        int guiTop = ((height - GUI_HEIGHT) / 2);

        context.pose().pushPose();

        // 繪製高清背景
        context.blit(
                BACKGROUND_TEXTURE,
                guiLeft,
                guiTop,
                0, 0, GUI_WIDTH, GUI_HEIGHT,
                GUI_WIDTH, GUI_HEIGHT
        );

        // 渲染內容區域
        renderContent(context,
                guiLeft + CONTENT_PADDING,
                guiTop + CONTENT_PADDING,
                mouseX, mouseY);

        context.pose().popPose();
    }

    private void refreshOptionsDisplay(){
        for(int i=0; i<3; i++){
            if(i < currentOptions.size()){
                RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig effect = currentOptions.get(i);
                optionButtons[i].setMessage(Component.literal(effect.name()));
                optionButtons[i].setTooltip(Tooltip.create(Component.literal(effect.description()).formatted(ChatFormatting.GRAY)));
            }else{
                optionButtons[i].setMessage(Component.empty());
            }
        }
    }

    public void refreshUpgradeDisplay(List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> upgrades){
        if(this.minecraft != null){
            if(upgrades.isEmpty() || !upgrades.getFirst().is_permanent()){
                TEMPORARY_EFFECTS = upgrades;
            }
            else{
                PERMANENT_EFFECTS = upgrades;
            }
        }
    }

    private void renderContent(GuiGraphics context, int x, int y, int mouseX, int mouseY) {

        // 暫時效果區域
        renderEffectsSection(context, x, y, "Temporary Effects", TEMPORARY_EFFECTS, mouseX, mouseY);

        // 永久效果區域
        renderEffectsSection(context, x + SECTION_WIDTH + SECTION_SPACING, y, "Permanent Effects", PERMANENT_EFFECTS, mouseX, mouseY);

        // 右側功能區域
        renderUtilitySection(context, x + 2 * (SECTION_WIDTH + SECTION_SPACING), y, mouseX, mouseY);

        // 刷新按鈕渲染
        renderRefreshButton(context, x, y, mouseX, mouseY);
    }

    private void renderEffectsSection(GuiGraphics context, int x, int y, String title, List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> effects, int mouseX, int mouseY) {
        // 標題文字
        context.drawCenteredString(font, title, x+SECTION_WIDTH/2, y, 0xFFFFFF);

        // 效果列表
        int itemHeight = 20;
        int itemWidth = 20; // 方塊寬度
        int itemPadding = 2;

        for (int i = 0; i < effects.size(); i++) {
            int itemY = y + CONTENT_PADDING + itemPadding + Math.floorMod(i,6) * (itemHeight+itemPadding);
            int itemX = x + Math.divideExact(i, 6)*(itemWidth+itemPadding);

            // 繪製顏色方塊
            RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig effect = effects.get(i);
            context.blit(ResourceLocation.tryParse(effect.icon()),itemX, itemY, 0, 0, itemWidth, itemHeight, itemWidth, itemHeight); // 設置透明度

            // 檢查滑鼠懸停
            if (isMouseOver(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight)) {
                // 繪製懸停提示
                List<Component> tooltip = Arrays.asList(
                        Component.literal(effect.name()).formatted(ChatFormatting.GREEN),
                        Component.literal(effect.description()).formatted(ChatFormatting.GRAY)
                );
                context.renderComponentTooltip(font, tooltip, mouseX, mouseY);
            }
        }
    }

    private void renderUtilitySection(GuiGraphics context, int x, int y, int mouseX, int mouseY) {
        // 區域背景
        context.fill(x, y-5, x + SECTION_WIDTH, y + GUI_HEIGHT - 2*CONTENT_PADDING+5, 0x80303030);
        context.drawCenteredString(font, "Effect Pool", x+SECTION_WIDTH/2, y, 0xFFFFFF);

        // 繪製三個按鈕的視覺元素
        int buttonX = x + BUTTON_PADDING;
        int buttonY = y + CONTENT_PADDING;
        for(int i=0; i<3; i++){
            // 按鈕背景
            context.fill(
                    buttonX, buttonY + i*(BUTTON_HEIGHT + BUTTON_PADDING),
                    buttonX + BUTTON_WIDTH, buttonY + (i+1)*BUTTON_HEIGHT + i*BUTTON_PADDING,
                    0xFF404040 // 深灰色背景
            );

            if(i < currentOptions.size()) {
                RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig effect = currentOptions.get(i);
                // 顏色方塊
                context.blit(
                        ResourceLocation.tryParse(effect.icon()),
                        buttonX + BUTTON_PADDING,
                        buttonY + i * (BUTTON_HEIGHT + BUTTON_PADDING) + BUTTON_HEIGHT/2 - 10,
//                        buttonX + BUTTON_PADDING + 20,
//                        buttonY + i * (BUTTON_HEIGHT + BUTTON_PADDING) + BUTTON_HEIGHT/2 + 10,
                        0, 0,
                        20, 20, 20, 20
                );

                // 文字渲染
                final MultiLineLabel effectName = MultiLineLabel.create(font, Component.nullToEmpty(effect.name()));
                context.drawWordWrap(
                        font,
                        FormattedText.of(effect.name()),
                        buttonX + BUTTON_PADDING * 2 + 20, // 顏色方塊右側
                        buttonY + i * (BUTTON_HEIGHT + BUTTON_PADDING) + BUTTON_HEIGHT/2 - BUTTON_PADDING, // 垂直居中
                        BUTTON_WIDTH - BUTTON_PADDING * 2 - 20,
                        0xFFFFFF
                );
                // 繪製懸停提示
                if (optionButtons[i].isMouseOver(mouseX, mouseY)) {
                    context.renderTooltip(font,
                            List.of(
                                    Component.literal(effect.name()).formatted(ChatFormatting.GREEN),
                                    Component.literal(effect.description()).formatted(ChatFormatting.GRAY)
                            ),
                            mouseX, mouseY
                    );
                }
            }
        }
    }

    private void renderRefreshButton(GuiGraphics context, int x, int y, int mouseX, int mouseY) {
        // 刷新按鈕渲染修正
        context.fill(
                refreshButton.getX(),
                refreshButton.getY(),
                refreshButton.getX() + refreshButton.getWidth(),
                refreshButton.getY() + refreshButton.getHeight(),
                0xFF606060
        );
//        context.drawCenteredTextWithShadow(
//                textRenderer,
//                "Draw Upgrades",
//                refreshButton.getX() + BUTTON_WIDTH/2, // 文字居中
//                refreshButton.getY() + BUTTON_PADDING,
//                0xFFFFFF
//        );
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}