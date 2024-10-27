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
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;

import static net.minecraft.world.item.CreativeModeTabs.SPAWN_EGGS;


public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JahdooMod.MOD_ID);
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TAB.register(
        "jahdoo_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemsRegister.AUGMENT_CORE.get()))
            .title(Component.translatable("creativetab.jahdoo_tab"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(BlocksRegister.CRYSTAL_ORE.get());
                pOutput.accept(BlocksRegister.CRYSTAL_DEEPSLATE_ORE.get());
                pOutput.accept(ItemsRegister.JIDE_POWDER.get());
                pOutput.accept(ItemsRegister.AUGMENT_CORE.get());
                pOutput.accept(ItemsRegister.AUGMENT_FRAGMENT.get());
                pOutput.accept(ItemsRegister.TOME_OF_ETERNITY.get());
                pOutput.accept(ItemsRegister.AUGMENT_ITEM.get());
//                for(int i = 1; i < 7; i++) {
//                    ItemStack baseAugment = new ItemStack(ItemsRegister.AUGMENT_ITEM.get());
//                    CustomModelData customModelData = new CustomModelData(i);
//                    baseAugment.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
//                    pOutput.accept(baseAugment);
//                }

//                pOutput.accept(BlocksRegister.WAND_MANAGER_TABLE.get());
                pOutput.accept(BlocksRegister.TANK.get());
                pOutput.accept(BlocksRegister.CREATOR.get());
                pOutput.accept(ItemsRegister.INFUSER_ITEM.get());
                pOutput.accept(ItemsRegister.WAND_ITEM_MYSTIC.get());
                pOutput.accept(ItemsRegister.WAND_ITEM_FROST.get());
                pOutput.accept(ItemsRegister.WAND_ITEM_INFERNO.get());
                pOutput.accept(ItemsRegister.WAND_ITEM_LIGHTNING.get());
                pOutput.accept(ItemsRegister.WAND_ITEM_VITALITY.get());

            }
       ).withTabsBefore(SPAWN_EGGS)
        .build()
    );

}
