package org.jahdoo.challenge;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.entities.living.CustomSkeleton;
import org.jahdoo.entities.living.CustomZombie;
import org.jahdoo.entities.living.EternalWizard;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.ModHelpers.getRgb;

public class MobManager {

    public static void attachEquipment(LivingEntity livingEntity, ServerLevel serverLevel, int round){
        var lootparams = new LootParams.Builder(serverLevel)
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

    public static ObjectArrayList<ItemStack> equipWeapon(LivingEntity livingEntity, ServerLevel serverLevel, int round){
        var lootparams = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, livingEntity.position())
            .withParameter(LootContextParams.THIS_ENTITY, livingEntity)
            .create(LootContextParamSets.VAULT);
        return new MobItemHandler(serverLevel,(float) round / 10).getRandomWeapon().getRandomItems(lootparams);
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
        var collection = equipWeapon(entity, serverLevel, round);
        if(!collection.isEmpty()) {
            var weapon = ModHelpers.getRandomListElement(collection);
            entity.setItemSlot(EquipmentSlot.MAINHAND, weapon.is(Items.BOW) ? ItemStack.EMPTY : weapon);
        };
        return entity;
    }

    public static LivingEntity getReadyEternalWizard(ServerLevel serverLevel){
        var entity = new EternalWizard(serverLevel, null, 12, 100, 2, -1, 30);
        entity.setPersistenceRequired();
        entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemsRegister.WAND_ITEM_VITALITY.get()));
        entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemsRegister.MAGE_HELMET.get()));
        entity.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemsRegister.MAGE_CHESTPLATE.get()));
        entity.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemsRegister.MAGE_LEGGINGS.get()));
        entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(ItemsRegister.MAGE_BOOTS.get()));
        return entity;
    }

    public static void permIncreaseBaseAttribute(LivingEntity livingEntity, double percentage, Holder<Attribute> attribute){
        if(livingEntity.getAttributes().hasAttribute(attribute)){
            var baseAttribute = livingEntity.getAttributeBaseValue(attribute);
            var attributeMultiplier = (percentage * (baseAttribute)) / 100;
            var res = res(String.valueOf(UUID.randomUUID()));
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
        var collection = equipWeapon(entity, serverLevel, round);
        if(!collection.isEmpty()){
            var weapon = ModHelpers.getRandomListElement(collection);
            entity.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        }
        return entity;
    }

    public static void addAndPositionEntity(ChallengeAltarBlockEntity entity, BlockPos pos){
        if(entity.getLevel() instanceof ServerLevel serverLevel){
            var readyZombie = getReadyZombie(serverLevel, ChallengeAltarData.getProperties(entity).round());
            var readySkeleton = getReadySkeleton(serverLevel, ChallengeAltarData.getProperties(entity).round());
            var readyWizard = getReadyEternalWizard(serverLevel);
            var skellyOrWiz = Random.nextInt(20) == 0 ? readyWizard : readySkeleton;
            var getList = List.of(readyZombie, skellyOrWiz).get(Random.nextInt(2));
            var actualEntity = MobManager.generateMob(ChallengeAltarData.getRound(entity) > 5 ? getList : readyZombie);
            setOuterRingPulses(actualEntity.level(), pos.getCenter(), actualEntity.getBbWidth());
            getSoundWithPosition(actualEntity.level(), pos, SoundEvents.WITHER_SPAWN, 0.05f, 3f);
            getSoundWithPosition(actualEntity.level(), pos, SoundRegister.HEAL.get(), 1f, 2f);
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
        var spawnPos =  player != null ? player.blockPosition() : pos;
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

    public static void setOuterRingPulses(Level level, Vec3 position, double radius){
        var particleOptions = genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, getRgb(), getRgb(), Random.nextInt(7, 10), 0.1f, true, 1);
        PositionGetters.getOuterRingOfRadiusRandom(position, radius, radius * 40,
            pos -> ParticleHandlers.sendParticles(
                level, particleOptions, pos, 0, 0, 1,0, Random.nextDouble(0.1, 0.4)
            )
        );
    }
}
