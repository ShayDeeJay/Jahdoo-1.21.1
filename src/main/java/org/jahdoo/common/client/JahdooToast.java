package org.jahdoo.common.client;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

public class JahdooToast implements Toast {

    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
    private boolean playedSound;
    private final String header;
    private final String description;
    private final AdvancementType type;
    private final ResourceLocation icon;

    public JahdooToast(String header, String description, ResourceLocation icon, AdvancementType type) {
        this.header = header;
        this.type = type;
        this.icon = icon;
        this.description = description;
    }

    public Toast.Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        guiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        var list = toastComponent.getMinecraft().font.split(FormattedText.of(description), 125);
        var i = type == AdvancementType.CHALLENGE ? 16746751 : 16776960;

        if (list.size() == 1) {
            guiGraphics.drawString(toastComponent.getMinecraft().font, header, 30, 7, i | -16777216, false);
            guiGraphics.drawString(toastComponent.getMinecraft().font, list.getFirst(), 30, 18, -1, false);
        } else {
            if (timeSinceLastVisible < 1500L) {
                var k = Mth.floor(Mth.clamp((float)(1500L - timeSinceLastVisible) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;

                guiGraphics.drawString(toastComponent.getMinecraft().font, header, 30, 12, i | k, false);
            } else {
                var i1 = Mth.floor(Mth.clamp((float)(timeSinceLastVisible - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                var l = this.height() / 2 - list.size() * 9 / 2;

                for(FormattedCharSequence sequence : list) {
                    guiGraphics.drawString(toastComponent.getMinecraft().font, sequence, 30, l + 1, 16777215 | i1, false);
                    l += 9;
                }
            }
        }

        if (!this.playedSound && timeSinceLastVisible > 0L) {
            this.playedSound = true;
            if (type == AdvancementType.CHALLENGE) {
                SoundHelpers.uiSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
            }
        }

        var side = 24;
        guiGraphics.blit(icon, 8, 8, 4, 4, side, side, side, side);
        return timeSinceLastVisible >= 5000.0F * toastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
}
