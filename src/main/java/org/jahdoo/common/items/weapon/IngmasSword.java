package org.jahdoo.common.items.weapon;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

import static org.jahdoo.trial_nexus.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.withStyleComponent;

public class IngmasSword extends BaseWeapon {

    public IngmasSword() {
        super(
            Tiers.NETHERITE,
            new Properties().attributes(createAttributes(Tiers.NETHERITE, 5, -2.4F))
        );
    }

    @Override
    public Component getName(ItemStack stack) {
        return withStyleComponent(super.getName(stack).getString(), SUB_HEADER_COLOUR);
    }

}
