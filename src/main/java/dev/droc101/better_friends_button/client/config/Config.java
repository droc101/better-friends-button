package dev.droc101.better_friends_button.client.config;

public class Config {

    public LayoutStyle titleScreenStyle = LayoutStyle.CLASSIC;
    public boolean showOnTitleScreen = true;

    public LayoutStyle pauseMenuStyle = LayoutStyle.WIDE_ICON_ROW;
    public boolean showOnPauseScreen = true;

    public static Config getDefault() {
        return new Config();
    }
}

