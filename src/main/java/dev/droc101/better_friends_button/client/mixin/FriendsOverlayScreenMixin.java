package dev.droc101.better_friends_button.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.network.chat.CommonComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FriendsOverlayScreen.class)
public class FriendsOverlayScreenMixin {

    @Unique
    Button doneButton;

    @Inject(method = "init", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LinearLayout;vertical()Lnet/minecraft/client/gui/layouts/LinearLayout;"))
    void init(CallbackInfo ci) {
        FriendsOverlayScreen _this = (FriendsOverlayScreen)(Object)this;
        if (doneButton != null) {
            _this.removeWidget(doneButton);
            doneButton = null;
        }

        doneButton = Button.builder(CommonComponents.GUI_DONE, (btn) -> {
            Minecraft.getInstance().gui.setScreen(_this.backgroundScreen);
        }).build();
        doneButton.setHeight(20);
        _this.addRenderableWidget(doneButton);
    }

    @Inject(method = "repositionElements", at=@At("RETURN"))
    void repositionElements(CallbackInfo ci) {
        FriendsOverlayScreen _this = (FriendsOverlayScreen)(Object)this;
        doneButton.setWidth(_this.tabNavigationBar.getWidth());
        doneButton.setX(_this.width / 2 - (doneButton.getWidth()) / 2);
        doneButton.setY(_this.layout.getY() + _this.layout.getHeight() + 8);
    }

    @Inject(method = "repositionElements", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar;setPosition(II)V", shift = At.Shift.AFTER))
    void fixTabBarPosition(CallbackInfo ci) {
        FriendsOverlayScreen _this = (FriendsOverlayScreen)(Object)this;
        int layoutWidth = _this.layout.getWidth();
        int tabWidth = _this.tabNavigationBar.getWidth();
        int offset = (layoutWidth / 2) - (tabWidth / 2);

        _this.tabNavigationBar.setX(_this.layout.getX() + offset);
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
