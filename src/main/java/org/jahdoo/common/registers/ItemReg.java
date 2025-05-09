package org.jahdoo.common.registers;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.items.*;
import org.jahdoo.common.items.armor.ancient_golem_armor.AncientGolemArmor;
import org.jahdoo.common.items.armor.battle_mage.BattleMageArmor;
import org.jahdoo.common.items.armor.knight_king.KnightKingArmor;
import org.jahdoo.common.items.armor.mage.MageArmor;
import org.jahdoo.common.items.armor.wizard.WizardArmor;
import org.jahdoo.common.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.common.items.block_items.DisassemblerBlockItem;
import org.jahdoo.common.items.block_items.LootChestBlockItem;
import org.jahdoo.common.items.block_items.ModularChaosCubeItem;
import org.jahdoo.common.items.caster_item.basic_wand.StarterWand;
import org.jahdoo.common.items.caster_item.elemental_wand.FrostWand;
import org.jahdoo.common.items.caster_item.elemental_wand.InfernoWand;
import org.jahdoo.common.items.caster_item.elemental_wand.MysticWand;
import org.jahdoo.common.items.caster_item.elemental_wand.VitalityWand;
import org.jahdoo.common.items.gauntlet.BattlemageGauntlet;
import org.jahdoo.common.items.magnet.Magnet;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.shields.JahdooShieldItem;
import org.jahdoo.common.items.tome.TomeOfUnity;
import org.jahdoo.common.items.weapon.AncientGlaive;
import org.jahdoo.common.items.weapon.ElementalSword;
import org.jahdoo.common.items.weapon.IngmasSword;

import java.util.function.Supplier;

import static net.minecraft.world.item.ArmorItem.Type.*;
import static org.jahdoo.common.registers.BlockReg.*;
import static org.jahdoo.common.registers.ComponentReg.*;

public class ItemReg {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(Registries.ITEM, JahdooMod.MOD_ID);

    //Basic Items
    public static final DeferredHolder<Item, Item> ESSENCE_FRAGMENT =
        basicItem("essence_fragment");

    public static final DeferredHolder<Item, Item> NEXITE_POWDER =
        basicItem("nexite_powder");

    public static final DeferredHolder<Item, Item> CHAMPIONS_CROWN =
        basicItem("champions_crown");

    //Complex Items
    public static final DeferredHolder<Item, Item> INFERNO_AUGMENT =
        complexItem("inferno_augment", AbilityAugment::new);

    public static final DeferredHolder<Item, Item> FROST_AUGMENT =
        complexItem("frost_augment", AbilityAugment::new);

    public static final DeferredHolder<Item, Item> VITALITY_AUGMENT =
        complexItem("vitality_augment", AbilityAugment::new);

    public static final DeferredHolder<Item, Item> MYSTIC_AUGMENT =
        complexItem("mystic_augment", AbilityAugment::new);

    public static final DeferredHolder<Item, Item> SKILL_POINT =
        complexItem("skill_point", SkillPointItem::new);

    public static final DeferredHolder<Item, Item> CARE_PACKAGE =
        complexItem("starter_pack", StarterPack::new);

    public static final DeferredHolder<Item, Item> RECOVERY_RECEIPT =
        complexItem("recovery_receipt", RecoveryReceipt::new);

    public static final DeferredHolder<Item, Item> CHALLENGER_TICKET =
        complexItem("challenger_ticket", ChallengerTicket::new);

    public static final DeferredHolder<Item, Item> COIN =
        complexItem("coin", CoinItem::new);

    public static final DeferredHolder<Item, Item> LOOT_KEY =
        complexItem("key", KeyItem::new);

    public static final DeferredHolder<Item, Item> EXIT_KEY =
        complexItem("exit_key", KeyItem::new);

    public static final DeferredHolder<Item, Item> AUGMENT_CORE =
        complexItem("augment_core", () -> new CoreItem(new Item.Properties().component(CORE_DATA, new CoreData(50, 0))));

    public static final DeferredHolder<Item, Item> ADVANCED_AUGMENT_CORE =
        complexItem("advanced_augment_core", () -> new CoreItem(new Item.Properties().component(CORE_DATA, new CoreData(150, 0))));

    public static final DeferredHolder<Item, Item> AUGMENT_HYPER_CORE =
        complexItem("augment_hyper_core", () -> new CoreItem(new Item.Properties().component(CORE_DATA, new CoreData(500, 0))));

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

    public static final DeferredHolder<Item, Item> EXPERIENCE_ORB =
        complexItem("xp_orb", ExperienceOrb::new);

    public static final DeferredHolder<Item, Item> MAGNET =
        complexItem("magnet", Magnet::new);

    public static final DeferredHolder<Item, Item> BASIC_SHIELD =
        complexItem("basic_shield", JahdooShieldItem::new);

    public static final DeferredHolder<Item, Item> UNDEAD_PROTECTOR =
        complexItem("undead_protector", JahdooShieldItem::new);

    public static final DeferredHolder<Item, Item> COIN_SACK =
        complexItem("coin_sack", CoinSack::new);

    public static final DeferredHolder<Item, Item> STONE_OF_REGRET =
        complexItem("stone_of_regret", StoneOfRegret::new);

    //Block Items
    public static final DeferredHolder<Item, Item> DISASSEMBLER_ITEM =
        complexItem("disassembler", () -> new DisassemblerBlockItem(DISSEMBLER.get()));

    public static final DeferredHolder<Item, Item> CHALLENGE_ALTAR_ITEM =
        complexItem("challenge_altar", () -> new ChallengeAltarBlockItem(CHALLENGE_ALTAR.get()));

    public static final DeferredHolder<Item, Item> LOOT_CHEST_ITEM =
        complexItem("loot_chest", () -> new LootChestBlockItem(LOOT_CHEST.get()));

    public static final DeferredHolder<Item, Item> MODULAR_CHAOS_CUBE_ITEM =
        complexItem("modular_chaos_cube", () -> new ModularChaosCubeItem(MODULAR_CHAOS_CUBE.get()));

    //Wands needed their own subclass as animations do not fire for all wand instances otherwise.
    //UPDATE: Above should now be fixed in latest update, should now be able
    public static final DeferredHolder<Item, Item> STARTER_WAND =
        complexItem("wand_basic", StarterWand::new);

    public static final DeferredHolder<Item, Item> WAND_ITEM_MYSTIC =
        complexItem("wand_mystic", MysticWand::new);

    public static final DeferredHolder<Item, Item> WAND_ITEM_FROST =
        complexItem("wand_frost", FrostWand::new);

    public static final DeferredHolder<Item, Item> WAND_ITEM_INFERNO =
        complexItem("wand_inferno", InfernoWand::new);

    public static final DeferredHolder<Item, Item> WAND_ITEM_VITALITY =
        complexItem("wand_vitality", VitalityWand::new);

    public static final DeferredHolder<Item, Item> HEALTH_CONTAINER =
        complexItem("health_perk", HealthContainer::new);

    public static final DeferredHolder<Item, Item> MANA_CONTAINER =
        complexItem("mana_perk", ManaContainer::new);

    public static final DeferredHolder<Item, Item> BOON_CONTAINER =
        complexItem("boon_perk", ManaContainer::new);

    public static final DeferredHolder<Item, Item> QUEST_CONTAINER =
        complexItem("quest_perk", ManaContainer::new);

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

    //Battlemage
    public static final DeferredHolder<Item, Item> BATTLEMAGE_HELMET =
        complexItem("battlemage_helmet", () -> new BattleMageArmor(HELMET));

    public static final DeferredHolder<Item, Item> BATTLEMAGE_CHESTPLATE =
        complexItem("battlemage_chestplate", () -> new BattleMageArmor(CHESTPLATE));

    public static final DeferredHolder<Item, Item> BATTLEMAGE_LEGGINGS =
        complexItem("battlemage_leggings", () -> new BattleMageArmor(LEGGINGS));

    public static final DeferredHolder<Item, Item> BATTLEMAGE_BOOTS =
        complexItem("battlemage_boots", () -> new BattleMageArmor(BOOTS));

    //Knight King
    public static final DeferredHolder<Item, Item> KNIGHT_KING_HELMET =
        complexItem("knight_king_helmet", () -> new KnightKingArmor(HELMET));

    public static final DeferredHolder<Item, Item> KNIGHT_KING_CHESTPLATE =
        complexItem("knight_king_chestplate", () -> new KnightKingArmor(CHESTPLATE));

    public static final DeferredHolder<Item, Item> KNIGHT_KING_LEGGINGS =
        complexItem("knight_king_leggings", () -> new KnightKingArmor(LEGGINGS));

    public static final DeferredHolder<Item, Item> KNIGHT_KING_BOOTS =
        complexItem("knight_king_boots", () -> new KnightKingArmor(BOOTS));

    //Ancient Golem
    public static final DeferredHolder<Item, Item> ANCIENT_GOLEM_HELMET =
        complexItem("ancient_golem_helmet", () -> new AncientGolemArmor(HELMET));

    public static final DeferredHolder<Item, Item> ANCIENT_GOLEM_CHESTPLATE =
        complexItem("ancient_golem_chestplate", () -> new AncientGolemArmor(CHESTPLATE));

    public static final DeferredHolder<Item, Item> ANCIENT_GOLEM_LEGGINGS =
        complexItem("ancient_golem_leggings", () -> new AncientGolemArmor(LEGGINGS));

    public static final DeferredHolder<Item, Item> ANCIENT_GOLEM_BOOTS =
        complexItem("ancient_golem_boots", () -> new AncientGolemArmor(BOOTS));

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
