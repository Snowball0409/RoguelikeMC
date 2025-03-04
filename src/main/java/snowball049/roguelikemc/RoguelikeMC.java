package snowball049.roguelikemc;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import snowball049.roguelikemc.command.RoguelikeMCCommands;

public class RoguelikeMC implements ModInitializer {

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("upgrade")
					.then(CommandManager.argument("tier", StringArgumentType.string())
							.executes(RoguelikeMCCommands::executeCommandUpgrade)
					)
			);
		});
	}
}
