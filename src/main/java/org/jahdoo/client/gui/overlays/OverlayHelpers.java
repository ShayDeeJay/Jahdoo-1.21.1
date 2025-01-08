package org.jahdoo.client.gui.overlays;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jahdoo.client.SharedUI;
import org.jahdoo.utils.ColourStore;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.network.chat.Component.translatable;
import static org.jahdoo.client.SharedUI.BORDER_COLOUR;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.ModHelpers.withStyleComponent;

public class OverlayHelpers {

    static void attributeStats(@NotNull GuiGraphics pGuiGraphics, Minecraft minecraft, LocalPlayer player) {
        var attSpacer = new AtomicInteger();
        var syncableAttributes = player.getAttributes().getSyncableAttributes();
        var startX = 14;
        var startY = 14;
        var adjustForHeader = startY + 10;
        int size = 0;
        int maxWidth = 0;

        if(!InputConstants.isKeyDown(minecraft.getWindow().getWindow(), InputConstants.KEY_TAB)) return;

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

        SharedUI.boxMaker(pGuiGraphics, startX - 4, startY - 4, maxWidth/2 + 4, attSpacer.get()/2 + adjustForHeader/2 + 1  , BORDER_COLOUR);
    }

}
