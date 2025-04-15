package org.jahdoo.common.block.chaos_cube;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.client.AbstractInternalContainer;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.MenuReg;

public class ChaosCubeMenu extends AbstractInternalContainer {

    public static final int posX = 80;
    public static final int posY = 30;
    public int offSetX = 0;
    public int offSetY = 30;

    public ChaosCubeMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(MenuReg.MODULAR_CHAOS_CUBE_MENU.get(), id, inv, extraData);
    }

    public ChaosCubeMenu(int id, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenuReg.MODULAR_CHAOS_CUBE_MENU.get(), id, inv, entity, data);
    }

    @Override
    protected int getAllSlots() {
        return 0;
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlockReg.MODULAR_CHAOS_CUBE.get();
    }

    public ChaosCubeEntity getAutomationEntity(){
        if(this.blockEntity instanceof ChaosCubeEntity modularChaosCubeEntity) return modularChaosCubeEntity;
        return null;
    }

}
