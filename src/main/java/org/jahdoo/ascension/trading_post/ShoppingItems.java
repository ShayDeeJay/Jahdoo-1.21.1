package org.jahdoo.ascension.trading_post;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.RuneReg;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.minecraft.world.entity.EquipmentSlot.*;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.*;
import static org.jahdoo.ascension.rarity.JahdooRarity.*;
import static org.jahdoo.ascension.trading_post.RewardLootTables.magnetItem;
import static org.jahdoo.ascension.trading_post.ShoppingArmor.*;
import static org.jahdoo.ascension.trading_post.ShoppingWeapon.enchantSword;
import static org.jahdoo.ascension.trading_post.ShoppingWeapon.getElementalSword;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.Helpers.listRandom;
import static org.jahdoo.ascension.utils.LocalLootBeamData.attachLootBeamComponent;
import static org.jahdoo.ascension.utils.Maths.getPercentageTotal;
import static org.jahdoo.ascension.utils.Maths.singleFormattedDouble;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;

public record ShoppingItems(ItemStack ShoppingItem, CurrencyConverter itemCosts){

    public static ShoppingItems shoppingArmorItem(ServerLevel serverLevel) {
        var getRandomArmor = listRandom(List.of(getMageArmorPiece(), getWizardArmorPiece()));
        if (getRandomArmor.getItem() instanceof ArmorItem armorItem) {
            enchantArmorItem(serverLevel, getRandomArmor, armorItem, true);
        }

        return new ShoppingItems(getRandomArmor, setGoldCost(110));
    }

    public static ShoppingItems shoppingMagnetItem() {
        var getRarity = UNIQUE;
        var magnetStack = new ItemStack(ItemReg.MAGNET.get());
        var getMagnet = magnetItem(getRarity, magnetStack);

        addAttribute(getMagnet);
        return new ShoppingItems(getMagnet, setGoldCost(110));
    }

    public static ShoppingItems shoppingAmuletItem(){
        var itemStack = new ItemStack(ItemReg.PENDENT);
        var refinement = Random.nextInt(300, 500);

        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(3));
        itemStack.set(ComponentReg.RUNE_HOLDER, RuneHolder.makeRuneSlots(4, refinement));
        return new ShoppingItems(itemStack, setGoldCost(100));
    }

    public static ShoppingItems ingmasSword(ServerLevel serverLevel) {
        var ingmasSword = new ItemStack(ItemReg.INGMAS_SWORD);
        enchantSword(serverLevel, ingmasSword, true);
        return new ShoppingItems(ingmasSword, setGoldCost(110));
    }

    public static ShoppingItems glaive(ServerLevel serverLevel) {
        var glaive = new ItemStack(ItemReg.ANCIENT_GLAIVE);
        enchantSword(serverLevel, glaive, true);
        return new ShoppingItems(glaive, setGoldCost(110));
    }

    public static ShoppingItems elementalSword(ServerLevel serverLevel) {
        var meleeWeapons = getElementalSword();

        enchantSword(serverLevel, meleeWeapons, true);
        return new ShoppingItems(meleeWeapons, setGoldCost(110));
    }

    public static ShoppingItems getEliteShoppingItem(ServerLevel serverLevel){
        var items = List.of(
            soldWands(UNIQUE),
            shoppingTomeItem(),
            shoppingAmuletItem(),
            shoppingArmorItem(serverLevel),
            elementalSword(serverLevel),
            shoppingMagnetItem(),
            shoppingGauntletItem(),
            shoppingRuneItem()
        );

        return Helpers.listRandom(items);
    }

    private static void addAttribute(ItemStack itemStack) {
        var rarityAttribute = getRarity();
        var getTypeAttributes = RuneReg.getRuneWithRarity(rarityAttribute);
        getTypeAttributes.ifPresent(
            rune -> {
                var newRarity = getRarity();
                var gen = rune.runeGenerator(newRarity.getId(), newRarity.getAttributes());
                var delegate = gen.getType();
                var registeredName = delegate.getRegisteredName();

                var isPercentage = gen.getPercentage() > 0 ? (gen.getValue() * gen.getPercentage()) / 100 : gen.getValue();
                replaceOrAddAttribute(itemStack, registeredName, delegate, isPercentage, MAINHAND, true, "bonus");
            }
        );
    }

    public static ShoppingItems shoppingRuneItem(){
        var low = new ShoppingItems(RuneHelpers.generateRandomTypAttribute(ETERNAL, COMMON, RARE), setGoldCost(150));
        var med = new ShoppingItems(RuneHelpers.generateRandomTypAttribute(ETERNAL, EPIC), setGoldCost(300));
        var high = new ShoppingItems(RuneHelpers.generateRandomTypAttribute(ETERNAL, LEGENDARY), setPlatinumCost(150));
        var best = new ShoppingItems(RuneHelpers.generateRandomTypAttribute(ETERNAL, ETERNAL), setPlatinumCost((300)));
        var getAll = List.of(low, med, high, best);
        return Helpers.listRandom(getAll);
    }

    public static ShoppingItems shoppingGauntletItem(){
        var shoppingItem = new ItemStack(ItemReg.BATTLEMAGE_GAUNTLET);
        return new ShoppingItems(shoppingItem, setPlatinumCost((850)));
    }

    public static ShoppingItems shoppingTomeItem(){
        var rarityValues = ETERNAL;
        var itemStack = new ItemStack(ItemReg.TOME_OF_UNITY);
        var randomRegenValue = singleFormattedDouble(rarityValues.getAttributes().getRandomManaRegen());

        var getRegen = randomRegenValue + randomRegenValue * Random.nextDouble(0.0, 1.5);
        var randomManaPool = singleFormattedDouble(rarityValues.getAttributes().getRandomManaPool());

        var getMana = randomManaPool + randomManaPool * Random.nextDouble(0.0, 1.5);
        var rarity = UNIQUE;

        attachLootBeamComponent(itemStack, rarity);
        itemStack.set(JAHDOO_RARITY.get(), rarity.getId());

        replaceOrAddAttribute(itemStack, UUID.randomUUID().toString(), MANA_REGEN, getRegen, BODY, false, "");
        replaceOrAddAttribute(itemStack, UUID.randomUUID().toString(), MANA_POOL, getMana, BODY, false, "");
        return new ShoppingItems(itemStack, setGoldCost(180));
    }

    public static void attachDurability(ItemStack itemStack, int durability) {
        itemStack.set(DataComponents.MAX_DAMAGE, (int) (Math.round(durability /10.0) * 10));
        itemStack.set(DataComponents.DAMAGE, 0);
    }

    public static ItemStack getRandomWand(@Nullable JahdooRarity rarity, @Nullable ItemStack item, int chestRarity) {
        var itemStack = item == null ? new ItemStack(Objects.requireNonNull(ElementReg.random().getWand())) : item;
        createWandAttributes(rarity, itemStack, chestRarity);
        return itemStack;
    }

    public static void createWandAttributes(
        @Nullable JahdooRarity jahdooRarity,
        ItemStack itemStack,
        int chestRarity
    ) {
        var element = ElementReg.fromWand(itemStack.getItem()).orElseThrow();
        var rarity = jahdooRarity == null ? getRaritiesByChestRarity(chestRarity) : jahdooRarity;
        var isUnique = rarity == UNIQUE;
        var rarityId = rarity.getId();

        attachSharedProperties(itemStack, rarityId, rarity, isUnique ? 50 : 0);

        if(isUnique) addAttribute(itemStack);

        if(rarityId > 0){
            addDamageImplicit(rarity, itemStack, element, isUnique);
            if(rarityId > 1) addManaImplicit(rarity, itemStack, element, isUnique);
            if(rarityId > 2) addCooldownImplicit(rarity, itemStack, element, isUnique);
        }
    }

    public static void createTomeAttributes(ItemStack itemStack, int chestRarity){
        var rarity = getRaritiesByChestRarity(chestRarity);
        var randomRegenValue = singleFormattedDouble(rarity.getAttributes().getRandomManaRegen());
        var randomManaPool = singleFormattedDouble(rarity.getAttributes().getRandomManaPool());
        var manaRegen = MANA_REGEN;
        var manaPool = MANA_POOL;

        attachLootBeamComponent(itemStack, rarity);
        itemStack.set(ComponentReg.JAHDOO_RARITY.get(), rarity.getId());

        replaceOrAddAttribute(itemStack, manaRegen.getRegisteredName(), manaRegen, randomRegenValue, MAINHAND, false, "");
        replaceOrAddAttribute(itemStack, manaPool.getRegisteredName(), manaPool, randomManaPool, OFFHAND, false, "");
    }

    public static JahdooRarity getRaritiesByChestRarity(int chestRarity){
        var common = List.of(Pair.of(COMMON, 1), Pair.of(RARE, 5000));
        var rare = List.of(Pair.of(COMMON, 1), Pair.of(RARE, 1000), Pair.of(EPIC, 5000));
        var legendary = List.of(Pair.of(RARE, 1), Pair.of(EPIC, 1000), Pair.of(LEGENDARY, 4000));
        var eternal = List.of(Pair.of(EPIC, 1), Pair.of(LEGENDARY, 2000), Pair.of(ETERNAL, 5000));
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
        var randomWand = getRandomWand(randomRarity, null, 0);
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
        attachSharedProperties(newStack, 0, rarity, 0);

        return newStack;
    }

    public static void attachSharedProperties(ItemStack itemStack, int runeSlots, @Nullable JahdooRarity getRarity, double uniqueMultiplier){
        var rarity = getRarity == null ? getRarity() : getRarity;
        attachLootBeamComponent(itemStack, rarity);
        itemStack.set(JAHDOO_RARITY, rarity.getId());

        var potential = rarity.getAttributes().getRandomPotential();
        RuneHolder.createNewRuneSlots(itemStack, runeSlots, (int) getPercentageTotal(uniqueMultiplier, potential));

        var durability = rarity.getAttributes().getRandomTime();
        attachDurability(itemStack, (int) getPercentageTotal(uniqueMultiplier, durability));
    }

    public static ItemStack getShieldWithRarity(@Nullable ItemStack itemStack, @Nullable JahdooRarity getRarity){
        var rarity = getRarity == null ? getRarity() : getRarity;
        var newStack = itemStack == null ? new ItemStack(ItemReg.BASIC_SHIELD) : itemStack;
        attachSharedProperties(newStack, 0, rarity, 0);

        newStack.set(ComponentReg.SHIELD_BLOCK_CHANCE, rarity.getAttributes().getRandomDamage());

        return newStack;
    }
}
