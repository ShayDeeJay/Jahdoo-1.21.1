package org.jahdoo.ascension.trading_post;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.items.wand.WandData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.ascension.utils.Helpers;

import java.util.List;

import static net.minecraft.world.entity.EquipmentSlot.*;
import static org.jahdoo.ascension.LocalLootBeamData.attachLootBeamComponent;
import static org.jahdoo.ascension.RewardLootTables.magnetItem;
import static org.jahdoo.ascension.trading_post.ShoppingArmor.enchantArmorItem;
import static org.jahdoo.ascension.trading_post.ShoppingArmor.getMageArmorPiece;
import static org.jahdoo.ascension.trading_post.ShoppingArmor.getWizardArmorPiece;
import static org.jahdoo.ascension.trading_post.ShoppingRunes.*;
import static org.jahdoo.ascension.trading_post.ShoppingRunes.getEternalEliteRunes;
import static org.jahdoo.ascension.trading_post.ShoppingWeapon.enchantSword;
import static org.jahdoo.ascension.trading_post.ShoppingWeapon.getElementalSword;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.generateFullRune;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;
import static org.jahdoo.common.registers.ElementReg.*;
import static org.jahdoo.common.registers.ElementReg.random;
import static org.jahdoo.ascension.utils.Maths.singleFormattedDouble;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.Helpers.listRandom;

public record ShoppingItems(ItemStack ShoppingItem, ItemCosts itemCosts){

    public static ShoppingItems shoppingArmorItem(ServerLevel serverLevel) {
        var getRandomArmor = listRandom(List.of(getMageArmorPiece(), getWizardArmorPiece()));
        if (getRandomArmor.getItem() instanceof ArmorItem armorItem) {
            enchantArmorItem(serverLevel, getRandomArmor, armorItem, true);
        }

        return new ShoppingItems(getRandomArmor, ItemCosts.getGoldCost(110));
    }

    public static ShoppingItems shoppingMagnetItem() {
        var getRarity = JahdooRarity.UNIQUE;
        var magnetStack = new ItemStack(ItemReg.MAGNET.get());
        var getMagnet = magnetItem(getRarity, magnetStack);

        addAttribute(ElementReg.random(), getMagnet);
        return new ShoppingItems(getMagnet, ItemCosts.getGoldCost(110));
    }

    public static ShoppingItems shoppingAmuletItem(){
        var itemStack = new ItemStack(ItemReg.PENDENT);
        var refinement = Random.nextInt(300, 500);

        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(3));
        itemStack.set(ComponentReg.RUNE_HOLDER, RuneHolder.makeRuneSlots(4, refinement));
        return new ShoppingItems(itemStack, ItemCosts.getGoldCost(100));
    }

    public static ShoppingItems shoppingSwordItem(ServerLevel serverLevel) {
        var glaive = new ItemStack(ItemReg.ANCIENT_GLAIVE);
        var ingmasSword = new ItemStack(ItemReg.INGMAS_SWORD);
        var meleeWeapons = List.of(getElementalSword(), glaive, ingmasSword);
        var getRandomArmor = listRandom(meleeWeapons);

        enchantSword(serverLevel, getRandomArmor, true);
        return new ShoppingItems(getRandomArmor, ItemCosts.getGoldCost(110));
    }

    public static ShoppingItems getEliteShoppingItem(ServerLevel serverLevel){
        return switch (Random.nextInt(7)){
            case 1 -> shoppingWandItem();
            case 2 -> shoppingTomeItem();
            case 3 -> shoppingAmuletItem();
            case 4 -> shoppingArmorItem(serverLevel);
            case 5 -> shoppingSwordItem(serverLevel);
            case 6 -> shoppingMagnetItem();
            default -> shoppingRuneItem();
        };
    }

    private static void addAttribute(AbstractElement altElement, ItemStack itemStack) {
        var rarityAttribute = JahdooRarity.getRarity();
        var getTypeAttributes = getGetRune(altElement, rarityAttribute.getAttributes(), rarityAttribute.getId())
            .stream()
            .filter(rune -> rune.getType() != Attributes.MOVEMENT_SPEED)
            .toList();
        var entry = Helpers.listRandom(getTypeAttributes);
        var delegate = entry.getType();
        var registeredName = delegate.getRegisteredName();
        var second = entry.getValue();

        replaceOrAddAttribute(itemStack, registeredName, delegate, second, MAINHAND, false);
    }

    public static ShoppingItems shoppingRuneItem(){

        var getElement = random();
        var rarity = JahdooRarity.UNIQUE;
        var attributes = rarity.getAttributes();
        var id = JahdooRarity.ETERNAL.getId();
        var getAll = List.of(
            Pair.of(getMidRangeEliteRunes(getElement, attributes, id), ItemCosts.getGoldCost(150)),
            Pair.of(getBetterRangeEliteRunes(attributes, id), ItemCosts.getGoldCost(300)),
            Pair.of(getLegendaryRangeEliteRunes(attributes, id), ItemCosts.getPlatinumCost(150)),
            Pair.of(getEternalEliteRunes(attributes, id), ItemCosts.getPlatinumCost(300))
        );
        var stack = new ItemStack(ItemReg.RUNE.get());
        var getRandomRune = listRandom(getAll);

        generateFullRune(stack, getRandomRune.getFirst());
        return new ShoppingItems(stack, getRandomRune.getSecond());
    }

    public static ShoppingItems shoppingTomeItem(){
        var rarityValues = JahdooRarity.ETERNAL;
        var itemStack = new ItemStack(ItemReg.TOME_OF_UNITY);
        var randomRegenValue = singleFormattedDouble(rarityValues.getAttributes().getRandomManaRegen());
        var getRegen = randomRegenValue + randomRegenValue * Random.nextDouble(0.0, 1.5);
        var randomManaPool = singleFormattedDouble(rarityValues.getAttributes().getRandomManaPool());
        var getMana = randomManaPool + randomManaPool * Random.nextDouble(0.0, 1.5);
        var manaRegen = MANA_REGEN;
        var manaPool = MANA_POOL;
        var rarity = JahdooRarity.UNIQUE;

        attachLootBeamComponent(itemStack, rarity);
        itemStack.set(JAHDOO_RARITY.get(), rarity.getId());
        replaceOrAddAttribute(itemStack, manaRegen.getRegisteredName(), manaRegen, getRegen, EquipmentSlot.MAINHAND, false);
        replaceOrAddAttribute(itemStack, manaPool.getRegisteredName(), manaPool, getMana, EquipmentSlot.OFFHAND, false);
        return new ShoppingItems(itemStack, ItemCosts.getGoldCost(180));
    }

    public static ShoppingItems shoppingWandItem(){
        var element = random();
        var randomWand = element.getWand();
        var wand = randomWand != null ? randomWand : ItemReg.WAND_ITEM_FROST.get();
        var itemStack = new ItemStack(wand);
        var rarity = JahdooRarity.UNIQUE;
        var refinementPotential = Random.nextInt(300, 500);

        var cooldownReductionType = element.cooldownReduction();
        var cooldownReductionName = cooldownReductionType.getRegisteredName();
        var cooldownReductionValue = rarity.getAttributes().getRandomCooldown();

        var manaReductionType = element.manaReduction();
        var manaReductionName = manaReductionType.getRegisteredName();
        var manaReductionValue = rarity.getAttributes().getRandomManaReduction();

        var damageAmplifierType = element.damageAmplifier();
        var damageAmplifierName = damageAmplifierType.getRegisteredName();
        var damageAmplifierValue = rarity.getAttributes().getRandomDamage();

        attachLootBeamComponent(itemStack, rarity);
        WandData.createRarity(itemStack, rarity.getId());
        WandData.createNewAbilitySlots(itemStack, 10);
        RuneHolder.createNewRuneSlots(itemStack, 4, refinementPotential);
        itemStack.set(JAHDOO_RARITY, rarity.getId());

        replaceOrAddAttribute(itemStack, cooldownReductionName, cooldownReductionType, cooldownReductionValue, MAINHAND, false);
        replaceOrAddAttribute(itemStack, manaReductionName, manaReductionType, manaReductionValue, MAINHAND, false);
        replaceOrAddAttribute(itemStack, damageAmplifierName, damageAmplifierType, damageAmplifierValue, MAINHAND, false);

        var altElement = Helpers.listRandom(getWithout(element));
        addAttribute(altElement, itemStack);
        return new ShoppingItems(itemStack, ItemCosts.getPlatinumCost(200));
    }
}
