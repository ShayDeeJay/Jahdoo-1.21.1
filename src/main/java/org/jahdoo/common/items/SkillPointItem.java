package org.jahdoo.common.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.AttachmentReg;

public class SkillPointItem extends Item  {

    public SkillPointItem() { super(new Properties()); }

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
