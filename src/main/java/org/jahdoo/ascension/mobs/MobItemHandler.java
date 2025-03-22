package org.jahdoo.ascension.mobs;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jahdoo.common.registers.ItemReg;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.min;
import static net.minecraft.core.component.DataComponents.POTION_CONTENTS;
import static net.minecraft.core.component.DataComponents.TRIM;
import static net.minecraft.core.registries.Registries.*;
import static net.minecraft.world.item.armortrim.TrimMaterials.*;
import static net.minecraft.world.item.armortrim.TrimPatterns.*;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance;
import static net.minecraft.world.level.storage.loot.providers.number.UniformGenerator.between;
import static org.jahdoo.ascension.utils.Helpers.*;

public class MobItemHandler {

    private final float multiplier;
    private final String difficulty;
    private final HolderLookup.RegistryLookup<Enchantment> regLookup2;
    private final ArmorTrim armorTrimBasic;
    private final ArmorTrim armorTrimBasicSecondary;
    private final ArmorTrim armorTrimIntermediateSecondary;
    private final ArmorTrim armorTrimIntermediate;
    private final ArmorTrim armorTrimLegendarySecondary;
    private final ArmorTrim armorTrimLegendary;

    public MobItemHandler(ServerLevel serverLevel, float multiplier, String difficulty){
        var regLookup = serverLevel.registryAccess().lookup(TRIM_PATTERN).orElseThrow();
        var regLookup1 = serverLevel.registryAccess().lookup(TRIM_MATERIAL).orElseThrow();

        this.regLookup2 = serverLevel.registryAccess().lookupOrThrow(ENCHANTMENT);
        this.armorTrimBasic = new ArmorTrim(regLookup1.get(COPPER).orElseThrow(), regLookup.get(FLOW).orElseThrow());
        this.armorTrimBasicSecondary = new ArmorTrim(regLookup1.get(COPPER).orElseThrow(), regLookup.get(BOLT).orElseThrow());
        this.armorTrimIntermediate = new ArmorTrim(regLookup1.get(IRON).orElseThrow(), regLookup.get(EYE).orElseThrow());
        this.armorTrimIntermediateSecondary = new ArmorTrim(regLookup1.get(IRON).orElseThrow(), regLookup.get(SENTRY).orElseThrow());
        this.armorTrimLegendary = new ArmorTrim(regLookup1.get(AMETHYST).orElseThrow(), regLookup.get(DUNE).orElseThrow());
        this.armorTrimLegendarySecondary = new ArmorTrim(regLookup1.get(AMETHYST).orElseThrow(), regLookup.get(SENTRY).orElseThrow());
        this.multiplier = multiplier;
        this.difficulty = difficulty;
    }

    public LootTable getRandomWeapon(){
        return weaponFromDifficulty(regLookup2, multiplier, difficulty);
    }

    public LootTable getRandomIron(){
        return weaponFromDifficulty(armorTrimIntermediate, armorTrimIntermediateSecondary, regLookup2, multiplier, difficulty);
    }

    public LootTable getRandomLeather(){
        return buildForLeather(armorTrimBasic, armorTrimBasicSecondary, regLookup2, multiplier, difficulty);
    }

    public LootTable getRandomNetherite(){
        return buildForNetherite(armorTrimLegendary, armorTrimLegendarySecondary, regLookup2, multiplier, difficulty);
    }
    
    public static LootTable weaponFromDifficulty(HolderLookup.RegistryLookup<Enchantment> registryLookup, float multiplier, String difficulty) {
        var builder = LootTable.lootTable();
        builder.withPool(weaponWithChance(registryLookup, Items.BOW, multiplier, difficulty));

        switch (difficulty) {
            case EASY -> builder.withPool(weaponWithChance(registryLookup, Items.IRON_SWORD, multiplier, difficulty));
            case MEDIUM -> builder.withPool(weaponWithChance(registryLookup, Items.DIAMOND_SWORD, multiplier, difficulty));
            case HARD -> builder.withPool(weaponWithChance(registryLookup, ItemReg.ELEMENTAL_SWORD.get(), multiplier, difficulty));
        }

        return builder.build();
    }

    public static LootTable buildForDiamond(HolderLookup.RegistryLookup<Enchantment> registryLookup, float multiplier, String difficulty) {
        return LootTable.lootTable()
            .withPool(weaponWithChance(registryLookup, Items.DIAMOND_SWORD, multiplier, difficulty))
            .build();
    }

    public static LootTable buildForNetherite(HolderLookup.RegistryLookup<Enchantment> registryLookup, float multiplier, String difficulty) {
        return LootTable.lootTable()
            .withPool(weaponWithChance(registryLookup, Items.NETHERITE_SWORD, multiplier, difficulty))
            .build();
    }

    private static LootPool.Builder weaponWithChance(HolderLookup.RegistryLookup<Enchantment> registry, Item item, float multiplier, String difficulty) {
        var chance = 0.01F * multiplier;
        var min = min(0.01F * multiplier, 1.0F);
        return LootPool.lootPool()
            .setRolls(between(min, 1.0f))
            .when(randomChance(chance))
            .add(getEnchantement(registry, multiplier, item, difficulty).setWeight(1));
    }

    private static LootPool.Builder armorWithChance(
        ArmorTrim trimA,
        ArmorTrim armorTrim2,
        HolderLookup.RegistryLookup<Enchantment> registry,
        Item item,
        float multiplier,
        String difficulty
    ) {
        var chance = 0.01F * multiplier;
        var min = min(0.01F * multiplier, 1.0F);
        return LootPool.lootPool()
            .setRolls(between(min, 1.0f))
            .when(randomChance(chance))
            .add(getArmor(trimA, armorTrim2, registry, item, multiplier, difficulty).setWeight(1));
    }

    private static LootTable buildForLeather(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, float multiplier, String difficulty) {
        return LootTable.lootTable()
            .withPool(armorWithChance(trimA, trimB, registry, Items.LEATHER_HELMET, multiplier, difficulty))
            .withPool(armorWithChance(trimA, trimB, registry, Items.LEATHER_CHESTPLATE, multiplier, difficulty))
            .withPool(armorWithChance(trimA, trimB, registry, Items.LEATHER_LEGGINGS, multiplier, difficulty))
            .withPool(armorWithChance(trimA, trimB, registry, Items.LEATHER_BOOTS, multiplier, difficulty))
            .build();
    }


    private static LootTable weaponFromDifficulty(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, float multiplier, String difficulty) {
        return LootTable.lootTable()
            .withPool(armorWithChance(trimA, trimB, registry, Items.IRON_HELMET, multiplier, difficulty))
            .withPool(armorWithChance(trimA, trimB, registry, Items.IRON_CHESTPLATE, multiplier, difficulty))
            .withPool(armorWithChance(trimA, trimB, registry, Items.IRON_LEGGINGS, multiplier, difficulty))
            .withPool(armorWithChance(trimA, trimB, registry, Items.IRON_BOOTS, multiplier, difficulty))
            .build();
    }

    private static LootTable buildForNetherite(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, float multiplier, String difficulty) {
        return LootTable.lootTable()
            .withPool(armorWithChance(trimA, trimB, registry, Items.NETHERITE_HELMET, multiplier, difficulty))
            .withPool(armorWithChance(trimA, trimB, registry, Items.NETHERITE_CHESTPLATE, multiplier, difficulty))
            .withPool(armorWithChance(trimA, trimB, registry, Items.NETHERITE_LEGGINGS, multiplier, difficulty))
            .withPool(armorWithChance(trimA, trimB, registry, Items.NETHERITE_BOOTS, multiplier, difficulty))
            .build();
    }

    public static ItemStack getAllowedArrow(int round, String difficulty){
        ItemStack normalArrow;

        var acceptableArrows = new ArrayList<Holder<Potion>>();

        switch (difficulty){
            case EASY -> {
                if (round > 30) acceptableArrows.add(Potions.WEAKNESS);
                if (round > 50) acceptableArrows.add(Potions.POISON);
            }
            case MEDIUM -> {
                if (round > 20) acceptableArrows.add(Potions.WEAKNESS);
                if (round > 35) acceptableArrows.add(Potions.POISON);
                if (round > 50) acceptableArrows.add(Potions.SLOWNESS);
            }
            case HARD -> {
                acceptableArrows.add(Potions.WEAKNESS);
                acceptableArrows.add(Potions.POISON);
                if (round > 15) {
                    acceptableArrows.add(Potions.SLOWNESS);
                    acceptableArrows.add(Potions.HARMING);
                }
            }
        }

        if (round >= (difficulty.equals(EASY) ? 30 : difficulty.equals(MEDIUM) ? 10 : 0)) {
            if(!acceptableArrows.isEmpty()){
                normalArrow = new ItemStack(Items.TIPPED_ARROW);
                var getPotion = listRandom(acceptableArrows);
                normalArrow.set(POTION_CONTENTS, new PotionContents(getPotion));
                return normalArrow;
            }
        }
        
        normalArrow = new ItemStack(Items.ARROW);
        return normalArrow;
    }

    public LootTable getByDifficulty(String difficulty) {
        return switch (difficulty) {
            case MEDIUM -> getRandomIron();
            case HARD -> getRandomNetherite();
            default -> getRandomLeather();
        };
    }
    
    private static LootPoolSingletonContainer.Builder<?> getEnchantement(
        HolderLookup.RegistryLookup<Enchantment> registry,
        float multiplier,
        Item item,
        String difficulty
    ) {
        var chance = min(0.1F * multiplier, 1.0f);
        var builder = LootItem.lootTableItem(item);

        if(!Objects.equals(difficulty, EASY)){
            builder.apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(INFINITY), ConstantValue.exactly(0F))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(POWER), between(min(0.0F * multiplier, 5.0f), 5.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(UNBREAKING), between(min(0.0F * multiplier, 3.0f), 3.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(FLAME), ConstantValue.exactly(0F))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(FEATHER_FALLING), between(min(0.0F * multiplier, 4.0f), 4.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(PUNCH), between(min(0.0F * multiplier, 3.0f), 2.0f))
            ).apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(SHARPNESS), between(min(0.0F * multiplier, 5.0f), 5.0f))
            ).apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(KNOCKBACK), between(min(0.0F * multiplier, 2.0f), 2.0f))
            ).apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(FIRE_ASPECT), between(min(0.0F * multiplier, 2.0f), 2.0f))
            );
        }

        return builder;
    }

    private static LootPoolSingletonContainer.Builder<?> getArmor(
        ArmorTrim trimA,
        ArmorTrim trimB,
        HolderLookup.RegistryLookup<Enchantment> registry,
        Item armourPiece,
        float roundMultiplier,
        String difficulty
    ) {
        var chance = min(roundMultiplier/10, 1.0f);

        var pool = LootItem.lootTableItem(armourPiece)
            .apply(SetComponentsFunction.setComponent(TRIM, trimA).when(randomChance(chance)))
            .apply(SetComponentsFunction.setComponent(TRIM, trimB).when(randomChance(chance)));

        if(!Objects.equals(difficulty, EASY)){
            pool.apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(PROTECTION), between(min(0.0F * roundMultiplier, 4.0f), 4.0f))
                    .withEnchantment(registry.getOrThrow(PROJECTILE_PROTECTION), between(min(0.0F * roundMultiplier, 4.0f), 4.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(THORNS), between(min(0.0F * roundMultiplier, 4.0f), 4.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(UNBREAKING), between(min(0.0F * roundMultiplier, 4.0f), 3.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(THORNS), between(min(0.0F * roundMultiplier, 4.0f), 3.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(FEATHER_FALLING), between(min(0.0F * roundMultiplier, 4.0f), 4.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(randomChance(chance))
                    .withEnchantment(registry.getOrThrow(DEPTH_STRIDER), between(min(0.0F * roundMultiplier, 4.0f), 3.0f))
            );
        }

        return pool;
    }

}
