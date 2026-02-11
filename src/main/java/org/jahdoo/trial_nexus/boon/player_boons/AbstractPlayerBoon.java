package org.jahdoo.trial_nexus.boon.player_boons;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.common.networking.client2server.AttributeC2SP;
import org.jahdoo.common.registers.mod.RuneReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;

import static java.util.Arrays.stream;
import static net.minecraft.network.chat.Component.translatable;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;

public abstract class AbstractPlayerBoon {

    public Boon doOnClick(){
        var id = attribute().value().getDescriptionId();
        var split = translatable(id).getString();
        var string = stream(split.split(" ")).toList();
        var getValue = org.shaydee.shaydeeapi.Maths.roundNonWholeString(org.shaydee.shaydeeapi.Maths.doubleFormattedDouble(value()));
        var formattedString = (value() < 0 ? "" : "+") + getValue + (isPercentage() ? "% " : " ");
        var colourBy = RuneReg.getRuneFromAttribute(attribute()).runeColour();
        var componentList = new ArrayList<net.minecraft.network.chat.Component>();

        componentList.add(TextHelpers.withStyleComponent(formattedString, value() < 0 ? ColourHelpers.getNegativeRed() : ColourHelpers.getUniqueA()));

        if(split.length() > 22){
            for (var i = 0; i < string.size(); i += 2) {
                var s = i + 1 < string.size() ? " " + string.get(i + 1) : "";
                componentList.add(TextHelpers.withStyleComponent(string.get(i) + s, colourBy));
            }
        } else {
            componentList.add(TextHelpers.withStyleComponent(split, colourBy));
        }

        return new Boon(componentList, colourBy, () -> sendToServer(new AttributeC2SP(attribute(), value())), icon());
    }

    public abstract boolean isPercentage();

    public abstract int colour();

    public abstract ResourceLocation icon();

    public abstract Holder<Attribute> attribute();

    public abstract double value();



}
