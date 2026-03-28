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

import java.util.List;

@Environment(EnvType.CLIENT)
public class RoguelikeMCDrawScreen extends Screen {
    private static final int OPTION_COUNT = 3;
    private static final int BASE_CARD_WIDTH = 130;
    private static final int BASE_CARD_HEIGHT = 170;
    private static final int MIN_CARD_WIDTH = 92;
    private static final int MIN_CARD_HEIGHT = 136;
    private static final int BASE_CARD_SPACING = 18;
    private static final int HORIZONTAL_MARGIN = 16;
    private static final int HEADER_TOP = 20;
    private static final int HEADER_GAP = 8;
    private static final int CONTENT_GAP = 14;
    private static final int FOOTER_GAP = 12;
    private static final int ERROR_DISPLAY_TICKS = 60;

    private final ButtonWidget[] optionButtons = new ButtonWidget[OPTION_COUNT];
    private final Screen previousScreen;
    private ButtonWidget refreshButton;

    private int cardWidth;
    private int cardHeight;
    private int cardSpacing;
    private int cardY;
    private int titleY;
    private int pointY;
    private int hintY;
    private int errorMessageTicks;
    private Text errorMessage;

    public RoguelikeMCDrawScreen(Screen previousScreen) {
        super(Text.literal("RoguelikeMC Draw"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        super.init();

        titleY = HEADER_TOP;
        pointY = titleY + textRenderer.fontHeight + HEADER_GAP;

        cardSpacing = Math.max(8, Math.min(BASE_CARD_SPACING, width / 40));
        int availableWidth = Math.max(MIN_CARD_WIDTH * OPTION_COUNT, width - HORIZONTAL_MARGIN * 2 - cardSpacing * (OPTION_COUNT - 1));
        cardWidth = Math.max(MIN_CARD_WIDTH, Math.min(BASE_CARD_WIDTH, availableWidth / OPTION_COUNT));

        int headerBottom = pointY + textRenderer.fontHeight;
        int footerHeight = 20 + FOOTER_GAP + textRenderer.fontHeight;
        int availableHeight = height - headerBottom - footerHeight - CONTENT_GAP * 2;
        cardHeight = Math.max(MIN_CARD_HEIGHT, Math.min(BASE_CARD_HEIGHT, availableHeight));

        int totalWidth = OPTION_COUNT * cardWidth + (OPTION_COUNT - 1) * cardSpacing;
        int startX = (width - totalWidth) / 2;
        cardY = Math.max(headerBottom + CONTENT_GAP, (height - cardHeight - footerHeight) / 2);

        for (int i = 0; i < OPTION_COUNT; i++) {
            final int index = i;
            optionButtons[i] = ButtonWidget.builder(Text.empty(), button -> selectUpgrade(index))
                    .dimensions(startX + i * (cardWidth + cardSpacing), cardY, cardWidth, cardHeight)
                    .build();
            addDrawableChild(optionButtons[i]);
        }

        int refreshY = cardY + cardHeight + FOOTER_GAP;
        refreshButton = ButtonWidget.builder(Text.translatable("button.roguelikemc.draw_upgrades"), button -> requestOptions())
                .dimensions(width / 2 - 55, refreshY, 110, 20)
                .build();
        addDrawableChild(refreshButton);

        hintY = refreshY + 28;
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
        renderOverlayBackground(context);
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("button.roguelikemc.draw_upgrades"), width / 2, titleY, 0xFFFFFF);
        renderPointHeader(context);

        for (int i = 0; i < OPTION_COUNT; i++) {
            renderUpgradeCard(context, optionButtons[i], i, mouseX, mouseY);
        }

        renderStatusMessage(context);

//        Text hint = RoguelikeMCClientData.INSTANCE.currentOptions.isEmpty()
//                ? Text.literal("Draw upgrades to reveal three choices").formatted(Formatting.GRAY)
//                : Text.literal("Choose one upgrade").formatted(Formatting.GRAY);
//        context.drawCenteredTextWithShadow(textRenderer, hint, width / 2, hintY, 0xB0B0B0);
    }

    @Override
    public void tick() {
        super.tick();
        updateButtonState();
        if (errorMessageTicks > 0) {
            errorMessageTicks--;
            if (errorMessageTicks == 0) {
                errorMessage = null;
            }
        }
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

    private void renderOverlayBackground(DrawContext context) {
        context.fill(0, 0, width, height, 0x88000000);
    }

    private void renderPointHeader(DrawContext context) {
        Text pointText = Text.translatable("gui.roguelikemc.upgrade_points")
                .append(Text.literal(String.valueOf(RoguelikeMCClientData.INSTANCE.currentPoints)));
        int iconSize = 10;
        int spacing = 4;
        int textWidth = textRenderer.getWidth(pointText);
        int totalWidth = iconSize + spacing + textWidth;
        int startX = width / 2 - totalWidth / 2;

        context.drawTexture(
                Identifier.tryParse("roguelikemc", "textures/item/upgrade_point_orb.png"),
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

    private void renderUpgradeCard(DrawContext context, ButtonWidget button, int index, int mouseX, int mouseY) {
        int x = button.getX();
        int y = button.getY();
        boolean hovered = button.isMouseOver(mouseX, mouseY);
        int backgroundColor = hovered ? 0xE0303030 : 0xC0202020;
        int borderColor = hovered ? 0xFFFFFFFF : 0xFF808080;

        context.fill(x, y, x + button.getWidth(), y + button.getHeight(), backgroundColor);
        context.drawBorder(x, y, button.getWidth(), button.getHeight(), borderColor);

        if (index >= RoguelikeMCClientData.INSTANCE.currentOptions.size()) {
            context.drawCenteredTextWithShadow(
                    textRenderer,
                    Text.literal("?").formatted(Formatting.DARK_GRAY),
                    x + button.getWidth() / 2,
                    y + button.getHeight() / 2 - 4,
                    0x666666
            );
            return;
        }

        RoguelikeMCUpgradeData upgrade = RoguelikeMCClientData.INSTANCE.currentOptions.get(index);
        Formatting rarityColor = getColorByRarity(upgrade.tier());
        int textColor = rarityColor.getColorValue() != null ? rarityColor.getColorValue() : 0xFFFFFF;

        int iconSize = Math.max(28, Math.min(48, cardWidth / 2 - 12));
        int iconY = y + 12;
        int nameY = iconY + iconSize + 10;
        int tagsY = nameY + textRenderer.fontHeight + 4;
        int descriptionY = tagsY + textRenderer.fontHeight + 8;

        context.drawTexture(
                Identifier.tryParse(upgrade.icon()),
                x + button.getWidth() / 2 - iconSize / 2,
                iconY,
                0,
                0,
                iconSize,
                iconSize,
                iconSize,
                iconSize
        );
        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.translatable(upgrade.name()).formatted(rarityColor, Formatting.BOLD),
                x + button.getWidth() / 2,
                nameY,
                textColor
        );

        Text tags = (upgrade.isPermanent() ? Text.literal("[P]").formatted(Formatting.GOLD) : Text.literal("[T]").formatted(Formatting.DARK_GRAY))
                .append(upgrade.isUnique() ? Text.literal(" [U]").formatted(Formatting.LIGHT_PURPLE) : Text.empty());
        context.drawCenteredTextWithShadow(textRenderer, tags, x + button.getWidth() / 2, tagsY, 0xFFFFFF);

        context.drawTextWrapped(
                textRenderer,
                Text.translatable(upgrade.description()).formatted(Formatting.GRAY),
                x + 10,
                descriptionY,
                button.getWidth() - 20,
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

    private void requestOptions() {
        if (!RoguelikeMCClientData.INSTANCE.currentOptions.isEmpty()) {
            return;
        }

        if (RoguelikeMCClientData.INSTANCE.currentPoints <= 0) {
            showErrorMessage(Text.translatable("message.roguelikemc.not_enough_upgrade_point"));
            return;
        }

        errorMessage = null;
        errorMessageTicks = 0;
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

    private Formatting getColorByRarity(String rarity) {
        return switch (rarity) {
            case "common" -> Formatting.WHITE;
            case "rare" -> Formatting.BLUE;
            case "epic" -> Formatting.DARK_PURPLE;
            case "legendary" -> Formatting.GOLD;
            default -> Formatting.WHITE;
        };
    }

    private void returnToPreviousScreen() {
        if (client != null && previousScreen != null) {
            client.setScreen(previousScreen);
            return;
        }
        close();
    }

    private void renderStatusMessage(DrawContext context) {
        if (errorMessage == null) {
            return;
        }

        Text displayText = errorMessage.copy().formatted(Formatting.RED);
        int maxTextWidth = Math.max(80, width - HORIZONTAL_MARGIN * 4);
        int textWidth = Math.min(maxTextWidth, textRenderer.getWidth(displayText));
        int textX = width / 2 - textWidth / 2;
        context.drawTextWrapped(textRenderer, displayText, textX, hintY, maxTextWidth, 0xFF5555);
    }

    private void showErrorMessage(Text message) {
        errorMessage = message;
        errorMessageTicks = ERROR_DISPLAY_TICKS;
    }
}
