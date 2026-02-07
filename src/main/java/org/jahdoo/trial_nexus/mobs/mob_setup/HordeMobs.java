package org.jahdoo.trial_nexus.mobs.mob_setup;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jahdoo.common.entities.custom_entities.CustomSkeleton;
import org.jahdoo.common.entities.custom_entities.CustomZombie;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.mobs.MobItemHandler;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static net.minecraft.world.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.VAULT;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.*;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.listRandom;

public class HordeMobs {

    public static LivingEntity getZombies(ServerLevel serverLevel, InstanceData instanceData, String id) {
        return generateMob(getReadyZombie(serverLevel, id, instanceData),  instanceData);
    }

    public static LivingEntity getSkeletons(ServerLevel serverLevel, InstanceData instanceData) {
        return generateMob(getReadySkeleton(serverLevel, instanceData),  instanceData);
    }

    public static ObjectArrayList<ItemStack> equipWeapon(LivingEntity livingEntity, ServerLevel serverLevel, InstanceData data){
        var lootparams = new LootParams
            .Builder(serverLevel)
            .withParameter(ORIGIN, livingEntity.position())
            .withParameter(THIS_ENTITY, livingEntity)
            .create(VAULT);

        return new MobItemHandler(serverLevel, data.getClearedRooms(), data.getDifficulty()).getRandomWeapon().getRandomItems(lootparams);
    }

    public static LivingEntity getReadySkeleton(ServerLevel serverLevel, InstanceData data){
        var arrow = MobItemHandler.getAllowedArrow(0, data.getDifficulty());
        var entity = new CustomSkeleton(serverLevel, null, arrow);

        entity.setPersistenceRequired();
        attachEquipment(entity, serverLevel, data);
        var collection = equipWeapon(entity, serverLevel, data);

        if(!collection.isEmpty()){
            entity.setItemSlot(MAINHAND, listRandom(collection));
        }
        return entity;
    }


    public static void attachEquipment(LivingEntity livingEntity, ServerLevel serverLevel, InstanceData data){
        var lootparams = new LootParams.Builder(serverLevel)
            .withParameter(ORIGIN, livingEntity.position())
            .withParameter(THIS_ENTITY, livingEntity)
            .create(VAULT);
        var difficulty = data.getDifficulty();
        var randomArmor = new MobItemHandler(serverLevel, data.getClearedRooms(), difficulty).getByDifficulty(difficulty);

        for (var randomItem : randomArmor.getRandomItems(lootparams)) {
            if(randomItem.getItem() instanceof ArmorItem armorItem){
                var equipmentSlot = armorItem.getEquipmentSlot();
                livingEntity.setItemSlot(equipmentSlot, randomItem);
            }
        }
    }

    public static LivingEntity getReadyZombie(ServerLevel serverLevel, String id, InstanceData data){
        Monster entity;

        if(EASY_ROOMS.contains(id)){
            entity = new CustomZombie(serverLevel, null);
        } else if (HARD_ROOMS.contains(id)) {
            entity = new ZombieVillager(EntityType.ZOMBIE_VILLAGER, serverLevel);
        } else if (MEDIUM_ROOMS.contains(id)) {
            entity = new Husk(EntityType.HUSK, serverLevel);
        } else {
            entity = new ZombifiedPiglin(EntityType.ZOMBIFIED_PIGLIN, serverLevel);
        }

        var collection = equipWeapon(entity, serverLevel, data);
        if(!collection.isEmpty()) {
            var weapon = JahdooHelpers.listRandom(collection);
            entity.setItemSlot(MAINHAND, weapon.is(Items.BOW) ? ItemStack.EMPTY : weapon);
        }

        attachEquipment(entity, serverLevel, data);
        entity.setPersistenceRequired();
        return entity;
    }
}
