package org.jahdoo.trial_nexus.mobs;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Maledictus.Maledictus_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Entity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.entities.custom_entities.CustomSkeleton;
import org.jahdoo.common.entities.custom_entities.CustomZombie;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.inferno_creeper.InfernoCreeper;
import org.jahdoo.common.entities.void_spider.VoidSpider;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.ability.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.Maths;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.github.L_Ender.cataclysm.init.ModEntities.MALEDICTUS;
import static com.github.L_Ender.cataclysm.init.ModEntities.SCYLLA;
import static net.minecraft.core.component.DataComponents.TRIM;
import static net.minecraft.core.registries.Registries.TRIM_MATERIAL;
import static net.minecraft.core.registries.Registries.TRIM_PATTERN;
import static net.minecraft.world.entity.EquipmentSlot.*;
import static net.minecraft.world.entity.EquipmentSlot.CHEST;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static net.minecraft.world.item.Items.*;
import static net.minecraft.world.item.armortrim.TrimMaterials.GOLD;
import static net.minecraft.world.item.armortrim.TrimMaterials.REDSTONE;
import static net.minecraft.world.item.armortrim.TrimPatterns.RIB;
import static net.minecraft.world.item.armortrim.TrimPatterns.SILENCE;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.VAULT;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY;
import static org.jahdoo.common.entities.ancient_golem.AncientGolem.INFINITE_LIFE;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.difficultyFromInstance;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.MASTER;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.*;
import static org.jahdoo.trial_nexus.utils.EnchantmentHelpers.enchant;
import static org.jahdoo.trial_nexus.utils.Helpers.*;
import static org.jahdoo.trial_nexus.utils.PositionFinders.getOuterRingOfRadiusRandom;
import static org.jahdoo.trial_nexus.utils.PositionFinders.getRandomSphericalBlockPositions;

public class MobManager {

    private static LivingEntity getVoidSpider(ServerLevel serverLevel) {
        return new VoidSpider(serverLevel);
    }

    private static LivingEntity getInfernoCreeper(ServerLevel serverLevel) {
        return new InfernoCreeper(serverLevel);
    }

    private static void addProtection(ServerLevel serverLevel, ItemStack stack, int round) {
        var level = calculateEnchantmentLevel(round);

        enchant(stack, serverLevel.registryAccess(), PROTECTION, level);
    }

    private static LivingEntity getAncienGolem(ServerLevel serverLevel, int round) {
        var damage = Maths.getPercentageTotal(round, 12);
//        var ancientGolem = new AncientGolem(serverLevel, null, damage, 100, 1, INFINITE_LIFE, 20);
        var ancientGolem = new Scylla_Entity(SCYLLA.get(), serverLevel);
        addBaseAttribute(MAX_HEALTH, ancientGolem, 300);
//        addBaseAttribute(SCALE, ancientGolem, 50);
        return ancientGolem;
    }

    public static void effectWithChance(LivingEntity livingEntity, Holder<MobEffect> effect, int amplifier, int chance) {
        if(Maths.percentageChance(chance)){
            if(!livingEntity.hasEffect(effect)){
                livingEntity.addEffect(new JahdooMobEffect(effect, MobEffectInstance.INFINITE_DURATION, amplifier));
            }
        }
    }

    public static void addBaseAttribute(
        Holder<Attribute> attributes,
        LivingEntity getEntity,
        double multiplier
    ){
        if(getEntity.getAttributes().hasAttribute(attributes)){
            var attributeInstance = getEntity.getAttributes().getInstance(attributes);
            if (attributeInstance == null) return;
            attributeInstance.setBaseValue(Maths.getPercentageTotal(multiplier, attributeInstance.getValue()));
        }
    }

    public static LivingEntity generateMob(LivingEntity livingEntity, InstanceData getData){
        var getEntity = livingEntity.level().getNearestPlayer(livingEntity, 200);
        addBaseAttribute(MAX_HEALTH, livingEntity, getData.getHealth());
        addBaseAttribute(ARMOR, livingEntity, getData.getArmor());
        addBaseAttribute(ATTACK_DAMAGE, livingEntity, getData.getAttackDamage());
        addBaseAttribute(MOVEMENT_SPEED, livingEntity, getData.getSpeed());
        if (livingEntity instanceof Mob mob) mob.setTarget(getEntity);
        return livingEntity;
    }

    private static int calculateEnchantmentLevel(int round) {
        if (round > 200) return 10;
        if (round > 140) return 9;
        if (round > 120) return 8;
        if (round > 100) return 5;
        if (round > 80) return 4;
        if (round > 60) return 3;
        if (round > 40) return 2;
        return 1;
    }

    public static ObjectArrayList<ItemStack> equipWeapon(LivingEntity livingEntity, ServerLevel serverLevel, InstanceData data){
        var lootparams = new LootParams
            .Builder(serverLevel)
            .withParameter(ORIGIN, livingEntity.position())
            .withParameter(THIS_ENTITY, livingEntity)
            .create(VAULT);

        return new MobItemHandler(serverLevel, data.getClearedRooms(), data.getDifficulty()).getRandomWeapon().getRandomItems(lootparams);
    }

    public static void setOuterRingPulses(Level level, Vec3 position, double radius){
        var lifetime = Random.nextInt(7, 10);
        var parType = ParticleStore.MAGIC_PARTICLE;
        var particleOptions = ParticleHandlers.genericParticle(parType, getRgb(), getRgb(), lifetime, 0.1f, true, 1);

        getOuterRingOfRadiusRandom(position, radius, radius * 40,
            pos -> ParticleHandlers.sendParticles(
                level, particleOptions, pos, 0, 0, 1,0, Random.nextDouble(0.1, 0.4)
            )
        );
    }

    public static LivingEntity getReadyZombie(ServerLevel serverLevel, String id, InstanceData data){
//        var entity = switch (id){
//            case THE_HALL::contains -> new CustomZombie(serverLevel, null);
//            case THE_CHAMBERS -> new ZombieVillager(EntityType.ZOMBIE_VILLAGER, serverLevel)  ;
//            case THE_OASIS -> new Husk(EntityType.HUSK, serverLevel);
//            default -> new ZombifiedPiglin(EntityType.ZOMBIFIED_PIGLIN, serverLevel);
//        };

        Monster entity;
        if(THE_HALL.contains(id)){
            entity = new CustomZombie(serverLevel, null);
        } else if (THE_CHAMBERS.contains(id)) {
            entity = new ZombieVillager(EntityType.ZOMBIE_VILLAGER, serverLevel);
        } else if (THE_OASIS.contains(id)) {
            entity = new Husk(EntityType.HUSK, serverLevel);
        } else {
            entity = new ZombifiedPiglin(EntityType.ZOMBIFIED_PIGLIN, serverLevel);
        }

        attachEquipment(entity, serverLevel, data);

        var collection = equipWeapon(entity, serverLevel, data);
        if(!collection.isEmpty()) {
            var weapon = Helpers.listRandom(collection);
            entity.setItemSlot(MAINHAND, weapon.is(Items.BOW) ? ItemStack.EMPTY : weapon);
        };

        entity.setPersistenceRequired();
        return entity;
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
        var randomLeather = new MobItemHandler(serverLevel, data.getClearedRooms(), difficulty).getByDifficulty(difficulty);

        for (var randomItem : randomLeather.getRandomItems(lootparams)) {
            if(randomItem.getItem() instanceof ArmorItem armorItem){
                var equipmentSlot = armorItem.getEquipmentSlot();
                livingEntity.setItemSlot(equipmentSlot, randomItem);
            }
        }
    }

    public static void addAndPositionEntity(ServerLevel serverLevel, BlockPos pos, LivingEntity entity){
        setOuterRingPulses(serverLevel, pos.getCenter(), entity.getBbWidth());
        getSoundWithPosition(serverLevel, pos, SoundReg.ORB_CREATE.get(), 0.4F, 1.8F);
        getSoundWithPosition(serverLevel, pos, SoundEvents.ALLAY_HURT, 0.3F, 0.8F);
        entity.moveTo(pos.getCenter());
        serverLevel.addFreshEntity(entity);
    }

    public static boolean championSpawn(ServerLevel serverLevel, LivingEntity entity) {
        var data = serverLevel.getData(AttachmentReg.INSTANCE_DATA);
        var instanceDiff = difficultyFromInstance(data);

        if(instanceDiff.isPresent() && Maths.percentageChance(instanceDiff.get().getSpecialSpawnChance())) {
            var ob = instanceDiff.get();
            var id = ob.getId() * 3;

            entity.addEffect(new JahdooMobEffect(EffectReg.CHAMPION_EFFECT, INFINITE_LIFE, id));
            entity.addEffect(new JahdooMobEffect(MobEffects.GLOWING, INFINITE_LIFE, 1));

            addBaseAttribute(MAX_HEALTH, entity, entity.getAttributeValue(MAX_HEALTH) * id);
            addBaseAttribute(ARMOR, entity, entity.getAttributeValue(ARMOR) * id);
            addBaseAttribute(ATTACK_DAMAGE, entity, entity.getAttributeValue(ATTACK_DAMAGE) * id);
            addBaseAttribute(MOVEMENT_SPEED, entity, entity.getAttributeValue(MOVEMENT_SPEED) * id);


            if (Objects.equals(data.getDifficulty(), MASTER.getSerializedName())) {
                entity.addEffect(new JahdooMobEffect(MobEffects.REGENERATION, INFINITE_LIFE, 1));
            }

            entity.setHealth(entity.getMaxHealth());
            return true;
        }

        return false;
    }

    public static LivingEntity getEliteSkeleton(ServerLevel serverLevel, int level){
//        var skeleton = new CustomSkeleton(serverLevel, null, new ItemStack(ARROW));
//        var getEliteArmor = getEliteArmor(serverLevel, 100);
        var skeleton = new Maledictus_Entity(MALEDICTUS.get(), serverLevel);

        addBaseAttribute(MAX_HEALTH, skeleton, 300);
//        addBaseAttribute(SCALE, skeleton, 50);
//        skeleton.setElite();
//
//        effectWithChance(skeleton, MobEffects.MOVEMENT_SPEED, 0, 100);
//
//        skeleton.setCustomName(Component.literal("Master Archer"));
//        skeleton.setItemSlot(HEAD, getEliteArmor.getFirst());
//        skeleton.setItemSlot(CHEST, getEliteArmor.get(1));
//        skeleton.setItemSlot(LEGS, getEliteArmor.get(2));
//        skeleton.setItemSlot(FEET, getEliteArmor.get(3));
//        skeleton.setItemSlot(MAINHAND, getEliteArmor.get(4));
        return skeleton;
    }

    public static void summonEntities(AltarBlockEntity entity, String roomId){
        if(!(entity.getLevel() instanceof ServerLevel level)) return;

        if(!Objects.equals(roomId, BOSS_CRUCIBLE)){
            var actualEntity = buildMobs(level, roomId);
            //Heal as when adding more health still spawns with only the amount of health that is default
            for (var livingEntity : actualEntity) {
                livingEntity.setHealth(livingEntity.getMaxHealth());
            }
            entity.spawnableMobs.addAll(actualEntity);

        } else {
            var round = entity.getData(INSTANCE_DATA).getClearedRooms();
            var ancienGolem = getAncienGolem(level, round);
            var eliteSkeleton = getEliteSkeleton(level, round);
            var boss = Helpers.listRandom(List.of(ancienGolem, eliteSkeleton));

            //Heal as when adding more health still spawns with only the amount of health that is default
            boss.setHealth(boss.getMaxHealth());
            boss.getPersistentData().putBoolean("boss", true);
            addAndPositionEntity(level, entity.getBlockPos().relative(entity.direction, -3), boss);
            boss.setYBodyRot(entity.direction.toYRot());
            entity.onField.add(boss.getUUID());
        }
    }

    public static void spawnAroundEntity(
        Level level,
        BlockPos spawnPos,
        int radius,
        int points,
        Consumer<BlockPos> spawn
    ) {
        for (var blockPos : getRandomSphericalBlockPositions(spawnPos, radius, points)) {
            var above = level.getBlockState(blockPos.above(2));
            var main = level.getBlockState(blockPos.above());
            var below = level.getBlockState(blockPos);
            if (above.isAir() && main.isAir() && !(below.isAir() || !below.getFluidState().isEmpty())) spawn.accept(blockPos);
        }
    }

    public static LivingEntity getReadyEternalWizard(ServerLevel serverLevel, InstanceData data){
        var damage = 10 + (1 * data.getAttackDamage());
        var entity = new EternalWizard(serverLevel, null, damage, 200, 2, -1, 30, 5);
        var wand = new ItemStack(ItemReg.WAND_ITEM_VITALITY.get());
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

    public static List<ItemStack> getEliteArmor(ServerLevel serverLevel, int round){
        var reg = serverLevel.registryAccess();
        var lookupA = reg.lookup(TRIM_MATERIAL).orElseThrow();
        var lookupB = reg.lookup(TRIM_PATTERN).orElseThrow();

        var trimA = new ArmorTrim(lookupA.get(REDSTONE).orElseThrow(), lookupB.get(RIB).orElseThrow());
        var trimB = new ArmorTrim(lookupA.get(GOLD).orElseThrow(), lookupB.get(SILENCE).orElseThrow());
        var trimC = new ArmorTrim(lookupA.get(GOLD).orElseThrow(), lookupB.get(RIB).orElseThrow());

        var stack = new ItemStack(NETHERITE_HELMET);
        var stack1 = new ItemStack(NETHERITE_CHESTPLATE);
        var stack2 = new ItemStack(NETHERITE_LEGGINGS);
        var stack3 = new ItemStack(NETHERITE_BOOTS);
        var bow = new ItemStack(BOW);

        addProtection(serverLevel, stack, round);
        addProtection(serverLevel, stack1, round);
        addProtection(serverLevel, stack2, round);
        addProtection(serverLevel, stack3, round);

        enchant(stack3, reg, FEATHER_FALLING, 4);
        enchant(bow, reg, POWER, calculateEnchantmentLevel(round));

        stack.set(TRIM, trimA);
        stack1.set(TRIM, trimB);
        stack2.set(TRIM, trimC);
        stack3.set(TRIM, trimC);

        return List.of(stack, stack1, stack2, stack3, bow);
    }

    private static List<LivingEntity> buildMobs(ServerLevel serverLevel, String roomId) {
        var data = serverLevel.getData(INSTANCE_DATA);
        var entities = new ArrayList<LivingEntity>();
        var getInstanceData = serverLevel.getData(INSTANCE_DATA);

        for (var i = 0; i < getInstanceData.getHorde(); i++){
            entities.add(generateMob(getReadyZombie(serverLevel, roomId, data), getInstanceData));
        }

        for (var i = 0; i < getInstanceData.getSkeleton(); i++){
            entities.add(generateMob(getReadySkeleton(serverLevel, data), getInstanceData));
        }

        for (var i = 0; i < getInstanceData.getEternalWizard(); i++){
            entities.add(generateMob(getReadyEternalWizard(serverLevel, data), getInstanceData));
        }

        for (var i = 0; i < getInstanceData.getVoidSpider(); i++){
            entities.add(generateMob(getVoidSpider(serverLevel), getInstanceData));
        }

        for (var i = 0; i < getInstanceData.getInfernoCreeper(); i++){
            entities.add(generateMob(getInfernoCreeper(serverLevel), getInstanceData));
        }

        return entities;
    }
}
