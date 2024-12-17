package org.jahdoo.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.components.PowerGemData;
import org.jahdoo.components.WandData;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;

import java.util.List;

import static org.jahdoo.registers.DataComponentRegistry.*;

public class PowerGems extends Item {

    public PowerGems() {
        super(new Properties().component(DataComponentRegistry.POWER_GEM_DATA.get(), PowerGemData.DEFAULT));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(PowerGemData.getName(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
//        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.literal(PowerGemData.getDescription(stack)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(level.isClientSide){
            var stack = player.getMainHandItem();
            var getElement = ElementRegistry.getRandomElement();

            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewRarity(JahdooRarity.RARE.getId()));
            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewName(getElement.getElementName() + " " + PowerGemData.SUFFIX));
            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewColour(getElement.textColourPrimary()));
        }

        return super.use(level, player, usedHand);
    }

}
