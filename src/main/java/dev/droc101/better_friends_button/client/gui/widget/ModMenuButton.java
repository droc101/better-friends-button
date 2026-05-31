package dev.droc101.better_friends_button.client.gui.widget;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.widget.UpdateAvailableBadge;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ModMenuButton extends SpriteIconButton.CenteredIcon {

    private static final WidgetSprites MODS_BUTTON_ICON = new WidgetSprites(Identifier.fromNamespaceAndPath("better_friends_button", "mod_menu/mods"));

    public ModMenuButton(int x, int y, int width, Screen parent) {
        Component contents = ModMenuApi.createModsButtonText();
        super(width, 20, contents, 16, 16, 0, 0, MODS_BUTTON_ICON, (b) -> Minecraft.getInstance().gui.setScreen(ModMenuApi.createModsScreen(parent)), contents, Button.DEFAULT_NARRATION, false);
        setPosition(x, y);
    }

    @Override
    public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            if (ModMenuConfig.BUTTON_UPDATE_BADGE.getValue() && ModMenu.areModUpdatesAvailable() && a >= 1.0f) {
                UpdateAvailableBadge.renderBadge(graphics, getX() + getWidth() - 5, getY() - 3);
            }
        }
    }
}
