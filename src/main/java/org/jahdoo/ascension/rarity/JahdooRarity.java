package org.jahdoo.ascension.rarity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.IndexedEnum;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.items.augments.AugmentItemHelper;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.items.wand.WandData;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.rarity.RarityAttributes.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.LocalLootBeamData.attachLootBeamComponent;
import static org.jahdoo.ascension.utils.Maths.singleFormattedDouble;
import static org.jahdoo.common.items.augments.AugmentItemHelper.setAbilityToAugment;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.getRuneData;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;

@IndexedEnum
public enum JahdooRarity implements StringRepresentable, IExtensibleEnum {

//    COMMON(0, "Common", color(120, 203, 83), 1, COMMON_ATTRIBUTES),
//    RARE(1, "Rare", color(67, 164, 222), 1500, RARE_ATTRIBUTES),
//    EPIC(2, "Epic", color(222, 136, 255), 4500, EPIC_ATTRIBUTES),
//    LEGENDARY(3, "Legendary", color(241, 194, 50), 5500, LEGENDARY_ATTRIBUTES),
//    ETERNAL(4, "Eternal", color(225,92,199), 6000, ETERNAL_ATTRIBUTES),
//    UNIQUE(5, "Unique", color(104, 243, 252), -1, ETERNAL_ATTRIBUTES);

    COMMON(0, "Common", color(120, 203, 83), COMMON_ATTRIBUTES),
    RARE(1, "Rare", color(67, 164, 222), RARE_ATTRIBUTES),
    EPIC(2, "Epic", color(222, 136, 255), EPIC_ATTRIBUTES),
    LEGENDARY(3, "Legendary", color(241, 194, 50), LEGENDARY_ATTRIBUTES),
    ETERNAL(4, "Eternal", color(225,92,199), ETERNAL_ATTRIBUTES),
    UNIQUE(5, "Unique", color(104, 243, 252), ETERNAL_ATTRIBUTES);
    public static final List<Pair<JahdooRarity, Integer>> BASE_RARITY_CHANCES = List.of(
        Pair.of(COMMON, 1),
        Pair.of(RARE, 1500),
        Pair.of(EPIC, 4500),
        Pair.of(LEGENDARY, 5500),
        Pair.of(ETERNAL, 6000)
    );

    private final int id;
    private final String name;
    private final int color;
    private final UnaryOperator<Style> styleModifier;
//    private final int chanceRange;
    private final RarityAttributes attributes;
    private static final List<JahdooRarity> getAllRarities = List.of(COMMON, RARE, EPIC, LEGENDARY, ETERNAL, UNIQUE);

    JahdooRarity(int id, String name, int color, RarityAttributes attributes) {
        this.id = id;
        this.name = name;
        this.color = color;
//        this.chanceRange = chanceRange;
        this.styleModifier = style -> style.withColor(color);
        this.attributes = attributes;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public int getColour() {
        return this.color;
    }

    public UnaryOperator<Style> getStyleModifier() {
        return this.styleModifier;
    }

    public RarityAttributes getAttributes() {
        return this.attributes;
    }

    public static List<JahdooRarity> getAllRarities() {
        return getAllRarities;
    }

    private static JahdooRarity getJahdooRarity(@Nullable JahdooRarity rarity) {
        var correctRarity = rarity == null ? JahdooRarity.getRarity() : rarity;
        return correctRarity == JahdooRarity.UNIQUE ? JahdooRarity.EPIC : correctRarity;
    }

    public static AbilityRegistrar getAbilityUtil(@Nullable JahdooRarity rarity) {
        var listAll = AbilityReg.getMatchingRarityUtilOnly(getJahdooRarity(rarity));
        return listAll.get(Random.nextInt(0, listAll.size()));
    }

    public static Component addRarityTooltip(int id){
        var rarity = JahdooRarity.getAllRarities().get(id);
        return withStyleComponentTrans("rarity.jahdoo.current_rarity", -9013642)
                .copy()
                .append(withStyleComponent(rarity.getSerializedName(), rarity.getColour()));
    }

    public static ItemStack setGeneratedAugment(Item item){
        var itemStack = new ItemStack(item);
        itemStack.set(ComponentReg.NUMBER, 5);
        AugmentItemHelper.augmentIdentifierSharedUtil(itemStack,  null);
        return itemStack;
    }

    public static void setGeneratedAugment(ItemStack itemStack, JahdooRarity rarity){
        itemStack.set(ComponentReg.NUMBER, 5);
        AugmentItemHelper.augmentIdentifierSharedRarity(itemStack, false, rarity);
        itemStack.set(JAHDOO_RARITY, rarity.id);
    }

    public static ItemStack setGeneratedTome(JahdooRarity rarity, Item item){
        var itemStack = new ItemStack(item);
        createTomeAttributes(rarity, itemStack);
        return itemStack;
    }

    public static Component attachRarityTooltip(ItemStack wandItem, Level level) {
        var getRarityId = wandItem.get(JAHDOO_RARITY);
        if(getRarityId != null && level != null) {
            var getRarity = JahdooRarity.getAllRarities().get(Math.clamp(getRarityId, 0, 5));
            return JahdooRarity.addRarityTooltip(getRarity, level);
        }
        return null;
    }

    public static JahdooRarity getRarity() {
        var getRandom = Random.nextInt(1, 6020);
        var filteredList = new ArrayList<>(
            BASE_RARITY_CHANCES
                .stream()
                .filter(jahdooRarity -> jahdooRarity.getSecond() <= getRandom)
                .toList()
        );
        return listRandom(filteredList).getFirst();
    }

    public static JahdooRarity getRarity(@Nullable List<Pair<JahdooRarity, Integer>> rarities) {
        var getRandom = Random.nextInt(1, 6020);
        var filteredList = new ArrayList<>(
            rarities != null ? rarities : BASE_RARITY_CHANCES
                .stream()
                .filter(jahdooRarity -> jahdooRarity.getSecond() <= getRandom)
                .toList()
        );
        return listRandom(filteredList).getFirst();
    }

    public static AbilityRegistrar getAbilityWithRarity(boolean withUtil, @Nullable JahdooRarity rarity) {
        var removeRarity = getJahdooRarity(rarity);
        var listAll = AbilityReg.getMatchingRarity(removeRarity);
        var listNoUtil = AbilityReg.getMatchingRarityNoUtil(removeRarity);
        var list = withUtil ? listAll : listNoUtil;
        return list.get(Random.nextInt(0, list.size()));
    }

    public static Component addRarityTooltip(JahdooRarity rarity, Level level){
        if(level == null) return Component.empty();
        var id = "rarity.jahdoo.current_rarity";
        var colour = getColorTransition(ColourStore.UNIQUE_A, ColourStore.UNIQUE_B, (int) level.getGameTime(), 50);
        var getCorrectColour = rarity.id == 5 ? colour : rarity.getColour();
        var sibling = withStyleComponent(rarity.getSerializedName(), getCorrectColour);

        return withStyleComponentTrans(id, -9013642).copy().append(sibling);
    }

    public static void setGeneratedWand(JahdooRarity rarity, ItemStack item) {
        var totalSlots = 3;
        switch (rarity) {
            case RARE -> totalSlots = Random.nextInt(4, 7);
            case EPIC -> totalSlots = Random.nextInt(5, 8);
            case LEGENDARY -> totalSlots = Random.nextInt(6, 9);
            case ETERNAL -> totalSlots = Random.nextInt(8, 11);
        }
        createWandAttributes(rarity, item, rarity.id, totalSlots);
    }

    public static ItemStack setGeneratedWand(JahdooRarity rarity, Item item) {
        var itemStack = new ItemStack(item);
        var totalSlots = 3;

        switch (rarity) {
            case RARE -> totalSlots = Random.nextInt(4, 7);
            case EPIC -> totalSlots = Random.nextInt(5, 8);
            case LEGENDARY -> totalSlots = Random.nextInt(6, 9);
            case ETERNAL -> totalSlots = Random.nextInt(8, 11);
        }

        createWandAttributes(rarity, itemStack, rarity.id, totalSlots);
        return itemStack;
    }

    public static Component attachRuneTierTooltip(ItemStack wandItem) {
        var data = getRuneData(wandItem);
        var getRarity = JahdooRarity.getAllRarities().get(Math.clamp(data.tier(), 0, 5));
        var getTier = switch (getRarity.id){
            case 1 -> "II";
            case 2 -> "III";
            case 3 -> "IV";
            case 4 -> "V";
            case 5 -> "VI";
            default -> "I";
        };
        return withStyleComponent("Tier " + getTier, ColourStore.HEADER_COLOUR);
    }

    public static ItemStack getAbilityAugment(JahdooRarity...jahdooRarities){
        var allRarities = Arrays.stream(jahdooRarities).toList();
        var list = AbilityReg.getMatchingRarity(allRarities.get(Random.nextInt(0, allRarities.size())));
        var ability = list.get(Random.nextInt(0, list.size()));
        var emptyStack = new ItemStack(ItemReg.AUGMENT.get());
        ability.setModifiers(emptyStack);
        emptyStack.set(ComponentReg.NUMBER, 5);
        var wandAbilityHolder = emptyStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        setAbilityToAugment(emptyStack, ability, wandAbilityHolder);
        return emptyStack;
    }

    public static void createTomeAttributes(JahdooRarity rarity, ItemStack itemStack){
        var randomRegenValue = singleFormattedDouble(rarity.attributes.getRandomManaRegen());
        var randomManaPool = singleFormattedDouble(rarity.attributes.getRandomManaPool());
        var manaRegen = MANA_REGEN;
        var manaPool = MANA_POOL;

        attachLootBeamComponent(itemStack, rarity);
        itemStack.set(ComponentReg.JAHDOO_RARITY.get(), rarity.getId());

        replaceOrAddAttribute(itemStack, manaRegen.getRegisteredName(), manaRegen, randomRegenValue, EquipmentSlot.MAINHAND, false);
        replaceOrAddAttribute(itemStack, manaPool.getRegisteredName(), manaPool, randomManaPool, EquipmentSlot.OFFHAND, false);

//        CuriosApi.addModifier(
//            itemStack, manaRegen, manaRegen.getId(),
//            randomRegenValue, AttributeModifier.Operation.ADD_VALUE, "relic"
//        );

//        CuriosApi.addModifier(
//            itemStack, manaPool, manaPool.getId(),
//            randomManaPool, AttributeModifier.Operation.ADD_VALUE, "relic"
//        );

    }

    public static void createWandAttributes(
        JahdooRarity rarity,
        ItemStack itemStack,
        int runeSlots,
        int abilitySlots
    ) {
        var element = ElementReg.fromWand(itemStack.getItem()).orElseThrow();
        attachLootBeamComponent(itemStack, rarity);

        WandData.createRarity(itemStack, rarity.id);
        WandData.createNewAbilitySlots(itemStack, abilitySlots);
        RuneHolder.createNewRuneSlots(itemStack, runeSlots, rarity.attributes.getRandomRefinementPotential());
        itemStack.set(JAHDOO_RARITY, rarity.id);

        var cooldownReductionType = element.cooldownReduction();
        var cooldownReductionName = cooldownReductionType.getRegisteredName();
        var cooldownReductionValue = rarity.attributes.getRandomCooldown();

        var manaReductionType = element.manaReduction();
        var manaReductionName = manaReductionType.getRegisteredName();
        var manaReductionValue = rarity.attributes.getRandomManaReduction();

        var damageAmplifierType = element.damageAmplifier();
        var damageAmplifierName = damageAmplifierType.getRegisteredName();
        var damageAmplifierValue = rarity.attributes.getRandomDamage();

        replaceOrAddAttribute(itemStack, cooldownReductionName, cooldownReductionType, cooldownReductionValue, EquipmentSlot.MAINHAND, false);
        replaceOrAddAttribute(itemStack, manaReductionName, manaReductionType, manaReductionValue, EquipmentSlot.MAINHAND, false);
        replaceOrAddAttribute(itemStack, damageAmplifierName, damageAmplifierType, damageAmplifierValue, EquipmentSlot.MAINHAND, false);
    }

    //Debug using use on item
    public static void debugRarity(Player player){
        if(!player.level().isClientSide){
            playDebugMessage(player, "NEW ROLL");
            playDebugMessage(player, "---------------------------------");

            var common = new AtomicInteger();
            var uncommon = new AtomicInteger();
            var epic = new AtomicInteger();
            var legendary = new AtomicInteger();
            var ethereal = new AtomicInteger();

            for (int i = 2000; i > 0; i--) {
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

            playDebugMessage(player, "Common " + common.get());
            playDebugMessage(player, "Un-Common " + uncommon.get());
            playDebugMessage(player, "Epic " + epic.get());
            playDebugMessage(player, "Legendary " + legendary.get());
            playDebugMessage(player, "Eternal " + ethereal.get());
            playDebugMessage(player, "  ");
        }
    }
}
