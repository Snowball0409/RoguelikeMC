package snowball049.roguelikemc.gui;

import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class RoguelikeMCConfigScreen extends Screen {
    private final Screen parent;
    private final List<ConfigOption> configOptions = new ArrayList<>();

    // Scrolling related variables
    private int scrollPosition = 0;
    private boolean isDragging = false;
    private int contentHeight;
    private static final int OPTION_HEIGHT = 25;
    private static final int SCROLL_SPEED = 10;
    private static final int TOP_PADDING = 40;  // Space at the top for title
    private static final int BOTTOM_PADDING = 50;  // Space at the bottom for save button

    private int scrollableAreaHeight;  // Height of the scrollable area
    private int maxScrollPosition;     // Maximum scroll position

    public RoguelikeMCConfigScreen(Screen parent) {
        super(Text.literal("RoguelikeMC Common Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Calculate the height of the scrollable area
        scrollableAreaHeight = height - TOP_PADDING - BOTTOM_PADDING;

        // Create config options (not yet positioned on screen)
        createConfigOptions();

        // Calculate total content height
        contentHeight = configOptions.size() * OPTION_HEIGHT;

        // Calculate max scroll position
        maxScrollPosition = Math.max(0, contentHeight - scrollableAreaHeight);

        // Clamp scroll position
        scrollPosition = MathHelper.clamp(scrollPosition, 0, maxScrollPosition);

        // Update button positions based on scroll position
        updateButtonPositions();

        // Add save button at the bottom
        addDrawableChild(ButtonWidget.builder(Text.literal("Save & Exit"), button -> {
            RoguelikeMCCommonConfig.saveConfig();
            assert client != null;
            client.setScreen(parent);
        }).position(width / 2 - 100, height - 30).size(200, 20).build());
    }

    private void createConfigOptions() {
        configOptions.clear();

        addConfigOption("Enable Upgrade System",
                () -> RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem,
                value -> RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem = value
        );

        addConfigOption("Enable Kill Hostile Upgrade",
                () -> RoguelikeMCCommonConfig.INSTANCE.enableKillHostileEntityUpgrade,
                value -> RoguelikeMCCommonConfig.INSTANCE.enableKillHostileEntityUpgrade = value
        );

        addConfigOption("Enable Advancement Upgrade",
                () -> RoguelikeMCCommonConfig.INSTANCE.enableAdvancementUpgrade,
                value -> RoguelikeMCCommonConfig.INSTANCE.enableAdvancementUpgrade = value
        );

        addConfigOption("Enable Level Upgrade",
                () -> RoguelikeMCCommonConfig.INSTANCE.enableLevelUpgrade,
                value -> RoguelikeMCCommonConfig.INSTANCE.enableLevelUpgrade = value
        );

        addConfigOption("Enable Clear Inventory on Death",
                () -> RoguelikeMCCommonConfig.INSTANCE.enableClearInventoryAfterDeath,
                value -> RoguelikeMCCommonConfig.INSTANCE.enableClearInventoryAfterDeath = value
        );

        addConfigOption("Enable Clear Equipment on Death",
                () -> RoguelikeMCCommonConfig.INSTANCE.enableClearEquipmentAfterDeath,
                value -> RoguelikeMCCommonConfig.INSTANCE.enableClearEquipmentAfterDeath = value
        );

        addConfigOption("Enable Decay Inventory on Death",
                () -> RoguelikeMCCommonConfig.INSTANCE.enableDecayInventoryAfterDeath,
                value -> RoguelikeMCCommonConfig.INSTANCE.enableDecayInventoryAfterDeath = value
        );

        addConfigOption("Enable Decay Equipment on Death",
                () -> RoguelikeMCCommonConfig.INSTANCE.enableDecayEquipmentAfterDeath,
                value -> RoguelikeMCCommonConfig.INSTANCE.enableDecayEquipmentAfterDeath = value
        );

        addConfigOption("Enable Linear Game Stage",
                () -> RoguelikeMCCommonConfig.INSTANCE.enableLinearGameStage,
                value -> RoguelikeMCCommonConfig.INSTANCE.enableLinearGameStage = value
        );
    }

    private void addConfigOption(String label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        int labelX = width / 2 - 200; // Label on left
        int buttonX = width - 120; // Button on right
        int buttonWidth = 80;

        // Create the button but don't position it yet
        ButtonWidget button = ButtonWidget.builder(getToggleMessage(getter.get()), b -> {
            boolean newValue = !getter.get();
            setter.accept(newValue);
            b.setMessage(getToggleMessage(newValue));
        }).size(buttonWidth, 20).build();
        button.setX(buttonX);

        // Add the button to the screen
        addDrawableChild(button);

        // Store the config option
        configOptions.add(new ConfigOption(label, labelX, 0, button));
    }

    private void updateButtonPositions() {
        // Remove all existing buttons first
        clearChildren();

        // Add back the buttons with updated positions
        for (int i = 0; i < configOptions.size(); i++) {
            ConfigOption option = configOptions.get(i);

            // Calculate y position based on scroll
            int yPosition = TOP_PADDING + (i * OPTION_HEIGHT) - scrollPosition;
            option.y = yPosition;

            // Only add the button back if it's in the visible area
            if (yPosition + OPTION_HEIGHT > TOP_PADDING && yPosition < height - BOTTOM_PADDING) {
                option.button.setPosition(option.button.getX(), yPosition);
                addDrawableChild(option.button);
            }
        }

        // Re-add the save button
        addDrawableChild(ButtonWidget.builder(Text.literal("Save & Exit"), button -> {
            RoguelikeMCCommonConfig.saveConfig();
            assert client != null;
            client.setScreen(parent);
        }).position(width / 2 - 100, height - 30).size(200, 20).build());
    }

    private Text getToggleMessage(boolean value) {
        return Text.literal(value ? "§aEnabled" : "§cDisabled");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        renderBackground(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);

        // Draw dark background on top padding and bottom padding
        context.fill(0, 0, width, TOP_PADDING - 5, 0x80000000);
        context.fill(0, height - BOTTOM_PADDING + 5, width, height, 0x80000000);

        // Draw title
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFF);

        // Draw scrollbar if needed
        if (contentHeight > scrollableAreaHeight) {
            int scrollbarX = width - 10;
            int scrollbarY = TOP_PADDING;
            int scrollbarHeight = scrollableAreaHeight;

            // Draw scrollbar background
            context.fill(scrollbarX - 1, scrollbarY, scrollbarX + 1, scrollbarY + scrollbarHeight, 0x80808080);

            // Draw scrollbar thumb
            float scrollbarThumbHeight = ((float) scrollableAreaHeight / contentHeight) * scrollbarHeight;
            float scrollbarThumbY = scrollbarY + ((float) scrollPosition / maxScrollPosition) * (scrollbarHeight - scrollbarThumbHeight);

            context.fill(scrollbarX - 2, (int) scrollbarThumbY, scrollbarX + 2,
                    (int) (scrollbarThumbY + scrollbarThumbHeight), 0xFFFFFFFF);
        }

        // Draw config options
        for (ConfigOption option : configOptions) {
            // Only draw if within the visible area
            if (option.y + OPTION_HEIGHT > TOP_PADDING && option.y < height - BOTTOM_PADDING) {
                int textYOffset = (20 - textRenderer.fontHeight) / 2;
                context.drawText(textRenderer, option.label, option.x, option.y + textYOffset, 0xFFFFFF, false);
            }
        }

        // Redraw the Save & Exit button on top of everything
        // Get the last child which should be the Save button
        if (!children().isEmpty()) {
            ButtonWidget saveButton = (ButtonWidget) children().getLast();
            saveButton.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Scroll up or down
        scrollPosition = MathHelper.clamp(scrollPosition - (int)(verticalAmount * SCROLL_SPEED), 0, maxScrollPosition);
        updateButtonPositions();
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && isDragging) {
            // Handle dragging the scrollbar
            if (mouseX > width - 20) {
                float scrollRatio = (float) deltaY / (scrollableAreaHeight - (scrollableAreaHeight * scrollableAreaHeight / contentHeight));
                scrollPosition = MathHelper.clamp(scrollPosition + (int)(scrollRatio * maxScrollPosition), 0, maxScrollPosition);
                updateButtonPositions();
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Start dragging if clicking on the scrollbar
        if (button == 0 && mouseX > width - 20 && mouseY >= TOP_PADDING && mouseY <= height - BOTTOM_PADDING) {
            isDragging = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Stop dragging
        if (button == 0) {
            isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(parent);
    }

    private static class ConfigOption {
        String label;
        int x, y;
        ButtonWidget button;

        ConfigOption(String label, int x, int y, ButtonWidget button) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.button = button;
        }
    }
}