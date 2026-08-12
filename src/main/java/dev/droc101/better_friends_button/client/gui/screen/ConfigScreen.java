package dev.droc101.better_friends_button.client.gui.screen;

import dev.droc101.better_friends_button.client.config.ConfigManager;
import dev.droc101.better_friends_button.client.config.LayoutStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class ConfigScreen extends OptionsSubScreen {
    public ConfigScreen(Screen previous) {
        super(previous, Minecraft.getInstance().options, Component.translatable("menu.better_friends_button.config.title"));
    }

    @Override
    protected void addOptions() {
        assert this.list != null;
        ArrayList<AbstractWidget> options = new ArrayList<>();

        this.list.addHeader(Component.translatable("options.better_friends_button.title_screen"));

        options.add(CycleButton.builder(
                        (v) -> Component.translatable("options.better_friends_button.layout_style." + v.getSerializedName()),
                        ConfigManager.getConfig().titleScreenStyle)
                .withValues(LayoutStyle.values())
                .create(
                        Component.translatable("options.better_friends_button.layout_style"),
                        (_, v) -> {
                            ConfigManager.getConfig().titleScreenStyle = v;
                            ConfigManager.saveConfig();
                        }));
        options.add(CycleButton.booleanBuilder(
                Component.translatable("options.visible"),
                Component.translatable("options.hidden"),
                ConfigManager.getConfig().showOnTitleScreen
        ).create(
                Component.translatable("options.better_friends_button.visible"),
                (_, v) -> {
                    ConfigManager.getConfig().showOnTitleScreen = v;
                    ConfigManager.saveConfig();
                }
        ));
        this.list.addSmall(options);
        options.clear();


        this.list.addHeader(Component.translatable("options.better_friends_button.pause_menu"));

        options.add(CycleButton.builder(
                        (v) -> Component.translatable("options.better_friends_button.layout_style." + v.getSerializedName()),
                        ConfigManager.getConfig().pauseMenuStyle)
                .withValues(LayoutStyle.values())
                .create(
                        Component.translatable("options.better_friends_button.layout_style"),
                        (_, v) -> {
                            ConfigManager.getConfig().pauseMenuStyle = v;
                            ConfigManager.saveConfig();
                        }));
        options.add(CycleButton.booleanBuilder(
                Component.translatable("options.visible"),
                Component.translatable("options.hidden"),
                ConfigManager.getConfig().showOnPauseScreen
        ).create(
                Component.translatable("options.better_friends_button.visible"),
                (_, v) -> {
                    ConfigManager.getConfig().showOnPauseScreen = v;
                    ConfigManager.saveConfig();
                }
        ));
        this.list.addSmall(options);
        options.clear();
    }
}
