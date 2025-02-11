package org.jahdoo.utils;

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
import org.jahdoo.items.runes.rune_data.RuneHolder;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;

import static net.minecraft.world.item.CreativeModeTabs.SPAWN_EGGS;


public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JahdooMod.MOD_ID);
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TAB.register(
        "jahdoo_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemsRegister.AUGMENT_ITEM.get()))
            .title(Component.translatable("creative_tab.jahdoo_tab"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(BlocksRegister.NEXITE_ORE.get());
                    pOutput.accept(BlocksRegister.NEXITE_DEEPSLATE_ORE.get());
                    pOutput.accept(BlocksRegister.NEXITE_BLOCK.get());
                    pOutput.accept(BlocksRegister.RAW_NEXITE_BLOCK.get());
                    pOutput.accept(ItemsRegister.MODULAR_CHAOS_CUBE_ITEM.get());
                    pOutput.accept(BlocksRegister.TANK.get());
                    pOutput.accept(BlocksRegister.CREATOR.get());
                    pOutput.accept(BlocksRegister.WAND_MANAGER_TABLE.get());
                    pOutput.accept(BlocksRegister.AUGMENT_MODIFICATION_STATION.get());
                    pOutput.accept(ItemsRegister.INFUSER_ITEM.get());
                    pOutput.accept(ItemsRegister.NEXITE_POWDER.get());
                    pOutput.accept(ItemsRegister.INGMAS_SWORD.get());

                    registerElementalSwords(pOutput);
                    registerAmulets(pOutput);
                    registerXpOrbs(pOutput);
                    registerMagnets(pOutput);

                    pOutput.accept(ItemsRegister.ANCIENT_GLAIVE.get());
                    pOutput.accept(ItemsRegister.AUGMENT_ITEM.get());
                    pOutput.accept(ItemsRegister.RUNE.get());
                    pOutput.accept(ItemsRegister.AUGMENT_CORE.get());
                    pOutput.accept(ItemsRegister.ADVANCED_AUGMENT_CORE.get());
                    pOutput.accept(ItemsRegister.AUGMENT_HYPER_CORE.get());
                    pOutput.accept(ItemsRegister.TOME_OF_UNITY.get());
                    pOutput.accept(ItemsRegister.BATTLEMAGE_GAUNTLET.get());
                    pOutput.accept(ItemsRegister.BRONZE_COIN.get());
                    pOutput.accept(ItemsRegister.SILVER_COIN.get());
                    pOutput.accept(ItemsRegister.GOLD_COIN.get());
                    pOutput.accept(ItemsRegister.PLATINUM_COIN.get());
                    pOutput.accept(ItemsRegister.WIZARD_HELMET.get());
                    pOutput.accept(ItemsRegister.WIZARD_CHESTPLATE.get());
                    pOutput.accept(ItemsRegister.WIZARD_LEGGINGS.get());
                    pOutput.accept(ItemsRegister.WIZARD_BOOTS.get());
                    pOutput.accept(ItemsRegister.MAGE_HELMET.get());
                    pOutput.accept(ItemsRegister.MAGE_CHESTPLATE.get());
                    pOutput.accept(ItemsRegister.MAGE_LEGGINGS.get());
                    pOutput.accept(ItemsRegister.MAGE_BOOTS.get());
                    pOutput.accept(BlocksRegister.WAND_MANAGER_TABLE.get());
                    pOutput.accept(ItemsRegister.WAND_ITEM_MYSTIC.get());
                    pOutput.accept(ItemsRegister.WAND_ITEM_FROST.get());
                    pOutput.accept(ItemsRegister.WAND_ITEM_INFERNO.get());
                    pOutput.accept(ItemsRegister.WAND_ITEM_LIGHTNING.get());
                    pOutput.accept(ItemsRegister.WAND_ITEM_VITALITY.get());

            }
       ).withTabsBefore(SPAWN_EGGS)
        .build()
    );

    private static void registerElementalSwords(CreativeModeTab.Output pOutput) {
        pOutput.accept(ItemsRegister.ELEMENTAL_SWORD.get());
        
        for(int i = 1; i < 5; i++) {
            var elementalSword = new ItemStack(ItemsRegister.ELEMENTAL_SWORD.get());
            var customModelData = new CustomModelData(i);
            elementalSword.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            pOutput.accept(elementalSword);
        }
    }

    private static void registerAmulets(CreativeModeTab.Output pOutput) {
        var pendent = new ItemStack(ItemsRegister.PENDENT.get());
        RuneHolder.createNewRuneSlots(pendent, 1, 0);
        pOutput.accept(pendent);

        for (int i = 1; i < 4; i++){
            var basePendent = new ItemStack(ItemsRegister.PENDENT.get());
            var customModelData = new CustomModelData(i);
            basePendent.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            RuneHolder.createNewRuneSlots(basePendent, i+1, 0);
            pOutput.accept(basePendent);
        }
    }

    private static void registerXpOrbs(CreativeModeTab.Output pOutput) {
        pOutput.accept(ItemsRegister.EXPERIENCE_ORB.get());

        for(int i = 1; i < 3; i++) {
            var xpOrb = new ItemStack(ItemsRegister.EXPERIENCE_ORB.get());
            var customModelData = new CustomModelData(i);
            xpOrb.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            pOutput.accept(xpOrb);
        }
    }

    private static void registerMagnets(CreativeModeTab.Output pOutput) {
        pOutput.accept((ItemsRegister.MAGNET.get()));

        for(int i = 1; i < 5; i++) {
            var magnet= new ItemStack(ItemsRegister.MAGNET.get());
            var customModelData = new CustomModelData(i);
            magnet.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            pOutput.accept(magnet);
        }
    }

}
