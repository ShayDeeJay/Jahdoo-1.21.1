package org.jahdoo.common.items.weapon;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;


public class IngmasSword extends BaseWeapon {

    public IngmasSword() {
        super(
            Tiers.NETHERITE,
            new Properties().attributes(createAttributes(Tiers.NETHERITE, 5, -2.4F))
        );
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ColourHelpers.getSubHeaderColour());
    }

}
