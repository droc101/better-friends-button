package dev.droc101.better_friends_button.client.gui;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import com.terraformersmc.modmenu.gui.widget.UpdateCheckerTexturedButtonWidget;
import dev.droc101.better_friends_button.client.gui.widget.FullFriendsButton;
import dev.droc101.better_friends_button.client.gui.widget.ModMenuButton;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TitleScreenModifier {

    public static void ModifyTitleScreen(TitleScreen titleScreen) {
        boolean modMenuLoaded = FabricLoader.getInstance().isModLoaded("modmenu");

        List<AbstractWidget> widgets = Screens.getWidgets(titleScreen);
        int modsButtonIndex = -1;
        int modsIconIndex = -1;
        int realmsButtonIndex = -1;

        for (int i = 0; i < widgets.size(); i++) {
            AbstractWidget w = widgets.get(i);
            if (modMenuLoaded) {
                if (w instanceof ModMenuButtonWidget b) {
                    modsButtonIndex = i;
                } else if (w instanceof UpdateCheckerTexturedButtonWidget b) {
                    modsIconIndex = i;
                }
            } else if (w instanceof AbstractButton) {
                final String realmsButtonText = Component.translatable("menu.online").getString();
                final String buttonText = w.getMessage().getString();
                if (buttonText.equals(realmsButtonText)) {
                    realmsButtonIndex = i;
                }
            }
        }

        FullFriendsButton fullFriendsButton = null;

        if (modsButtonIndex != -1) {
            ModMenuButtonWidget modsButton = (ModMenuButtonWidget) widgets.get(modsButtonIndex);
            if (ModMenuConfig.MODIFY_TITLE_SCREEN.getValue()) {
                if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.TitleMenuButtonStyle.CLASSIC) {
                    fullFriendsButton = new FullFriendsButton(Minecraft.getInstance(), modsButton.getX(), modsButton.getY(), 98, 20, titleScreen);
                    modsButton.setWidth(98);
                    modsButton.setX(modsButton.getX() + 102);
                } else {
                    fullFriendsButton = new FullFriendsButton(Minecraft.getInstance(), titleScreen.width / 2 - 100, modsButton.getY() + 24, 200, 20, titleScreen);
                }
            }
        } else if (modsIconIndex != -1) {
            UpdateCheckerTexturedButtonWidget modsIcon = (UpdateCheckerTexturedButtonWidget) widgets.get(modsIconIndex);
            fullFriendsButton = new FullFriendsButton(Minecraft.getInstance(), titleScreen.width / 2 - 100, modsIcon.getY(), 200, 20, titleScreen);
            ModMenuButton modMenuButton = new ModMenuButton(titleScreen.width / 2 + 104, fullFriendsButton.getY(), 20, titleScreen);
            titleScreen.removeWidget(modsIcon);
            titleScreen.addRenderableWidget(modMenuButton);
        } else if (realmsButtonIndex != -1) {
            AbstractButton realmsButton = (AbstractButton)widgets.get(realmsButtonIndex);
            fullFriendsButton = new FullFriendsButton(Minecraft.getInstance(), titleScreen.width / 2 - 100, realmsButton.getY() + 24, 200, 20, titleScreen);
        }


        if (fullFriendsButton != null) {
            titleScreen.addRenderableWidget(fullFriendsButton);

            int bottomOptionsY = fullFriendsButton.getY() + 24;
            widgets.forEach((w) -> {
                if (w instanceof SpriteIconButton i && !(w instanceof ModMenuButton)) {
                    i.setY(bottomOptionsY);
                } else if (w instanceof Button) {
                    final String optionsButtonText = Component.translatable("menu.options").getString();
                    final String quitButtonText = Component.translatable("menu.quit").getString();
                    final String buttonText = w.getMessage().getString();
                    if (buttonText.equals(optionsButtonText) || buttonText.equals(quitButtonText)) {
                        w.setY(bottomOptionsY);
                    }
                }
            });
        }

        ScreenEvents.afterTick(titleScreen).register(TitleScreenModifier::TickTitleScreen);
    }

    public static void TickTitleScreen(Screen screen) {
        if (screen instanceof TitleScreen titleScreen) {
            List<AbstractWidget> widgets = Screens.getWidgets(titleScreen);
            widgets.forEach((w) -> {
                if (w instanceof FullFriendsButton fullFriendsButton) {
                    fullFriendsButton.refreshIncomingRequestCount();
                }
            });
        }
    }

}
