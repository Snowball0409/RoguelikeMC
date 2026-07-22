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
import snowball049.roguelikemc.gui.RoguelikeMCDrawScreen;
import snowball049.roguelikemc.gui.RoguelikeMCScreen;
import snowball049.roguelikemc.network.packet.RefreshCurrentBossStageS2CPayload;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.SendUpgradePointsS2CPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;

public class RoguelikeMCClient implements ClientModInitializer {
	private static KeyBinding openGuiKey;
	private static KeyBinding openDrawGuiKey;
	private static final RoguelikeMCScreen currentScreen = new RoguelikeMCScreen();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		registerKeyBindings();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (openGuiKey.wasPressed())
				client.setScreen(currentScreen);
			if (openDrawGuiKey.wasPressed())
				client.setScreen(new RoguelikeMCDrawScreen(null));
		});

		// Netowrk Packet
		// Refresh Upgrade Options
		ClientPlayNetworking.registerGlobalReceiver(UpgradeOptionS2CPayload.ID, (client, handler, buf, responseSender) -> {
			UpgradeOptionS2CPayload payload = UpgradeOptionS2CPayload.read(buf);
			client.execute(() -> {
				RoguelikeMCUpgradeData upgrade = payload.upgrade();
				if (upgrade != null)
					RoguelikeMCClientData.INSTANCE.currentOptions.add(upgrade);
			});
		});
		// Refresh Current Upgrades
		ClientPlayNetworking.registerGlobalReceiver(RefreshCurrentUpgradeS2CPayload.ID, (client, handler, buf, responseSender) -> {
			RefreshCurrentUpgradeS2CPayload payload = RefreshCurrentUpgradeS2CPayload.read(buf);
			client.execute(() -> {
				if (payload.isPermanent()) {
					RoguelikeMCClientData.INSTANCE.permanentUpgrades.clear();
					RoguelikeMCClientData.INSTANCE.permanentUpgrades.addAll(payload.upgrades());
				} else {
					RoguelikeMCClientData.INSTANCE.temporaryUpgrades.clear();
					RoguelikeMCClientData.INSTANCE.temporaryUpgrades.addAll(payload.upgrades());
				}
			});
		});
		// Refresh Upgrade Points
		ClientPlayNetworking.registerGlobalReceiver(SendUpgradePointsS2CPayload.ID, (client, handler, buf, responseSender) -> {
			SendUpgradePointsS2CPayload payload = SendUpgradePointsS2CPayload.read(buf);
			client.execute(() -> RoguelikeMCClientData.INSTANCE.currentPoints = payload.point());
		});
		// Refresh Next Boss
		ClientPlayNetworking.registerGlobalReceiver(RefreshCurrentBossStageS2CPayload.ID, (client, handler, buf, responseSender) -> {
			RefreshCurrentBossStageS2CPayload payload = RefreshCurrentBossStageS2CPayload.read(buf);
			client.execute(() -> RoguelikeMCClientData.INSTANCE.nextBoss = Identifier.tryParse(payload.nextBoss()));
		});
	}

	private static void registerKeyBindings() {
		openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.roguelikemc.open_gui",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"category.roguelikemc.gui"
		));
		openDrawGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.roguelikemc.open_draw_gui",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_H,
				"category.roguelikemc.gui"
		));
	}

	public static KeyBinding getOpenGuiKey() {
		return openGuiKey;
	}

	public static KeyBinding getOpenDrawGuiKey() {
		return openDrawGuiKey;
	}
}
