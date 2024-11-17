package org.jahdoo.loot;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.components.WandData;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.items.curious_items.TomeOfUnity;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.items.wand.WandSlotManager;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

import static org.jahdoo.all_magic.JahdooRarity.*;
import static org.jahdoo.registers.AttributesRegister.*;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.ModHelpers.singleFormattedDouble;

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

    public static void createWandAttributes(
        JahdooRarity rarity,
        ItemStack itemStack,
        int upgradeSlots,
        int abilitySlots,
        boolean allowInfiniteItem,
        int infiniteItemChance,
        Pair<Double, Double> cooldownRange,
        Pair<Double, Double> manaCostRange,
        Pair<Double, Double> damageRange
    ){
        WandSlotManager.createNewSlotsForWand(itemStack, upgradeSlots);
        if(allowInfiniteItem){
            if(infiniteItemChance == 0 || Random.nextInt(0, infiniteItemChance) == 0){
                itemStack.set(DataComponentRegistry.INFINITE_ITEM.get(), true);
            }
        }

        itemStack.update(DataComponentRegistry.WAND_DATA.get(), WandData.DEFAULT, wandData -> wandData.insertNewSlots(abilitySlots));
        itemStack.set(DataComponentRegistry.JAHDOO_RARITY.get(), rarity.getId());
        //0-5 basic;
        replaceOrAddAttribute(itemStack, COOLDOWN_REDUCTION_PREFIX, COOLDOWN_REDUCTION, Random.nextDouble(cooldownRange.getFirst(), cooldownRange.getSecond()), EquipmentSlot.MAINHAND);
        //0-10 basic;
        replaceOrAddAttribute(itemStack, MANA_COST_REDUCTION_PREFIX, MANA_COST_REDUCTION, Random.nextDouble(manaCostRange.getFirst(), manaCostRange.getSecond()), EquipmentSlot.MAINHAND);
        //0-10 basic;
        replaceOrAddAttribute(itemStack, MAGIC_DAMAGE_MULTIPLIER_PREFIX, MAGIC_DAMAGE_MULTIPLIER, Random.nextDouble(damageRange.getFirst(), damageRange.getSecond()), EquipmentSlot.MAINHAND);

    }

    public static void createTomeAttributes(JahdooRarity rarity, ItemStack itemStack, Pair<Double, Double> regenValue, Pair<Double, Double> manaPool){
        var rangeRegenValue = ModHelpers.Random.nextDouble(regenValue.getFirst(), regenValue.getSecond());
        var randomRegenValue = singleFormattedDouble(rangeRegenValue);
        var rangeManaPool = ModHelpers.Random.nextDouble(manaPool.getFirst(), manaPool.getSecond());
        var randomManaPool = singleFormattedDouble(rangeManaPool);

        CuriosApi.addModifier(
            itemStack, AttributesRegister.MANA_REGEN, ModHelpers.res("mana_regen"),
            randomRegenValue, AttributeModifier.Operation.ADD_VALUE, "tome"
        );

        CuriosApi.addModifier(
            itemStack, AttributesRegister.MANA_POOL, ModHelpers.res("mana_pool"),
            randomManaPool, AttributeModifier.Operation.ADD_VALUE, "tome"
        );

        itemStack.set(DataComponentRegistry.JAHDOO_RARITY.get(), rarity.getId());
    }

    public static ItemStack setGeneratedAugment(Item item){
        var random = ModHelpers.Random;
        var itemStack = new ItemStack(item);
        var perfectRoller = random.nextInt(0, 200) == 0 ? 20 : random.nextInt(1, 19);
        itemStack.set(DataComponentRegistry.AUGMENT_RATING, (double) perfectRoller);
        if(random.nextInt(0, 20) != 0){
            itemStack.set(DataComponentRegistry.NUMBER, 5);
            AugmentItemHelper.augmentIdentifierSharedRarity(itemStack);
        }
        return itemStack;
    }

    public static ItemStack setGeneratedWand(JahdooRarity rarity, Item item){
        var itemStack = new ItemStack(item);

        switch (rarity){
            case COMMON -> createWandAttributes(COMMON, itemStack, 2, 4, false, 0, Pair.of(0.0,5.0), Pair.of(0.0,10.0), Pair.of(0.0, 5.0));
            case UNCOMMON -> createWandAttributes(UNCOMMON, itemStack, 2, 4, false, 0, Pair.of(0.0,5.0), Pair.of(0.0,10.0), Pair.of(4.0, 8.0));
            case EPIC -> createWandAttributes(EPIC, itemStack, 2, 6, false, 0, Pair.of(2.0,6.0), Pair.of(5.0, 10.0), Pair.of(4.0, 8.0));
            case LEGENDARY -> createWandAttributes(LEGENDARY, itemStack, 2, 7, false, 0, Pair.of(2.0, 6.0), Pair.of(10.0, 15.0), Pair.of(10.0, 15.0));
            case ETERNAL -> createWandAttributes(ETERNAL, itemStack, 4, 8, true, 10, Pair.of(5.0, 10.0), Pair.of(15.0, 20.0), Pair.of(15.0, 25.0));
        }
        return itemStack;
    }


    public static ItemStack setGeneratedTome(JahdooRarity rarity, Item item){
        var itemStack = new ItemStack(item);

        switch (rarity){
            case COMMON -> createTomeAttributes(COMMON, itemStack, Pair.of(5.0, 10.0), Pair.of(20.0, 40.0));
            case UNCOMMON -> createTomeAttributes(UNCOMMON, itemStack, Pair.of(10.0, 20.0), Pair.of(40.0, 60.0));
            case EPIC -> createTomeAttributes(EPIC, itemStack, Pair.of(15.0, 20.0), Pair.of(60.0, 80.0));
            case LEGENDARY -> createTomeAttributes(LEGENDARY, itemStack, Pair.of(20.0, 30.0), Pair.of(80.0, 100.0));
            case ETERNAL -> createTomeAttributes(ETERNAL, itemStack, Pair.of(30.0, 50.0), Pair.of(100.0, 120.0));
        }
        return itemStack;
    }


    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
