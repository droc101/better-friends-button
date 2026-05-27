package dev.droc101.better_friends_button.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FriendsButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Unique
    Button friends;

    @Unique
    int incomingRequestCount;

    @Unique
    private static final Identifier NOTIFICATION_BACKGROUND = Identifier.fromNamespaceAndPath("better_friends_button", "friends/notification");
    @Unique
    private static final Identifier NOTIFICATION_BACKGROUND_HOVER = Identifier.fromNamespaceAndPath("better_friends_button", "friends/notification_highlight");

    @Unique
    private static final Component FRIENDS_BUTTON_TEXT = Component.translatable("gui.friends.open");

    @Unique
    void refreshIncomingRequestCount() {
        PlayerSocialManager playerSocialManager = Minecraft.getInstance().getPlayerSocialManager();
        if (!playerSocialManager.isFriendListEnabled()) {
            this.incomingRequestCount = 0;
        } else {
            this.incomingRequestCount = playerSocialManager.getIncomingRequests().size();
        }
    }

    @Inject(method = "init", at = @At("RETURN"))
    void init(CallbackInfo ci,
              @Local(name = "language") SpriteIconButton language,
              @Local(name = "accessibility") SpriteIconButton accessibility,
              @Local(name = "topPos") int topPos) {
        TitleScreen _this = (TitleScreen) (Object) this;
        if (_this.friends != null) {
            _this.removeWidget(_this.friends);
        }

        if (friends != null) {
            _this.removeWidget(friends);
            friends = null;
        }

        topPos -= 24;
        friends = Button.builder(FRIENDS_BUTTON_TEXT, (btn) -> {
                    OnlineOptionsScreen.confirmFriendsListEnabled(Minecraft.getInstance(), () -> Minecraft.getInstance().gui.setScreen(new FriendsOverlayScreen(_this)), _this);
                })
                .bounds(_this.width / 2 - 100, topPos, 200, 20)
                .build();
        _this.addRenderableWidget(friends);


        topPos += 24;
        language.setPosition(_this.width / 2 - 124, topPos);
        accessibility.setPosition(_this.width / 2 + 104, topPos);

        refreshIncomingRequestCount();
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        TitleScreen _this = (TitleScreen) (Object) this;

        if (friends != null) {
            if (friends.isActive() && incomingRequestCount > 0) {
                String notificationCount = Integer.toString(incomingRequestCount);
                if (incomingRequestCount > 9) {
                    notificationCount = "9+";
                }
                int textWidth = _this.getFont().width(notificationCount);
                int textHeight = _this.getFont().lineHeight;
                int buttonWidth = 20;
                if (textWidth >= buttonWidth - 6) {
                    buttonWidth = textWidth + 6;
                }

                Identifier notificationIcon = NOTIFICATION_BACKGROUND;

                if (friends.isHovered()) {
                    notificationIcon = NOTIFICATION_BACKGROUND_HOVER;
                }

                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, notificationIcon, friends.getX() + friends.getWidth() - buttonWidth, friends.getY(), buttonWidth, 20, friends.getAlpha());
                graphics.text(_this.getFont(), notificationCount, friends.getX() + friends.getWidth() - (buttonWidth / 2 + textWidth / 2), friends.getY() + (20 / 2 - textHeight / 2), -1, true);
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/FriendsButton;refreshIncomingRequestCount()V"))
    void redirectRefresh(FriendsButton instance) {
        refreshIncomingRequestCount();
    }

}
