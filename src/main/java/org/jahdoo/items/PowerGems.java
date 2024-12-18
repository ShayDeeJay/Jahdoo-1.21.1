package org.jahdoo.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.components.PowerGemData;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;

import java.util.List;

import static org.jahdoo.items.wand.WandItemHelper.standAloneAttributes;
import static org.jahdoo.registers.AttributesRegister.replaceOrAddAttribute;
import static org.jahdoo.registers.DataComponentRegistry.*;

public class PowerGems extends Item {

    public PowerGems() {
        super(new Properties().component(DataComponentRegistry.POWER_GEM_DATA.get(), PowerGemData.DEFAULT));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(PowerGemData.getName(stack)).withStyle();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
//        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.addAll(standAloneAttributes(stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var stack = player.getMainHandItem();
        if(stack.getAttributeModifiers().modifiers().isEmpty()){
            if (!level.isClientSide) {
                var getElement = ElementRegistry.getRandomElement();
                replaceOrAddAttribute(stack, getElement.getTypeCooldownReduction().getFirst(), getElement.getTypeCooldownReduction().getSecond(), 12, EquipmentSlot.MAINHAND, true);
                stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewRarity(JahdooRarity.RARE.getId()));
                stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewName(getElement.getElementName() + " " + PowerGemData.SUFFIX));
                stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewColour(getElement.particleColourSecondary()));
            }
        }

        return super.use(level, player, usedHand);
    }

}
