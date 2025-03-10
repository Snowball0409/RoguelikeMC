package snowball049.roguelikemc.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowball049.roguelikemc.network.RoguelikeMCUpgradePacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RoguelikeMCScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoguelikeMCScreen.class);
    // 模擬靜態數據
    private static final List<UpgradeEffect> UPGRADE_POOL = Arrays.asList(
            new UpgradeEffect("+10% Speed", "暫時提升移動速度", 0x00FF00, false),
            new UpgradeEffect("Attack Boost", "暫時增加攻擊力", 0xFF0000, false),
            new UpgradeEffect("Health Up", "永久增加最大生命值", 0xFFA500, true),
            new UpgradeEffect("Armor Reinforce", "永久提升護甲值", 0x0000FF, true),
            new UpgradeEffect("Luck", "永久提升幸運值", 0x800080, true)
    );
    private final List<UpgradeEffect> TEMPORARY_EFFECTS = new ArrayList<>();
    private final List<UpgradeEffect> PERMANENT_EFFECTS = new ArrayList<>();
    private final List<UpgradeEffect> currentOptions = new ArrayList<>(3);

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
                        if (currentOptions.size() > index) {
                            UpgradeEffect selected = currentOptions.get(index);
                            // 發送數據包到服務器
                            boolean correct = RoguelikeMCUpgradePacket.sendUpgradeToServer(selected);
                            // 清空選項
                            currentOptions.clear();
                            refreshOptionsDisplay();
                            if (correct) {
                                refreshUpgradeDisplay(selected);
                            }
                        }
                    })
                    // 調整按鈕位置計算
                    .dimensions(
                            rightPanelX,
                            rightPanelY + i * (BUTTON_HEIGHT + BUTTON_PADDING),
                            BUTTON_WIDTH,  // 按鈕寬度配合右側區域
                            BUTTON_HEIGHT    // 按鈕高度
                    )
                    .build();
            this.addDrawableChild(optionButtons[i]);
        }

        refreshButton = ButtonWidget.builder(Text.literal("Draw Upgrades"), button -> {
                    currentOptions.clear();
                    List<UpgradeEffect> pool = new ArrayList<>(UPGRADE_POOL);
                    Collections.shuffle(pool);
                    for (int i = 0; i < 3 && i < pool.size(); i++) {
                        currentOptions.add(pool.get(i));
                    }
                    //refreshOptionsDisplay();
                })
                .dimensions(
                        guiLeft + GUI_WIDTH / 2 - 50, // GUI水平居中
                        guiTop + GUI_HEIGHT - 30,   // GUI底部向上30像素
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
        int guiLeft = (int)((width - GUI_WIDTH ) / 2);
        int guiTop = (int)((height - GUI_HEIGHT) / 2);

        context.getMatrices().push();

        // 繪製高清背景
        context.drawTexture(
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

        context.getMatrices().pop();
    }

    private void refreshOptionsDisplay(){
        for(int i=0; i<3; i++){
            if(i < currentOptions.size()){
                UpgradeEffect effect = currentOptions.get(i);
                optionButtons[i].setMessage(Text.literal(effect.name));
            }else{
                optionButtons[i].setMessage(Text.empty());
            }
        }
    }

    private void refreshUpgradeDisplay(UpgradeEffect effect){
        if(effect.isPermanent){
            PERMANENT_EFFECTS.add(effect);
        }else{
            TEMPORARY_EFFECTS.add(effect);
        }
    }

    private void renderContent(DrawContext context, int x, int y, int mouseX, int mouseY) {

        // 暫時效果區域
        renderEffectsSection(context, x, y, "Temporary Effects", TEMPORARY_EFFECTS, mouseX, mouseY);

        // 永久效果區域
        renderEffectsSection(context, x + SECTION_WIDTH + SECTION_SPACING, y, "Permanent Effects", PERMANENT_EFFECTS, mouseX, mouseY);

        // 右側功能區域
        renderUtilitySection(context, x + 2 * (SECTION_WIDTH + SECTION_SPACING), y, "Effect Pool", mouseX, mouseY);

        // 刷新按鈕渲染
        renderRefreshButton(context, x, y, mouseX, mouseY);
    }

    private void renderEffectsSection(DrawContext context, int x, int y, String title, List<UpgradeEffect> effects, int mouseX, int mouseY) {
        // 標題文字
        context.drawCenteredTextWithShadow(textRenderer, title, x+SECTION_WIDTH/2, y, 0xFFFFFF);

        // 效果列表
        int itemHeight = 20;
        int itemWidth = 20; // 方塊寬度
        int itemPadding = 2;

        for (int i = 0; i < effects.size(); i++) {
            int itemY = y + CONTENT_PADDING + itemPadding + Math.floorMod(i,6) * (itemHeight+itemPadding);
            int itemX = x + Math.divideExact(i, 6)*(itemWidth+itemPadding);

            // 繪製顏色方塊
            UpgradeEffect effect = effects.get(i);
            context.fill(itemX, itemY, itemX + itemWidth, itemY + itemHeight, effect.color | 0xFF000000); // 設置透明度

            // 檢查滑鼠懸停
            if (isMouseOver(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight)) {
                // 繪製懸停提示
                List<Text> tooltip = Arrays.asList(
                        Text.literal(effect.name).formatted(Formatting.GREEN),
                        Text.literal(effect.description).formatted(Formatting.GRAY)
                );
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }
        }
    }

    private void renderUtilitySection(DrawContext context, int x, int y, String title, int mouseX, int mouseY) {
        // 區域背景
        context.fill(x, y-5, x + SECTION_WIDTH, y + GUI_HEIGHT - 2*CONTENT_PADDING+5, 0x80303030);
        context.drawCenteredTextWithShadow(textRenderer, title, x+SECTION_WIDTH/2, y, 0xFFFFFF);

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
                UpgradeEffect effect = currentOptions.get(i);
                // 顏色方塊
                context.fill(
                        buttonX + BUTTON_PADDING,
                        buttonY + i * (BUTTON_HEIGHT + BUTTON_PADDING) + BUTTON_HEIGHT/2 - 10,
                        buttonX + BUTTON_PADDING + 20,
                        buttonY + i * (BUTTON_HEIGHT + BUTTON_PADDING) + BUTTON_HEIGHT/2 + 10,
                        effect.color | 0xFF000000
                );

                // 文字渲染
                final MultilineText effectName = MultilineText.create(textRenderer, Text.of(effect.name));
                context.drawTextWrapped(
                        textRenderer,
                        StringVisitable.plain(effect.name),
                        buttonX + BUTTON_PADDING * 2 + 20, // 顏色方塊右側
                        buttonY + i * (BUTTON_HEIGHT + BUTTON_PADDING) + BUTTON_HEIGHT/2 - BUTTON_PADDING, // 垂直居中
                        BUTTON_WIDTH - BUTTON_PADDING * 2 - 20,
                        0xFFFFFF
                );
                // 繪製懸停提示
                if (optionButtons[i].isMouseOver(mouseX, mouseY)) {
                    context.drawTooltip(textRenderer,
                            List.of(
                                    Text.literal(effect.name).formatted(Formatting.GREEN),
                                    Text.literal(effect.description).formatted(Formatting.GRAY)
                            ),
                            mouseX, mouseY
                    );
                }
            }
        }
    }

    private void renderRefreshButton(DrawContext context, int x, int y, int mouseX, int mouseY) {
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
        if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public static class UpgradeEffect {
        String name;
        String description;
        int color;
        boolean isPermanent;

        UpgradeEffect(String name, String description, int color, boolean isPermanent) {
            this.name = name;
            this.description = description;
            this.color = color;
            this.isPermanent = isPermanent;
        }
    }
}