package org.jahdoo.registers;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.items.*;
import org.jahdoo.items.armor.MageArmor;
import org.jahdoo.items.armor.WizardArmor;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.items.block_items.LootChestBlockItem;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.block_items.ModularChaosCubeItem;
import org.jahdoo.items.block_items.InfuserBlockItem;
import org.jahdoo.items.wand.subWands.*;

public class ItemsRegister {

    public static MobEffect effects;

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, JahdooMod.MOD_ID);

    //Basic Items
    public static final DeferredHolder<Item, Item> AUGMENT_FRAGMENT = basicItem("augment_fragment");
    public static final DeferredHolder<Item, Item> NEXITE_POWDER = basicItem("nexite_powder");
    public static final DeferredHolder<Item, Item> AUGMENT_CORE = basicItem("augment_core");
    public static final DeferredHolder<Item, Item> ADVANCED_AUGMENT_CORE = ITEMS.register("advanced_augment_core", CoreItem::new);
    public static final DeferredHolder<Item, Item> AUGMENT_HYPER_CORE = ITEMS.register("augment_hyper_core", CoreItem::new);

    //Complex Items
    public static final DeferredHolder<Item, Item> AUGMENT_ITEM = ITEMS.register("unidentified_augment", Augment::new);
    public static final DeferredHolder<Item, Item> TOME_OF_UNITY = ITEMS.register("tome_of_unity", TomeOfUnity::new);
    public static final DeferredHolder<Item, Item> ARCHMAGE_GAUNTLET = ITEMS.register("archmage_gauntlet", ArchmageGauntlet::new);
    public static final DeferredHolder<Item, Item> RUNE = ITEMS.register("rune", RuneItem::new);

    //Block Items
    public static final DeferredHolder<Item, Item> INFUSER_ITEM =
        ITEMS.register("infuser", () -> new InfuserBlockItem(BlocksRegister.INFUSER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> CHALLENGE_ALTAR_ITEM =
        ITEMS.register("challenge_altar", () -> new ChallengeAltarBlockItem(BlocksRegister.CHALLENGE_ALTAR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> LOOT_CHEST_ITEM =
        ITEMS.register("loot_chest", () -> new LootChestBlockItem(BlocksRegister.LOOT_CHEST.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> MODULAR_CHAOS_CUBE_ITEM =
        ITEMS.register("modular_chaos_cube", () -> new ModularChaosCubeItem(BlocksRegister.MODULAR_CHAOS_CUBE.get(), new Item.Properties()));

    //Wands needed their own subclass as animations do not fire for all wand instances otherwise.
    public static final DeferredHolder<Item, Item> WAND_ITEM_MYSTIC =
        ITEMS.register("wand_mystic", MysticWand::new);
    public static final DeferredHolder<Item, Item> WAND_ITEM_FROST =
        ITEMS.register("wand_frost", FrostWand::new);
    public static final DeferredHolder<Item, Item> WAND_ITEM_INFERNO =
        ITEMS.register("wand_inferno", InfernoWand::new);
    public static final DeferredHolder<Item, Item> WAND_ITEM_LIGHTNING =
        ITEMS.register("wand_lightning", LightningWand::new);
    public static final DeferredHolder<Item, Item> WAND_ITEM_VITALITY =
        ITEMS.register("wand_vitality", VitalityWand::new);
    public static final DeferredHolder<Item, Item> HEALTH_CONTAINER =
        ITEMS.register("health_container", () -> new HealthContainer(new Item.Properties()));

    //Armor
    //Wizard
    public static final DeferredHolder<Item, Item> WIZARD_HELMET =
        ITEMS.register("wizard_helmet", () -> new WizardArmor(ArmorMaterialRegistry.WIZARD, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredHolder<Item, Item> WIZARD_CHESTPLATE =
        ITEMS.register("wizard_chestplate", () -> new WizardArmor(ArmorMaterialRegistry.WIZARD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final DeferredHolder<Item, Item> WIZARD_LEGGINGS =
        ITEMS.register("wizard_leggings", () -> new WizardArmor(ArmorMaterialRegistry.WIZARD, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredHolder<Item, Item> WIZARD_BOOTS =
        ITEMS.register("wizard_boots", () -> new WizardArmor(ArmorMaterialRegistry.WIZARD, ArmorItem.Type.BOOTS, new Item.Properties()));

    //Mage
    public static final DeferredHolder<Item, Item> MAGE_HELMET =
        ITEMS.register("mage_helmet", () -> new MageArmor(ArmorMaterialRegistry.MAGE, ArmorItem.Type.HELMET));
    public static final DeferredHolder<Item, Item> MAGE_CHESTPLATE =
        ITEMS.register("mage_chestplate", () -> new MageArmor(ArmorMaterialRegistry.MAGE, ArmorItem.Type.CHESTPLATE));
    public static final DeferredHolder<Item, Item> MAGE_LEGGINGS =
        ITEMS.register("mage_leggings", () -> new MageArmor(ArmorMaterialRegistry.MAGE, ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, Item> MAGE_BOOTS =
        ITEMS.register("mage_boots", () -> new MageArmor(ArmorMaterialRegistry.MAGE, ArmorItem.Type.BOOTS));

    //Coins
    public static final DeferredHolder<Item, Item> BRONZE_COIN =
        ITEMS.register("bronze_coin", () -> new CoinItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SILVER_COIN =
        ITEMS.register("silver_coin", () -> new CoinItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GOLD_COIN =
        ITEMS.register("gold_coin", () -> new CoinItem(new Item.Properties()));

    public static DeferredHolder<Item, Item> basicItem(String name){
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
