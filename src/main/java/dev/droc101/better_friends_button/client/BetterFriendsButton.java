package dev.droc101.better_friends_button.client;

import dev.droc101.better_friends_button.client.gui.TitleScreenModifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.Identifier;

public class BetterFriendsButton implements ClientModInitializer {

    public static final Identifier SCREEN_AFTER_INIT_EVENT_ID = Identifier.fromNamespaceAndPath("better_friends_button", "title_screen_init");

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register(SCREEN_AFTER_INIT_EVENT_ID, (client, screen, w, h) -> {
            if (screen instanceof TitleScreen t) {
                TitleScreenModifier.ModifyTitleScreen(t);
            }
        });
    }
}
