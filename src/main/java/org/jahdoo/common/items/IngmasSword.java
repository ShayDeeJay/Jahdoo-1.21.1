package org.jahdoo.common.items;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

import static net.minecraft.world.item.SwordItem.*;

public class IngmasSword extends SwordItem {

    public IngmasSword() {
        super(
            Tiers.NETHERITE,
            new Properties().attributes(createAttributes(Tiers.NETHERITE, 3, -2.4F))
        );
    }

}
