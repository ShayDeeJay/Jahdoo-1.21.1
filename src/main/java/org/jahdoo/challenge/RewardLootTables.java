package org.jahdoo.challenge;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.TomeOfUnity;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jetbrains.annotations.NotNull;
import org.shaydee.loot_beams_neoforge.data_component.DataComponentsReg;

import static org.jahdoo.utils.ModHelpers.Random;

public class RewardLootTables {
    public static final LootPoolSingletonContainer.Builder<?> BOOK_BUILDER = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
    public static final LootPoolSingletonContainer.Builder<?> GOLD_BUILDER = LootItem.lootTableItem(Items.GOLD_INGOT);
    public static final LootPoolSingletonContainer.Builder<?> NETHERITE_BUILDER = LootItem.lootTableItem(Items.NETHERITE_SCRAP);
    public static final LootPoolSingletonContainer.Builder<?> IRON_BUILDER = LootItem.lootTableItem(Items.IRON_INGOT);
    public static final LootPoolSingletonContainer.Builder<?> SHULKER_SHELLS_BUILDER = LootItem.lootTableItem(Items.SHULKER_SHELL);
    public static final LootPoolSingletonContainer.Builder<?> ENCHANTED_BOTTLES_BUILDER = LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE);
    public static final LootPoolSingletonContainer.Builder<?> GOLDEN_CARROT_BUILDER = LootItem.lootTableItem(Items.GOLDEN_CARROT);
    public static final LootPoolSingletonContainer.Builder<?> EMERALD_BUILDER = LootItem.lootTableItem(Items.EMERALD);
    public static final LootPoolSingletonContainer.Builder<?> DIAMOND_BUILDER = LootItem.lootTableItem(Items.DIAMOND);
    public static final LootPoolSingletonContainer.Builder<?> IRON_SWORD_BUILDER = LootItem.lootTableItem(Items.IRON_SWORD);
    public static final LootPoolSingletonContainer.Builder<?> DIAMOND_SWORD_BUILDER = LootItem.lootTableItem(Items.DIAMOND_SWORD);
    public static final LootPoolSingletonContainer.Builder<?> NEXITE_BLOCK_BUILDER = LootItem.lootTableItem(BlocksRegister.NEXITE_BLOCK.get());
    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_CORE_BUILDER = LootItem.lootTableItem(ItemsRegister.AUGMENT_CORE.get());
    public static final LootPoolSingletonContainer.Builder<?> NETHERITE_SWORD_BUILDER = LootItem.lootTableItem(Items.NETHERITE_SWORD);
    public static final LootPoolSingletonContainer.Builder<?> ADVANCED_AUGMENT_CORE_BUILDER = LootItem.lootTableItem(ItemsRegister.ADVANCED_AUGMENT_CORE.get());
    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_ITEM_BUILDER = LootItem.lootTableItem(ItemsRegister.AUGMENT_ITEM.get());
    public static final LootPoolSingletonContainer.Builder<?> TOME_OF_UNITY_BUILDER = LootItem.lootTableItem(ItemsRegister.TOME_OF_UNITY.get());
    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_HYPER_CORE_BUILDER = LootItem.lootTableItem(ItemsRegister.AUGMENT_HYPER_CORE.get());
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_HELM_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_HELMET.get());
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_CHEST_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_CHESTPLATE.get());
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_LEGGINGS_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_LEGGINGS.get());
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_BOOTS_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_BOOTS.get());
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_HELM_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_HELMET.get());
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_CHEASTPLATE_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_CHESTPLATE.get());
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_LEGGINGS_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_LEGGINGS.get());
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_BOOTS_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_BOOTS.get());
    public static final LootPoolSingletonContainer.Builder<?> ELYTRA_BUILDER = LootItem.lootTableItem(Items.ELYTRA);

    public static ObjectArrayList<ItemStack> getCompletionLoot(ServerLevel serverLevel, Vec3 pos, int level) {
        var randomWand = ElementRegistry.getRandomElement().getWand();
        var wand = randomWand != null ? randomWand : ItemsRegister.WAND_ITEM_FROST.get();

        var wand_builder = LootItem.lootTableItem(wand);
        var loot = LootTable.lootTable().withPool(commonPool());

        loot.withPool(rareWeaponPool(serverLevel, wand_builder));
        loot.withPool( rarePool(serverLevel));

        if (Random.nextInt(Math.max(1, 10 - level)) == 0) loot.withPool(epicPool());
        if (Random.nextInt(Math.max(1, 100 - level)) == 0) loot.withPool(legendaryPool());

        var param = LootContextParams.ORIGIN;
        var vault = LootContextParamSets.VAULT;
        var lootParams = new LootParams.Builder(serverLevel).withParameter(param, pos).create(vault);
        return loot.build().getRandomItems(lootParams);
    }

    private static LootPool.@NotNull Builder legendaryPool() {
        return LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1.0F))
            .add(AUGMENT_HYPER_CORE_BUILDER.setWeight(10))
            .add(WIZARD_HELM_BUILDER.setWeight(1))
            .add(WIZARD_CHEST_BUILDER.setWeight(1))
            .add(WIZARD_LEGGINGS_BUILDER.setWeight(1))
            .add(WIZARD_BOOTS_BUILDER.setWeight(1));
    }

    private static LootPool.@NotNull Builder epicPool() {
        return LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1.0F))
            .add(ADVANCED_AUGMENT_CORE_BUILDER.setWeight(10))
            .add(TOME_OF_UNITY_BUILDER.setWeight(8))
            .add(ELYTRA_BUILDER.setWeight(5))
            .add(BATTLEMAGE_HELM_BUILDER.setWeight(1))
            .add(BATTLEMAGE_CHEASTPLATE_BUILDER.setWeight(1))
            .add(BATTLEMAGE_LEGGINGS_BUILDER.setWeight(1))
            .add(BATTLEMAGE_BOOTS_BUILDER.setWeight(1));
    }

    private static LootPool.@NotNull Builder rarePool(ServerLevel serverLevel) {
        return LootPool.lootPool()
            .setRolls(UniformGenerator.between(1.0F, 3.0F))
            .add(NEXITE_BLOCK_BUILDER.setWeight(20))
            .add(AUGMENT_CORE_BUILDER.setWeight(15))
            .add(BOOK_BUILDER.setWeight(5)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()));
    }

    private static LootPool.@NotNull Builder rareWeaponPool(ServerLevel serverLevel, LootPoolSingletonContainer.Builder<? extends LootPoolSingletonContainer.Builder<?>> WAND_BUILDER) {
        return LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1.0F))
            .add(IRON_SWORD_BUILDER.setWeight(20)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
            .add(DIAMOND_SWORD_BUILDER.setWeight(10)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
            .add(AUGMENT_ITEM_BUILDER.setWeight(5))
            .add(NETHERITE_SWORD_BUILDER.setWeight(3)).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess()))
            .add(WAND_BUILDER.setWeight(2));
    }

    private static LootPool.@NotNull Builder commonPool() {
        return LootPool.lootPool()
            .setRolls(UniformGenerator.between(2.0F, 5.0F))
            .add(GOLDEN_CARROT_BUILDER.setWeight(50))
            .add(IRON_BUILDER.setWeight(35))
            .add(GOLD_BUILDER.setWeight(25))
            .add(EMERALD_BUILDER.setWeight(20))
            .add(DIAMOND_BUILDER.setWeight(10))
            .add(ENCHANTED_BOTTLES_BUILDER.setWeight(8))
            .add(SHULKER_SHELLS_BUILDER.setWeight(5))
            .add(NETHERITE_BUILDER.setWeight(2));
    }

    public static void attachItemData(ServerLevel serverLevel, Item item, JahdooRarity rarity, ItemStack itemStack) {
        switch (item){
            case WandItem ignored -> JahdooRarity.setGeneratedWand(rarity, itemStack);
            case TomeOfUnity ignored -> JahdooRarity.createTomeAttributes(rarity, itemStack);
            case Augment ignored -> JahdooRarity.setGeneratedAugment(itemStack);
            case ArmorItem ignored -> {}
            case SwordItem ignored -> {
                if((itemStack.is(Items.DIAMOND_SWORD) || itemStack.is(Items.NETHERITE_SWORD)) && Random.nextInt(20) == 0){
                    EnchantmentHelpers.enchant(itemStack, serverLevel.registryAccess(), Enchantments.SHARPNESS, 10);
                    EnchantmentHelpers.enchant(itemStack, serverLevel.registryAccess(), Enchantments.SWEEPING_EDGE, 10);
                    EnchantmentHelpers.enchant(itemStack, serverLevel.registryAccess(), Enchantments.LOOTING, 10);
                    itemStack.set(DataComponentsReg.INSTANCE.getLOOT_BEAM_DATA(), LocalLootBeamData.SPECIALLY_ENCHANTED_BOOK);
                }
            }
            default -> { /*IGNORE*/ }
        }
    }

    public static void armorItemEnchanter(){

    }
}
