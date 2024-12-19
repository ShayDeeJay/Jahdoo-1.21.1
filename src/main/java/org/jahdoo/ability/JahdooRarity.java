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
import org.jahdoo.components.PowerGemData;
import org.jahdoo.components.WandData;
import org.jahdoo.items.augments.AugmentItemHelper;
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
import static org.jahdoo.components.PowerGemData.PowerGemHelpers.getGemData;
import static org.jahdoo.items.augments.AugmentItemHelper.setAbilityToAugment;
import static org.jahdoo.registers.AttributesRegister.*;
import static org.jahdoo.registers.DataComponentRegistry.JAHDOO_RARITY;
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

    public static Component attachRarityTooltip(ItemStack wandItem) {
        var getRarityId = wandItem.get(JAHDOO_RARITY);
        if(getRarityId != null) {
            var getRarity = JahdooRarity.getAllRarities().get(Math.clamp(getRarityId, 0, 5));
            return JahdooRarity.addRarityTooltip(getRarity);
        }
        return Component.empty();
    }

    public static Component attachGemRarityTooltip(ItemStack wandItem) {
        var data = getGemData(wandItem).orElse(PowerGemData.DEFAULT);
        var getRarity = JahdooRarity.getAllRarities().get(Math.clamp(data.rarityId(), 0, 5));
        return JahdooRarity.addRarityTooltip(getRarity);
    }

    public static ItemStack setGeneratedAugment(Item item){
        var itemStack = new ItemStack(item);
        if(ModHelpers.Random.nextInt(0, 200) != 0){
            itemStack.set(DataComponentRegistry.NUMBER, 5);
            AugmentItemHelper.augmentIdentifierSharedRarity(itemStack);
        }
        return itemStack;
    }

    public static double getManaRange(JahdooRarity rarity){
        return switch (rarity){
            case COMMON ->  Random.nextDouble(20.0, 40.0);
            case RARE -> Random.nextDouble(40.0, 60.0) ;
            case EPIC -> Random.nextDouble(60.0, 80.0) ;
            case LEGENDARY -> Random.nextDouble(80.0, 100.0);
            case ETERNAL -> Random.nextDouble(100.0, 120.0) ;
        };
    }

    public static double getManaRegenRange(JahdooRarity rarity){
        return switch (rarity){
            case COMMON ->  Random.nextDouble(5.0, 10.0);
            case RARE -> Random.nextDouble(10.0, 20.0) ;
            case EPIC -> Random.nextDouble(15.0, 20.0) ;
            case LEGENDARY -> Random.nextDouble(20.0, 30.0);
            case ETERNAL -> Random.nextDouble(30.0, 50.0) ;
        };
    }

    public static double getCooldownRange(JahdooRarity rarity){
        return switch (rarity){
            case COMMON ->  Random.nextDouble(0.5,3.5);
            case RARE -> Random.nextDouble(1.0,4.5) ;
            case EPIC -> Random.nextDouble(2.0,6.0) ;
            case LEGENDARY -> Random.nextDouble(3.0,7.0);
            case ETERNAL -> Random.nextDouble(6.0, 10.0) ;
        };
    }

    public static double getManaReductionRange(JahdooRarity rarity){
        return switch (rarity){
            case COMMON ->  Random.nextDouble(0.5,8.0);
            case RARE -> Random.nextDouble(2.0,10.0) ;
            case EPIC -> Random.nextDouble(3.0,14) ;
            case LEGENDARY -> Random.nextDouble(10.0,17.0);
            case ETERNAL -> Random.nextDouble(15.0, 20.0) ;
        };
    }

    public static double getDamageRange(JahdooRarity rarity){
        return switch (rarity){
            case COMMON -> Random.nextDouble(3.0,10.0);
            case RARE -> Random.nextDouble(5.0,13.0) ;
            case EPIC -> Random.nextDouble(7.0,16.0) ;
            case LEGENDARY -> Random.nextDouble(10.0,20.0);
            case ETERNAL -> Random.nextDouble(15.0, 25.0) ;
        };
    }

    public static ItemStack setGeneratedWand(JahdooRarity rarity, Item item){
        var itemStack = new ItemStack(item);
        switch (rarity){
            case COMMON -> createWandAttributes(COMMON, itemStack, Random.nextInt(1, 2), 3, false, 0, getCooldownRange(COMMON), getManaReductionRange(COMMON), getDamageRange(COMMON));
            case RARE -> createWandAttributes(RARE, itemStack, Random.nextInt(1, 3), Random.nextInt(4, 7), false, 0, getCooldownRange(RARE), getManaReductionRange(RARE), getDamageRange(RARE));
            case EPIC -> createWandAttributes(EPIC, itemStack, Random.nextInt(1, 3), Random.nextInt(5, 8), false, 0,getCooldownRange(EPIC), getManaReductionRange(EPIC), getDamageRange(EPIC));
            case LEGENDARY -> createWandAttributes(LEGENDARY, itemStack, Random.nextInt(1, 4),Random.nextInt(6, 9), false, 0, getCooldownRange(LEGENDARY), getManaReductionRange(LEGENDARY), getDamageRange(LEGENDARY));
            case ETERNAL -> createWandAttributes(ETERNAL, itemStack, Random.nextInt(2, 5),Random.nextInt(8, 11), false, 10,getCooldownRange(ETERNAL), getManaReductionRange(ETERNAL), getDamageRange(ETERNAL));
        }
        return itemStack;
    }


    public static ItemStack setGeneratedTome(JahdooRarity rarity, Item item){
        var itemStack = new ItemStack(item);
        switch (rarity){
            case COMMON -> createTomeAttributes(COMMON, itemStack, getManaRegenRange(COMMON), getManaRange(COMMON));
            case RARE -> createTomeAttributes(RARE, itemStack, getManaRegenRange(RARE), getManaRange(RARE));
            case EPIC -> createTomeAttributes(EPIC, itemStack, getManaRegenRange(EPIC), getManaRange(EPIC));
            case LEGENDARY -> createTomeAttributes(LEGENDARY, itemStack, getManaRegenRange(LEGENDARY), getManaRange(LEGENDARY));
            case ETERNAL -> createTomeAttributes(ETERNAL, itemStack, getManaRegenRange(ETERNAL), getManaRange(ETERNAL));
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
        Double cooldownRange,
        Double manaCostRange,
        Double damageRange
    ){
        WandData.createNewSlotsForWand(itemStack, upgradeSlots);
        var element = ElementRegistry.getElementByWandType(itemStack.getItem());

        if(!element.isEmpty()){
            if(allowInfiniteItem){
                if(infiniteItemChance == 0 || Random.nextInt(0, infiniteItemChance) == 0){
                    itemStack.set(DataComponentRegistry.INFINITE_ITEM.get(), true);
                }
            }

            itemStack.update(DataComponentRegistry.WAND_DATA.get(), WandData.DEFAULT, wandData -> wandData.insertNewAbilitySlots(abilitySlots));
            itemStack.set(DataComponentRegistry.JAHDOO_RARITY.get(), rarity.getId());
            //0-5 basic;
            replaceOrAddAttribute(itemStack, element.getFirst().getTypeCooldownReduction().getFirst(), element.getFirst().getTypeCooldownReduction().getSecond(), cooldownRange, EquipmentSlot.MAINHAND, false);
            //0-10 basic;
            replaceOrAddAttribute(itemStack, element.getFirst().getTypeManaReduction().getFirst(), element.getFirst().getTypeManaReduction().getSecond(), manaCostRange, EquipmentSlot.MAINHAND, false);
            //0-10 basic;
            replaceOrAddAttribute(itemStack, element.getFirst().getDamageTypeAmplifier().getFirst(), element.getFirst().getDamageTypeAmplifier().getSecond(), damageRange, EquipmentSlot.MAINHAND, false);
        }
    }

    public static void createTomeAttributes(JahdooRarity rarity, ItemStack itemStack, double rangeRegenValue, double rangeManaPool){
        var randomRegenValue = singleFormattedDouble(rangeRegenValue);
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
