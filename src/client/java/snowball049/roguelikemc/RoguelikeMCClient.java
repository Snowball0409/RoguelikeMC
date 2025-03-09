package snowball049.roguelikemc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowball049.roguelikemc.gui.RoguelikeMCScreen;

public class RoguelikeMCClient implements ClientModInitializer {
	private static KeyBinding openGuiKey;
	public static final String MOD_ID = "roguelikemc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
				client.setScreen(new RoguelikeMCScreen());
			}
		});
	}
}