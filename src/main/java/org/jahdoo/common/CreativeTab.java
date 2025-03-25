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
import org.jahdoo.common.items.runes.rune_data.RuneHolder;

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
            .icon(() -> new ItemStack(AUGMENT.get()))
            .title(Component.translatable("creative_tab.jahdoo_tab"))
            .displayItems((parameters, outPut) -> {
                outPut.accept(NEXITE_ORE.get());
                outPut.accept(NEXITE_DEEPSLATE_ORE.get());
                outPut.accept(NEXITE_BLOCK.get());
                outPut.accept(RAW_NEXITE_BLOCK.get());
                outPut.accept(MODULAR_CHAOS_CUBE_ITEM.get());
                outPut.accept(TANK.get());
                outPut.accept(WAND_MANAGER_TABLE.get());
                outPut.accept(AUGMENT_MODIFICATION_STATION.get());
                outPut.accept(INFUSER_ITEM.get());
                outPut.accept(NEXITE_POWDER.get());
                outPut.accept(INGMAS_SWORD.get());
                outPut.accept(RECALL_TOKEN.get());
                outPut.accept(CHALLENGER_TICKET.get());
                outPut.accept(RECOVERY_RECEIPT.get());
                
                registerCarePackages(outPut);
                registerElementalSwords(outPut);
                registerAmulets(outPut);
                registerXpOrbs(outPut);
                registerMagnets(outPut);
                registerCoins(outPut);

                outPut.accept(ANCIENT_GLAIVE.get());
                outPut.accept(AUGMENT.get());
                outPut.accept(RUNE.get());
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
                outPut.accept(WAND_MANAGER_TABLE.get());
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

    private static void registerAmulets(CreativeModeTab.Output pOutput) {
        var pendent = new ItemStack(PENDENT.get());
        RuneHolder.createNewRuneSlots(pendent, 1, 0);
        pOutput.accept(pendent);

        for (int i = 1; i < 4; i++){
            var basePendent = new ItemStack(PENDENT.get());
            var customModelData = new CustomModelData(i);

            basePendent.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            RuneHolder.createNewRuneSlots(basePendent, i+1, 0);
            pOutput.accept(basePendent);
        }
    }

}
