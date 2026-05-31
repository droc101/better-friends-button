package dev.droc101.better_friends_button.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

public class FullFriendsButton extends Button.Plain {

    private static final Identifier NOTIFICATION_BACKGROUND = Identifier.fromNamespaceAndPath("better_friends_button", "friends/notification");
    private static final Identifier NOTIFICATION_BACKGROUND_HOVER = Identifier.fromNamespaceAndPath("better_friends_button", "friends/notification_highlight");

    private static final Component FRIENDS_BUTTON_TEXT = Component.translatable("gui.friends.open");

    int incomingRequestCount;

    public FullFriendsButton(
            Minecraft client,
            int x,
            int y,
            int w,
            int h,
            Screen parent
    ) {
        super(x, y, w, h, FRIENDS_BUTTON_TEXT, (btn) -> {
            OnlineOptionsScreen.confirmFriendsListEnabled(Minecraft.getInstance(), () -> Minecraft.getInstance().gui.setScreen(new FriendsOverlayScreen(parent)), parent);
        }, Button.DEFAULT_NARRATION);
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
        if (isActive() && incomingRequestCount > 0) {
            String notificationCount = Integer.toString(incomingRequestCount);
            if (incomingRequestCount > 9) {
                notificationCount = "9+";
            }

            ActiveTextCollector textRenderer = graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE);

            Font font = Minecraft.getInstance().font;

            int textWidth = font.width(notificationCount);
            int textHeight = font.lineHeight;
            int buttonWidth = 20;
            if (textWidth >= buttonWidth - 6) {
                buttonWidth = textWidth + 6;
            }

            Identifier notificationIcon = NOTIFICATION_BACKGROUND;

            if (isHovered()) {
                notificationIcon = NOTIFICATION_BACKGROUND_HOVER;
            }


            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, notificationIcon, getX() + getWidth() - buttonWidth, getY(), buttonWidth, 20, getAlpha());
            graphics.text(font, notificationCount, getX() + getWidth() - (buttonWidth / 2 + textWidth / 2), getY() + (20 / 2 - textHeight / 2), ARGB.color(getAlpha(), -1), true);
        }
    }

    public void refreshIncomingRequestCount() {
        PlayerSocialManager playerSocialManager = Minecraft.getInstance().getPlayerSocialManager();
        if (!playerSocialManager.isFriendListEnabled()) {
            incomingRequestCount = 0;
        } else {
            incomingRequestCount = playerSocialManager.getIncomingRequests().size();
        }
    }
}