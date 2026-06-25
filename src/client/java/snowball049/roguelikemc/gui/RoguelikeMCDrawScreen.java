package snowball049.roguelikemc.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import snowball049.roguelikemc.RoguelikeMCClient;
import snowball049.roguelikemc.data.RoguelikeMCClientData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.upgrade.UpgradePresentation;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RoguelikeMCDrawScreen extends Screen {
    private final ButtonWidget[] optionButtons = new ButtonWidget[Assets.OPTION_COUNT];
    private final Screen previousScreen;

    private ButtonWidget refreshButton;
    private Layout layout;
    private int errorMessageTicks;
    private Text errorMessage;

    public RoguelikeMCDrawScreen(Screen previousScreen) {
        super(Text.literal("RoguelikeMC Draw"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        super.init();
        layout = Layout.compute(width, height, textRenderer.fontHeight);
        initOptionButtons(ensureLayout());
        initRefreshButton(ensureLayout());
        updateButtonState();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Layout currentLayout = ensureLayout();
        drawOverlay(context);
        super.render(context, mouseX, mouseY, delta);
        renderScreen(context, currentLayout, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();
        updateButtonState();
        tickErrorMessage();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            returnToPreviousScreen();
            return true;
        }
        if (RoguelikeMCClient.getOpenDrawGuiKey().matchesKey(keyCode, scanCode)) {
            returnToPreviousScreen();
            return true;
        }
        if (client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private Layout ensureLayout() {
        if (layout == null) {
            layout = Layout.compute(width, height, textRenderer.fontHeight);
        }
        return layout;
    }

    private void initOptionButtons(Layout currentLayout) {
        for (int i = 0; i < Assets.OPTION_COUNT; i++) {
            final int index = i;
            int x = currentLayout.cardStartX() + i * (currentLayout.cardWidth() + currentLayout.cardSpacing());
            optionButtons[i] = ButtonWidget.builder(Text.empty(), button -> selectUpgrade(index))
                    .dimensions(x, currentLayout.cardY(), currentLayout.cardWidth(), currentLayout.cardHeight())
                    .build();
            addDrawableChild(optionButtons[i]);
        }
    }

    private void initRefreshButton(Layout currentLayout) {
        refreshButton = ButtonWidget.builder(Text.translatable("button.roguelikemc.draw_upgrades"), button -> requestOptions())
                .dimensions(width / 2 - 55, currentLayout.refreshY(), 110, 20)
                .build();
        addDrawableChild(refreshButton);
    }

    private void renderScreen(DrawContext context, Layout currentLayout, int mouseX, int mouseY) {
        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.translatable("button.roguelikemc.draw_upgrades"),
                width / 2,
                currentLayout.titleY(),
                0xFFFFFF
        );
        drawPointHeader(context, currentLayout.pointY());

        for (int i = 0; i < Assets.OPTION_COUNT; i++) {
            drawUpgradeCard(context, optionButtons[i], i, currentLayout.cardWidth(), mouseX, mouseY);
        }

        drawStatusMessage(context, currentLayout.hintY());
    }

    private void drawOverlay(DrawContext context) {
        context.fill(0, 0, width, height, 0x88000000);
    }

    private void drawPointHeader(DrawContext context, int pointY) {
        Text pointText = Text.translatable("gui.roguelikemc.upgrade_points")
                .append(Text.literal(String.valueOf(RoguelikeMCClientData.INSTANCE.currentPoints)));

        int iconSize = Assets.POINT_ICON_SIZE;
        int spacing = Assets.POINT_ICON_SPACING;
        int textWidth = textRenderer.getWidth(pointText);
        int startX = width / 2 - (iconSize + spacing + textWidth) / 2;

        context.drawTexture(
                Assets.POINT_ICON,
                startX,
                pointY - 1,
                0,
                0,
                iconSize,
                iconSize,
                iconSize,
                iconSize
        );
        context.drawTextWithShadow(
                textRenderer,
                pointText,
                startX + iconSize + spacing,
                pointY,
                0xD397FE
        );
    }

    private void drawUpgradeCard(
            DrawContext context,
            ButtonWidget button,
            int index,
            int cardWidth,
            int mouseX,
            int mouseY
    ) {
        int x = button.getX();
        int y = button.getY();
        boolean hovered = button.isMouseOver(mouseX, mouseY);

        drawCardFrame(context, button, hovered);

        if (index >= RoguelikeMCClientData.INSTANCE.currentOptions.size()) {
            drawEmptyCard(context, button);
            return;
        }

        drawFilledCard(context, button, RoguelikeMCClientData.INSTANCE.currentOptions.get(index), cardWidth, hovered, mouseX, mouseY);
    }

    private void drawCardFrame(DrawContext context, ButtonWidget button, boolean hovered) {
        int x = button.getX();
        int y = button.getY();
        int backgroundColor = hovered ? 0xE0303030 : 0xC0202020;
        int borderColor = hovered ? 0xFFFFFFFF : 0xFF808080;

        context.fill(x, y, x + button.getWidth(), y + button.getHeight(), backgroundColor);
        context.drawBorder(x, y, button.getWidth(), button.getHeight(), borderColor);
    }

    private void drawEmptyCard(DrawContext context, ButtonWidget button) {
        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("?").formatted(Formatting.DARK_GRAY),
                button.getX() + button.getWidth() / 2,
                button.getY() + button.getHeight() / 2 - 4,
                0x666666
        );
    }

    private void drawFilledCard(
            DrawContext context,
            ButtonWidget button,
            RoguelikeMCUpgradeData upgrade,
            int cardWidth,
            boolean hovered,
            int mouseX,
            int mouseY
    ) {
        int x = button.getX();
        int y = button.getY();
        Formatting rarityColor = UpgradePresentation.rarityColor(upgrade);
        int textColor = rarityColor.getColorValue() != null ? rarityColor.getColorValue() : 0xFFFFFF;

        int iconSize = Math.max(Cards.MIN_ICON_SIZE, Math.min(Cards.MAX_ICON_SIZE, cardWidth / 2 - 12));
        int iconY = y + Cards.CARD_PADDING;
        int nameY = iconY + iconSize + Cards.NAME_GAP;
        int tagsY = nameY + textRenderer.fontHeight + Cards.TAG_GAP;
        int descriptionY = tagsY + textRenderer.fontHeight + Cards.DESCRIPTION_GAP;

        Identifier icon = Identifier.tryParse(upgrade.icon());
        if (icon != null) {
            context.drawTexture(
                    icon,
                    x + button.getWidth() / 2 - iconSize / 2,
                    iconY,
                    0,
                    0,
                    iconSize,
                    iconSize,
                    iconSize,
                    iconSize
            );
        }

        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.translatable(upgrade.name()).formatted(rarityColor, Formatting.BOLD),
                x + button.getWidth() / 2,
                nameY,
                textColor
        );
        context.drawCenteredTextWithShadow(
                textRenderer,
                UpgradePresentation.classificationTags(upgrade),
                x + button.getWidth() / 2,
                tagsY,
                0xFFFFFF
        );
        context.drawTextWrapped(
                textRenderer,
                Text.translatable(upgrade.description()).formatted(Formatting.GRAY),
                x + Cards.CARD_PADDING,
                descriptionY,
                button.getWidth() - Cards.CARD_PADDING * 2,
                0xCFCFCF
        );

        if (hovered) {
            context.drawTooltip(
                    textRenderer,
                    List.of(
                            Text.translatable(upgrade.name()).formatted(rarityColor),
                            Text.translatable(upgrade.description()).formatted(Formatting.GRAY)
                    ),
                    mouseX,
                    mouseY
            );
        }
    }

    private void drawStatusMessage(DrawContext context, int hintY) {
        if (errorMessage == null) {
            return;
        }

        Text displayText = errorMessage.copy().formatted(Formatting.RED);
        int maxTextWidth = Math.max(80, width - Assets.HORIZONTAL_MARGIN * 4);
        int textWidth = Math.min(maxTextWidth, textRenderer.getWidth(displayText));
        context.drawTextWrapped(textRenderer, displayText, width / 2 - textWidth / 2, hintY, maxTextWidth, 0xFF5555);
    }

    private void requestOptions() {
        if (!RoguelikeMCClientData.INSTANCE.currentOptions.isEmpty()) {
            return;
        }

        if (RoguelikeMCClientData.INSTANCE.currentPoints <= 0) {
            showErrorMessage(Text.translatable("message.roguelikemc.not_enough_upgrade_point"));
            return;
        }

        clearErrorMessage();
        ClientPlayNetworking.send(new RefreshUpgradeOptionC2SPayload());
    }

    private void selectUpgrade(int index) {
        if (index >= RoguelikeMCClientData.INSTANCE.currentOptions.size()) {
            return;
        }

        RoguelikeMCUpgradeData selected = RoguelikeMCClientData.INSTANCE.currentOptions.get(index);
        ClientPlayNetworking.send(new SelectUpgradeOptionC2SPayload(selected));
        RoguelikeMCClientData.INSTANCE.currentOptions.clear();
        returnToPreviousScreen();
    }

    private void updateButtonState() {
        boolean hasOptions = !RoguelikeMCClientData.INSTANCE.currentOptions.isEmpty();
        refreshButton.active = !hasOptions;

        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].active = i < RoguelikeMCClientData.INSTANCE.currentOptions.size();
        }
    }

    private void tickErrorMessage() {
        if (errorMessageTicks <= 0) {
            return;
        }

        errorMessageTicks--;
        if (errorMessageTicks == 0) {
            errorMessage = null;
        }
    }

    private void showErrorMessage(Text message) {
        errorMessage = message;
        errorMessageTicks = Assets.ERROR_DISPLAY_TICKS;
    }

    private void clearErrorMessage() {
        errorMessage = null;
        errorMessageTicks = 0;
    }

    private void returnToPreviousScreen() {
        if (client != null && previousScreen != null) {
            client.setScreen(previousScreen);
            return;
        }
        close();
    }

    private record Layout(
            int titleY,
            int pointY,
            int hintY,
            int cardWidth,
            int cardHeight,
            int cardSpacing,
            int cardStartX,
            int cardY,
            int refreshY
    ) {
        static Layout compute(int screenWidth, int screenHeight, int fontHeight) {
            int titleY = Assets.HEADER_TOP;
            int pointY = titleY + fontHeight + Assets.HEADER_GAP;

            int cardSpacing = Math.max(8, Math.min(Assets.BASE_CARD_SPACING, screenWidth / 40));
            int availableWidth = Math.max(
                    Assets.MIN_CARD_WIDTH * Assets.OPTION_COUNT,
                    screenWidth - Assets.HORIZONTAL_MARGIN * 2 - cardSpacing * (Assets.OPTION_COUNT - 1)
            );
            int cardWidth = Math.max(Assets.MIN_CARD_WIDTH, Math.min(Assets.BASE_CARD_WIDTH, availableWidth / Assets.OPTION_COUNT));

            int headerBottom = pointY + fontHeight;
            int footerHeight = 20 + Assets.FOOTER_GAP + fontHeight;
            int availableHeight = screenHeight - headerBottom - footerHeight - Assets.CONTENT_GAP * 2;
            int cardHeight = Math.max(Assets.MIN_CARD_HEIGHT, Math.min(Assets.BASE_CARD_HEIGHT, availableHeight));

            int totalWidth = Assets.OPTION_COUNT * cardWidth + (Assets.OPTION_COUNT - 1) * cardSpacing;
            int cardStartX = (screenWidth - totalWidth) / 2;
            int cardY = Math.max(headerBottom + Assets.CONTENT_GAP, (screenHeight - cardHeight - footerHeight) / 2);
            int refreshY = cardY + cardHeight + Assets.FOOTER_GAP;
            int hintY = refreshY + 28;

            return new Layout(titleY, pointY, hintY, cardWidth, cardHeight, cardSpacing, cardStartX, cardY, refreshY);
        }
    }

    private static final class Assets {
        static final Identifier POINT_ICON = Identifier.of("roguelikemc", "textures/item/upgrade_point_orb.png");

        static final int OPTION_COUNT = 3;
        static final int BASE_CARD_WIDTH = 130;
        static final int BASE_CARD_HEIGHT = 170;
        static final int MIN_CARD_WIDTH = 92;
        static final int MIN_CARD_HEIGHT = 136;
        static final int BASE_CARD_SPACING = 18;
        static final int HORIZONTAL_MARGIN = 16;
        static final int HEADER_TOP = 20;
        static final int HEADER_GAP = 8;
        static final int CONTENT_GAP = 14;
        static final int FOOTER_GAP = 12;
        static final int ERROR_DISPLAY_TICKS = 60;

        static final int POINT_ICON_SIZE = 10;
        static final int POINT_ICON_SPACING = 4;

        private Assets() {
        }
    }

    private static final class Cards {
        static final int CARD_PADDING = 10;
        static final int MIN_ICON_SIZE = 28;
        static final int MAX_ICON_SIZE = 48;
        static final int NAME_GAP = 10;
        static final int TAG_GAP = 4;
        static final int DESCRIPTION_GAP = 8;

        private Cards() {
        }
    }
}
