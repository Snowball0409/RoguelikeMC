package snowball049.roguelikemc.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import java.util.Arrays;
import java.util.List;

public class RoguelikeMCScreen extends Screen {
    // 模擬靜態數據
    private static final List<UpgradeEffect> TEMPORARY_EFFECTS = Arrays.asList(
            new UpgradeEffect("+10% Speed", "暫時提升移動速度", 0x00FF00),
            new UpgradeEffect("Attack Boost", "暫時增加攻擊力", 0xFF0000),
            new UpgradeEffect("Jump Boost", "暫時增加跳躍高度", 0xFFFF00)
    );

    private static final List<UpgradeEffect> PERMANENT_EFFECTS = Arrays.asList(
            new UpgradeEffect("Health Up", "永久增加最大生命值", 0xFFA500),
            new UpgradeEffect("Armor Reinforce", "永久提升護甲值", 0x0000FF),
            new UpgradeEffect("Luck", "永久提升幸運值", 0x800080)
    );

    // 自定義 GUI 背景圖
    private static final Identifier BACKGROUND_TEXTURE = Identifier.tryParse("roguelikemc", "textures/gui/upgrade_bg.png");

    // GUI 尺寸
    private static final int GUI_WIDTH = 360;
    private static final int GUI_HEIGHT = 240;

    // 內容區域邊距
    private static final int CONTENT_PADDING = 20;
    private static final int SECTION_SPACING = 15;

    public RoguelikeMCScreen() {
        super(Text.literal("RoguelikeMC Upgrade"));
    }

    @Override
    protected void init() {
        super.init();
        // 初始化按鈕等元件（未來擴展用）
    }

    @Override
    public boolean shouldPause() {
        return false; // 不暫停遊戲世界
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 計算動態縮放比例
        double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        scaleFactor = 1.0;

        // 繪製自適應背景
        int guiLeft = (int)((width - GUI_WIDTH * scaleFactor) / 2);
        int guiTop = (int)((height - GUI_HEIGHT * scaleFactor) / 2);

        context.getMatrices().push();
        context.getMatrices().scale((float)scaleFactor, (float)scaleFactor, 1.0f);

        // 繪製高清背景
        context.drawTexture(
                BACKGROUND_TEXTURE,
                (int)(guiLeft / scaleFactor),
                (int)(guiTop / scaleFactor),
                0, 0, GUI_WIDTH, GUI_HEIGHT,
                GUI_WIDTH, GUI_HEIGHT
        );

        // 渲染內容區域
        renderContent(context,
                (int)(guiLeft / scaleFactor) + CONTENT_PADDING,
                (int)(guiTop / scaleFactor) + CONTENT_PADDING,
                mouseX, mouseY);

        context.getMatrices().pop();
    }

    private void renderContent(DrawContext context, int x, int y, int mouseX, int mouseY) {
        // 三欄式佈局
        int sectionWidth = (GUI_WIDTH - 2 * CONTENT_PADDING - 2 * SECTION_SPACING) / 3;

        // 暫時效果區域
        renderEffectsSection(context, x, y, sectionWidth, "Temporary Effects", TEMPORARY_EFFECTS, mouseX, mouseY);

        // 永久效果區域
        renderEffectsSection(context, x + sectionWidth + SECTION_SPACING, y, sectionWidth, "Permanent Effects", PERMANENT_EFFECTS, mouseX, mouseY);

        // 右側功能區域
        renderUtilitySection(context, x + 2 * (sectionWidth + SECTION_SPACING), y, sectionWidth);
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

    private void renderUtilitySection(DrawContext context, int x, int y, int width) {
        // 繪製右側功能區域
        context.fill(x, y, x + width, y + GUI_HEIGHT - 2 * CONTENT_PADDING, 0x80000000);
        context.drawTextWithShadow(textRenderer, "Special Abilities", x + 10, y + 10, 0xFFFFFF);
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

    private static class UpgradeEffect {
        String name;
        String description;
        int color;

        UpgradeEffect(String name, String description, int color) {
            this.name = name;
            this.description = description;
            this.color = color;
        }
    }
}