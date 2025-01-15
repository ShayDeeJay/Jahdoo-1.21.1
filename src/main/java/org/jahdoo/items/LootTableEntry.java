package org.jahdoo.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

public class LootTableEntry  extends LootPoolSingletonContainer {

    protected LootTableEntry(int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {

    }

    @Override
    public LootPoolEntryType getType() {
        return null;
    }

}
