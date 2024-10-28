package org.jahdoo.items;

import net.minecraft.network.chat.Component;
import org.jahdoo.all_magic.JahdooRarity;

import java.util.List;

import static org.jahdoo.utils.GeneralHelpers.withStyleComponent;

public class ShareItemHelpers {
    public static void addRarity(List<Component> toolTips, JahdooRarity rarity){
        toolTips.add(withStyleComponent("Rarity: ", -9013642).copy().append(withStyleComponent(rarity.getSerializedName(), rarity.getColour())));
    }
}
