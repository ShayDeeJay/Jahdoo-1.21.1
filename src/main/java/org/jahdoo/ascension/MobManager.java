package org.jahdoo.ascension;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.abilities.arcane_shift.ArcaneShift;
import org.jahdoo.ascension.attachments.player_abilities.InstanceData;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.entities.CustomSkeleton;
import org.jahdoo.common.entities.CustomZombie;
import org.jahdoo.common.entities.ancient_golem.AncientGolem;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.void_spider.VoidSpider;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.core.BlockPos.*;
import static net.minecraft.core.component.DataComponents.*;
import static net.minecraft.core.registries.Registries.*;
import static net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE;
import static net.minecraft.world.effect.MobEffects.HEALTH_BOOST;
import static net.minecraft.world.entity.EquipmentSlot.*;
import static net.minecraft.world.entity.EquipmentSlot.CHEST;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static net.minecraft.world.item.Items.*;
import static net.minecraft.world.item.armortrim.TrimMaterials.*;
import static net.minecraft.world.item.armortrim.TrimMaterials.REDSTONE;
import static net.minecraft.world.item.armortrim.TrimPatterns.*;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.VAULT;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY;
import static org.jahdoo.ascension.LevelStageModifiers.addBaseAttribute;
import static org.jahdoo.ascension.LevelStageModifiers.effectWithChance;
import static org.jahdoo.ascension.ability.abilities.arcane_shift.ArcaneShift.*;
import static org.jahdoo.ascension.utils.EnchantmentHelpers.enchant;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.PositionFinders.*;
import static org.jahdoo.ascension.utils.PositionFinders.getOuterRingOfRadiusRandom;
import static org.jahdoo.ascension.utils.PositionFinders.getRandomSphericalBlockPositions;
import static org.jahdoo.common.entities.ancient_golem.AncientGolem.INFINITE_LIFE;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;

public class MobManager {

    private static LivingEntity getVoidSpider(ServerLevel serverLevel) {
        return new VoidSpider(serverLevel);
    }

    private static void addProtection(ServerLevel serverLevel, ItemStack stack, int round) {
        var level = calculateEnchantmentLevel(round);

        enchant(stack, serverLevel.registryAccess(), PROTECTION, level);
    }

    private static LivingEntity getAncienGolem(ServerLevel serverLevel, int round) {
        var damage = Maths.getPercentageTotal(round, 12);

        return new AncientGolem(serverLevel, null, damage, 100, 1, INFINITE_LIFE, 20);
    }

    public static LivingEntity generateMob(LivingEntity livingEntity, InstanceData getData){
        var getEntity = livingEntity.level().getNearestPlayer(livingEntity, 200);
        addBaseAttribute(MAX_HEALTH, livingEntity, getData.getHealthMultiplier());
        addBaseAttribute(ARMOR, livingEntity, getData.getArmorMultiplier());
        addBaseAttribute(ATTACK_DAMAGE, livingEntity, getData.getAttackDamageMultiplier());
        addBaseAttribute(MOVEMENT_SPEED, livingEntity, getData.getSpeedMultiplier());
        if (livingEntity instanceof Mob mob) mob.setTarget(getEntity);
        return livingEntity;
    }

    private static int calculateEnchantmentLevel(int round) {
        if (round > 70) return 10;
        if (round > 60) return 9;
        if (round > 50) return 8;
        if (round > 40) return 7;
        if (round > 30) return 6;
        return 5;
    }

    public static ObjectArrayList<ItemStack> equipWeapon(LivingEntity livingEntity, ServerLevel serverLevel, int round){
        var lootparams = new LootParams
            .Builder(serverLevel)
            .withParameter(ORIGIN, livingEntity.position())
            .withParameter(THIS_ENTITY, livingEntity)
            .create(VAULT);

        return new MobItemHandler(serverLevel,(float) round / 10).getRandomWeapon().getRandomItems(lootparams);
    }

    public static void setOuterRingPulses(Level level, Vec3 position, double radius){
        var lifetime = Random.nextInt(7, 10);
        var parType = ParticleStore.MAGIC_PARTICLE_SELECTION;
        var particleOptions = genericParticleOptions(parType, getRgb(), getRgb(), lifetime, 0.1f, true, 1);

        getOuterRingOfRadiusRandom(position, radius, radius * 40,
            pos -> ParticleHandlers.sendParticles(
                level, particleOptions, pos, 0, 0, 1,0, Random.nextDouble(0.1, 0.4)
            )
        );
    }

    public static LivingEntity getReadyZombie(ServerLevel serverLevel){
        var entity = new CustomZombie(serverLevel, null);
        entity.setPersistenceRequired();
//        attachEquipment(entity, serverLevel, round);

//        var collection = equipWeapon(entity, serverLevel, round);
//        if(!collection.isEmpty()) {
//            var weapon = Helpers.listRandom(collection);
//            entity.setItemSlot(MAINHAND, weapon.is(Items.BOW) ? ItemStack.EMPTY : weapon);
//        };

        return entity;
    }

    public static LivingEntity getReadySkeleton(ServerLevel serverLevel){
        var arrow = MobItemHandler.getAllowedArrow(0);
        var entity = new CustomSkeleton(serverLevel, null, arrow);

        entity.setPersistenceRequired();
        attachEquipment(entity, serverLevel, 10);
        var collection = equipWeapon(entity, serverLevel, 50);

        if(!collection.isEmpty()){
            entity.setItemSlot(MAINHAND, listRandom(collection));
        }
        return entity;
    }

    public static void attachEquipment(LivingEntity livingEntity, ServerLevel serverLevel, int round){
        var lootparams = new LootParams.Builder(serverLevel)
            .withParameter(ORIGIN, livingEntity.position())
            .withParameter(THIS_ENTITY, livingEntity)
            .create(VAULT);
        var randomLeather = new MobItemHandler(serverLevel,(float) round /10).getByRound(round);

        for (var randomItem : randomLeather.getRandomItems(lootparams)) {
            if(randomItem.getItem() instanceof ArmorItem armorItem){
                var equipmentSlot = armorItem.getEquipmentSlot();
                livingEntity.setItemSlot(equipmentSlot, randomItem);
            }
        }
    }

    public static void addAndPositionEntity(ServerLevel serverLevel, BlockPos pos, LivingEntity entity){
        setOuterRingPulses(serverLevel, pos.getCenter(), entity.getBbWidth());
        getSoundWithPosition(serverLevel, pos, SoundEvents.WITHER_SPAWN, 0.05f, 3f);
        getSoundWithPosition(serverLevel, pos, SoundReg.HEAL.get(), 1f, 2f);
        entity.moveTo(pos.getCenter());
        serverLevel.addFreshEntity(entity);
    }

    public static LivingEntity getEliteSkeleton(ServerLevel serverLevel){
        var skeleton = new CustomSkeleton(serverLevel, null, new ItemStack(ARROW));
        var getEliteArmor = getEliteArmor(serverLevel, 100);
        skeleton.setElite();

        effectWithChance(skeleton, HEALTH_BOOST, 5, 100);
        effectWithChance(skeleton, MobEffects.MOVEMENT_SPEED, 0, 100);
        effectWithChance(skeleton, DAMAGE_RESISTANCE, 3, 100);

        skeleton.setCustomName(Component.literal("Master Archer"));
        skeleton.setItemSlot(HEAD, getEliteArmor.getFirst());
        skeleton.setItemSlot(CHEST, getEliteArmor.get(1));
        skeleton.setItemSlot(LEGS, getEliteArmor.get(2));
        skeleton.setItemSlot(FEET, getEliteArmor.get(3));
        skeleton.setItemSlot(MAINHAND, getEliteArmor.get(4));
        return skeleton;
    }

    public static void summonEntities(AltarBlockEntity entity){
        if(!(entity.getLevel() instanceof ServerLevel level)) return;
        var actualEntity = buildMobs(level);
        var randomPoses = getInnerRingOfRadiusRandom(entity.getBlockPos().getCenter(), 12, 200)
            .stream()
            .filter(pos -> level.getBlockState(containing(pos)).isAir() && level.getBlockState(containing(pos).above()).isAir())
            .toList();

        for (var livingEntity : actualEntity) {
            addAndPositionEntity(level, containing(listRandom(randomPoses)), livingEntity);
            entity.spawnedMobs.add(livingEntity.getUUID());
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
            if (above.isAir() && main.isAir() && !below.isAir()) spawn.accept(blockPos);
        }
    }

    public static LivingEntity getReadyEternalWizard(ServerLevel serverLevel, double damage){
        var entity = new EternalWizard(serverLevel, null, damage, 100, 2, -1, 30);
        var wand = new ItemStack(ItemReg.WAND_ITEM_VITALITY.get());
        var helm = new ItemStack(ItemReg.MAGE_HELMET.get());
        var chestplate = new ItemStack(ItemReg.MAGE_CHESTPLATE.get());
        var leggings = new ItemStack(ItemReg.MAGE_LEGGINGS.get());
        var boots = new ItemStack(ItemReg.MAGE_BOOTS.get());

        entity.setPersistenceRequired();
        entity.setItemSlot(MAINHAND, wand);
        addProtection(serverLevel, helm, 1);
        addProtection(serverLevel, chestplate, 1);
        addProtection(serverLevel, leggings, 1);
        addProtection(serverLevel, boots, 1);
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

    private static List<LivingEntity> buildMobs(ServerLevel serverLevel) {
        var entities = new ArrayList<LivingEntity>();
        var getInstanceData = serverLevel.getData(AttachmentReg.INSTANCE_DATA);

        for (var i = 0; i < getInstanceData.getZombies(); i++){
            entities.add(generateMob(getReadyZombie(serverLevel), getInstanceData));
        }

        for (var i = 0; i < getInstanceData.getSkeleton(); i++){
            entities.add(generateMob(getReadySkeleton(serverLevel), getInstanceData));
        }

        for (var i = 0; i < getInstanceData.getEternalWizard(); i++){
            entities.add(generateMob( getReadyEternalWizard(serverLevel, 10), getInstanceData));
        }

        for (var i = 0; i < getInstanceData.getVoidSpiders(); i++){
            entities.add(generateMob(getVoidSpider(serverLevel), getInstanceData));
        }

        return entities;
    }
}
