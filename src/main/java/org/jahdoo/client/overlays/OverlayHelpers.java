package org.jahdoo.client.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jahdoo.client.SharedUI;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.network.chat.Component.translatable;
import static org.jahdoo.client.SharedUI.BORDER_COLOUR;
import static org.jahdoo.registers.ElementRegistry.*;
import static org.jahdoo.utils.ColourStore.*;
import static org.jahdoo.utils.ModHelpers.*;

public class OverlayHelpers {

    static void attributeStats(@NotNull GuiGraphics pGuiGraphics, Minecraft minecraft, LocalPlayer player, int startX, int startY) {
        var attSpacer = new AtomicInteger();
        var syncableAttributes = player.getAttributes().getSyncableAttributes();
        var adjustForHeader = startY + 10;
        int size = 0;
        int maxWidth = 0;

        var jahdoo = "Jahdoo";
        pGuiGraphics.drawString(minecraft.font, jahdoo, startX, startY, OFF_WHITE);
        for (AttributeInstance syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getValue() > 0) {
                if(modName.equals(jahdoo.toLowerCase())){
                    var prefix = translatable(syncableAttribute.getAttribute().value().getDescriptionId());
                    var readableValues = roundNonWholeString(tripleFormattedDouble(syncableAttribute.getValue()));
                    var suffix = withStyleComponent(" " + readableValues, -9882);
                    var string = prefix.append(suffix);
                    pGuiGraphics.drawString(minecraft.font, string, startX, adjustForHeader + attSpacer.get(), HEADER_COLOUR);
                    attSpacer.addAndGet(10);
                    maxWidth = Math.max(minecraft.font.width(string), maxWidth);
                }
            }
        }

        attSpacer.addAndGet(10);
        var minecraftN = "Minecraft";
        pGuiGraphics.drawString(minecraft.font, minecraftN, startX, adjustForHeader + attSpacer.get() + size, OFF_WHITE);
        for (AttributeInstance syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getValue() > 0) {

                if(modName.equals(minecraftN.toLowerCase())){
                    var prefix = translatable(syncableAttribute.getAttribute().value().getDescriptionId());
                    var readableValues = roundNonWholeString(tripleFormattedDouble(syncableAttribute.getValue()));
                    var suffix = withStyleComponent(" " + readableValues, -9882);
                    var string = prefix.append(suffix);
                    pGuiGraphics.drawString(minecraft.font, string, startX, adjustForHeader + attSpacer.get() + size + 10, HEADER_COLOUR);
                    attSpacer.addAndGet(10);
                    maxWidth = Math.max(minecraft.font.width(string), maxWidth);
                }
            }
        }

        SharedUI.boxMaker(pGuiGraphics, startX - 4, startY - 4, maxWidth/2 + 4, attSpacer.get()/2 + adjustForHeader/2 - 40, BORDER_COLOUR);
    }



    static int getModStat(@NotNull GuiGraphics pGuiGraphics, Minecraft minecraft, LocalPlayer player, int startX, int startY, String getName, int setNameColour) {
        var attSpacer = new AtomicInteger();
        var syncableAttributes = player.getAttributes().getSyncableAttributes();
        var adjustForHeader = startY + 10;

        pGuiGraphics.drawCenteredString(minecraft.font, getName, startX + 55, startY + 2, setNameColour);
        SharedUI.boxMaker(pGuiGraphics, startX - 4, startY - 4, 60, 10, SUB_HEADER_COLOUR, BORDER_COLOUR);

        for (AttributeInstance syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getValue() > 0) {
                if(modName.equals(getName.toLowerCase())){
                    var text = syncableAttribute.getAttribute().value().getDescriptionId();
                    var colour = getColour(text);
                    var prefix = ModHelpers.withStyleComponentTrans(text, colour);
                    var readableValues = roundNonWholeString(tripleFormattedDouble(syncableAttribute.getValue()));
                    var suffix = withStyleComponent(" " + readableValues, -9882);
                    var string = prefix.copy().append(suffix);
                    var splitText = minecraft.font.split(string, 90);
                    var spacer = 0;

                    for (var formattedCharSequence : splitText) {
                        pGuiGraphics.drawCenteredString(minecraft.font, formattedCharSequence, startX + 56, adjustForHeader + 13 + attSpacer.get() + spacer, HEADER_COLOUR);
                        spacer += 10;
                    }

                    var startY1 = startY + 18 + attSpacer.get();
                    var heightOffset = 4 + spacer / 2;

                    SharedUI.boxMaker(pGuiGraphics, startX - 4, startY1, 60, heightOffset, BORDER_COLOUR, SharedUI.getFadedColourBackground(0.7F));
                    attSpacer.addAndGet(spacer == 10 ? 20 : 30);
                }
            }
        }

        return attSpacer.get();
    }

    public static int getColour(String parse){
        return switch (parse){
            case String s when s.contains("vitality") -> VITALITY.get().textColourPrimary();
            case String s when s.contains("inferno") -> INFERNO.get().textColourPrimary();
            case String s when s.contains("frost") -> FROST.get().textColourPrimary();
            case String s when s.contains("mystic") -> MYSTIC.get().textColourPrimary();
            case String s when s.contains("lightning") -> LIGHTNING.get().textColourPrimary();
            case String s when s.contains("mana") -> AETHER_BLUE;
            default -> HEADER_COLOUR;
        };
    }

    public static int getAllStat(@NotNull GuiGraphics pGuiGraphics, Minecraft minecraft, LocalPlayer player, int startX, int startY){
        var getMod = getModStat(pGuiGraphics, minecraft, player, startX, startY, "Jahdoo", SUB_HEADER_COLOUR);
        var getMC = getModStat(pGuiGraphics, minecraft, player, startX, startY + getMod + 30, "Minecraft", SUB_HEADER_COLOUR);
        return getMod + getMC;
    }
}
