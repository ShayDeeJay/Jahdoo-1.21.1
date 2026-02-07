package org.jahdoo.common.items.weapon;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.items.JahdooItem;

import java.util.List;

public class BaseWeapon extends SwordItem implements JahdooItem {

    public BaseWeapon(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        appendItemToolTips(stack, context, tooltipComponents, false);
        if(isItemBroken(stack)){
            brokenGearMessage(tooltipComponents, stack);
            return;
        }
        enchantmentTooltip(stack, tooltipComponents, true, context.level());
        appendWeaponToolTip(stack, context, tooltipComponents);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment != Enchantments.MENDING;
    }

    @Override
    public ItemStack applyEnchantments(ItemStack stack, List<EnchantmentInstance> enchantments) {
        enchantments.removeIf(s -> s.enchantment.equals(Enchantments.MENDING));
        return super.applyEnchantments(stack, enchantments);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canGrindstoneRepair(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return -1;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        for (var holderEntry : book.get(DataComponents.STORED_ENCHANTMENTS).entrySet()) {
            if(holderEntry.getKey().value().description().getString().contains("Mending")){
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(attacker.level() instanceof ServerLevel serverLevel){
            JahdooHelpers.hurtAndKeepItem(stack, 1, serverLevel, attacker);
        }
    }
}
