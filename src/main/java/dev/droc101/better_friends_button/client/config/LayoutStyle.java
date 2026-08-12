package dev.droc101.better_friends_button.client.config;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum LayoutStyle implements StringRepresentable {
    CLASSIC("classic"),
    WIDE_ICON_ROW("wide_icon_row"),
    SMALL_ICON_ROW("small_icon_row");

    private final String name;

    LayoutStyle(String name) {
        this.name = name;
    }

    @Override
    public @NonNull String getSerializedName() {
        return name;
    }
}
