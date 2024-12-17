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
import org.jahdoo.items.TomeOfUnity;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.ElementRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static org.jahdoo.ability.JahdooRarity.*;

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
        if(this.item == null) return generatedLoot;

        for (LootItemCondition condition : this.conditions) if (!condition.test(context)) return generatedLoot;

        ItemStack itemStack = switch (this.item) {
            case Augment ignored -> setGeneratedAugment(this.item);
            case WandItem ignored -> setGeneratedWand(getRarity(), ElementRegistry.getRandomElement().getWand());
            case TomeOfUnity ignored -> setGeneratedTome(getRarity(), this.item);
            default -> new ItemStack(this.item);
        };

        generatedLoot.add(itemStack);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
