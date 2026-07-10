package dev.droc101.better_friends_button.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {

    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 1))
    void createPauseMenu(CallbackInfo ci, @Local(name = "iconButtonRow") LinearLayout iconButtonRow) {
        AtomicInteger columns = new AtomicInteger();
        Consumer<LayoutElement> itemCountConsumer = (e) -> columns.getAndIncrement();
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

}
