package org.jahdoo.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.client.gui.block.augment_modification_station.AugmentModificationMenu;
import org.jahdoo.client.gui.block.infusion_table.InfusionTableMenu;
import org.jahdoo.client.gui.block.modular_chaos_cube.ModularChaosCubeMenu;
import org.jahdoo.client.gui.block.wand_block.WandBlockMenu;
import org.jahdoo.client.gui.block.wand_manager_table.WandManagerMenu;

import java.util.function.Supplier;

public class MenusRegister {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, JahdooMod.MOD_ID);

    public static final Supplier<MenuType<InfusionTableMenu>> CRYSTAL_INFUSION_MENU =
        registerMenuType(InfusionTableMenu::new, "crystal_infusion_menu");

    public static final Supplier<MenuType<WandBlockMenu>> WAND_BLOCK_MENU =
        registerMenuType(WandBlockMenu::new, "wand_block_menu");

    public static final Supplier<MenuType<ModularChaosCubeMenu>> MODULAR_CHAOS_CUBE_MENU =
        registerMenuType(ModularChaosCubeMenu::new, "modular_chaos_cube");

    public static final Supplier<MenuType<AugmentModificationMenu>> AUGMENT_MODIFICATION_MENU =
        registerMenuType(AugmentModificationMenu::new, "augment_modification_menu");

    public static final Supplier<MenuType<WandManagerMenu>> WAND_MANAGER_MENU =
        registerMenuType(WandManagerMenu::new, "wand_manager_menu");

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

