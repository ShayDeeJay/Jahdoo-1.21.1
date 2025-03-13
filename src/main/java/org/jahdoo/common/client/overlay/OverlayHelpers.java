package org.jahdoo.common.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Comparator.*;
import static net.minecraft.network.chat.Component.translatable;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.SharedUI.BORDER_COLOUR;
import static org.jahdoo.common.registers.ElementReg.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Maths.roundNonWholeString;
import static org.jahdoo.ascension.utils.Maths.tripleFormattedDouble;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;

public class OverlayHelpers {

    public static int getAllStat(@NotNull GuiGraphics pGuiGraphics, Minecraft minecraft, LocalPlayer player, int startX, int startY){
        var getMod = getModStat(pGuiGraphics, minecraft, player, startX, startY, "Jahdoo");
        var getMC = getModStat(pGuiGraphics, minecraft, player, startX, startY + getMod + 30, "Minecraft");
        return getMod + getMC;
    }

    public static int getColour(String parse){
        return switch (parse){
            case String s when s.contains("vitality") -> vitality().textColourA();
            case String s when s.contains("inferno") -> inferno().textColourA();
            case String s when s.contains("frost") -> frost().textColourA();
            case String s when s.contains("mystic") -> mystic().textColourA();
            case String s when s.contains("mana") -> AETHER_BLUE;
            default -> HEADER_COLOUR;
        };
    }

    static int getModStat(GuiGraphics graphics, Minecraft minecraft, LocalPlayer player, int startX, int startY, String getName) {
        var attSpacer = new AtomicInteger();
        var syncableAttributes = player
            .getAttributes()
            .getSyncableAttributes()
            .stream()
            .sorted(comparing(d -> translatable(d.getAttribute().value().getDescriptionId()).getString()))
            .toList();
        var adjustForHeader = startY + 10;

        graphics.drawCenteredString(minecraft.font, getName, startX + 55, startY + 2, SUB_HEADER_COLOUR);
        boxMaker(graphics, startX - 4, startY - 4, 60, 10, SUB_HEADER_COLOUR, BORDER_COLOUR);

        for (AttributeInstance syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getValue() > 0) {
                if(modName.equals(getName.toLowerCase())){
                    var text = syncableAttribute.getAttribute().value().getDescriptionId();
                    var colour = getColour(text);
                    var prefix = Helpers.withStyleComponentTrans(text, colour);
                    var readableValues = roundNonWholeString(tripleFormattedDouble(syncableAttribute.getValue()));
                    var suffix = withStyleComponent(" " + readableValues, -9882);
                    var string = prefix.copy().append(suffix);
                    var splitText = minecraft.font.split(string, 90);
                    var spacer = 0;

                    for (var sequence    : splitText) {
                        graphics.drawCenteredString(minecraft.font, sequence, startX + 56, adjustForHeader + 13 + attSpacer.get() + spacer, HEADER_COLOUR);
                        spacer += 10;
                    }

                    var startY1 = startY + 18 + attSpacer.get();
                    var heightOffset = 4 + spacer / 2;

                    boxMaker(graphics, startX - 4, startY1, 60, heightOffset, BORDER_COLOUR, getFadedColourBackground(0.7F));
                    attSpacer.addAndGet(spacer == 10 ? 20 : 30);
                }
            }
        }

        return attSpacer.get();
    }

}
