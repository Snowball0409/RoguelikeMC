package snowball049.roguelikemc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;
import snowball049.roguelikemc.gui.RoguelikeMCScreen;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;

public class RoguelikeMCClient implements ClientModInitializer {
	private static KeyBinding openGuiKey;
	private static final RoguelikeMCScreen currentScreen = new RoguelikeMCScreen();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.roguelikemc.open_gui",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"category.roguelikemc.gui"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (openGuiKey.wasPressed()) {
				client.setScreen(currentScreen);
			}
		});

		// Netowrk Packet
		// Refresh Upgrade Options
		ClientPlayNetworking.registerGlobalReceiver(UpgradeOptionS2CPayload.ID, (payload, context) -> {
			RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade upgrade = payload.upgrade();
			if(upgrade != null) currentScreen.currentOptions.add(upgrade);
		});
		// Refresh Current Upgrades
		ClientPlayNetworking.registerGlobalReceiver(RefreshCurrentUpgradeS2CPayload.ID, (payload, context) -> {
			currentScreen.refreshUpgradeDisplay(payload.upgrades());
		});
	}
}