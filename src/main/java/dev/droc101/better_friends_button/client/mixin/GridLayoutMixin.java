package dev.droc101.better_friends_button.client.mixin;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import dev.droc101.better_friends_button.client.BetterFriendsButton;
import dev.droc101.better_friends_button.client.config.ConfigManager;
import dev.droc101.better_friends_button.client.config.LayoutStyle;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GridLayout.RowHelper.class)
public class GridLayoutMixin {

    @Inject(method = "addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;I)Lnet/minecraft/client/gui/layouts/LayoutElement;", at = @At("HEAD"), cancellable = true)
    void addChild(LayoutElement widget, int columnWidth, CallbackInfoReturnable<LayoutElement> cir) {
        if (BetterFriendsButton.isModMenuLoaded() && widget instanceof ModMenuButtonWidget) {
            if (ConfigManager.getConfig().pauseMenuStyle == LayoutStyle.CLASSIC && ModMenuConfig.GAME_MENU_BUTTON_STYLE.getValue() == ModMenuConfig.GameMenuButtonStyle.INSERT) {
                cir.cancel();
            }
        }
    }

}
