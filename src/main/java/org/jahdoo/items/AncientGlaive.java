package org.jahdoo.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.utils.ModHelpers;

public class AncientGlaive extends SwordItem {

    public AncientGlaive() {
        super(Tiers.NETHERITE, glaiveProperties());
    }

    public static Properties glaiveProperties(){
        var properties = new Properties();
        int attackDamage = 3;
        float attackSpeed = -1.4F;
        var modifier = new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + Tiers.NETHERITE.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE);
        var modifier1 = new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE);
        var modifier2 = new AttributeModifier(ModHelpers.res("reach"), 2, AttributeModifier.Operation.ADD_VALUE);
        var mainHand = EquipmentSlotGroup.MAINHAND;
        var attributes = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, modifier, mainHand)
                .add(Attributes.ATTACK_SPEED, modifier1, mainHand)
                .add(Attributes.ENTITY_INTERACTION_RANGE, modifier2, mainHand)
                .build();
        properties.attributes(attributes);
        return properties;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return super.use(level, player, usedHand);
    }

}
