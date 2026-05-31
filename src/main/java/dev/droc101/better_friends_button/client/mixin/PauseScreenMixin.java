package dev.droc101.better_friends_button.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.widget.UpdateAvailableBadge;
import dev.droc101.better_friends_button.client.gui.widget.ModMenuButton;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {

    @Unique
    private static final Identifier MODS_BUTTON_ICON = Identifier.fromNamespaceAndPath("better_friends_button", "mod_menu/mods");

    @Unique
    ModMenuButton modsButton = null;

    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 1))
    void createPauseMenu(CallbackInfo ci, @Local(name = "iconButtonRow") LinearLayout iconButtonRow) {
        boolean modMenuLoaded = FabricLoader.getInstance().isModLoaded("modmenu");
        PauseScreen _this = (PauseScreen) (Object) this;
        modsButton = null;

        if (modMenuLoaded && ModMenuConfig.MODIFY_GAME_MENU.getValue() && ModMenuConfig.GAME_MENU_BUTTON_STYLE.getValue() == ModMenuConfig.GameMenuButtonStyle.ICON) {
            List<SpriteIconButton> buttonsToKeep = new ArrayList<>();
            Consumer<LayoutElement> itemCountConsumer = (e) -> {
                if (e instanceof SpriteIconButton b) {
                    buttonsToKeep.add(b);
                }
            };
            iconButtonRow.visitChildren(itemCountConsumer);
            iconButtonRow.removeChildren();
            buttonsToKeep.forEach(iconButtonRow::addChild);

            modsButton = new ModMenuButton(0, 0, 20, _this);
            iconButtonRow.addChild(modsButton);
        }

        AtomicInteger columns = new AtomicInteger();
        Consumer<LayoutElement> itemCountConsumer = (e) -> {
            columns.getAndIncrement();
        };
        iconButtonRow.visitChildren(itemCountConsumer);
        int spacing = 8;
        if (columns.get() > 4) {
            spacing = 6;
        }
        iconButtonRow.spacing(spacing);

        int itemWidth = (204 - (spacing * (columns.get() - 1))) / columns.get();
        Consumer<LayoutElement> consumer = (e) -> {
            if (e instanceof SpriteIconButton w) {
                w.setWidth(itemWidth);
            }
        };
        iconButtonRow.visitChildren(consumer);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("modmenu") && modsButton != null) {
            if (ModMenuConfig.BUTTON_UPDATE_BADGE.getValue() && ModMenu.areModUpdatesAvailable() && a >= 1.0f) {
                UpdateAvailableBadge.renderBadge(graphics, modsButton.getX() + modsButton.getWidth() - 5, modsButton.getY() - 3);
            }
        }
    }

}
