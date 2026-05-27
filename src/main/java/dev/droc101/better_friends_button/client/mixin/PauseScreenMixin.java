package dev.droc101.better_friends_button.client.mixin;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {

    @Unique
    final int SPACING = 8;

    @ModifyConstant(method = "createPauseMenu", constant = @Constant(intValue = 20))
    int modifyButtonsWidth(int orig) {
        int columns = 4;
        return (204 - (SPACING*(columns-1))) / columns;
    }

    @ModifyConstant(method = "createPauseMenu", constant = @Constant(intValue = 4, ordinal = 3))
    int modifySpacing(int orig) {
        return SPACING;
    }

    @Redirect(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/social/PlayerSocialManager;isFriendListEnabled()Z"))
    boolean alwaysShowFriends(PlayerSocialManager instance) {
        return true;
    }

}
