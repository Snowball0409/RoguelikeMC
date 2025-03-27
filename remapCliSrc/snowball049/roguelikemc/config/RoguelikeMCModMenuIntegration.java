package snowball049.roguelikemc.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import snowball049.roguelikemc.gui.RoguelikeMCConfigScreen;

public class RoguelikeMCModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return RoguelikeMCConfigScreen::new;
    }
}
