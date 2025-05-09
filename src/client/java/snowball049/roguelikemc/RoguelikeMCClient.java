package snowball049.roguelikemc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import snowball049.roguelikemc.data.RoguelikeMCClientData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.gui.RoguelikeMCScreen;
import snowball049.roguelikemc.network.packet.RefreshCurrentBossStageS2CPayload;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.SendUpgradePointsS2CPayload;
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
			RoguelikeMCUpgradeData upgrade = payload.upgrade();
			if(upgrade != null) RoguelikeMCClientData.INSTANCE.currentOptions.add(upgrade);
		});
		// Refresh Current Upgrades
		ClientPlayNetworking.registerGlobalReceiver(RefreshCurrentUpgradeS2CPayload.ID, (payload, context) -> {
			if(payload.is_permanent()){
				RoguelikeMCClientData.INSTANCE.permanentUpgrades.clear();
				RoguelikeMCClientData.INSTANCE.permanentUpgrades.addAll(payload.upgrades());
			} else {
				RoguelikeMCClientData.INSTANCE.temporaryUpgrades.clear();
				RoguelikeMCClientData.INSTANCE.temporaryUpgrades.addAll(payload.upgrades());
			}
		});
		// Refresh Upgrade Points
		ClientPlayNetworking.registerGlobalReceiver(SendUpgradePointsS2CPayload.ID, (payload, context) -> {
			RoguelikeMCClientData.INSTANCE.currentPoints = payload.point();
		});
		// Refresh Next Boss
		ClientPlayNetworking.registerGlobalReceiver(RefreshCurrentBossStageS2CPayload.ID, (payload, context) -> {
			RoguelikeMCClientData.INSTANCE.nextBoss = Identifier.tryParse(payload.nextBoss());
		});
	}
}