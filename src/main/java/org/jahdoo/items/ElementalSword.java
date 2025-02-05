package org.jahdoo.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.core.util.Throwables;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.effects.JahdooMobEffect;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.ElementRegistry;

import java.util.Objects;

import static org.jahdoo.items.wand.WandItemHelper.canOffHand;
import static org.jahdoo.registers.ElementRegistry.*;
import static org.jahdoo.utils.ModHelpers.*;

public class ElementalSword extends SwordItem {

    public ElementalSword() {
        super(Tiers.NETHERITE, new Properties().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 10, -2.4F)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
//        var mainHandItem = player.getMainHandItem();
//        if(!(level instanceof ServerLevel)) return InteractionResultHolder.fail(mainHandItem);
//        mainHandItem.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(Random.nextInt(5)));
        return super.use(level, player, usedHand);
    }

    @Override
    public Component getName(ItemStack stack) {
        var element = element(stack);
        var name = element.getElementName() + " Twinblade";
        var colour = element.particleColourSecondary();

        return withStyleComponent(name, colour);
    }

    private static AbstractElement element(ItemStack stack) {
        var elementIndex = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var actualIndex = elementIndex != null ? (elementIndex.value() + 1) : 1;
        return getElementById(actualIndex).orElseGet(INFERNO);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        var canOffhand = canOffHand(attacker, InteractionHand.OFF_HAND, false);
        var getWand = attacker.getOffhandItem().getItem();

        getElementFromWand(getWand).ifPresent(
            abstractElement -> {
                var isMatchingType = Objects.equals(element(stack), abstractElement);
                if(Random.nextInt(10) == 0){
                    if(canOffhand && isMatchingType){
                        var element = element(stack);
                        var setEffect = new JahdooMobEffect(element.elementEffect(), 40, 1);
                        target.addEffect(setEffect);
                    }
                }
            }
        );

        super.postHurtEnemy(stack, target, attacker);
    }

}
