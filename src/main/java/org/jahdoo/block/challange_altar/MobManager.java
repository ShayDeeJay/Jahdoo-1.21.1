package org.jahdoo.block.challange_altar;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.entities.CustomZombie;
import org.jahdoo.utils.EnchantmentHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.utils.ModHelpers.Random;

public class MobManager {



    public static void attachEquipment(LivingEntity livingEntity, ServerLevel serverLevel){
        var itemStack = new ItemStack(Items.DIAMOND_SWORD);
        var enchanter = new EnchantRandomlyFunction.Builder();

        EnchantmentHelpers.enchant(itemStack, serverLevel.registryAccess(), Enchantments.SHARPNESS, 1);
        LootParams lootparams = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, livingEntity.position())
            .withParameter(LootContextParams.THIS_ENTITY, livingEntity)
            .create(LootContextParamSets.VAULT);
        var random = getChestPlate(serverLevel).getRandomItems(lootparams).getFirst();
//        ToolTipFlag.enchant(itemStack, serverLevel.registryAccess(), Enchantments.SHARPNESS, 5);
        livingEntity.setItemSlot(EquipmentSlot.CHEST, random);
    }

    public static LootTable getChestPlate(ServerLevel serverLevel){
        return LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(UniformGenerator.between(1.0F, 3.0F))
            .add(LootItem.lootTableItem(Items.CHAINMAIL_CHESTPLATE).setWeight(20)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
            .add(LootItem.lootTableItem(Items.CHAINMAIL_CHESTPLATE).setWeight(20)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
            .add(LootItem.lootTableItem(Items.LEATHER_CHESTPLATE).setWeight(20)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
            .add(LootItem.lootTableItem(Items.IRON_CHESTPLATE).setWeight(15)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
            .add(LootItem.lootTableItem(Items.GOLDEN_CHESTPLATE).setWeight(10)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
            .add(LootItem.lootTableItem(Items.DIAMOND_CHESTPLATE).setWeight(5)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
            .add(LootItem.lootTableItem(Items.NETHERITE_CHESTPLATE).setWeight(1)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
        ).build();
    }

    public static LivingEntity generateMob(ServerLevel serverLevel){
        var entity = new CustomZombie(serverLevel, null);
        var getEntity = entity.level().getNearestPlayer(entity, 30);
        attachEquipment(entity, serverLevel);
        if (entity instanceof Mob mob) mob.setTarget(getEntity);
        if (Random.nextInt(3) == 0) {
            entity.addEffect(new CustomMobEffect(MobEffects.MOVEMENT_SPEED, 200, Random.nextInt(0, 3)));
        }
        return entity;
    }

    public static void addAndPositionEntity(ChallengeAltarBlockEntity entity, BlockPos pos){
        if(entity.getLevel() instanceof ServerLevel serverLevel){
            var actualEntity = MobManager.generateMob(serverLevel);
            actualEntity.moveTo(pos.getCenter());
            ChallengeAltarData.addEntity(entity, actualEntity.getUUID());
            serverLevel.addFreshEntity(actualEntity);
        }
    }

    public static void summonEntities(ChallengeAltarBlockEntity entity){
        var pos = entity.getBlockPos();
        var data = ChallengeAltarData.getProperties(entity);
        var player = entity.getLevel().getNearestPlayer(TargetingConditions.DEFAULT, pos.getX(), pos.getY(), pos.getZ());
        var counter = new AtomicInteger();
        var spawnPos =  player != null && player.onGround() ? player.blockPosition() : pos;
        var radius = Random.nextInt(4, 9);
        var points = 200;
        for (var blockPos : PositionGetters.getRandomSphericalBlockPositions(spawnPos, radius, points)) {
            if(counter.get() < data.maxSpawnableMobs()){
                var above = entity.getLevel().getBlockState(blockPos.above());
                var main = entity.getLevel().getBlockState(blockPos);
                var below = entity.getLevel().getBlockState(blockPos.below());
                if (above.isAir() && main.isAir() && !below.isAir()) {
                    MobManager.addAndPositionEntity(entity, blockPos);
                    counter.set(counter.get() + 1);
                }
            }
        }
    }
}
