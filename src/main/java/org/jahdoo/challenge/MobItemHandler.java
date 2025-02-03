package org.jahdoo.challenge;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static org.jahdoo.utils.ModHelpers.Random;

public class MobItemHandler {

    private final HolderLookup.RegistryLookup<TrimPattern> regLookup;
    private final HolderLookup.RegistryLookup<TrimMaterial> regLookup1;
    private final HolderLookup.RegistryLookup<Enchantment> regLookup2;
    private final ArmorTrim armorTrimBasic;
    private final ArmorTrim armorTrimBasicSecondary;
    private final float multiplier;
    private final ArmorTrim armorTrimIntermediateSecondary;
    private final ArmorTrim armorTrimIntermediate;
    private final ArmorTrim armorTrimProSecondary;
    private final ArmorTrim armorTrimPro;
    private final ArmorTrim armorTrimLegendarySecondary;
    private final ArmorTrim armorTrimLegendary;

    public MobItemHandler(ServerLevel serverLevel, float multiplier){
        this.regLookup = serverLevel.registryAccess().lookup(Registries.TRIM_PATTERN).orElseThrow();
        this.regLookup1 = serverLevel.registryAccess().lookup(Registries.TRIM_MATERIAL).orElseThrow();
        this.regLookup2 = serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        this.armorTrimBasic = new ArmorTrim(regLookup1.get(TrimMaterials.COPPER).orElseThrow(), regLookup.get(TrimPatterns.FLOW).orElseThrow());
        this.armorTrimBasicSecondary = new ArmorTrim(regLookup1.get(TrimMaterials.COPPER).orElseThrow(), regLookup.get(TrimPatterns.BOLT).orElseThrow());
        this.armorTrimIntermediate = new ArmorTrim(regLookup1.get(TrimMaterials.IRON).orElseThrow(), regLookup.get(TrimPatterns.EYE).orElseThrow());
        this.armorTrimIntermediateSecondary = new ArmorTrim(regLookup1.get(TrimMaterials.IRON).orElseThrow(), regLookup.get(TrimPatterns.SENTRY).orElseThrow());
        this.armorTrimPro = new ArmorTrim(regLookup1.get(TrimMaterials.QUARTZ).orElseThrow(), regLookup.get(TrimPatterns.SPIRE).orElseThrow());
        this.armorTrimProSecondary = new ArmorTrim(regLookup1.get(TrimMaterials.QUARTZ).orElseThrow(), regLookup.get(TrimPatterns.VEX).orElseThrow());
        this.armorTrimLegendary = new ArmorTrim(regLookup1.get(TrimMaterials.AMETHYST).orElseThrow(), regLookup.get(TrimPatterns.DUNE).orElseThrow());
        this.armorTrimLegendarySecondary = new ArmorTrim(regLookup1.get(TrimMaterials.AMETHYST).orElseThrow(), regLookup.get(TrimPatterns.SENTRY).orElseThrow());
        this.multiplier = multiplier;
    }

    public static ItemStack getAllowedArrow(int round){
        ItemStack normalArrow;
        if(Random.nextInt(0, 30) == 0){
            if (round >= 30) {
                normalArrow = new ItemStack(Items.TIPPED_ARROW);
                var acceptableArrows = new ArrayList<Holder<Potion>>();
                if (round > 30) acceptableArrows.add(Potions.WEAKNESS);
                if (round > 45) acceptableArrows.add(Potions.POISON);
                if (round > 60) acceptableArrows.add(Potions.SLOWNESS);
                if (round > 75) acceptableArrows.add(Potions.WEAVING);
                if (round > 90) acceptableArrows.add(Potions.HARMING);
                if(!acceptableArrows.isEmpty()){
                    var getPotion = acceptableArrows.get(Random.nextInt(0, acceptableArrows.size()));
                    normalArrow.set(DataComponents.POTION_CONTENTS, new PotionContents(getPotion));
                }
                return normalArrow;
            }
        }
        normalArrow = new ItemStack(Items.ARROW);
        return normalArrow;
    }

    public LootTable getByRound(int round){
        var switchChance = Random.nextInt(2) == 0;
        if(round > 0 && round <= 10) return getRandomLeather();
        if(round > 10 && round <= 20) return switchChance ? getRandomLeather() : getRandomChain();
        if(round > 20 && round <= 30) return getRandomChain();
        if(round > 30 && round <= 40) return switchChance ? getRandomChain() : getRandomIron();
        if(round > 40 && round <= 50) return getRandomIron();
        if(round > 50 && round <= 60) return switchChance ? getRandomIron() : getRandomGold();
        if(round > 60 && round <= 70) return getRandomGold();
        if(round > 70 && round <= 80) return switchChance ? getRandomGold() : getRandomDiamond();
        if(round > 80 && round <= 90) return getRandomDiamond();
        return getRandomNetherite();
    }

    public LootTable getRandomWeapon(){
        return buildForIron(regLookup2, multiplier);
    }

    public LootTable getRandomLeather(){
        return buildForLeather(armorTrimBasic, armorTrimBasicSecondary, regLookup2, multiplier);
    }

    public LootTable getRandomChain(){
        return buildForChain(armorTrimBasic, armorTrimBasicSecondary, regLookup2, multiplier);
    }

    public LootTable getRandomIron(){
        return buildForIron(armorTrimIntermediate, armorTrimIntermediateSecondary, regLookup2, multiplier);
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

    private static LootTable buildForLeather(ArmorTrim armortrim, ArmorTrim armortrim1, HolderLookup.RegistryLookup<Enchantment> registrylookup2, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.LEATHER_HELMET, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.LEATHER_CHESTPLATE, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.LEATHER_LEGGINGS, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.LEATHER_BOOTS, multiplier))
            .build();
    }

    private static @NotNull LootTable buildForChain(ArmorTrim armortrim, ArmorTrim armortrim1, HolderLookup.RegistryLookup<Enchantment> registrylookup2, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.CHAINMAIL_HELMET, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.CHAINMAIL_CHESTPLATE, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.CHAINMAIL_LEGGINGS, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.CHAINMAIL_BOOTS, multiplier))
            .build();
    }

    private static @NotNull LootTable buildForIron(ArmorTrim armortrim, ArmorTrim armortrim1, HolderLookup.RegistryLookup<Enchantment> registrylookup2, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.IRON_HELMET, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.IRON_CHESTPLATE, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.IRON_LEGGINGS, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.IRON_BOOTS, multiplier))
            .build();
    }

    private static @NotNull LootTable buildForGold(ArmorTrim armortrim, ArmorTrim armortrim1, HolderLookup.RegistryLookup<Enchantment> registrylookup2, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.GOLDEN_HELMET, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.GOLDEN_CHESTPLATE, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.GOLDEN_LEGGINGS, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.GOLDEN_BOOTS, multiplier))
            .build();
    }

    private static @NotNull LootTable buildForDiamond(ArmorTrim armortrim, ArmorTrim armortrim1, HolderLookup.RegistryLookup<Enchantment> registrylookup2, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.DIAMOND_HELMET, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.DIAMOND_CHESTPLATE, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.DIAMOND_LEGGINGS, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.DIAMOND_BOOTS, multiplier))
            .build();
    }

    private static @NotNull LootTable buildForNetherite(ArmorTrim armortrim, ArmorTrim armortrim1, HolderLookup.RegistryLookup<Enchantment> registrylookup2, float multiplier) {
        return LootTable.lootTable()
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.NETHERITE_HELMET, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.NETHERITE_CHESTPLATE, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.NETHERITE_LEGGINGS, multiplier))
            .withPool(armorWithChance(armortrim, armortrim1, registrylookup2, Items.NETHERITE_BOOTS, multiplier))
            .build();
    }

    public static LootTable buildForIron(HolderLookup.RegistryLookup<Enchantment> registryLookup, float multiplier) {
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

    private static LootPool.@NotNull Builder armorWithChance(ArmorTrim armortrim, ArmorTrim armorTrim2, HolderLookup.RegistryLookup<Enchantment> registrylookup2, Item item, float multiplier) {
        var chance = 0.4F * multiplier;
        var min = Math.min(0.1F * multiplier, 1.0F);
        return LootPool.lootPool()
            .setRolls(UniformGenerator.between(min, 1.0f))
            .when(LootItemRandomChanceCondition.randomChance(chance))
            .add(getArmor(armortrim, armorTrim2, registrylookup2, item, multiplier).setWeight(1));
    }

    private static LootPool.@NotNull Builder weaponWithChance(HolderLookup.RegistryLookup<Enchantment> registryLookup, Item item, float multiplier) {
        var chance = 0.4F * multiplier;
        var min = Math.min(0.1F * multiplier, 1.0F);
        return LootPool.lootPool()
            .setRolls(UniformGenerator.between(min, 1.0f))
            .when(LootItemRandomChanceCondition.randomChance(chance))
            .add(getEnchantement(registryLookup, multiplier, item).setWeight(1));
    }

    private static LootPoolSingletonContainer.@NotNull Builder<?> getEnchantement(HolderLookup.RegistryLookup<Enchantment> registrylookup2, float roundMultiplier, Item item) {
        var chance = Math.min(0.1F * roundMultiplier, 1.0f);

        return LootItem.lootTableItem(item)
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.INFINITY), ConstantValue.exactly(0F))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.POWER), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 5.0f), 5.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 3.0f), 3.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.FLAME), ConstantValue.exactly(0F))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.FEATHER_FALLING), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 4.0f), 4.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.PUNCH), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 3.0f), 2.0f))
            ).apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.SHARPNESS), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 5.0f), 5.0f))
            ).apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.KNOCKBACK), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 2.0f), 2.0f))
            ).apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.FIRE_ASPECT), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 2.0f), 2.0f))
            );
    }

    private static LootPoolSingletonContainer.@NotNull Builder<?> getArmor(ArmorTrim armortrim, ArmorTrim armortrim1, HolderLookup.RegistryLookup<Enchantment> registrylookup2, Item armourPiece, float roundMultiplier) {
        var chance = Math.min(0.1F * roundMultiplier, 1.0f);

        return LootItem.lootTableItem(armourPiece)
            .apply(
                SetComponentsFunction.setComponent(DataComponents.TRIM, armortrim)
                    .when(LootItemRandomChanceCondition.randomChance(chance))
            )
            .apply(
                SetComponentsFunction.setComponent(DataComponents.TRIM, armortrim1)
                    .when(LootItemRandomChanceCondition.randomChance(chance))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.PROTECTION), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 4.0f), 4.0f))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.PROJECTILE_PROTECTION), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 4.0f), 4.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.THORNS), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 4.0f), 4.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 4.0f), 3.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.THORNS), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 4.0f), 3.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.FEATHER_FALLING), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 4.0f), 4.0f))
            )
            .apply(
                new SetEnchantmentsFunction.Builder()
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .withEnchantment(registrylookup2.getOrThrow(Enchantments.DEPTH_STRIDER), UniformGenerator.between(Math.min(0.0F * roundMultiplier, 4.0f), 3.0f))
            );
    }

}
