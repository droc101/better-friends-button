package dev.droc101.better_friends_button.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import dev.droc101.better_friends_button.client.BetterFriendsButton;
import dev.droc101.better_friends_button.client.config.ConfigManager;
import dev.droc101.better_friends_button.client.config.LayoutStyle;
import dev.droc101.better_friends_button.client.gui.widget.FullFriendsButton;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FriendsButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {

    @Shadow
    @Final
    private static Component REPORT_BUGS;

    @Shadow
    @Final
    private static Component SEND_FEEDBACK;

    @Shadow
    @Final
    private static int BUTTON_WIDTH_FULL;

    @Shadow
    @Final
    private static int BUTTON_WIDTH_HALF;

    @Unique
    FullFriendsButton friendsButton;

    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 1))
    void arrangeIconRow(CallbackInfo ci, @Local(name = "iconButtonRow") LinearLayout iconButtonRow) {
        if (ConfigManager.getConfig().pauseMenuStyle == LayoutStyle.WIDE_ICON_ROW) {
            AtomicInteger columns = new AtomicInteger();
            Consumer<LayoutElement> itemCountConsumer = (_) -> columns.getAndIncrement();
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

    @Unique
    private static Button openLinkButton(final Screen screen, final Component message, final URI link) {
        return Button.builder(message, ConfirmLinkScreen.confirmLink(screen, link)).width(98).build();
    }

    @Unique
    private static ModMenuButtonWidget modsButton(final Screen screen, int width) {
        return new ModMenuButtonWidget(0, 0, width, 20, ModMenuApi.createModsButtonText(), screen);
    }

    @Redirect(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 1))
    LayoutElement classicLayout(GridLayout.RowHelper instance, LayoutElement widget, int columnWidth, LayoutSettings layoutSettings) {
        if (ConfigManager.getConfig().pauseMenuStyle != LayoutStyle.CLASSIC) {
            friendsButton = null;
            return instance.addChild(widget, columnWidth, layoutSettings);
        }

        PauseScreen _this = (PauseScreen) (Object) this;

        LinearLayout verticalLayout = LinearLayout.vertical().spacing(4);

        LinearLayout rowOne = LinearLayout.horizontal().spacing(8);
        rowOne.addChild(
                openLinkButton(_this, SEND_FEEDBACK, SharedConstants.getCurrentVersion().stable() ? CommonLinks.RELEASE_FEEDBACK : CommonLinks.SNAPSHOT_FEEDBACK)
        );
        rowOne.addChild(openLinkButton(_this, REPORT_BUGS, CommonLinks.SNAPSHOT_BUGS_FEEDBACK)).active = !SharedConstants.getCurrentVersion()
                .dataVersion()
                .isSideSeries();
        verticalLayout.addChild(rowOne);


        boolean modMenuLoaded = BetterFriendsButton.isModMenuLoaded();

        LinearLayout rowTwo = LinearLayout.horizontal().spacing(8);
        if (ConfigManager.getConfig().showOnPauseScreen) {
            if (modMenuLoaded) {
                friendsButton = new FullFriendsButton(Minecraft.getInstance(), 0, 0, BUTTON_WIDTH_HALF, 20, _this);
                rowTwo.addChild(friendsButton);
                rowTwo.addChild(modsButton(_this, BUTTON_WIDTH_HALF));
            } else {
                friendsButton = new FullFriendsButton(Minecraft.getInstance(), 0, 0, BUTTON_WIDTH_FULL, 20, _this);
                rowTwo.addChild(friendsButton);
            }

        } else if (modMenuLoaded) {
            rowTwo.addChild(modsButton(_this, BUTTON_WIDTH_FULL));
        }
        verticalLayout.addChild(rowTwo);


        return instance.addChild(verticalLayout, 2, instance.getGrid().newCellSettings().alignHorizontallyCenter());
    }

    @Redirect(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    LayoutElement deleteFriendsButton(LinearLayout instance, LayoutElement child) {
        if (child instanceof FriendsButton && !ConfigManager.getConfig().showOnPauseScreen) {
            return null;
        }
        return instance.addChild(child);
    }

    @Inject(method = "onFriendListUpdate", at = @At("HEAD"))
    void onFriendListUpdate(CallbackInfo ci) {
        if (friendsButton != null) {
            friendsButton.refreshIncomingRequestCount();
        }
    }


}
