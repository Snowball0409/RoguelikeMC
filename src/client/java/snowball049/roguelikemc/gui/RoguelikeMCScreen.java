package snowball049.roguelikemc.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.network.RoguelikeMCNetworkHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import snowball049.roguelikemc.network.RoguelikeMCNetworkHandler;

public class RoguelikeMCScreen extends Screen {
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

    // GUI 元素
    private final ButtonWidget[] optionButtons = new ButtonWidget[3];

    // GUI 尺寸
    private static final int GUI_WIDTH = 360;
    private static final int GUI_HEIGHT = 200;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 32;

    // 內容區域邊距
    private static final int CONTENT_PADDING = 20;
    private static final int SECTION_SPACING = 15;

    public RoguelikeMCScreen() {
        super(Text.literal("RoguelikeMC Upgrade"));
    }

    @Override
    protected void init() {
        super.init();
        // 初始化右側按鈕
        int baseX = (width + GUI_WIDTH)/2 - 120;
        int baseY = height/2 - 50;
        RoguelikeMCNetworkHandler networkHandler = new RoguelikeMCNetworkHandler();
        for(int i=0; i<3; i++) {
            final int index = i;
            optionButtons[i] = ButtonWidget.builder(Text.empty(), button -> {
                        if (currentOptions.size() > index) {
                            UpgradeEffect selected = currentOptions.get(index);
                            // 發送數據包到服務器
                            boolean correct = networkHandler.sendUpgradeToServer(selected);
                            // 清空選項
                            currentOptions.clear();
                            refreshOptionsDisplay();
                            if (correct) {
                                refreshUpgradeDisplay(selected);
                            }
                        }
                    })
                    .dimensions(baseX, baseY + i * 40, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build();
            this.addDrawableChild(optionButtons[i]);
        }
        // 隨機刷新按鈕
        ButtonWidget refreshButton = ButtonWidget.builder(Text.literal("抽取升級"), button -> {
                    currentOptions.clear();
                    List<UpgradeEffect> pool = new ArrayList<>(UPGRADE_POOL);
                    Collections.shuffle(pool);
                    for (int i = 0; i < 3 && i < pool.size(); i++) {
                        currentOptions.add(pool.get(i));
                    }
                    refreshOptionsDisplay();
                })
                .dimensions(width / 2 - 50, height - 40, 100, 20)
                .build();
        this.addDrawableChild(refreshButton);
    }

    @Override
    public boolean shouldPause() {
        return false; // 不暫停遊戲世界
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

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
        // 三欄式佈局
        int sectionWidth = (GUI_WIDTH - 2 * CONTENT_PADDING - 2 * SECTION_SPACING) / 3;

        // 暫時效果區域
        renderEffectsSection(context, x, y, sectionWidth, "Temporary Effects", TEMPORARY_EFFECTS, mouseX, mouseY);

        // 永久效果區域
        renderEffectsSection(context, x + sectionWidth + SECTION_SPACING, y, sectionWidth, "Permanent Effects", PERMANENT_EFFECTS, mouseX, mouseY);

        // 右側功能區域
        renderUtilitySection(context, x + 2 * (sectionWidth + SECTION_SPACING), y, sectionWidth, mouseX, mouseY);
    }

    private void renderEffectsSection(DrawContext context, int x, int y, int width, String title, List<UpgradeEffect> effects, int mouseX, int mouseY) {
        // 標題文字
        context.drawTextWithShadow(textRenderer, title, x, y, 0xFFFFFF);

        // 效果列表
        int itemHeight = 20;
        int itemWidth = 20; // 方塊寬度
        for (int i = 0; i < effects.size(); i++) {
            int itemY = y + 15 + i * itemHeight;

            // 繪製顏色方塊
            UpgradeEffect effect = effects.get(i);
            context.fill(x, itemY, x + itemWidth, itemY + itemHeight, effect.color | 0xFF000000); // 設置透明度

            // 檢查滑鼠懸停
            if (isMouseOver(mouseX, mouseY, x, itemY, itemWidth, itemHeight)) {
                // 繪製懸停提示
                List<Text> tooltip = Arrays.asList(
                        Text.literal(effect.name).formatted(Formatting.GREEN),
                        Text.literal(effect.description).formatted(Formatting.GRAY)
                );
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }
        }
    }

    private void renderUtilitySection(DrawContext context, int x, int y, int width, int mouseX, int mouseY) {
        // 繪製右側功能區域
        context.fill(x, y, x + width, y + GUI_HEIGHT - 2 * CONTENT_PADDING, 0x80000000);
        context.drawTextWithShadow(textRenderer, "Upgrade Pool", x + 10, y + 10, 0xFFFFFF);

        // 渲染右側按鈕內容
        int buttonX = x+1;
        int buttonY = y+1;
        for(int i=0; i<currentOptions.size(); i++){
            UpgradeEffect effect = currentOptions.get(i);
            // 繪製顏色區塊
            context.fill(buttonX, buttonY + i*40,
                    buttonX + 20, buttonY + i*40 + BUTTON_HEIGHT,
                    effect.color | 0xFF000000);
            // 繪製懸停提示
            if(optionButtons[i].isMouseOver(mouseX, mouseY)){
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
            this.isPermanent = false;
        }
    }
}