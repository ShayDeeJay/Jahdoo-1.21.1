package org.jahdoo.common.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.block.chaos_cube.ChaosCubeMenu;
import org.jahdoo.common.block.divine_forge.RuneTableMenu;
import org.jahdoo.common.block.wand_manager.WandManagerMenu;

import java.util.function.Supplier;

public class MenuReg {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, JahdooMod.MOD_ID);

    public static final Supplier<MenuType<ChaosCubeMenu>> MODULAR_CHAOS_CUBE_MENU =
        registerMenuType(ChaosCubeMenu::new, "modular_chaos_cube");

    public static final Supplier<MenuType<WandManagerMenu>> WAND_MANAGER_MENU =
        registerMenuType(WandManagerMenu::new, "wand_manager_menu");

    public static final Supplier<MenuType<RuneTableMenu>> RUNE_TABLE_MENU =
        registerMenuType(RuneTableMenu::new, "rune_table_menu");

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(
        IContainerFactory<T> factory,
        String name
    ){
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

