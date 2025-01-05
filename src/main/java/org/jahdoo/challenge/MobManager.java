package org.jahdoo.challenge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.entities.living.CustomSkeleton;
import org.jahdoo.entities.living.CustomZombie;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
import static org.jahdoo.utils.ModHelpers.Random;

public class MobManager {

    public static void attachEquipment(LivingEntity livingEntity, ServerLevel serverLevel, int round){
        LootParams lootparams = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, livingEntity.position())
            .withParameter(LootContextParams.THIS_ENTITY, livingEntity)
            .create(LootContextParamSets.VAULT);
        var randomLeather = new MobItemHandler(serverLevel,(float) round /10).getByRound(round);
        for (var randomItem : randomLeather.getRandomItems(lootparams)) {
            if(randomItem.getItem() instanceof ArmorItem armorItem){
                var equipmentSlot = armorItem.getEquipmentSlot();
                livingEntity.setItemSlot(equipmentSlot, randomItem);
            }
        }
    }

    public static void equipWeapon(LivingEntity livingEntity, ServerLevel serverLevel, int round){
        LootParams lootparams = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, livingEntity.position())
            .withParameter(LootContextParams.THIS_ENTITY, livingEntity)
            .create(LootContextParamSets.VAULT);
        var randomLeather = new MobItemHandler(serverLevel,(float) round /10).getRandomSkeletonWeapon().getRandomItems(lootparams);
        if(!randomLeather.isEmpty()){
            livingEntity.setItemSlot(EquipmentSlot.MAINHAND, randomLeather.get(Random.nextInt(randomLeather.size())));
        };
    }

    public static LivingEntity generateMob(LivingEntity livingEntity){
        var getEntity = livingEntity.level().getNearestPlayer(livingEntity, 200);
        if (livingEntity instanceof Mob mob) mob.setTarget(getEntity);
        return livingEntity;
    }

    public static LivingEntity getReadyZombie(ServerLevel serverLevel, int round){
        var entity = new CustomZombie(serverLevel, null);
        entity.setPersistenceRequired();
        attachEquipment(entity, serverLevel, round);
        equipWeapon(entity, serverLevel, round);
        return entity;
    }

    public static void permIncreaseBaseAttribute(LivingEntity livingEntity, double percentage, Holder<Attribute> attribute){
        if(livingEntity.getAttributes().hasAttribute(attribute)){
            var baseAttribute = livingEntity.getAttributeBaseValue(attribute);
            var attributeMultiplier = (percentage * (baseAttribute)) / 100;
            var res = ModHelpers.res(String.valueOf(UUID.randomUUID()));
            var modifier = new AttributeModifier(res, attributeMultiplier, ADD_VALUE);
            var instance = livingEntity.getAttributes().getInstance(attribute);
            if (instance != null) instance.addOrReplacePermanentModifier(modifier);
        }
    }

    public static LivingEntity getReadySkeleton(ServerLevel serverLevel, int round){
        var arrow = MobItemHandler.getAllowedArrow(round);
        var entity = new CustomSkeleton(serverLevel, null, arrow);
        entity.setPersistenceRequired();
        permIncreaseBaseAttribute(entity, 120, Attributes.ARMOR);
        attachEquipment(entity, serverLevel, round);
        equipWeapon(entity, serverLevel, round);
        return entity;
    }

    public static void addAndPositionEntity(ChallengeAltarBlockEntity entity, BlockPos pos){
        if(entity.getLevel() instanceof ServerLevel serverLevel){
            var readyZombie = getReadyZombie(serverLevel, ChallengeAltarData.getProperties(entity).round);
            var readySkeleton = getReadySkeleton(serverLevel, ChallengeAltarData.getProperties(entity).round);
            var getList = List.of(readyZombie, readySkeleton).get(Random.nextInt(2));
            var actualEntity = MobManager.generateMob(ChallengeAltarData.getRound(entity) > 5 ? getList : readyZombie);
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
