package org.jahdoo.items;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Tool;

public class IngmasSword extends SwordItem {

    public IngmasSword() {
        super(Tiers.NETHERITE, new Properties().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4F)));
    }



}
