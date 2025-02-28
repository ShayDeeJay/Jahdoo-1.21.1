package org.jahdoo.common.items.armor;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jahdoo.common.items.wand.WandItemHelper;
import org.jahdoo.common.registers.ComponentReg;

import java.util.List;

public class BaseArmor extends ArmorItem {
    public BaseArmor(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        var data = stack.get(ComponentReg.RUNE_HOLDER);
        if(data != null){
            WandItemHelper.appendRefinementPotential(tooltipComponents, stack);
        }
    }
}
