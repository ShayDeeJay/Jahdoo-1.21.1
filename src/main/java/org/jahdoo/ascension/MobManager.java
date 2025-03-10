package org.jahdoo.ascension;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.entities.CustomSkeleton;
import org.jahdoo.common.entities.CustomZombie;
import org.jahdoo.common.entities.ancient_golem.AncientGolem;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.void_spider.VoidSpider;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static net.minecraft.world.effect.MobEffects.*;
import static net.minecraft.world.entity.EquipmentSlot.*;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.VAULT;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY;
import static org.jahdoo.ascension.LevelStageModifiers.attributeWithChance;
import static org.jahdoo.ascension.LevelStageModifiers.effectWithChance;
import static org.jahdoo.ascension.utils.EnchantmentHelpers.enchant;
import static org.jahdoo.ascension.utils.Helpers.*;
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

        enchant(stack, serverLevel.registryAccess(), Enchantments.PROTECTION, level);
    }

    private static LivingEntity getAncienGolem(ServerLevel serverLevel, int round) {
        var damage = Maths.getPercentageTotal(round, 12);

        return new AncientGolem(serverLevel, null, damage, 100, 1, INFINITE_LIFE, 20);
    }

    public static LivingEntity generateMob(LivingEntity livingEntity){
        var getEntity = livingEntity.level().getNearestPlayer(livingEntity, 200);

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

    public static LivingEntity getReadyZombie(ServerLevel serverLevel, int round){
        var entity = new CustomZombie(serverLevel, null);
        entity.setPersistenceRequired();
        attachEquipment(entity, serverLevel, round);

        var collection = equipWeapon(entity, serverLevel, round);
        if(!collection.isEmpty()) {
            var weapon = Helpers.listRandom(collection);
            entity.setItemSlot(MAINHAND, weapon.is(Items.BOW) ? ItemStack.EMPTY : weapon);
        };

        return entity;
    }

    public static LivingEntity getReadySkeleton(ServerLevel serverLevel, int round){
        var arrow = MobItemHandler.getAllowedArrow(round);
        var entity = new CustomSkeleton(serverLevel, null, arrow);

        entity.setPersistenceRequired();
        attachEquipment(entity, serverLevel, round);
        var collection = equipWeapon(entity, serverLevel, round);

        if(!collection.isEmpty()){
            var weapon = Helpers.listRandom(collection);
            entity.setItemSlot(MAINHAND, weapon);
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

    public static void addAndPositionEntity(AltarBlockEntity entity, BlockPos pos){
        if(entity.getLevel() instanceof ServerLevel serverLevel){
            var actualEntity = getSelectedMethod(entity, serverLevel);
            var level = actualEntity.level();

            setOuterRingPulses(level, pos.getCenter(), actualEntity.getBbWidth());
            getSoundWithPosition(level, pos, SoundEvents.WITHER_SPAWN, 0.05f, 3f);
            getSoundWithPosition(level, pos, SoundReg.HEAL.get(), 1f, 2f);
            actualEntity.moveTo(pos.getCenter().subtract(0,0.5,0));
            entity.spawnedMobs.add(actualEntity.getUUID());
            serverLevel.addFreshEntity(actualEntity);
        }
    }

    public static LivingEntity getEliteSkeleton(ServerLevel serverLevel, int round){
        var skeleton = new CustomSkeleton(serverLevel, null, new ItemStack(Items.ARROW));
        skeleton.setElite();
        var getEliteArmor = getEliteArmor(serverLevel, round);

        effectWithChance(skeleton, HEALTH_BOOST, 5, 100);
        effectWithChance(skeleton, MOVEMENT_SPEED, 0, 100);
        effectWithChance(skeleton, DAMAGE_RESISTANCE, 3, 100);

        skeleton.setCustomName(Component.literal("Master Archer"));

        skeleton.setItemSlot(HEAD, getEliteArmor.getFirst());
        skeleton.setItemSlot(CHEST, getEliteArmor.get(1));
        skeleton.setItemSlot(LEGS, getEliteArmor.get(2));
        skeleton.setItemSlot(FEET, getEliteArmor.get(3));
        skeleton.setItemSlot(MAINHAND, getEliteArmor.get(4));
        return skeleton;
    }

    public static void summonEntities(AltarBlockEntity entity, int maxSpawn){
        var pos = entity.getBlockPos();
        var counter = new AtomicInteger();
        var points = 200;

        spawnAroundEntity(entity.getLevel(), pos, 9, points,
            blockPos -> {
                if(counter.get() < maxSpawn){
                    System.out.println(counter.get());
                    MobManager.addAndPositionEntity(entity, blockPos.above());
                    counter.getAndIncrement();
                }
            }
        );
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

    public static LivingEntity getReadyEternalWizard(ServerLevel serverLevel, int round){
        var entity = new EternalWizard(serverLevel, null, Maths.getPercentageTotal(round, 12), 100, 2, -1, 30);
        var wand = new ItemStack(ItemReg.WAND_ITEM_VITALITY.get());
        entity.setPersistenceRequired();
        entity.setItemSlot(MAINHAND, wand);
        var helm = new ItemStack(ItemReg.MAGE_HELMET.get());
        var chestplate = new ItemStack(ItemReg.MAGE_CHESTPLATE.get());
        var leggings = new ItemStack(ItemReg.MAGE_LEGGINGS.get());
        var boots = new ItemStack(ItemReg.MAGE_BOOTS.get());

        addProtection(serverLevel, helm, round);
        addProtection(serverLevel, chestplate, round);
        addProtection(serverLevel, leggings, round);
        addProtection(serverLevel, boots, round);
        entity.setItemSlot(HEAD, helm);
        entity.setItemSlot(CHEST, chestplate);
        entity.setItemSlot(LEGS, leggings);
        entity.setItemSlot(FEET, boots);
        return entity;
    }

    public static List<ItemStack> getEliteArmor(ServerLevel serverLevel, int round){
        var regLookup = serverLevel.registryAccess().lookup(Registries.TRIM_PATTERN).orElseThrow();
        var regLookup1 = serverLevel.registryAccess().lookup(Registries.TRIM_MATERIAL).orElseThrow();
        var value = new ArmorTrim(regLookup1.get(TrimMaterials.REDSTONE).orElseThrow(), regLookup.get(TrimPatterns.RIB).orElseThrow());
        var value1 = new ArmorTrim(regLookup1.get(TrimMaterials.GOLD).orElseThrow(), regLookup.get(TrimPatterns.SILENCE).orElseThrow());
        var value2 = new ArmorTrim(regLookup1.get(TrimMaterials.GOLD).orElseThrow(), regLookup.get(TrimPatterns.RIB).orElseThrow());
        var value3 = new ArmorTrim(regLookup1.get(TrimMaterials.GOLD).orElseThrow(), regLookup.get(TrimPatterns.RIB).orElseThrow());

        var stack = new ItemStack(Items.NETHERITE_HELMET);
        var stack1 = new ItemStack(Items.NETHERITE_CHESTPLATE);
        var stack2 = new ItemStack(Items.NETHERITE_LEGGINGS);
        var stack3 = new ItemStack(Items.NETHERITE_BOOTS);
        var bow = new ItemStack(Items.BOW);

        addProtection(serverLevel, stack, round);
        addProtection(serverLevel, stack1, round);
        addProtection(serverLevel, stack2, round);
        addProtection(serverLevel, stack3, round);
        enchant(stack3, serverLevel.registryAccess(), Enchantments.FEATHER_FALLING, 4);
        enchant(bow, serverLevel.registryAccess(), Enchantments.POWER, calculateEnchantmentLevel(round));

        stack.set(DataComponents.TRIM, value);
        stack1.set(DataComponents.TRIM, value1);
        stack2.set(DataComponents.TRIM, value2);
        stack.set(DataComponents.TRIM, value3);

        return List.of(stack, stack1, stack2, stack3, bow);
    }

    private static LivingEntity getSelectedMethod(AltarBlockEntity entity, ServerLevel serverLevel) {
        var entities = new ArrayList<LivingEntity>();
        var round = ChallengeLevelData.getRound(entity);
        var readyZombie = getReadyZombie(serverLevel, round);
        var readySkeleton = getReadySkeleton(serverLevel, round);
        var readyWizard = getReadyEternalWizard(serverLevel, round);
        var readyEliteSkeleton = getEliteSkeleton(serverLevel, round);
        var golem = getAncienGolem(serverLevel, round);
        var voidSpider = getVoidSpider(serverLevel);
        entities.add(readyZombie);

        if(round > 5) {
            entities.add(readySkeleton);
            if(Maths.percentageChance(40)) entities.add(readyWizard);
        }

        if(round > 10) {
            if(Maths.percentageChance(40)) entities.add(voidSpider);
        }

        if(round > 20 && Maths.percentageChance(10)) {
            entities.add(readyEliteSkeleton);
        }

        if(round > 40) {
            if(Maths.percentageChance(40)) entities.add(golem);
        }

        var livingEntity = MobManager.generateMob(Helpers.listRandom(entities));
        attributeWithChance(Attributes.MAX_HEALTH, livingEntity, round, 100);
        attributeWithChance(Attributes.ARMOR, livingEntity, round * 10, 100);
        attributeWithChance(Attributes.ATTACK_DAMAGE, livingEntity, round, 100);
        attributeWithChance(Attributes.MOVEMENT_SPEED, livingEntity, round, 20);
        return livingEntity;
    }
}
