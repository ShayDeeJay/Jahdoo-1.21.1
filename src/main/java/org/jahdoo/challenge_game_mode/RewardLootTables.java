package org.jahdoo.challenge_game_mode;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;

public class RewardLootTables {

    public static ObjectArrayList<ItemStack> getCompletionLoot(ServerLevel serverLevel, Vec3 pos){
        var randomWand = ElementRegistry.getRandomElement().getWand();
        var wand = randomWand != null ? randomWand : ItemsRegister.WAND_ITEM_FROST.get();
        var loot =  LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(UniformGenerator.between(5.0F, 13.0F))
                .add(LootItem.lootTableItem(ItemsRegister.NEXITE_POWDER.get()).setWeight(30))
                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(20))
                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(10))
        ).withPool(
            LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F))
                .add(LootItem.lootTableItem(BlocksRegister.NEXITE_BLOCK.get()).setWeight(20))
                .add(LootItem.lootTableItem(ItemsRegister.AUGMENT_CORE.get()).setWeight(15))
                .add(LootItem.lootTableItem(Items.BOOK).setWeight(5).apply(EnchantRandomlyFunction.randomApplicableEnchantment(serverLevel.registryAccess())))
        ).withPool(
            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ItemsRegister.AUGMENT_ITEM.get()).setWeight(20))
                .add(LootItem.lootTableItem(wand).setWeight(15))
                .add(LootItem.lootTableItem(ItemsRegister.TOME_OF_UNITY.get()).setWeight(12))
                .add(LootItem.lootTableItem(ItemsRegister.ADVANCED_AUGMENT_CORE.get()).setWeight(3))
                .add(LootItem.lootTableItem(ItemsRegister.AUGMENT_HYPER_CORE.get()).setWeight(1))
        ).build();

        var lootparams = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, pos)
            .create(LootContextParamSets.VAULT);
        return loot.getRandomItems(lootparams);
    }

}
