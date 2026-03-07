package org.jahdoo.common.registers;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.items.*;
import org.jahdoo.common.items.ability_augment.AugmentCrystal;
import org.jahdoo.common.items.armor.ancient_golem_armor.AncientGolemArmor;
import org.jahdoo.common.items.armor.battle_mage.BattleMageArmor;
import org.jahdoo.common.items.armor.knight_king.KnightKingArmor;
import org.jahdoo.common.items.armor.mage.MageArmor;
import org.jahdoo.common.items.armor.wizard.WizardArmor;
import org.jahdoo.common.items.block_items.*;
import org.jahdoo.common.items.caster_item.basic_wand.StarterWand;
import org.jahdoo.common.items.caster_item.elemental_wand.FrostWand;
import org.jahdoo.common.items.caster_item.elemental_wand.InfernoWand;
import org.jahdoo.common.items.caster_item.elemental_wand.MysticWand;
import org.jahdoo.common.items.caster_item.elemental_wand.VitalityWand;
import org.jahdoo.common.items.caster_item.staff.ElementalStaff;
import org.jahdoo.common.items.gauntlet.BattlemageGauntlet;
import org.jahdoo.common.items.magnet.Magnet;
import org.jahdoo.common.items.paxel.DivinityPaxel;
import org.jahdoo.common.items.perk_soda.PerkaSoda;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.shields.JahdooShieldItem;
import org.jahdoo.common.items.shields.UndeadProtectorShield;
import org.jahdoo.common.items.tome.TomeOfUnity;
import org.jahdoo.common.items.weapon.AncientGlaive;
import org.jahdoo.common.items.weapon.ElementalSword;
import org.jahdoo.common.items.weapon.IngmasSword;

import java.util.function.Supplier;

import static net.minecraft.world.item.ArmorItem.Type.*;
import static org.jahdoo.common.registers.BlockReg.*;

public class ItemReg {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(Registries.ITEM, JahdooMod.MOD_ID);

    //Basic Items
    public static final DeferredHolder<Item, Item> ESSENCE_FRAGMENT =
        basicItem("essence_fragment");

    public static final DeferredHolder<Item, Item> NEXITE_POWDER =
        basicItem("nexite_powder");

    public static final DeferredHolder<Item, Item> ROSE_QUARTZ =
        basicItem("rose_quartz");

    public static final DeferredHolder<Item, Item> ENCHANTED_DIAMOND =
        basicItem("enchanted_diamond");

    public static final DeferredHolder<Item, Item> ASTRINIUM_INGOT =
        basicItem("astrinium_ingot");

    public static final DeferredHolder<Item, Item> LISITE_SHARD =
        basicItem("lisite_shard");

    public static final DeferredHolder<Item, Item> CHAMPIONS_CROWN =
        basicItem("champions_crown");

    public static final DeferredHolder<Item, Item> CREATOR_TOP =
        basicItem("creator_top");

    public static final DeferredHolder<Item, Item> DICE =
        basicItem("dice");

    public static final DeferredHolder<Item, Item> NETHERITE_NUGGET =
        basicItem("netherite_nugget");

    public static final DeferredHolder<Item, Item> DIAMOND_NUGGET =
        basicItem("diamond_nugget");

    public static final DeferredHolder<Item, Item> GEAR_SCRAP =
        basicItem("gear_scrap");

    public static final DeferredHolder<Item, Item> CHALLENGER_SOUL =
        basicItem("challenger_soul");

    public static final DeferredHolder<Item, Item> POCKET_DIMENSION =
        complexItem("pocket_dimension", PocketDimension::new);

    public static final DeferredHolder<Item, Item> SEAL_OF_CHANGE =
        complexItem("seal", Seals::new);

    //Core Items
    public static final DeferredHolder<Item, Item> CHARGED_AUGMENT_CORE =
        chargedCoreItems("augment_core_filled");

    public static final DeferredHolder<Item, Item> CHARGED_ADVANCED_AUGMENT_CORE =
        chargedCoreItems("advanced_augment_core_filled");

    public static final DeferredHolder<Item, Item> CHARGED_AUGMENT_HYPER_CORE =
        chargedCoreItems("augment_hyper_core_filled");

    public static final DeferredHolder<Item, Item> AUGMENT_CORE =
        complexItem("augment_core", () -> new CoreItem(50));

    public static final DeferredHolder<Item, Item> ADVANCED_AUGMENT_CORE =
        complexItem("advanced_augment_core", () -> new CoreItem(150));

    public static final DeferredHolder<Item, Item> AUGMENT_HYPER_CORE =
        complexItem("augment_hyper_core", () -> new CoreItem(500));

    public static DeferredHolder<Item, Item> chargedCoreItems(String name){
        return complexItem(name, () -> new CoreItem(0));
    }

    //Keys
    public static final DeferredHolder<Item, Item> KEY_FRAGMENT =
        complexItem("key_piece", KeyItem::new);

    public static final DeferredHolder<Item, Item> EXIT_KEY =
        complexItem("exit_key", KeyItem::new);

    public static final DeferredHolder<Item, Item> BAZAAR_KEY =
        complexItem("bazaar_key", KeyItem::new);

    public static final DeferredHolder<Item, Item> SANCTUARY_KEY =
        complexItem("sanctuary_key", KeyItem::new);

    public static final DeferredHolder<Item, Item> CRYPT_KEY =
        complexItem("crypt_key", KeyItem::new);

    public static final DeferredHolder<Item, Item> CHALLENGER_KEY =
        complexItem("challenger_key", KeyItem::new);

    //Complex Items
    public static final DeferredHolder<Item, Item> PERKA_SODA =
        complexItem("perka_soda", PerkaSoda::new);

    public static final DeferredHolder<Item, Item> DIVINITY_PAXEL =
        complexItem("divinity_paxel", DivinityPaxel::new);

    public static final DeferredHolder<Item, Item> CARE_PACKAGE =
        complexItem("starter_pack", StarterPack::new);

    public static final DeferredHolder<Item, Item> RECOVERY_RECEIPT =
        complexItem("recovery_receipt", RecoveryReceipt::new);

    public static final DeferredHolder<Item, Item> TRIAL_TICKET =
        complexItem("trial_ticket", TrialNexusTicket::new);

    public static final DeferredHolder<Item, Item> COIN =
        complexItem("coin", CoinItem::new);

    public static final DeferredHolder<Item, Item> STAMP =
        complexItem("stamp", Stamp::new);

    public static final DeferredHolder<Item, Item> LOOT_KEY =
        complexItem("key", KeyItem::new);

    public static final DeferredHolder<Item, Item> TOME_OF_UNITY =
        complexItem("tome_of_unity", TomeOfUnity::new);

    public static final DeferredHolder<Item, Item> BATTLEMAGE_GAUNTLET =
        complexItem("battlemage_gauntlet", BattlemageGauntlet::new);

    public static final DeferredHolder<Item, Item> AUGMENT_CRYSTAL =
        complexItem("augment_crystal", AugmentCrystal::new);

    public static final DeferredHolder<Item, Item> RUNE =
        complexItem("rune", RuneItem::new);

    public static final DeferredHolder<Item, Item> INGMAS_SWORD =
        complexItem("ingmas_sword", IngmasSword::new);

    public static final DeferredHolder<Item, Item> ELEMENTAL_SWORD =
        complexItem("elemental_sword", ElementalSword::new);

    public static final DeferredHolder<Item, Item> ANCIENT_GLAIVE =
        complexItem("ancient_glaive", AncientGlaive::new);

    public static final DeferredHolder<Item, Item> DIM_EXPERIENCE_ORB =
        complexItem("dim_xp_orb", ExperienceOrb::new);

    public static final DeferredHolder<Item, Item> GLOWING_EXPERIENCE_ORB =
        complexItem("glowing_xp_orb", ExperienceOrb::new);

    public static final DeferredHolder<Item, Item> RADIANT_EXPERIENCE_ORB =
        complexItem("radiant_xp_orb", ExperienceOrb::new);

    public static final DeferredHolder<Item, Item> MAGNET =
        complexItem("magnet", Magnet::new);

    public static final DeferredHolder<Item, Item> OVERENCHANTED_BOOK =
        complexItem("overenchanted_book", OverenchantedBook::new);

    public static final DeferredHolder<Item, Item> BASIC_SHIELD =
        complexItem("basic_shield", JahdooShieldItem::new);

    public static final DeferredHolder<Item, Item> UNDEAD_PROTECTOR_SHIELD =
        complexItem("undead_protector", UndeadProtectorShield::new);

    public static final DeferredHolder<Item, Item> COIN_SACK =
        complexItem("coin_sack", CoinSack::new);

    public static final DeferredHolder<Item, Item> SALAMANS_EYE =
        complexItem("salamans_eye", SalamansEye::new);

    //Block Items
    public static final DeferredHolder<Item, Item> DISASSEMBLER_ITEM =
        complexItem("disassembler", () -> new DisassemblerBlockItem(DISSEMBLER.get()));

    public static final DeferredHolder<Item, Item> CHALLENGE_ALTAR_ITEM =
        complexItem("challenge_altar", () -> new ChallengeAltarBlockItem(CHALLENGE_ALTAR.get()));

    public static final DeferredHolder<Item, Item> LOOT_CHEST_ITEM =
        complexItem("loot_chest", () -> new LootChestBlockItem(LOOT_CHEST.get()));

    public static final DeferredHolder<Item, Item> MODULAR_CHAOS_CUBE_ITEM =
        complexItem("modular_chaos_cube", () -> new ModularChaosCubeItem(MODULAR_CHAOS_CUBE.get()));

    public static final DeferredHolder<Item, Item> MYSTICAL_AUGMENTER_ITEM =
        complexItem("mystical_augmenter", () -> new MysticalAugmenterItem(MYSTICAL_AUGMENTER.get()));

    //Wands needed their own subclass as animations do not fire for all wand instances otherwise.
    //UPDATE: Above should now be fixed in latest update, should now be able
    public static final DeferredHolder<Item, Item> ELEMENTAL_STAFF =
        complexItem("elemental_staff", ElementalStaff::new);

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

    //CURRENTLY HAS NO USE
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


    public static DeferredHolder<Item, Item> complexItem(String name, Supplier<? extends Item> sup){
        return ITEMS.register(name, sup);
    }

    public static DeferredHolder<Item, Item> basicItem(String name){
        return ITEMS.register(name, () -> new BaseJahdooItem(new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
