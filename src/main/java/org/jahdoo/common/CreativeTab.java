package org.jahdoo.common;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.items.magnet.MagnetData;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;

import static net.minecraft.world.item.CreativeModeTabs.SPAWN_EGGS;
import static org.jahdoo.common.registers.BlockReg.*;
import static org.jahdoo.common.registers.ItemReg.*;


public class CreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JahdooMod.MOD_ID);

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TAB.register(
        "jahdoo_tab",

        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(WAND_ITEM_MYSTIC.get()))
            .title(Component.translatable("creative_tab.jahdoo_tab"))
            .displayItems((parameters, outPut) -> {
                outPut.accept(NEXITE_POWDER.get());
                outPut.accept(NEXITE_ORE.get());
                outPut.accept(NEXITE_DEEPSLATE_ORE.get());
                outPut.accept(NEXITE_BLOCK.get());
                outPut.accept(RAW_NEXITE_BLOCK.get());

                outPut.accept(MODULAR_CHAOS_CUBE_ITEM.get());
                outPut.accept(TANK.get());
//                outPut.accept(WAND_MANAGER_TABLE.get());
                outPut.accept(RUNE_TABLE.get());
                outPut.accept(CREATOR_BLOCK.get());
                outPut.accept(DISASSEMBLER_ITEM.get());
                outPut.accept(TRIAL_TICKET.get());
                outPut.accept(EXIT_KEY.get());
                outPut.accept(STONE_OF_REGRET.get());


                registerRecoveryReceipts(outPut);
                registerCarePackages(outPut);

                registerElementalSwords(outPut);
                outPut.accept(ANCIENT_GLAIVE.get());
                outPut.accept(INGMAS_SWORD.get());
                outPut.accept(BASIC_SHIELD.get());
                outPut.accept(UNDEAD_PROTECTOR_SHIELD.get());

                registerXpOrbs(outPut);
                registerMagnets(outPut);
                registerCoins(outPut);
                outPut.accept(COIN_SACK.get());
                outPut.accept(EXIT_KEY.get());

                outPut.accept(RuneHelpers.generateBlankRune());
                outPut.accept(ESSENCE_FRAGMENT.get());
                outPut.accept(AUGMENT_CORE.get());
                outPut.accept(ADVANCED_AUGMENT_CORE.get());
                outPut.accept(AUGMENT_HYPER_CORE.get());
                outPut.accept(TOME_OF_UNITY.get());
                outPut.accept(BATTLEMAGE_GAUNTLET.get());

                outPut.accept(WIZARD_HELMET.get());
                outPut.accept(WIZARD_CHESTPLATE.get());
                outPut.accept(WIZARD_LEGGINGS.get());
                outPut.accept(WIZARD_BOOTS.get());

                outPut.accept(MAGE_HELMET.get());
                outPut.accept(MAGE_CHESTPLATE.get());
                outPut.accept(MAGE_LEGGINGS.get());
                outPut.accept(MAGE_BOOTS.get());

                outPut.accept(BATTLEMAGE_HELMET.get());
                outPut.accept(BATTLEMAGE_CHESTPLATE.get());
                outPut.accept(BATTLEMAGE_LEGGINGS.get());
                outPut.accept(BATTLEMAGE_BOOTS.get());

                outPut.accept(KNIGHT_KING_HELMET.get());
                outPut.accept(KNIGHT_KING_CHESTPLATE.get());
                outPut.accept(KNIGHT_KING_LEGGINGS.get());
                outPut.accept(KNIGHT_KING_BOOTS.get());

                outPut.accept(ANCIENT_GOLEM_HELMET.get());
                outPut.accept(ANCIENT_GOLEM_CHESTPLATE.get());
                outPut.accept(ANCIENT_GOLEM_LEGGINGS.get());
                outPut.accept(ANCIENT_GOLEM_BOOTS.get());

                outPut.accept(WAND_MANAGER_TABLE.get());
                outPut.accept(STARTER_WAND.get());
                outPut.accept(WAND_ITEM_MYSTIC.get());
                outPut.accept(WAND_ITEM_FROST.get());
                outPut.accept(WAND_ITEM_INFERNO.get());
                outPut.accept(WAND_ITEM_VITALITY.get());
            }
       ).withTabsBefore(SPAWN_EGGS)
        .build()
    );

    private static void registerElementalSwords(CreativeModeTab.Output pOutput) {
        pOutput.accept(ELEMENTAL_SWORD.get());

        for(int i = 1; i < 4; i++) {
            var elementalSword = new ItemStack(ELEMENTAL_SWORD.get());
            var customModelData = new CustomModelData(i);
            elementalSword.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            pOutput.accept(elementalSword);
        }
    }

    private static void registerRecoveryReceipts(CreativeModeTab.Output pOutput) {
        for(int i = 1; i < 4; i++) {
            var carePackage = new ItemStack(RECOVERY_RECEIPT.get());
            var customModelData = new CustomModelData(i);

            carePackage.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            pOutput.accept(carePackage);
        }
    }

    private static void registerCarePackages(CreativeModeTab.Output pOutput) {
        pOutput.accept(CARE_PACKAGE.get());

        for(int i = 1; i < 3; i++) {
            var carePackage = new ItemStack(CARE_PACKAGE.get());
            var customModelData = new CustomModelData(i);

            carePackage.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            pOutput.accept(carePackage);
        }
    }

    private static void registerCoins(CreativeModeTab.Output pOutput) {
        pOutput.accept(COIN.get());

        for(int i = 1; i < 4; i++) {
            var xpOrb = new ItemStack(COIN.get());
            var customModelData = new CustomModelData(i);

            xpOrb.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            pOutput.accept(xpOrb);
        }
    }

    private static void registerXpOrbs(CreativeModeTab.Output pOutput) {
        pOutput.accept(EXPERIENCE_ORB.get());

        for(int i = 1; i < 3; i++) {
            var xpOrb = new ItemStack(EXPERIENCE_ORB.get());
            var customModelData = new CustomModelData(i);

            xpOrb.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            pOutput.accept(xpOrb);
        }
    }

    private static void registerMagnets(CreativeModeTab.Output pOutput) {
        pOutput.accept((MAGNET.get()));

        for(int i = 1; i < 5; i++) {
            var magnet = new ItemStack(MAGNET.get());
            var customModelData = new CustomModelData(i);

            magnet.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            MagnetData.setDataByType(magnet);
            pOutput.accept(magnet);
        }
    }

}
