package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.AttachmentReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

public class SkillPointItem extends BaseJahdooItem {

    public SkillPointItem() { super(new Properties()); }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ColourHelpers.getRating5Green());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);

        if(player.isShiftKeyDown()){
            for (int i = 0; i < item.getCount(); i++){
                player.getData(AttachmentReg.CASTER_DATA).incrementAbilityPoints(1);
            }
            item = ItemStack.EMPTY;
        }

        player.getData(AttachmentReg.CASTER_DATA).incrementAbilityPoints(1);
        item.shrink(1);

        return InteractionResultHolder.success(item);
    }

}
