package org.jahdoo.trial_nexus.mobs.mob_setup;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.inferno_creeper.InfernoCreeper;
import org.jahdoo.common.entities.void_spider.VoidSpider;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;

import static net.minecraft.world.entity.EquipmentSlot.*;
import static net.minecraft.world.entity.EquipmentSlot.FEET;
import static net.minecraft.world.entity.EquipmentSlot.LEGS;
import static net.minecraft.world.item.enchantment.Enchantments.PROTECTION;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.*;
import static org.jahdoo.trial_nexus.utils.EnchantmentHelpers.enchant;

public class SpecialMobs {
    public static LivingEntity getWizard(ServerLevel serverLevel, InstanceData instanceData) {
        return generateMob(getReadyEternalWizard(serverLevel,instanceData),  instanceData);
    }

    public static LivingEntity getSpider(ServerLevel serverLevel, InstanceData instanceData) {
        return generateMob(new VoidSpider(serverLevel),  instanceData);
    }

    public static LivingEntity getCreeper(ServerLevel serverLevel, InstanceData instanceData) {
        return generateMob(new InfernoCreeper(serverLevel),  instanceData);
    }

    public static void addProtection(ServerLevel serverLevel, ItemStack stack, int round) {
        var level = calculateEnchantmentLevel(round);
        enchant(stack, serverLevel.registryAccess(), PROTECTION, level);
    }

    private static int calculateEnchantmentLevel(int round) {
        return switch (round) {
            case 100 -> 5;
            case 80 -> 4;
            case 60 -> 3;
            case 40 -> 2;
            default -> 1;
        };
    }

    public static LivingEntity getReadyEternalWizard(ServerLevel serverLevel, InstanceData data){
        var damage = 10 + (1 * data.getAttackDamage());
        var entity = new EternalWizard(serverLevel, null, damage, 200, 2, -1, 30, 5);
        var wand = new ItemStack(ItemReg.WAND_VITALITY.get());
        var helm = new ItemStack(ItemReg.MAGE_HELMET.get());
        var chestplate = new ItemStack(ItemReg.MAGE_CHESTPLATE.get());
        var leggings = new ItemStack(ItemReg.MAGE_LEGGINGS.get());
        var boots = new ItemStack(ItemReg.MAGE_BOOTS.get());
        var multipliers = serverLevel.getData(INSTANCE_DATA).getClearedRooms();

        entity.setPersistenceRequired();
        entity.setItemSlot(MAINHAND, wand);
        addProtection(serverLevel, helm, multipliers);
        addProtection(serverLevel, chestplate, multipliers);
        addProtection(serverLevel, leggings, multipliers);
        addProtection(serverLevel, boots, multipliers);
        entity.setItemSlot(HEAD, helm);
        entity.setItemSlot(CHEST, chestplate);
        entity.setItemSlot(LEGS, leggings);
        entity.setItemSlot(FEET, boots);
        return entity;
    }

}
