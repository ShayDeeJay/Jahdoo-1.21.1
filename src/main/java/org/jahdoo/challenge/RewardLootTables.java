package org.jahdoo.challenge;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;

import static org.jahdoo.utils.ModHelpers.Random;

public class RewardLootTables {
    public static final LootPoolSingletonContainer.Builder<?> BOOK_BUILDER = LootItem.lootTableItem(Items.BOOK);
    public static final LootPoolSingletonContainer.Builder<?> NEXITE_POWDER_BUILDER = LootItem.lootTableItem(ItemsRegister.NEXITE_POWDER.get());
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
    public static final LootPoolSingletonContainer.Builder<?> ELYTRA_BUILDER = LootItem.lootTableItem(Items.ELYTRA);

    public static ObjectArrayList<ItemStack> getCompletionLoot(ServerLevel serverLevel, Vec3 pos, int level) {
        var randomWand = ElementRegistry.getRandomElement().getWand();
        var wand = randomWand != null ? randomWand : ItemsRegister.WAND_ITEM_FROST.get();

        var WAND_BUILDER = LootItem.lootTableItem(wand);
        var functionBuilder = EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess());

        var add3 = LootPool.lootPool()
            .setRolls(UniformGenerator.between(5.0F, 13.0F))
            .add(NEXITE_POWDER_BUILDER.setWeight(30))
            .add(EMERALD_BUILDER.setWeight(20))
            .add(DIAMOND_BUILDER.setWeight(10));
        var loot = LootTable.lootTable().withPool(add3);

        var add2 = LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1.0F))
            .add(IRON_SWORD_BUILDER.setWeight(20))
            .add(DIAMOND_SWORD_BUILDER.setWeight(10))
            .add(NETHERITE_SWORD_BUILDER.setWeight(4))
            .add(WAND_BUILDER.setWeight(2));
        loot.withPool(add2);

        var add1 = LootPool.lootPool()
            .setRolls(UniformGenerator.between(1.0F, 3.0F))
            .add(NEXITE_BLOCK_BUILDER.setWeight(20))
            .add(AUGMENT_CORE_BUILDER.setWeight(15))
            .add(BOOK_BUILDER.setWeight(5).apply(functionBuilder));
        loot.withPool(add1);

        if (Random.nextInt(Math.max(1, 10 - level)) == 0) {
            var add = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(ELYTRA_BUILDER.setWeight(2))
                .add(TOME_OF_UNITY_BUILDER.setWeight(12))
                .add(AUGMENT_ITEM_BUILDER.setWeight(20))
                .add(ADVANCED_AUGMENT_CORE_BUILDER.setWeight(10));
            loot.withPool(add);
        }

        if (Random.nextInt(Math.max(1, 100 - level)) == 0) {
            var add = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(WIZARD_HELM_BUILDER.setWeight(1))
                .add(WIZARD_CHEST_BUILDER.setWeight(1))
                .add(WIZARD_LEGGINGS_BUILDER.setWeight(1))
                .add(WIZARD_BOOTS_BUILDER.setWeight(1))
                .add(AUGMENT_HYPER_CORE_BUILDER.setWeight(10));
            loot.withPool(add);
        }

        var lootParams = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, pos)
            .create(LootContextParamSets.VAULT);
        return loot.build().getRandomItems(lootParams);
    }

}
