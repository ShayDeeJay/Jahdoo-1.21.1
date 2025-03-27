package org.jahdoo.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jahdoo.ascension.element.AbstractElement;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static net.minecraft.network.chat.Component.translatable;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponentTrans;
import static org.jahdoo.ascension.utils.Maths.roundNonWholeString;
import static org.jahdoo.ascension.utils.Maths.singleFormattedDouble;
import static org.jahdoo.common.client.SharedUI.*;

public class OverlayHelpers {

    public static int elementalModStat(
        GuiGraphics graphics,
        Minecraft minecraft,
        LocalPlayer player,
        double startX,
        double startY,
        String getName,
        AbstractElement element
    ) {
        var attSpacer = new AtomicInteger();
        var startX1 = (int) startX;
        var startY2 = (int) startY;
        var adjustForHeader = startY2 + 10;
        var syncableAttributes = player
            .getAttributes()
            .getAttributesToSync()
            .stream()
            .sorted(comparing(d -> translatable(d.getAttribute().value().getDescriptionId()).getString()))
            .toList();

        var filterType = element.name();
        var scale = 10;
        var fadeBackground = getFadedColourBackground(0.4F);
        var headerX = (int) startX - 8;
        var headerY = adjustForHeader - 2;
        boxMaker(graphics, headerX + 60, headerY - 6, 70, 27,  element.partColourA(), fadeBackground, fadeBackground);

        graphics.blit(requireNonNull(element.iconTexture()), headerX + 64, headerY - 2, scale, scale, scale, scale, scale, scale);
        graphics.drawString(minecraft.font, filterType, headerX + 78, headerY, element.textColourB());

        for (var syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getAttribute().value().getDescriptionId().contains(filterType.toLowerCase())) {
                if(modName.equals(getName.toLowerCase())){
                    var text = syncableAttribute.getAttribute().value().getDescriptionId();
                    var prefix = withStyleComponentTrans(text, OFF_WHITE);
                    var withoutType = prefix.getString().replace(filterType + " ", "");
                    var prefix2 = withStyleComponentTrans(withoutType, OFF_WHITE);
                    var value = syncableAttribute.getValue();
                    var readableValues = roundNonWholeString(singleFormattedDouble(value));
                    var suffix = withStyleComponent(" " + readableValues, value > 0 ? MAGNET_RANGE_GREEN : MAGNET_STRENGTH_RED);
                    var string = prefix2.copy().append(Component.literal(":")).append(suffix);

                    graphics.drawString(minecraft.font, string, startX1 + 56, adjustForHeader + 13 + attSpacer.get(), HEADER_COLOUR);
                    attSpacer.addAndGet(10);
                }
            }
        }

        return attSpacer.get();
    }


    public static int getModStat(
        GuiGraphics graphics,
        Minecraft minecraft,
        double startX,
        double startY,
        String header,
        ResourceLocation icon,
        int borderColour,
        int gradientColour,
        AttributeInstance...attributes
    ) {
        var attSpacer = new AtomicInteger();
        var startX1 = (int) startX - 6;
        var startY2 = (int) startY;
        var adjustForHeader = startY2 + 10;
        var scale = 10;
        var headerX = startX1 - 8;
        var headerY = adjustForHeader - 2;
        int maxLength = 0;

        for (var attribute : attributes) {
            var s = roundNonWholeString(singleFormattedDouble(attribute.getValue()));
            var length = Component.translatable(attribute.getAttribute().value().getDescriptionId()).getString().length() + s.length();
            if(length > maxLength) maxLength = length;
        }

        var fadeBackground = getFadedColourBackground(0.4F);
        boxMaker(graphics, headerX + 60, headerY - 6, maxLength * 2 + 28, 12 + 5 * stream(attributes).toList().size() , borderColour, fadeBackground, fadeBackground);
        graphics.blit(icon, headerX + 64, headerY - 2, scale, scale, scale, scale, scale, scale);
        graphics.drawString(minecraft.font, header, headerX + 78, headerY, gradientColour);

        for (var syncableAttribute : attributes) {
            var text = syncableAttribute.getAttribute().value().getDescriptionId();
            var prefix = withStyleComponentTrans(text, OFF_WHITE);
            var value = syncableAttribute.getValue();
            var readableValues = roundNonWholeString(singleFormattedDouble(value));
            var suffix = withStyleComponent(" " + readableValues, value > 0 ? MAGNET_RANGE_GREEN : MAGNET_STRENGTH_RED);
            var string = prefix.copy().append(Component.literal(":")).append(suffix);

            graphics.drawString(minecraft.font, string, startX1 + 56, adjustForHeader + 13 + attSpacer.get(), HEADER_COLOUR);
            attSpacer.addAndGet(10);
        }

        return attSpacer.get();
    }

}
