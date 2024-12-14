package org.jahdoo.ability;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.IndexedEnum;
import org.jahdoo.components.WandData;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.items.wand.WandSlotManager;
import org.jahdoo.registers.*;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.items.augments.AugmentItemHelper.setAbilityToAugment;
import static org.jahdoo.registers.AttributesRegister.*;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.ModHelpers.singleFormattedDouble;

@IndexedEnum
public enum JahdooRarity implements StringRepresentable, IExtensibleEnum {
    COMMON(0, "Common", color(255,11,176,16), 1),
    RARE(1, "Rare", color(255,67, 164, 222), 300),
    EPIC(2, "Epic", color(255,222, 136, 255), 800),
    LEGENDARY(3, "Legendary", color(255,225, 199, 107), 1500),
    ETERNAL(4, "Eternal", color(255,218, 71, 71), 3000);

    private final int id;
    private final String name;
    private final int color;
    private final UnaryOperator<Style> styleModifier;
    private final int chanceRange;
    private static final List<JahdooRarity> getAllRarities = List.of(COMMON, RARE, EPIC, LEGENDARY, ETERNAL);

    JahdooRarity(int id, String name, int color, int chanceRange) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.chanceRange = chanceRange;
        this.styleModifier = (style) -> style.withColor(color);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public int getId(){
        return this.id;
    }

    public int getColour(){
        return this.color;
    }

    public UnaryOperator<Style> getStyleModifier() {
        return this.styleModifier;
    }

    public static List<JahdooRarity> getAllRarities(){
        return getAllRarities;
    }

    public static JahdooRarity getRarity(){
        var getRandom = ModHelpers.Random.nextInt(1, 3400);
        var filteredList = new ArrayList<>(getAllRarities.stream().filter(jahdooRarity -> jahdooRarity.chanceRange <= getRandom).toList());
        return ModHelpers.getRandomListElement(filteredList);
    }

    public static AbilityRegistrar getAbilityWithRarity(){
        var list = AbilityRegister.getMatchingRarity(JahdooRarity.getRarity());
        return list.get(ModHelpers.Random.nextInt(0, list.size()));
    }

    public static ItemStack getAbilityAugment(JahdooRarity...jahdooRarities){
        var allRarities = Arrays.stream(jahdooRarities).toList();
        var list = AbilityRegister.getMatchingRarity(allRarities.get(ModHelpers.Random.nextInt(0, allRarities.size())));
        var ability = list.get(ModHelpers.Random.nextInt(0, list.size()));
        var emptyStack = new ItemStack(ItemsRegister.AUGMENT_ITEM.get());
        ability.setModifiers(emptyStack);
        emptyStack.set(DataComponentRegistry.NUMBER, 5);
        var wandAbilityHolder = emptyStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        setAbilityToAugment(emptyStack, ability, wandAbilityHolder);
        return emptyStack;
    }

    public static Component addRarityTooltip(JahdooRarity rarity){
        return withStyleComponentTrans("rarity.jahdoo.current_rarity", -9013642).copy().append(withStyleComponent(rarity.getSerializedName(), rarity.getColour()));
    }

    public static ItemStack setGeneratedAugment(Item item){
        var itemStack = new ItemStack(item);
        if(ModHelpers.Random.nextInt(0, 200) != 0){
            itemStack.set(DataComponentRegistry.NUMBER, 5);
            AugmentItemHelper.augmentIdentifierSharedRarity(itemStack);
        }
        return itemStack;
    }

    public static ItemStack setGeneratedWand(JahdooRarity rarity, Item item){
        var itemStack = new ItemStack(item);

        switch (rarity){
            case COMMON -> createWandAttributes(COMMON, itemStack, 2, 4, false, 0, Pair.of(0.0,5.0), Pair.of(0.0,10.0), Pair.of(0.0, 5.0));
            case RARE -> createWandAttributes(RARE, itemStack, 2, 4, false, 0, Pair.of(0.0,5.0), Pair.of(0.0,10.0), Pair.of(4.0, 8.0));
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
            case RARE -> createTomeAttributes(RARE, itemStack, Pair.of(10.0, 20.0), Pair.of(40.0, 60.0));
            case EPIC -> createTomeAttributes(EPIC, itemStack, Pair.of(15.0, 20.0), Pair.of(60.0, 80.0));
            case LEGENDARY -> createTomeAttributes(LEGENDARY, itemStack, Pair.of(20.0, 30.0), Pair.of(80.0, 100.0));
            case ETERNAL -> createTomeAttributes(ETERNAL, itemStack, Pair.of(30.0, 50.0), Pair.of(100.0, 120.0));
        }
        return itemStack;
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
        var element = ElementRegistry.getElementByWandType(itemStack.getItem());

        if(!element.isEmpty()){
            if(allowInfiniteItem){
                if(infiniteItemChance == 0 || Random.nextInt(0, infiniteItemChance) == 0){
                    itemStack.set(DataComponentRegistry.INFINITE_ITEM.get(), true);
                }
            }

            itemStack.update(DataComponentRegistry.WAND_DATA.get(), WandData.DEFAULT, wandData -> wandData.insertNewSlots(abilitySlots));
            itemStack.set(DataComponentRegistry.JAHDOO_RARITY.get(), rarity.getId());
            //0-5 basic;
            replaceOrAddAttribute(itemStack, element.getFirst().getTypeCooldownReduction().getFirst(), element.getFirst().getTypeCooldownReduction().getSecond(), Random.nextDouble(cooldownRange.getFirst(), cooldownRange.getSecond()), EquipmentSlot.MAINHAND);
            //0-10 basic;
            replaceOrAddAttribute(itemStack, element.getFirst().getTypeManaReduction().getFirst(), element.getFirst().getTypeManaReduction().getSecond(), Random.nextDouble(manaCostRange.getFirst(), manaCostRange.getSecond()), EquipmentSlot.MAINHAND);
            //0-10 basic;
            replaceOrAddAttribute(itemStack, element.getFirst().getDamageTypeAmplifier().getFirst(), element.getFirst().getDamageTypeAmplifier().getSecond(), Random.nextDouble(damageRange.getFirst(), damageRange.getSecond()), EquipmentSlot.MAINHAND);
        }
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




    //Debug using use on item
    public static void debugRarity(Player player){
        if(!player.level().isClientSide){
            ModHelpers.playDebugMessage(player, "NEW ROLL");
            ModHelpers.playDebugMessage(player, "---------------------------------");

            var common = new AtomicInteger();
            var uncommon = new AtomicInteger();
            var epic = new AtomicInteger();
            var legendary = new AtomicInteger();
            var ethereal = new AtomicInteger();

            for (int i = 500; i > 0; i--) {
                var rarity = JahdooRarity.getRarity();

                if (rarity == JahdooRarity.COMMON) {
                    common.set(common.get() + 1);
                } else if (rarity == JahdooRarity.RARE) {
                    uncommon.set(uncommon.get() + 1);
                } else if (rarity == JahdooRarity.EPIC) {
                    epic.set(epic.get() + 1);
                } else if (rarity == JahdooRarity.LEGENDARY) {
                    legendary.set(legendary.get() + 1);
                } else if (rarity == JahdooRarity.ETERNAL) {
                    ethereal.set(ethereal.get() + 1);
                }
            }

            ModHelpers.playDebugMessage(player, "Common " + common.get());
            ModHelpers.playDebugMessage(player, "Un-Common " + uncommon.get());
            ModHelpers.playDebugMessage(player, "Epic " + epic.get());
            ModHelpers.playDebugMessage(player, "Legendary " + legendary.get());
            ModHelpers.playDebugMessage(player, "Eternal " + ethereal.get());
            ModHelpers.playDebugMessage(player, "  ");
        }
    }
}
