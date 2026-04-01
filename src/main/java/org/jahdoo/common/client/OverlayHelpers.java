package org.jahdoo.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static net.minecraft.network.chat.Component.translatable;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.screens.StatScreen.fadeBackground;

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
        var headerX = (int) startX - 8;
        var headerY = adjustForHeader - 2;
        boxMaker(graphics, headerX + 60, headerY - 6, 70, 27,  0, fadeBackground, fadeBackground);

        graphics.blit(requireNonNull(element.iconTexture()), headerX + 64, headerY - 2, scale, scale, scale, scale, scale, scale);
        graphics.drawString(minecraft.font, filterType, headerX + 78, headerY, element.textColourB());

        for (var syncableAttribute : syncableAttributes) {
            var modName = syncableAttribute.getAttribute().getRegisteredName().split(":", 2)[0];
            if (syncableAttribute.getBaseValue() == 0 && syncableAttribute.getAttribute().value().getDescriptionId().contains(filterType.toLowerCase())) {
                if(modName.equals(getName.toLowerCase())){
                    var value = syncableAttribute.getValue();
                    var hasValue = value > 0;

                    var text = syncableAttribute.getAttribute().value().getDescriptionId();
                    var subHeaderColour = ColourHelpers.getHeaderColour();

                    var prefix = TextHelpers.withStyleComponentTrans(text, subHeaderColour);
                    var withoutType = prefix.getString().replace(filterType + " ", "");

                    var prefix2 = TextHelpers.withStyleComponentTrans(withoutType, subHeaderColour);
                    var readableValues = MathHelpers.roundNonWholeString(MathHelpers.doubleFormattedDouble(value));

                    var suffix = TextHelpers.withStyleComponent(" " + readableValues, hasValue ? element.textColourA() : subHeaderColour);
                    var string = prefix2.copy().append(Component.literal(":")).append(suffix);

                    graphics.drawString(minecraft.font, string, startX1 + 56, adjustForHeader + 13 + attSpacer.get(), ColourHelpers.getHeaderColour());
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
            var s = MathHelpers.roundNonWholeString(MathHelpers.doubleFormattedDouble(attribute.getValue()));
            var length = Component.translatable(attribute.getAttribute().value().getDescriptionId()).getString().length() + s.length();
            if(length > maxLength) maxLength = length;
        }

        boxMaker(graphics, headerX + 60, headerY - 6, maxLength * 2 + 28, 12 + 5 * stream(attributes).toList().size() , 0, fadeBackground, fadeBackground);
        graphics.blit(icon, headerX + 64, headerY - 2, scale, scale, scale, scale, scale, scale);
        graphics.drawString(minecraft.font, header, headerX + 78, headerY, gradientColour);

        for (var syncableAttribute : attributes) {
            var text = syncableAttribute.getAttribute().value().getDescriptionId();
            var headerColour = ColourHelpers.getHeaderColour();
            var prefix = TextHelpers.withStyleComponentTrans(text, headerColour);

            var value = syncableAttribute.getValue();
            var readableValues = MathHelpers.roundNonWholeString(MathHelpers.doubleFormattedDouble(value));

            var suffix = TextHelpers.withStyleComponent(" " + readableValues, value > 0 ? gradientColour : headerColour);
            var string = prefix.copy().append(Component.literal(":")).append(suffix);

            graphics.drawString(minecraft.font, string, startX1 + 56, adjustForHeader + 13 + attSpacer.get(), headerColour);
            attSpacer.addAndGet(10);
        }

        return attSpacer.get();
    }
}
