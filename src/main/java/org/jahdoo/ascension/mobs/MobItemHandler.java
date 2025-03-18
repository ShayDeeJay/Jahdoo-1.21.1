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

import java.util.ArrayList;

import static java.lang.Math.min;
import static net.minecraft.core.component.DataComponents.POTION_CONTENTS;
import static net.minecraft.core.component.DataComponents.TRIM;
import static net.minecraft.core.registries.Registries.*;
import static net.minecraft.world.item.armortrim.TrimMaterials.*;
import static net.minecraft.world.item.armortrim.TrimPatterns.*;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance;
import static net.minecraft.world.level.storage.loot.providers.number.UniformGenerator.between;
import static org.jahdoo.ascension.utils.Helpers.Random;

public class MobItemHandler {

    private final float multiplier;
    private final HolderLookup.RegistryLookup<Enchantment> regLookup2;
    private final ArmorTrim armorTrimBasic;
    private final ArmorTrim armorTrimBasicSecondary;
    private final ArmorTrim armorTrimIntermediateSecondary;
    private final ArmorTrim armorTrimIntermediate;
    private final ArmorTrim armorTrimProSecondary;
    private final ArmorTrim armorTrimPro;
    private final ArmorTrim armorTrimLegendarySecondary;
    private final ArmorTrim armorTrimLegendary;

    public MobItemHandler(ServerLevel serverLevel, float multiplier){
        var regLookup = serverLevel.registryAccess().lookup(TRIM_PATTERN).orElseThrow();
        var regLookup1 = serverLevel.registryAccess().lookup(TRIM_MATERIAL).orElseThrow();

        this.regLookup2 = serverLevel.registryAccess().lookupOrThrow(ENCHANTMENT);
        this.armorTrimBasic = new ArmorTrim(regLookup1.get(COPPER).orElseThrow(), regLookup.get(FLOW).orElseThrow());
        this.armorTrimBasicSecondary = new ArmorTrim(regLookup1.get(COPPER).orElseThrow(), regLookup.get(BOLT).orElseThrow());
        this.armorTrimIntermediate = new ArmorTrim(regLookup1.get(IRON).orElseThrow(), regLookup.get(EYE).orElseThrow());
        this.armorTrimIntermediateSecondary = new ArmorTrim(regLookup1.get(IRON).orElseThrow(), regLookup.get(SENTRY).orElseThrow());
        this.armorTrimPro = new ArmorTrim(regLookup1.get(QUARTZ).orElseThrow(), regLookup.get(SPIRE).orElseThrow());
        this.armorTrimProSecondary = new ArmorTrim(regLookup1.get(QUARTZ).orElseThrow(), regLookup.get(VEX).orElseThrow());
        this.armorTrimLegendary = new ArmorTrim(regLookup1.get(AMETHYST).orElseThrow(), regLookup.get(DUNE).orElseThrow());
        this.armorTrimLegendarySecondary = new ArmorTrim(regLookup1.get(AMETHYST).orElseThrow(), regLookup.get(SENTRY).orElseThrow());
        this.multiplier = multiplier;
    }

    public LootTable getRandomWeapon(){
        return ironWeapons(regLookup2, multiplier);
    }

    public LootTable getRandomLeather(){
        return buildForLeather(armorTrimBasic, armorTrimBasicSecondary, regLookup2, multiplier);
    }

    public LootTable getRandomChain(){
        return buildForChain(armorTrimBasic, armorTrimBasicSecondary, regLookup2, multiplier);
    }

    public LootTable getRandomIron(){
        return ironWeapons(armorTrimIntermediate, armorTrimIntermediateSecondary, regLookup2, multiplier);
    }

    public LootTable getRandomGold(){
        return buildForGold(armorTrimIntermediate, armorTrimIntermediateSecondary, regLookup2, multiplier);
    }

    public LootTable getRandomDiamond(){
        return buildForDiamond(armorTrimPro, armorTrimProSecondary, regLookup2, multiplier);
    }

    public LootTable getRandomNetherite(){
        return buildForNetherite(armorTrimLegendary, armorTrimLegendarySecondary, regLookup2, multiplier);
    }
    
    public static LootTable ironWeapons(HolderLookup.RegistryLookup<Enchantment> registryLookup, float multiplier) {
        return LootTable.lootTable()
            .withPool(weaponWithChance(registryLookup, Items.IRON_SWORD, multiplier))
            .withPool(weaponWithChance(registryLookup, Items.BOW, multiplier))
            .build();
    }

    public static LootTable buildForDiamond(HolderLookup.RegistryLookup<Enchantment> registryLookup, float multiplier) {
        return LootTable.lootTable()
            .withPool(weaponWithChance(registryLookup, Items.DIAMOND_SWORD, multiplier))
            .build();
    }

    public static LootTable buildForNetherite(HolderLookup.RegistryLookup<Enchantment> registryLookup, float multiplier) {
        return LootTable.lootTable()
            .withPool(weaponWithChance(registryLookup, Items.NETHERITE_SWORD, multiplier))
            .build();
    }

    private static LootPool.Builder weaponWithChance(HolderLookup.RegistryLookup<Enchantment> registry, Item item, float multiplier) {
        var chance = 0.4F * multiplier;
        var min = min(0.1F * multiplier, 1.0F);
        return LootPool.lootPool()
            .setRolls(between(min, 1.0f))
            .when(randomChance(chance))
            .add(getEnchantement(registry, multiplier, item).setWeight(1));
    }

    private static LootPool.Builder armorWithChance(ArmorTrim trimA, ArmorTrim armorTrim2, HolderLookup.RegistryLookup<Enchantment> registry, Item item, float multiplier) {
        var chance = 0.4F * multiplier;
        var min = min(0.1F * multiplier, 1.0F);
        return LootPool.lootPool()
            .setRolls(between(min, 1.0f))
            .when(randomChance(chance))
            .add(getArmor(trimA, armorTrim2, registry, item, multiplier).setWeight(1));
    }

    private static LootTable buildForLeather(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(trimA, trimB, registry, Items.LEATHER_HELMET, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.LEATHER_CHESTPLATE, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.LEATHER_LEGGINGS, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.LEATHER_BOOTS, multiplier))
            .build();
    }

    private static LootTable buildForChain(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(trimA, trimB, registry, Items.CHAINMAIL_HELMET, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.CHAINMAIL_CHESTPLATE, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.CHAINMAIL_LEGGINGS, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.CHAINMAIL_BOOTS, multiplier))
            .build();
    }

    private static LootTable ironWeapons(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(trimA, trimB, registry, Items.IRON_HELMET, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.IRON_CHESTPLATE, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.IRON_LEGGINGS, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.IRON_BOOTS, multiplier))
            .build();
    }

    private static LootTable buildForGold(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(trimA, trimB, registry, Items.GOLDEN_HELMET, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.GOLDEN_CHESTPLATE, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.GOLDEN_LEGGINGS, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.GOLDEN_BOOTS, multiplier))
            .build();
    }

    private static LootTable buildForDiamond(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(trimA, trimB, registry, Items.DIAMOND_HELMET, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.DIAMOND_CHESTPLATE, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.DIAMOND_LEGGINGS, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.DIAMOND_BOOTS, multiplier))
            .build();
    }

    private static LootTable buildForNetherite(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(trimA, trimB, registry, Items.NETHERITE_HELMET, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.NETHERITE_CHESTPLATE, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.NETHERITE_LEGGINGS, multiplier))
            .withPool(armorWithChance(trimA, trimB, registry, Items.NETHERITE_BOOTS, multiplier))
            .build();
    }

    public static ItemStack getAllowedArrow(int round){
        ItemStack normalArrow;
        
        if(Random.nextInt(0, 30) == 0){
            if (round >= 30) {
                var acceptableArrows = new ArrayList<Holder<Potion>>();
                if (round > 30) acceptableArrows.add(Potions.WEAKNESS);
                if (round > 45) acceptableArrows.add(Potions.POISON);
                if (round > 60) acceptableArrows.add(Potions.SLOWNESS);
                if (round > 75) acceptableArrows.add(Potions.WEAVING);
                if (round > 90) acceptableArrows.add(Potions.HARMING);
                if(!acceptableArrows.isEmpty()){
                    normalArrow = new ItemStack(Items.TIPPED_ARROW);
                    var getPotion = acceptableArrows.get(Random.nextInt(0, acceptableArrows.size()));
                    normalArrow.set(POTION_CONTENTS, new PotionContents(getPotion));
                    return normalArrow;
                }
            }
        }
        
        normalArrow = new ItemStack(Items.ARROW);
        return normalArrow;
    }

    public LootTable getByRound(int round) {
        var switchChance = Random.nextInt(2) == 0;
        int range = (round - 1) / 10; // Determine the range (0-9, 10-19, etc.)

        return switch (range) {
            case 0 -> getRandomLeather(); // 1-10
            case 1 -> switchChance ? getRandomLeather() : getRandomChain(); // 11-20
            case 2 -> getRandomChain();// 21-30
            case 3 -> switchChance ? getRandomChain() : getRandomIron(); // 31-40
            case 4 -> getRandomIron(); // 41-50
            case 5 -> switchChance ? getRandomIron() : getRandomGold(); // 51-60
            case 6 -> getRandomGold(); // 61-70
            case 7 -> switchChance ? getRandomGold() : getRandomDiamond(); // 71-80
            case 8 -> getRandomDiamond(); // 81-90
            default -> getRandomNetherite(); // 91 and above
        };
    }
    
    private static LootPoolSingletonContainer.Builder<?> getEnchantement(HolderLookup.RegistryLookup<Enchantment> registry, float multiplier, Item item) {
        var chance = min(0.1F * multiplier, 1.0f);

        return LootItem.lootTableItem(item)
            .apply(
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

    private static LootPoolSingletonContainer.Builder<?> getArmor(ArmorTrim trimA, ArmorTrim trimB, HolderLookup.RegistryLookup<Enchantment> registry, Item armourPiece, float roundMultiplier) {
        var chance = min(0.1F * roundMultiplier, 1.0f);

        return LootItem.lootTableItem(armourPiece)
            .apply(SetComponentsFunction.setComponent(TRIM, trimA).when(randomChance(chance)))
            .apply(SetComponentsFunction.setComponent(TRIM, trimB).when(randomChance(chance)))
            .apply(
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

}
