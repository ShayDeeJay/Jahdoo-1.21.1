package org.jahdoo.ability.rarity;

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
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.components.WandData;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.*;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ability.rarity.RarityAttributes.*;
import static org.jahdoo.components.RuneData.RuneHelpers.getRuneData;
import static org.jahdoo.items.augments.AugmentItemHelper.setAbilityToAugment;
import static org.jahdoo.registers.AttributesRegister.*;
import static org.jahdoo.registers.DataComponentRegistry.JAHDOO_RARITY;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.ModHelpers.singleFormattedDouble;

@IndexedEnum
public enum JahdooRarity implements StringRepresentable, IExtensibleEnum {
    // Enum Definitions
    COMMON(0, "Common", color(120, 203, 83), 1, COMMON_ATTRIBUTES),
    RARE(1, "Rare", color(67, 164, 222), 300, RARE_ATTRIBUTES),
    EPIC(2, "Epic", color(222, 136, 255), 800, EPIC_ATTRIBUTES),
    LEGENDARY(3, "Legendary", color(241, 194, 50), 1500, LEGENDARY_ATTRIBUTES),
    ETERNAL(4, "Eternal", color(218, 71, 71), 3000, ETERNAL_ATTRIBUTES);

    private final int id;
    private final String name;
    private final int color;
    private final UnaryOperator<Style> styleModifier;
    private final int chanceRange;
    private final RarityAttributes attributes;

    private static final List<JahdooRarity> getAllRarities = List.of(COMMON, RARE, EPIC, LEGENDARY, ETERNAL);

    JahdooRarity(int id, String name, int color, int chanceRange, RarityAttributes attributes) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.chanceRange = chanceRange;
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

    public static JahdooRarity getRarity() {
        var getRandom = ModHelpers.Random.nextInt(1, 3400);
        var filteredList = new ArrayList<>(getAllRarities.stream()
            .filter(jahdooRarity -> jahdooRarity.chanceRange <= getRandom).toList());
        return ModHelpers.getRandomListElement(filteredList);
    }

    public static AbilityRegistrar getAbilityWithRarity() {
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
        var getRarityId = wandItem.get(WAND_DATA);
        if(getRarityId != null) {
            var getRarity = JahdooRarity.getAllRarities().get(Math.clamp(getRarityId.rarityId(), 0, 5));
            return JahdooRarity.addRarityTooltip(getRarity);
        }
        return Component.empty();
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

    public static Component attachRuneRarityTooltip(ItemStack wandItem) {
        var data = getRuneData(wandItem);
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

    public static ItemStack setGeneratedWand(JahdooRarity rarity, Item item) {
        var itemStack = new ItemStack(item);
        int totalSlots = 3;

        switch (rarity) {
            case RARE -> totalSlots = Random.nextInt(4, 7);
            case EPIC -> totalSlots = Random.nextInt(5, 8);
            case LEGENDARY -> totalSlots = Random.nextInt(6, 9);
            case ETERNAL -> totalSlots = Random.nextInt(8, 11);
        }

        createWandAttributes(rarity, itemStack, rarity.id, totalSlots);
        return itemStack;
    }


    public static ItemStack setGeneratedTome(JahdooRarity rarity, Item item){
        var itemStack = new ItemStack(item);
        switch (rarity){
            case COMMON -> createTomeAttributes(COMMON, itemStack);
            case RARE -> createTomeAttributes(RARE, itemStack);
            case EPIC -> createTomeAttributes(EPIC, itemStack);
            case LEGENDARY -> createTomeAttributes(LEGENDARY, itemStack);
            case ETERNAL -> createTomeAttributes(ETERNAL, itemStack);
        }
        return itemStack;
    }


    public static void createWandAttributes(
        JahdooRarity rarity,
        ItemStack itemStack,
        int runeSlots,
        int abilitySlots
    ){
        var element = ElementRegistry.getElementByWandType(itemStack.getItem());

        if(!element.isEmpty()){
            WandData.createRarity(itemStack, rarity.id);
            WandData.createNewAbilitySlots(itemStack, abilitySlots);
            WandData.createNewRuneSlots(itemStack, runeSlots);
            WandData.createRefinementPotential(itemStack, rarity.attributes.getRandomRefinementPotential());

            //0-5 basic;
            replaceOrAddAttribute(itemStack, element.getFirst().getTypeCooldownReduction().getFirst(), element.getFirst().getTypeCooldownReduction().getSecond(), rarity.attributes.getRandomCooldown(), EquipmentSlot.MAINHAND, false);
            //0-10 basic;
            replaceOrAddAttribute(itemStack, element.getFirst().getTypeManaReduction().getFirst(), element.getFirst().getTypeManaReduction().getSecond(),  rarity.attributes.getRandomManaReduction(), EquipmentSlot.MAINHAND, false);
            //0-10 basic;
            replaceOrAddAttribute(itemStack, element.getFirst().getDamageTypeAmplifier().getFirst(), element.getFirst().getDamageTypeAmplifier().getSecond(),  rarity.attributes.getRandomDamage(), EquipmentSlot.MAINHAND, false);
        }
    }

    public static void createTomeAttributes(JahdooRarity rarity, ItemStack itemStack){
        var randomRegenValue = singleFormattedDouble(rarity.attributes.getRandomManaRegen());
        var randomManaPool = singleFormattedDouble(rarity.attributes.getRandomManaPool());

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
