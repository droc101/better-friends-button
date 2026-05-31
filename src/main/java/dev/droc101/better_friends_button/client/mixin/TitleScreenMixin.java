package dev.droc101.better_friends_button.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init", at = @At("RETURN"))
    void init(CallbackInfo ci,
              @Local(name = "language") SpriteIconButton language,
              @Local(name = "accessibility") SpriteIconButton accessibility) {
        TitleScreen _this = (TitleScreen) (Object) this;
        if (_this.friends != null) {
            _this.removeWidget(_this.friends);
        }
        language.setX(_this.width / 2 - 124);
        accessibility.setX(_this.width / 2 + 104);
    }

}
