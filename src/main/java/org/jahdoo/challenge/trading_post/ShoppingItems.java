package org.jahdoo.challenge.trading_post;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.runes.rune_data.RuneHolder;
import org.jahdoo.items.wand.WandData;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.*;
import static org.jahdoo.challenge.LocalLootBeamData.attachLootBeamComponent;
import static org.jahdoo.challenge.trading_post.ShoppingArmor.enchantArmorItem;
import static org.jahdoo.challenge.trading_post.ShoppingArmor.getMageArmorPiece;
import static org.jahdoo.challenge.trading_post.ShoppingArmor.getWizardArmorPiece;
import static org.jahdoo.challenge.trading_post.ShoppingRunes.*;
import static org.jahdoo.challenge.trading_post.ShoppingRunes.getEternalEliteRunes;
import static org.jahdoo.challenge.trading_post.ShoppingWeapon.enchantSword;
import static org.jahdoo.challenge.trading_post.ShoppingWeapon.getElementalSword;
import static org.jahdoo.items.runes.rune_data.RuneData.RuneHelpers.generateFullRune;
import static org.jahdoo.registers.AttributesRegister.*;
import static org.jahdoo.registers.DataComponentRegistry.JAHDOO_RARITY;
import static org.jahdoo.registers.ElementRegistry.getRandomElement;
import static org.jahdoo.utils.Maths.singleFormattedDouble;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.ModHelpers.getRandomListElement;

public record ShoppingItems(ItemStack ShoppingItem, ItemCosts itemCosts){

    public ShoppingItems shoppingWandItem(){
        var element = ElementRegistry.getRandomElement();
        var randomWand = element.getWand();
        var wand = randomWand != null ? randomWand : ItemsRegister.WAND_ITEM_FROST.get();
        var itemStack = new ItemStack(wand);
        var rarity = JahdooRarity.ETERNAL;
        var runeSlots = Random.nextInt(4, 7);
        var refinementPotential = Random.nextInt(300, 500);

        var cooldownReductionType = element.getTypeCooldownReduction();
        var cooldownReductionName = cooldownReductionType.getRegisteredName();
        var cooldownReductionValue = rarity.getAttributes().getRandomCooldown() * Random.nextDouble(0.0, 1.5);

        var manaReductionType = element.getTypeManaReduction();
        var manaReductionName = manaReductionType.getRegisteredName();
        var randomManaReduction = rarity.getAttributes().getRandomManaReduction();
        var manaReductionValue = randomManaReduction + randomManaReduction * Random.nextDouble(0.0, 1.5);

        var damageAmplifierType = element.getDamageTypeAmplifier();
        var damageAmplifierName = damageAmplifierType.getRegisteredName();
        var randomDamage = rarity.getAttributes().getRandomDamage();
        var damageAmplifierValue = randomDamage + randomDamage * Random.nextDouble(0.0, 1.5);

        attachLootBeamComponent(itemStack, rarity);
        WandData.createRarity(itemStack, rarity.getId());
        WandData.createNewAbilitySlots(itemStack, 10);
        RuneHolder.createNewRuneSlots(itemStack, runeSlots, refinementPotential);
        itemStack.set(JAHDOO_RARITY, rarity.getId());

        replaceOrAddAttribute(itemStack, cooldownReductionName, cooldownReductionType, cooldownReductionValue, EquipmentSlot.MAINHAND, false);
        replaceOrAddAttribute(itemStack, manaReductionName, manaReductionType, manaReductionValue, EquipmentSlot.MAINHAND, false);
        replaceOrAddAttribute(itemStack, damageAmplifierName, damageAmplifierType, damageAmplifierValue, EquipmentSlot.MAINHAND, false);

        return new ShoppingItems(itemStack, ItemCosts.getPlatinumCost(200));
    }

    public static ShoppingItems shoppingRuneItem(){

        var getElement = getRandomElement();
        var rarity = JahdooRarity.ETERNAL;
        var attributes = rarity.getAttributes();
        var id = rarity.getId();
        var getAll = List.of(
            Pair.of(getMidRangeEliteRunes(getElement, attributes, id), ItemCosts.getGoldCost(150)),
            Pair.of(getBetterRangeEliteRunes(attributes, id), ItemCosts.getGoldCost(300)),
            Pair.of(getLegendaryRangeEliteRunes(attributes, id), ItemCosts.getPlatinumCost(150)),
            Pair.of(getEternalEliteRunes(attributes, id), ItemCosts.getGoldCost(300))
        );
        var stack = new ItemStack(ItemsRegister.RUNE.get());
        var getRandomRune = getRandomListElement(getAll);

        generateFullRune(stack, getRandomRune.getFirst());
        return new ShoppingItems(stack, getRandomRune.getSecond());
    }

    public static ShoppingItems shoppingTomeItem(JahdooRarity rarity){
        var itemStack = new ItemStack(ItemsRegister.TOME_OF_UNITY);
        var randomRegenValue = singleFormattedDouble(rarity.getAttributes().getRandomManaRegen());
        var getRegen = randomRegenValue + randomRegenValue * Random.nextDouble(0.0, 1.5);
        var randomManaPool = singleFormattedDouble(rarity.getAttributes().getRandomManaPool());
        var getMana = randomManaPool + randomManaPool * Random.nextDouble(0.0, 1.5);
        var manaRegen = MANA_REGEN;
        var manaPool = MANA_POOL;

        attachLootBeamComponent(itemStack, rarity);
        itemStack.set(DataComponentRegistry.JAHDOO_RARITY.get(), rarity.getId());
        CuriosApi.addModifier(itemStack, manaRegen, manaRegen.getId(), getRegen, Operation.ADD_VALUE, "tome");
        CuriosApi.addModifier(itemStack, manaPool, manaPool.getId(), getMana, Operation.ADD_VALUE, "tome");
        return new ShoppingItems(itemStack, ItemCosts.getGoldCost(180));
    }

    public static ShoppingItems shoppingAmuletItem(){
        var itemStack = new ItemStack(ItemsRegister.PENDENT);
        var refinement = Random.nextInt(300, 500);

        itemStack.set(DataComponentRegistry.RUNE_HOLDER, RuneHolder.makeRuneSlots(4, refinement));
        return new ShoppingItems(itemStack, ItemCosts.getGoldCost(100));
    }

    public static ShoppingItems shoppingArmorItem(ServerLevel serverLevel) {
        var getRandomArmor = getRandomListElement(List.of(getMageArmorPiece(), getWizardArmorPiece()));

        if (getRandomArmor.getItem() instanceof ArmorItem armorItem) {
            enchantArmorItem(serverLevel, getRandomArmor, armorItem, true);
        }
        return new ShoppingItems(getRandomArmor, ItemCosts.getGoldCost(110));
    }

    public static ShoppingItems shoppingSwordItem(ServerLevel serverLevel) {
        var getRandomArmor = getRandomListElement(
                List.of(getElementalSword(),
                new ItemStack(ItemsRegister.ANCIENT_GLAIVE),
                new ItemStack(ItemsRegister.INGMAS_SWORD))
        );

        enchantSword(serverLevel, getRandomArmor, true);
        return new ShoppingItems(getRandomArmor, ItemCosts.getGoldCost(110));
    }
}
