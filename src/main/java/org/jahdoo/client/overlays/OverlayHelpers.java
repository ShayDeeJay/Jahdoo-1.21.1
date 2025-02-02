package org.jahdoo.client.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jahdoo.client.SharedUI;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.network.chat.Component.translatable;
import static org.jahdoo.client.SharedUI.BORDER_COLOUR;
import static org.jahdoo.utils.ModHelpers.*;

public class OverlayHelpers {

    static void attributeStats(@NotNull GuiGraphics pGuiGraphics, Minecraft minecraft, LocalPlayer player, int startX, int startY) {
        var attSpacer = new AtomicInteger();
        var syncableAttributes = player.getAttributes().getSyncableAttributes();
        var adjustForHeader = startY + 10;
        int size = 0;
        int maxWidth = 0;

        var jahdoo = "Jahdoo";
        pGuiGraphics.drawString(minecraft.font, jahdoo, startX, startY, ColourStore.OFF_WHITE);
        for (AttributeInstance syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getValue() > 0) {
                if(modName.equals(jahdoo.toLowerCase())){
                    var prefix = translatable(syncableAttribute.getAttribute().value().getDescriptionId());
                    var readableValues = roundNonWholeString(tripleFormattedDouble(syncableAttribute.getValue()));
                    var suffix = withStyleComponent(" " + readableValues, -9882);
                    var string = prefix.append(suffix);
                    pGuiGraphics.drawString(minecraft.font, string, startX, adjustForHeader + attSpacer.get(), ColourStore.HEADER_COLOUR);
                    attSpacer.addAndGet(10);
                    maxWidth = Math.max(minecraft.font.width(string), maxWidth);
                }
            }
        }

        attSpacer.addAndGet(10);
        var minecraftN = "Minecraft";
        pGuiGraphics.drawString(minecraft.font, minecraftN, startX, adjustForHeader + attSpacer.get() + size, ColourStore.OFF_WHITE);
        for (AttributeInstance syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getValue() > 0) {

                if(modName.equals(minecraftN.toLowerCase())){
                    var prefix = translatable(syncableAttribute.getAttribute().value().getDescriptionId());
                    var readableValues = roundNonWholeString(tripleFormattedDouble(syncableAttribute.getValue()));
                    var suffix = withStyleComponent(" " + readableValues, -9882);
                    var string = prefix.append(suffix);
                    pGuiGraphics.drawString(minecraft.font, string, startX, adjustForHeader + attSpacer.get() + size + 10, ColourStore.HEADER_COLOUR);
                    attSpacer.addAndGet(10);
                    maxWidth = Math.max(minecraft.font.width(string), maxWidth);
                }
            }
        }

        SharedUI.boxMaker(pGuiGraphics, startX - 4, startY - 4, maxWidth/2 + 4, attSpacer.get()/2 + adjustForHeader/2 - 40, BORDER_COLOUR);
    }


    static void jahdooStat(@NotNull GuiGraphics pGuiGraphics, Minecraft minecraft, LocalPlayer player, int startX, int startY) {
        var attSpacer = new AtomicInteger();
        var syncableAttributes = player.getAttributes().getSyncableAttributes();
        var adjustForHeader = startY + 10;

        var jahdoo = "Jahdoo";
        pGuiGraphics.drawCenteredString(minecraft.font, jahdoo, startX + 55, startY + 2, ColourStore.OFF_WHITE);
        SharedUI.boxMaker(pGuiGraphics, startX - 4, startY - 4, 60, 10, BORDER_COLOUR);

        for (AttributeInstance syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getValue() > 0) {
                if(modName.equals(jahdoo.toLowerCase())){
                    var text = syncableAttribute.getAttribute().value().getDescriptionId();
                    var prefix = ModHelpers.withStyleComponentTrans(text, ColourStore.HEADER_COLOUR);
                    var readableValues = roundNonWholeString(tripleFormattedDouble(syncableAttribute.getValue()));
                    var suffix = withStyleComponent(" " + readableValues, -9882);
                    var string = prefix.copy().append(suffix);
                    var splitText = minecraft.font.split(string, 90);
                    var spacer = 0;
                    for (var formattedCharSequence : splitText) {
                        pGuiGraphics.drawCenteredString(minecraft.font, formattedCharSequence, startX + 56, adjustForHeader + 13 + attSpacer.get() + spacer, ColourStore.HEADER_COLOUR);
                        spacer += 10;
                    }
                    SharedUI.boxMaker(pGuiGraphics, startX - 4, startY + 18 + attSpacer.get(), 60, 4 + spacer/2, BORDER_COLOUR);

                    attSpacer.addAndGet(spacer == 10 ? 19 : 30);
                }
            }
        }
    }
}
