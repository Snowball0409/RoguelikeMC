package snowball049.roguelikemc;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowball049.roguelikemc.command.RoguelikeMCCommands;
import snowball049.roguelikemc.config.RoguelikeMCConfig;

public class RoguelikeMC implements ModInitializer {
	public static final String MOD_ID = "roguelikemc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Init Config Support
		RoguelikeMCConfig.loadConfig();

		// Command Handler
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("upgrade")
					.then(CommandManager.argument("tier", StringArgumentType.string())
							.suggests(RoguelikeMCCommands::suggestUpgrades)
							.executes(RoguelikeMCCommands::executeCommandUpgrade)
					)
			);
		});

		LOGGER.info("RoguelikeMC Initialized");
	}
}
