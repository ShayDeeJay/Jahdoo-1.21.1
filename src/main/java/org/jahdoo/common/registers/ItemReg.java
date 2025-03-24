package org.jahdoo.common.registers;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.items.*;
import org.jahdoo.common.items.armor.mage.MageArmor;
import org.jahdoo.common.items.armor.wizard.WizardArmor;
import org.jahdoo.common.items.augments.Augment;
import org.jahdoo.common.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.common.items.block_items.LootChestBlockItem;
import org.jahdoo.common.items.gauntlet.BattlemageGauntlet;
import org.jahdoo.common.items.magnet.Magnet;
import org.jahdoo.common.items.pendent.Pendent;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.block_items.ModularChaosCubeItem;
import org.jahdoo.common.items.block_items.InfuserBlockItem;
import org.jahdoo.common.items.tome.TomeOfUnity;
import org.jahdoo.common.items.RecallToken;
import org.jahdoo.common.items.wand.subWands.*;

import java.util.function.Supplier;

import static net.minecraft.world.item.ArmorItem.Type.*;
import static org.jahdoo.common.registers.BlockReg.*;

public class ItemReg {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(Registries.ITEM, JahdooMod.MOD_ID);

    //Basic Items
    public static final DeferredHolder<Item, Item> AUGMENT_FRAGMENT =
        basicItem("augment_fragment");

    public static final DeferredHolder<Item, Item> NEXITE_POWDER =
        basicItem("nexite_powder");

    public static final DeferredHolder<Item, Item> AUGMENT_CORE =
        basicItem("augment_core");

    //Complex Items
    public static final DeferredHolder<Item, Item> RECALL_TOKEN =
        complexItem("recall_token", RecallToken::new);

    public static final DeferredHolder<Item, Item> RECOVERY_RECEIPT =
        complexItem("recovery_receipt", RecoveryReceipt::new);

    public static final DeferredHolder<Item, Item> CHALLENGER_TICKET =
        complexItem("challenger_ticket", ChallengerTicket::new);

    public static final DeferredHolder<Item, Item> COIN =
        complexItem("coin", CoinItem::new);

    public static final DeferredHolder<Item, Item> LOOT_KEY =
        complexItem("key", KeyItem::new);

    public static final DeferredHolder<Item, Item> ADVANCED_AUGMENT_CORE =
        complexItem("advanced_augment_core", CoreItem::new);

    public static final DeferredHolder<Item, Item> AUGMENT_HYPER_CORE =
        complexItem("augment_hyper_core", CoreItem::new);

    public static final DeferredHolder<Item, Item> AUGMENT =
        complexItem("unidentified_augment", Augment::new);

    public static final DeferredHolder<Item, Item> TOME_OF_UNITY =
        complexItem("tome_of_unity", TomeOfUnity::new);

    public static final DeferredHolder<Item, Item> BATTLEMAGE_GAUNTLET =
        complexItem("battlemage_gauntlet", BattlemageGauntlet::new);

    public static final DeferredHolder<Item, Item> RUNE =
        complexItem("rune", RuneItem::new);

    public static final DeferredHolder<Item, Item> INGMAS_SWORD =
        complexItem("ingmas_sword", IngmasSword::new);

    public static final DeferredHolder<Item, Item> ELEMENTAL_SWORD =
        complexItem("elemental_sword", ElementalSword::new);

    public static final DeferredHolder<Item, Item> ANCIENT_GLAIVE =
        complexItem("ancient_glaive", AncientGlaive::new);

    public static final DeferredHolder<Item, Item> PENDENT =
        complexItem("pendent", Pendent::new);

    public static final DeferredHolder<Item, Item> EXPERIENCE_ORB =
        complexItem("xp_orb", ExperienceOrb::new);

    public static final DeferredHolder<Item, Item> MAGNET =
        complexItem("magnet", Magnet::new);

    //Block Items
    public static final DeferredHolder<Item, Item> INFUSER_ITEM =
        complexItem("infuser", () -> new InfuserBlockItem(INFUSER.get()));

    public static final DeferredHolder<Item, Item> CHALLENGE_ALTAR_ITEM =
        complexItem("challenge_altar", () -> new ChallengeAltarBlockItem(CHALLENGE_ALTAR.get()));

    public static final DeferredHolder<Item, Item> LOOT_CHEST_ITEM =
        complexItem("loot_chest", () -> new LootChestBlockItem(LOOT_CHEST.get()));

    public static final DeferredHolder<Item, Item> MODULAR_CHAOS_CUBE_ITEM =
        complexItem("modular_chaos_cube", () -> new ModularChaosCubeItem(MODULAR_CHAOS_CUBE.get()));

    //Wands needed their own subclass as animations do not fire for all wand instances otherwise.
    //UPDATE: Above should now be fixed in latest update, should now be able
    public static final DeferredHolder<Item, Item> WAND_ITEM_MYSTIC =
        complexItem("wand_mystic", MysticWand::new);

    public static final DeferredHolder<Item, Item> WAND_ITEM_FROST =
        complexItem("wand_frost", FrostWand::new);

    public static final DeferredHolder<Item, Item> WAND_ITEM_INFERNO =
        complexItem("wand_inferno", InfernoWand::new);

    public static final DeferredHolder<Item, Item> WAND_ITEM_VITALITY =
        complexItem("wand_vitality", VitalityWand::new);

    public static final DeferredHolder<Item, Item> HEALTH_CONTAINER =
        complexItem("health_container", HealthContainer::new);

    public static final DeferredHolder<Item, Item> MANA_CONTAINER =
        complexItem("mana_container", ManaContainer::new);

    public static final DeferredHolder<Item, Item> BOON_CONTAINER =
        complexItem("boon_container", ManaContainer::new);

    //Wizard
    public static final DeferredHolder<Item, Item> WIZARD_HELMET =
        complexItem("wizard_helmet", () -> new WizardArmor(HELMET));

    public static final DeferredHolder<Item, Item> WIZARD_CHESTPLATE =
        complexItem("wizard_chestplate", () -> new WizardArmor(CHESTPLATE));

    public static final DeferredHolder<Item, Item> WIZARD_LEGGINGS =
        complexItem("wizard_leggings", () -> new WizardArmor(LEGGINGS));

    public static final DeferredHolder<Item, Item> WIZARD_BOOTS =
        complexItem("wizard_boots", () -> new WizardArmor(BOOTS));

    //Mage
    public static final DeferredHolder<Item, Item> MAGE_HELMET =
        complexItem("mage_helmet", () -> new MageArmor(HELMET));

    public static final DeferredHolder<Item, Item> MAGE_CHESTPLATE =
        complexItem("mage_chestplate", () -> new MageArmor(CHESTPLATE));

    public static final DeferredHolder<Item, Item> MAGE_LEGGINGS =
        complexItem("mage_leggings", () -> new MageArmor(LEGGINGS));

    public static final DeferredHolder<Item, Item> MAGE_BOOTS =
        complexItem("mage_boots", () -> new MageArmor(BOOTS));


    public static DeferredHolder<Item, Item> complexItem(String name, Supplier<? extends Item> sup){
        return ITEMS.register(name, sup);
    }

    public static DeferredHolder<Item, Item> basicItem(String name){
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
