package org.jahdoo.common.datagen.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jahdoo.common.items.augments.Augment;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static net.minecraft.core.registries.BuiltInRegistries.*;
import static org.jahdoo.ascension.rarity.JahdooRarity.*;

public class AddItemModifier extends LootModifier {

    private final Item item;

    public AddItemModifier(LootItemCondition[] conditionsIn, Item item) {
        super(conditionsIn);
        this.item = item;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public static final Supplier<MapCodec<AddItemModifier>> CODEC = Suppliers.memoize(
        () -> RecordCodecBuilder.mapCodec(
            builder -> codecStart(builder)
                .and(ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
                .apply(builder, AddItemModifier::new)
        )
    );

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(this.item == null) return generatedLoot;
        for (var condition : this.conditions) if (!condition.test(context)) return generatedLoot;
        var itemStack = this.item instanceof Augment ? setGeneratedAugment(this.item) : new ItemStack(this.item);

        generatedLoot.add(itemStack);
        return generatedLoot;
    }

}
