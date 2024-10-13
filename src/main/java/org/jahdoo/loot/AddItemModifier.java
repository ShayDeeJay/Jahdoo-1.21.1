package org.jahdoo.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddItemModifier extends LootModifier {
    public static final Supplier<MapCodec<AddItemModifier>> CODEC = Suppliers.memoize(
        () -> RecordCodecBuilder.mapCodec(
            builder -> codecStart(builder)
                .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
                .apply(builder, AddItemModifier::new)
        )
    );

    private final Item item;

    public AddItemModifier(LootItemCondition[] conditionsIn, Item item) {
        super(conditionsIn);
        this.item = item;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for(LootItemCondition condition : this.conditions) {
            if(!condition.test(context)) return generatedLoot;
        }

        if(this.item instanceof Augment){
            setGeneratedAugment(generatedLoot, this.item);
        } else {
            generatedLoot.add(new ItemStack(this.item));
        }

        return generatedLoot;
    }

    public static void setGeneratedAugment(ObjectArrayList<ItemStack> generatedLoot, Item item){
        var itemStack = new ItemStack(item);
        var random = GeneralHelpers.Random;
        if(random.nextInt(0, 20) == 0){
            generatedLoot.add(itemStack);
        } else {
            var perfectRoller = random.nextInt(0, 1000) == 0 ? 20 : random.nextInt(1, 19);
            itemStack.set(DataComponentRegistry.AUGMENT_RATING, (double) perfectRoller);
            itemStack.set(DataComponentRegistry.NUMBER, 5);
            AugmentItemHelper.augmentIdentifierSharedRarity(itemStack);
            generatedLoot.add(itemStack);
        }
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
