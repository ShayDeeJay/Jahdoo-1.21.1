package org.jahdoo.common.items;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class IngmasSword extends SwordItem {

    public IngmasSword() {
        super(
            Tiers.NETHERITE,
            new Properties().attributes(createAttributes(Tiers.NETHERITE, 5, -2.4F))
        );
    }

}
