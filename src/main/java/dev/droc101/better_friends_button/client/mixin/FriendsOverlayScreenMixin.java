package dev.droc101.better_friends_button.client.mixin;

import dev.droc101.better_friends_button.client.BetterFriendsButton;
import dev.droc101.better_friends_button.client.gui.screen.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FriendsOverlayScreen.class)
public class FriendsOverlayScreenMixin {

    @Unique
    Button doneButton;

    @Unique
    SpriteIconButton settingsButton;

    @Unique
    private static final Identifier SETTINGS_ICON = Identifier.fromNamespaceAndPath(BetterFriendsButton.MOD_ID, "icon/settings");

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LinearLayout;vertical()Lnet/minecraft/client/gui/layouts/LinearLayout;"))
    void init(CallbackInfo ci) {
        FriendsOverlayScreen _this = (FriendsOverlayScreen) (Object) this;
        if (doneButton != null) {
            _this.removeWidget(doneButton);
            doneButton = null;
        }
        if (settingsButton != null) {
            _this.removeWidget(settingsButton);
            settingsButton = null;
        }

        doneButton = Button.builder(CommonComponents.GUI_DONE, (_) -> Minecraft.getInstance().gui.setScreen(_this.backgroundScreen)).build();
        doneButton.setHeight(20);
        _this.addRenderableWidget(doneButton);

        settingsButton = SpriteIconButton.builder(
                        Component.translatable("better_friends_button.settings_button"), (_) -> Minecraft.getInstance().gui.setScreen(new ConfigScreen(Minecraft.getInstance().gui.screen())), true
                )
                .sprite(SETTINGS_ICON, 15, 15)
                .size(20, 20)
                .withTootip()
                .build();
        _this.addRenderableWidget(settingsButton);
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 110))
    int modifyTabWidth(int constant) {
        return constant - 10;
    }

    @Inject(method = "repositionElements", at = @At("RETURN"))
    void repositionElements(CallbackInfo ci) {
        FriendsOverlayScreen _this = (FriendsOverlayScreen) (Object) this;
        assert _this.tabNavigationBar != null;
        assert _this.layout != null;

        settingsButton.setY(_this.tabNavigationBar.getY());
        settingsButton.setX(_this.layout.getX() + 200);
        _this.tabNavigationBar.setWidth(200);

        doneButton.setWidth(220);
        doneButton.setX(_this.width / 2 - (doneButton.getWidth()) / 2);
        doneButton.setY(_this.layout.getY() + _this.layout.getHeight() + 8);
    }

    @ModifyArg(method = "repositionElements", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar;arrangeElements(I)V"), index = 0)
    int modifyTabBarWidth(int width) {
        return 200;
    }

    @ModifyVariable(method = "mouseClicked", at = @At("STORE"), name = "panelBottom")
    int modifyPanelBottom(int panelBottom) {
        return doneButton.getY() + doneButton.getHeight();
    }

    @ModifyConstant(method = "repositionElements", constant = @Constant(intValue = 80))
    int modifyHeight(int original) {
        return 120;
    }

}
