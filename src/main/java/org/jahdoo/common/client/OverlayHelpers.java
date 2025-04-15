package org.jahdoo.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.AttachmentReg;

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
            if (syncableAttribute.getAttribute().value().getDescriptionId().contains(filterType.toLowerCase())) {
                if(modName.equals(getName.toLowerCase())){
                    var text = syncableAttribute.getAttribute().value().getDescriptionId();
                    var prefix = withStyleComponentTrans(text, SUB_HEADER_COLOUR);
                    var withoutType = prefix.getString().replace(filterType + " ", "");
                    var prefix2 = withStyleComponentTrans(withoutType, SUB_HEADER_COLOUR);
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

        boxMaker(graphics, headerX + 60, headerY - 6, maxLength * 2 + 28, 12 + 5 * stream(attributes).toList().size() , 0, fadeBackground, fadeBackground);
        graphics.blit(icon, headerX + 64, headerY - 2, scale, scale, scale, scale, scale, scale);
        graphics.drawString(minecraft.font, header, headerX + 78, headerY, gradientColour);

        for (var syncableAttribute : attributes) {
            var text = syncableAttribute.getAttribute().value().getDescriptionId();
            var prefix = withStyleComponentTrans(text, SUB_HEADER_COLOUR);
            var value = syncableAttribute.getValue();
            var readableValues = roundNonWholeString(singleFormattedDouble(value));
            var suffix = withStyleComponent(" " + readableValues, value > 0 ? MAGNET_RANGE_GREEN : MAGNET_STRENGTH_RED);
            var string = prefix.copy().append(Component.literal(":")).append(suffix);

            graphics.drawString(minecraft.font, string, startX1 + 56, adjustForHeader + 13 + attSpacer.get(), HEADER_COLOUR);
            attSpacer.addAndGet(10);
        }

        return attSpacer.get();
    }

    public static void playerLevel(
        GuiGraphics graphics,
        Minecraft minecraft,
        double startX,
        double startY,
        int primary
    ) {
        var startX1 = (int) startX - 6;
        var startY2 = (int) startY;
        var headerX = startX1 - 8;
        var data1 = minecraft.player.getData(AttachmentReg.CASTER_DATA.get());
        var getXStart = headerX + 46;
        var spacer = 0;
        var runs = data1.getPastRuns().reversed();
        var index = runs.size();

        boxMaker(graphics, getXStart - 30, -100000000, 83, 100000000, primary, fadeBackground, fadeBackground);

        if(runs.isEmpty()){
            var pre1 = Helpers.withStyleComponent("No Runs Registered", primary);
            graphics.drawCenteredString(minecraft.font, pre1, graphics.guiWidth() / 2, graphics.guiHeight()/2 + 20, -1);
        }

        for (var pastRun : runs) {
            var preA = Helpers.withStyleComponent(index + "", primary);
            graphics.drawCenteredString(minecraft.font, preA, graphics.guiWidth()/2, startY2 + spacer - 18, -1);

            var preDate = Helpers.withStyleComponent("Date: ", HEADER_COLOUR);
            var valueDate = Helpers.withStyleComponent(pastRun.getDateAndTime().split(" ")[0], SUB_HEADER_COLOUR);
            var appendDate = preDate.copy().append(valueDate);
            graphics.drawString(minecraft.font, appendDate, getXStart, startY2 + spacer, -1);

            var preTime = Helpers.withStyleComponent("Time: ", HEADER_COLOUR);
            var valueTime = Helpers.withStyleComponent(pastRun.getDateAndTime().split(" ")[1], SUB_HEADER_COLOUR);
            var appendTime = preTime.copy().append(valueTime);
            graphics.drawString(minecraft.font, appendTime, getXStart, startY2 + 10 + spacer, -1);

            var preXp = Helpers.withStyleComponent("Experience: ", HEADER_COLOUR);
            var valueXp = Helpers.withStyleComponent(pastRun.getExperienceGained()+"XP", ABSORPTION_YELLOW);
            var appendXp = preXp.copy().append(valueXp);
            graphics.drawString(minecraft.font, appendXp, getXStart, startY2 + 20 + spacer, -1);

            var preRoom = Helpers.withStyleComponent("Rooms Cleared: ", HEADER_COLOUR);
            var valueRoom = Helpers.withStyleComponent(pastRun.getRoomsCleared() + "", AETHER_BLUE);
            var appendRoom = preRoom.copy().append(valueRoom);
            graphics.drawString(minecraft.font, appendRoom, getXStart, startY2 + 30 + spacer, -1);

            var preChest = Helpers.withStyleComponent("Loot Chests: ", HEADER_COLOUR);
            var valueChest = Helpers.withStyleComponent(pastRun.getChestsOpened() + "", COSMIC_PURPLE);
            var appendChest = preChest.copy().append(valueChest);
            graphics.drawString(minecraft.font, appendChest, getXStart, startY2 + 40 + spacer, -1);

            var preMob = Helpers.withStyleComponent("Mobs Killed: ", HEADER_COLOUR);
            var valueMob = Helpers.withStyleComponent(pastRun.getMobsKilled() + "", MAGNET_STRENGTH_RED);
            var appendMob = preMob.copy().append(valueMob);
            graphics.drawString(minecraft.font, appendMob, getXStart, startY2 + 50 + spacer, -1);

            boxMaker(graphics, getXStart - 6, startY2 - 6 + spacer, 60, 35, primary, fadeBackground, fadeBackground);

            spacer += 100;
            index--;
        }
    }


}
