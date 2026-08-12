package dev.droc101.better_friends_button.client;

import dev.droc101.better_friends_button.client.config.ConfigManager;
import dev.droc101.better_friends_button.client.gui.TitleScreenModifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterFriendsButton implements ClientModInitializer {

    public static final String MOD_ID = "better_friends_button";

    public static final Identifier SCREEN_AFTER_INIT_EVENT_ID = Identifier.fromNamespaceAndPath(MOD_ID, "title_screen_init");

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig(true);
        ScreenEvents.AFTER_INIT.register(SCREEN_AFTER_INIT_EVENT_ID, (_, screen, _, _) -> {
            if (screen instanceof TitleScreen t) {
                TitleScreenModifier.ModifyTitleScreen(t);
            }
        });
    }

    public static boolean isModMenuLoaded() {
        return FabricLoader.getInstance().isModLoaded("modmenu");
    }
}
