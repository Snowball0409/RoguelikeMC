package snowball049.roguelikemc;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.gui.RoguelikeMCScreen;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;

public class RoguelikeMCClient implements ClientModInitializer {
	private static KeyMapping openGuiKey;
	private static final RoguelikeMCScreen currentScreen = new RoguelikeMCScreen();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.roguelikemc.open_gui",
				InputConstants.Type.KEYSYM,
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
			RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade = payload.upgrade();
			if(upgrade != null) currentScreen.currentOptions.add(upgrade);
		});
		// Refresh Current Upgrades
		ClientPlayNetworking.registerGlobalReceiver(RefreshCurrentUpgradeS2CPayload.ID, (payload, context) -> {
			currentScreen.refreshUpgradeDisplay(payload.upgrades());
		});
	}
}