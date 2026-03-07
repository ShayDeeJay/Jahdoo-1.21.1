package org.jahdoo.common.items.weapon;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import org.jahdoo.trial_nexus.magic.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.Objects;

import static org.jahdoo.common.items.caster_item.CasterItemHelper.canOffHand;
import static org.jahdoo.common.registers.mod.ElementReg.fromId;
import static org.jahdoo.common.registers.mod.ElementReg.fromWand;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class ElementalSword extends BaseWeapon {

    public ElementalSword() {
        super(Tiers.NETHERITE, new Properties().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 10, -3F)));
    }

    private static AbstractElement element(ItemStack stack) {
        var elementIndex = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var actualIndex = elementIndex != null ? (elementIndex.value() + 1) : 1;
        return fromId(actualIndex).orElseThrow();
    }

    @Override
    public Component getName(ItemStack stack) {
        var element = element(stack);
        var name = element.name() + " Twinblade";
        var colour = element.partColourB();

        return TextHelpers.withStyleComponent(name, colour);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        var canOffhand = canOffHand(attacker, false);
        var getWand = attacker.getOffhandItem().getItem();

        fromWand(getWand).ifPresent(
            abstractElement -> {
                var isMatchingType = Objects.equals(element(stack), abstractElement);
                if(Random.nextInt(10) == 0){
                    if(canOffhand && isMatchingType){
                        var element = element(stack);
                        var setEffect = new JahdooMobEffect(element.effect(), 40, 1);
                        target.addEffect(setEffect);
                    }
                }
            }
        );

        super.postHurtEnemy(stack, target, attacker);
    }

}
