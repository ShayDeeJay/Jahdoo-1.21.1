package org.jahdoo.ascension.trading_post;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.RuneReg;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.world.entity.EquipmentSlot.*;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.*;
import static org.jahdoo.ascension.rarity.JahdooRarity.*;
import static org.jahdoo.ascension.trading_post.ShoppingArmor.enchantArmorItem;
import static org.jahdoo.ascension.trading_post.ShoppingWeapon.enchantSword;
import static org.jahdoo.ascension.trading_post.ShoppingWeapon.getElementalSword;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.Helpers.listRandom;
import static org.jahdoo.ascension.utils.LocalLootBeamData.attachLootBeamComponent;
import static org.jahdoo.ascension.utils.Maths.getPercentageTotal;
import static org.jahdoo.ascension.utils.Maths.singleFormattedDouble;
import static org.jahdoo.common.items.magnet.MagnetData.*;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;
import static org.jahdoo.common.registers.ComponentReg.MAGNET_DATA;

public record ShoppingItems(ItemStack ShoppingItem, CurrencyConverter itemCosts){

    public static ShoppingItems shoppingArmorItem(ServerLevel serverLevel) {
        var randomMage = new ShoppingItems(listRandom(ShoppingArmor.mageWithData(UNIQUE)), setGoldCost(150));
        var randomBattleMage = new ShoppingItems(listRandom(ShoppingArmor.battleMageWithData(UNIQUE)), setGoldCost(300));
        var randomWizard = new ShoppingItems(listRandom(ShoppingArmor.wizardWithData(UNIQUE)), setPlatinumCost(150));
        var randomKnightKing = new ShoppingItems(listRandom(ShoppingArmor.knightKingWithData(UNIQUE)), setPlatinumCost(150));
        var randomAncientGolem = new ShoppingItems(listRandom(ShoppingArmor.ancientGolemWithData(UNIQUE)), setPlatinumCost(300));
        var getRandomArmor = listRandom(List.of(randomMage, randomBattleMage, randomWizard, randomKnightKing, randomAncientGolem));

        var stack = getRandomArmor.ShoppingItem;
        if (stack.getItem() instanceof ArmorItem armorItem) {
            enchantArmorItem(serverLevel, stack, armorItem, UNIQUE);
        }

        return new ShoppingItems(stack, getRandomArmor.itemCosts());
    }

    public static ShoppingItems shoppingMagnetItem() {
        var getMagnet = magnetItem(null, UNIQUE);
        return new ShoppingItems(getMagnet, setGoldCost(50));
    }

    public static ItemStack magnetItem(@Nullable ItemStack itemStack, JahdooRarity jahdooRarity) {
        var magnetStack = itemStack != null ? itemStack : new ItemStack(ItemReg.MAGNET.get());
        var isUnique = jahdooRarity == UNIQUE;
        ShoppingItems.attachSharedProperties(magnetStack, jahdooRarity.getId() >= 4 ? 1 : 0, jahdooRarity, -1, isUnique ? 20 : -50, isUnique ? 20 : -50);
        var id = jahdooRarity.getId();

        if(isUnique){
            addAttribute(magnetStack, BODY, null, null);
            preInsertRunes(magnetStack);
        }

        if(id > 0){
            magnetStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(id));
        }

        var dataType = switch (id) {
            case 1 -> simpleMagnet();
            case 2 -> greaterMagnet();
            case 3 -> perfectMagnet();
            case 4 -> ancientMagnet();
            case 5 -> uniqueMagnet();
            default -> DEFAULT;
        };

        magnetStack.set(MAGNET_DATA, dataType);
        return magnetStack;
    }


    public static ShoppingItems shareWeaponData(ItemStack weapon,  @Nullable JahdooRarity rarity, @Nullable ServerLevel serverLevel){
        attachWeaponData(weapon, rarity, serverLevel);
        return new ShoppingItems(weapon, setGoldCost(110));
    }

    public static void attachWeaponData(ItemStack weapon, @Nullable JahdooRarity rarity, @Nullable ServerLevel serverLevel) {
        var getRarity = rarity == null ? JahdooRarity.getRarity() : rarity;
        var isSpecial = getRarity == UNIQUE;
        var adjustDurability = isSpecial ? 20 : 0;

        attachSharedProperties(weapon, isSpecial ? 1 : 0, getRarity, isSpecial ?  Random.nextInt(3, 6) : -1, adjustDurability, adjustDurability);
        if(serverLevel != null) enchantSword(serverLevel, weapon, isSpecial);

        if(isSpecial) preInsertRunes(weapon);
    }

    public static ShoppingItems ingmasSword(@Nullable JahdooRarity rarity, @Nullable ServerLevel serverLevel) {
        var ingmasSword = new ItemStack(ItemReg.INGMAS_SWORD);
        return shareWeaponData(ingmasSword, rarity, serverLevel);
    }

    public static ShoppingItems glaive(@Nullable JahdooRarity rarity, @Nullable ServerLevel serverLevel) {
        var glaive = new ItemStack(ItemReg.ANCIENT_GLAIVE);
        return shareWeaponData(glaive, rarity, serverLevel);
    }

    public static ShoppingItems elementalSword(@Nullable JahdooRarity rarity, @Nullable ServerLevel serverLevel) {
        var sword = getElementalSword();
        return shareWeaponData(sword, rarity, serverLevel);
    }

    public static ShoppingItems getEliteShoppingItem(ServerLevel serverLevel){
        var items = List.of(
            soldWands(UNIQUE),
            shoppingTomeItem(),
            shoppingArmorItem(serverLevel),
            elementalSword(UNIQUE, serverLevel),
            glaive(UNIQUE, serverLevel),
            ingmasSword(UNIQUE, serverLevel),
            shoppingMagnetItem(),
            shoppingGauntletItem(),
            shoppingRuneItem()
        );

        return listRandom(items);
    }

    public static void addAttribute(
        ItemStack itemStack,
        EquipmentSlot equipmentSlot,
        @Nullable JahdooRarity runeRarity,
        @Nullable JahdooRarity tierRarity
    ) {
        var rarityAttribute = runeRarity == null ? getRarity() : runeRarity;
        var getTypeAttributes = RuneReg.getRuneWithRarity(rarityAttribute);
        getTypeAttributes.ifPresent(
            rune -> {
                var newRarity = tierRarity == null ? getRarity() : tierRarity;
                var gen = rune.getAttribute(newRarity.getAttributes());
                var delegate = rune.attributeHolder();
                var registeredName = delegate.getRegisteredName();
                var isPercentage = rune.baseValue() > 0 ? (gen * rune.baseValue()) / 100 : gen;
                replaceOrAddAttribute(itemStack, registeredName, delegate, isPercentage, equipmentSlot, true, "bonus");
            }
        );
    }

    public static void addSpecificAttribute(
        ItemStack itemStack,
        EquipmentSlot equipmentSlot,
        @Nullable JahdooRarity tierRarity,
        AbstractRune rune
    ) {
        var newRarity = tierRarity == null ? getRarity() : tierRarity;
        var gen = rune.getAttribute(newRarity.getAttributes());
        var delegate = rune.attributeHolder();
        var registeredName = delegate.getRegisteredName();
        var isPercentage = rune.baseValue() > 0 ? (gen * rune.baseValue()) / 100 : gen;
        replaceOrAddAttribute(itemStack, registeredName, delegate, isPercentage, equipmentSlot, true, "bonus");
    }

    public static ShoppingItems shoppingRuneItem(){
        var med = new ShoppingItems(RuneHelpers.generateRandomTypAttribute(ETERNAL, EPIC), setGoldCost(300));
        var high = new ShoppingItems(RuneHelpers.generateRandomTypAttribute(ETERNAL, LEGENDARY), setPlatinumCost(150));
        var best = new ShoppingItems(RuneHelpers.generateRandomTypAttribute(ETERNAL, ETERNAL), setPlatinumCost((300)));
        var getAll = List.of(med, high, best);
        return listRandom(getAll);
    }

    public static ShoppingItems shoppingGauntletItem(){
        var shoppingItem = getGauntletWithRarity(null, UNIQUE);
        return new ShoppingItems(shoppingItem, setPlatinumCost((350)));
    }

    public static ShoppingItems shoppingTomeItem(){
        var stack = new ItemStack(ItemReg.TOME_OF_UNITY);
        var tome = createTomeAttributes(stack, UNIQUE);
        return new ShoppingItems(tome, setGoldCost(180));
    }

    public static void attachDurability(ItemStack itemStack, int durability) {
        itemStack.set(DataComponents.MAX_DAMAGE, (int) (Math.round(durability /10.0) * 10));
        itemStack.set(DataComponents.DAMAGE, 0);
    }

    public static ItemStack getRandomWand(@Nullable JahdooRarity rarity, @Nullable ItemStack item) {
        var itemStack = item == null ? new ItemStack(Objects.requireNonNull(ElementReg.random().getWand())) : item;
        createWandAttributes(rarity, itemStack);
        return itemStack;
    }

    public static void createWandAttributes(
        @Nullable JahdooRarity jahdooRarity,
        ItemStack itemStack
    ) {
        var element = ElementReg.fromWand(itemStack.getItem()).orElseThrow();
        var rarity = jahdooRarity == null ? JahdooRarity.getRarity() : jahdooRarity;
        var isUnique = rarity == UNIQUE;
        var rarityId = rarity.getId();

        attachSharedProperties(itemStack, rarityId, rarity, isUnique ? Random.nextInt(4,8) : -1, isUnique ? 50 : 0, isUnique ? 50 : 0);

        if(isUnique) {
            addAttribute(itemStack, MAINHAND, null, null);
            preInsertRunes(itemStack);
        }

        if(rarityId > 0){
            addDamageImplicit(rarity, itemStack, element, isUnique);
            if(rarityId > 1) addManaImplicit(rarity, itemStack, element, isUnique);
            if(rarityId > 2) addCooldownImplicit(rarity, itemStack, element, isUnique);
        }
    }

    public static void preInsertRunes(ItemStack itemStack) {
        var addRune = new ArrayList<ItemStack>();
        var getRuneHolderSize = JahdooGearData.getRuneholder(itemStack).runeSlots().size();
        for(int i = 0; i < getRuneHolderSize; i++){
            if(Maths.percentageChance(10)){
                var getRandomRun = RuneHelpers.generateRandomTypAttribute(null, null, null);
                addRune.add(getRandomRun);
            } else {
                addRune.add(ItemStack.EMPTY);
            }
        }
        JahdooGearData.updateRuneSlots(itemStack, addRune);
    }

    public static ItemStack createTomeAttributes(@Nullable ItemStack itemStack, @Nullable JahdooRarity withRarity){
        var rarity = withRarity == null ? getRarity() : withRarity;
        var stack = itemStack == null ? new ItemStack(ItemReg.TOME_OF_UNITY) : itemStack;
        var randomRegenValue = singleFormattedDouble(rarity.getAttributes().getRandomManaRegen());
        var randomManaPool = singleFormattedDouble(rarity.getAttributes().getRandomManaPool());
        var manaRegen = MANA_REGEN;
        var manaPool = MANA_POOL;
        var isUnique = rarity == UNIQUE;

        attachSharedProperties(stack, isUnique ? 1 : 0, rarity, isUnique ? Random.nextInt(3, 6) : -1, 25, 25);
        if(isUnique) preInsertRunes(stack);
        replaceOrAddAttribute(stack, manaRegen.getRegisteredName(), manaRegen, randomRegenValue * 1.5, MAINHAND, false, "");
        replaceOrAddAttribute(stack, manaPool.getRegisteredName(), manaPool, randomManaPool * 1.5, OFFHAND, false, "");

        return stack;
    }

    public static JahdooRarity getRaritiesByChestRarity(int chestRarity){
        var common = List.of(Pair.of(COMMON, 1), Pair.of(RARE, 5000));
        var rare = List.of(Pair.of(COMMON, 1), Pair.of(RARE, 1000), Pair.of(EPIC, 5800));
        var legendary = List.of(Pair.of(RARE, 1), Pair.of(EPIC, 3000), Pair.of(LEGENDARY, 5800));
        var eternal = List.of(Pair.of(EPIC, 1), Pair.of(LEGENDARY, 3000), Pair.of(ETERNAL, 5800));
        var withDone = switch (chestRarity){
            case 1 -> rare;
            case 2 -> legendary;
            case 3 -> eternal;
            default -> common;
        };
        return getRarity(withDone);
    }

    private static void addCooldownImplicit(JahdooRarity rarity, ItemStack itemStack, AbstractElement element, boolean isUnique) {
        var cooldownReductionType = element.cooldownReduction();
        var cooldownReductionName = cooldownReductionType.getRegisteredName();
        var cooldownReductionValue = getPercentageTotal(isUnique ? 20 : 0, rarity.getAttributes().getRandomCooldown());
        replaceOrAddAttribute(itemStack, cooldownReductionName, cooldownReductionType, cooldownReductionValue, MAINHAND, true, "wand");
    }

    private static void addManaImplicit(JahdooRarity rarity, ItemStack itemStack, AbstractElement element, boolean isUnique) {
        var manaReductionType = element.manaReduction();
        var manaReductionName = manaReductionType.getRegisteredName();
        var manaReductionValue = getPercentageTotal(isUnique ? 20 : 0, rarity.getAttributes().getRandomManaReduction());
        replaceOrAddAttribute(itemStack, manaReductionName, manaReductionType, manaReductionValue, MAINHAND, true, "wand");
    }

    private static void addDamageImplicit(JahdooRarity rarity, ItemStack itemStack, AbstractElement element, boolean isUnique) {
        var damageAmplifierType = element.damageAmplifier();
        var damageAmplifierName = damageAmplifierType.getRegisteredName();
        var damageAmplifierValue = getPercentageTotal(isUnique ? 20 : 0, rarity.getAttributes().getRandomDamage());
        replaceOrAddAttribute(itemStack, damageAmplifierName, damageAmplifierType, damageAmplifierValue, MAINHAND, true, "wand");
    }

    public static ShoppingItems soldWands(@Nullable JahdooRarity rarity){
        var randomRarity = rarity == null ? getRarity() : rarity;
        var randomWand = getRandomWand(randomRarity, null);
        return switch (randomRarity.getId()){
            case 1 -> new ShoppingItems(randomWand, setSilverCost(20));
            case 2 -> new ShoppingItems(randomWand, setGoldCost(10));
            case 3 -> new ShoppingItems(randomWand, setGoldCost(80));
            case 4 -> new ShoppingItems(randomWand, setPlatinumCost(20));
            case 5 -> new ShoppingItems(randomWand, setPlatinumCost(60));
            default -> new ShoppingItems(randomWand, setBronzeCost(20));
        };
    }

    public static ItemStack getGauntletWithRarity(@Nullable ItemStack itemStack, @Nullable JahdooRarity getRarity){
        var rarity = getRarity == null ? getRarity() : getRarity;
        var newStack = itemStack == null ? new ItemStack(ItemReg.BATTLEMAGE_GAUNTLET) : itemStack;
        var isUnique = rarity.equals(UNIQUE);
        attachSharedProperties(newStack, isUnique ? 1 : 0, rarity, isUnique ? Random.nextInt(3, 6) : -1, 25, 25);
        if(isUnique) preInsertRunes(newStack);
        return newStack;
    }

    public static void attachSharedProperties(
        ItemStack itemStack,
        int runeSlots,
        @Nullable JahdooRarity getRarity,
        int repairSlots,
        double adjustPotential,
        double adjustDurability
    ){
        var rarity = getRarity == null ? getRarity() : getRarity;
        attachLootBeamComponent(itemStack, rarity);
        itemStack.set(JAHDOO_RARITY, rarity.getId());

        var potential = rarity.getAttributes().getRandomPotential();
        JahdooGearData.createNewRuneSlots(itemStack, runeSlots,  repairSlots == -1 ? JahdooRarity.getRarity().getId() + 1 : repairSlots, (int) getPercentageTotal(adjustPotential, potential));

        var durability = rarity.getAttributes().getRandomTime();
        attachDurability(itemStack, (int) getPercentageTotal(adjustDurability, durability));
    }

    public static ItemStack getShieldWithRarity(@Nullable ItemStack itemStack, @Nullable JahdooRarity getRarity){
        var rarity = getRarity == null ? getRarity() : getRarity;
        var newStack = itemStack == null ? new ItemStack(ItemReg.BASIC_SHIELD) : itemStack;
        attachSharedProperties(newStack, 0, rarity, -1, 0, 0);

        newStack.set(ComponentReg.SHIELD_BLOCK_CHANCE, rarity.getAttributes().getRandomDamage());

        return newStack;
    }
}
