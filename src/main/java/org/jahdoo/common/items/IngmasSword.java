package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

import static org.jahdoo.ascension.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;

public class IngmasSword extends SwordItem implements JahdooItem{

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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        appendItemToolTips(stack, context, tooltipComponents, false);
        appendWeaponToolTip(stack, context, tooltipComponents);
    }

}
