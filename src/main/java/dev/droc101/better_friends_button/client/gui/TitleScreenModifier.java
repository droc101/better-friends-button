package dev.droc101.better_friends_button.client.gui;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import dev.droc101.better_friends_button.client.BetterFriendsButton;
import dev.droc101.better_friends_button.client.config.ConfigManager;
import dev.droc101.better_friends_button.client.config.LayoutStyle;
import dev.droc101.better_friends_button.client.gui.widget.FullFriendsButton;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TitleScreenModifier {

    public static void ModifyTitleScreen(TitleScreen titleScreen) {

        LayoutStyle style = ConfigManager.getConfig().titleScreenStyle;
        if (style == LayoutStyle.CLASSIC) {
            ApplyClassicLayout(titleScreen);
        } else if (style == LayoutStyle.WIDE_ICON_ROW) {
            ApplyWideIconRow(titleScreen);
        } else if (style == LayoutStyle.SMALL_ICON_ROW) {
            ApplySmallIconRow(titleScreen);
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

    private static void ApplyClassicLayout(TitleScreen titleScreen) {
        List<AbstractWidget> widgets = Screens.getWidgets(titleScreen);
        int modsButtonIndex = -1;
        int modsIconIndex = -1;
        int realmsButtonIndex = -1;

        for (int i = 0; i < widgets.size(); i++) {
            AbstractWidget w = widgets.get(i);
            if (BetterFriendsButton.isModMenuLoaded()) {
                if (w instanceof ModMenuButtonWidget) {
                    modsButtonIndex = i;
                    continue;
                } else if (w instanceof SpriteIconButton) {
                    modsIconIndex = i;
                    continue;
                }
            }
            if (w instanceof AbstractButton) {
                final String realmsButtonText = Component.translatable("menu.online").getString();
                final String buttonText = w.getMessage().getString();
                if (buttonText.equals(realmsButtonText)) {
                    realmsButtonIndex = i;
                }
            }
        }

        FullFriendsButton fullFriendsButton = null;
        SpriteIconButton modsIcon;

        int bottomOptionsY = 0;

        if (modsButtonIndex != -1) {
            modsIcon = null;
            ModMenuButtonWidget modsButton = (ModMenuButtonWidget) widgets.get(modsButtonIndex);
            if (ModMenuConfig.MODIFY_TITLE_SCREEN.getValue()) {
                if (ConfigManager.getConfig().showOnTitleScreen) {
                    if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.TitleMenuButtonStyle.CLASSIC) {
                        fullFriendsButton = new FullFriendsButton(Minecraft.getInstance(), modsButton.getX(), modsButton.getY(), 98, 20, titleScreen);
                        modsButton.setWidth(98);
                        modsButton.setX(modsButton.getX() + 102);
                    } else {
                        fullFriendsButton = new FullFriendsButton(Minecraft.getInstance(), titleScreen.width / 2 - 100, modsButton.getY() + 24, 200, 20, titleScreen);
                    }
                } else {
                    ModMenuConfig.TitleMenuButtonStyle modButtonStyle = ModMenuConfig.MODS_BUTTON_STYLE.getValue();
                    if (modButtonStyle != ModMenuConfig.TitleMenuButtonStyle.ICON) {
                        if (modButtonStyle == ModMenuConfig.TitleMenuButtonStyle.CLASSIC) {
                            bottomOptionsY = modsButton.getY() + 24;
                        } else {
                            bottomOptionsY = modsButton.getY() + 36;
                        }
                    }
                }
            }
        } else if (modsIconIndex != -1) {
            modsIcon = (SpriteIconButton) widgets.get(modsIconIndex);
            if (ConfigManager.getConfig().showOnTitleScreen) {
                fullFriendsButton = new FullFriendsButton(Minecraft.getInstance(), titleScreen.width / 2 - 100, modsIcon.getY(), 200, 20, titleScreen);
                modsIcon.setPosition(titleScreen.width / 2 + 104, fullFriendsButton.getY());
            } else if (realmsButtonIndex != -1) {
                AbstractWidget realmsButton = widgets.get(realmsButtonIndex);
                bottomOptionsY = realmsButton.getY() + 36;
                modsIcon.setPosition(titleScreen.width / 2 + 104, realmsButton.getY());
            }
        } else {
            modsIcon = null;
            if (realmsButtonIndex != -1) {
                AbstractButton realmsButton = (AbstractButton)widgets.get(realmsButtonIndex);
                fullFriendsButton = new FullFriendsButton(Minecraft.getInstance(), titleScreen.width / 2 - 100, realmsButton.getY() + 24, 200, 20, titleScreen);
            }
        }



        if (fullFriendsButton != null) {
            titleScreen.addRenderableWidget(fullFriendsButton);
            bottomOptionsY = fullFriendsButton.getY() + 24;
        }

        for (AbstractWidget w : widgets) {
            if (w instanceof SpriteIconButton i && w != modsIcon) {
                i.setY(bottomOptionsY);
            } else if (w instanceof Button) {
                final String optionsButtonText = Component.translatable("menu.options").getString();
                final String quitButtonText = Component.translatable("menu.quit").getString();
                final String buttonText = w.getMessage().getString();
                if (buttonText.equals(optionsButtonText) || buttonText.equals(quitButtonText)) {
                    w.setY(bottomOptionsY);
                }
            }
        }
    }

    private static void ApplyWideIconRow(TitleScreen titleScreen) {
        List<AbstractWidget> icons = new ArrayList<>();

        for (AbstractWidget w : Screens.getWidgets(titleScreen)) {
            if (w instanceof SpriteIconButton) {
                icons.add(w);
            }
        }

        int spacing = 4;
        int columns = icons.size();
        int itemWidth = (200 - (spacing * (columns - 1))) / columns;
        int x = titleScreen.width / 2 - 100;

        for (AbstractWidget icon: icons) {
            icon.setX(x);
            icon.setWidth(itemWidth);
            x += itemWidth + spacing;
        }
    }

    private static void ApplySmallIconRow(TitleScreen titleScreen) {
        List<AbstractWidget> icons = new ArrayList<>();

        for (AbstractWidget w : Screens.getWidgets(titleScreen)) {
            if (w instanceof SpriteIconButton) {
                icons.add(w);
            }
        }

        int spacing = 4;
        int columns = icons.size();
        int itemWidth = 20;
        int rowWidth = columns * itemWidth + ((columns - 1) * spacing);
        int x = titleScreen.width / 2 - rowWidth / 2;

        for (AbstractWidget icon: icons) {
            icon.setX(x);
            icon.setWidth(itemWidth);
            x += itemWidth + spacing;
        }
    }

}
