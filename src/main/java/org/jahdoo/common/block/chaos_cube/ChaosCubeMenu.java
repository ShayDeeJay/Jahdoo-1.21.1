package org.jahdoo.common.block.chaos_cube;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.client.AbstractInternalContainer;
import org.jahdoo.common.client.slots.ModularChaosCubeSlot;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.MenuReg;

import static org.jahdoo.common.block.chaos_cube.ChaosCubeEntity.*;

public class ChaosCubeMenu extends AbstractInternalContainer {

    public static final int posX = 80;
    public static final int posY = 30;
    public int offSetX = 0;
    public int offSetY = 30;

    public ChaosCubeMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(MenuReg.MODULAR_CHAOS_CUBE_MENU.get(), id, inv, extraData);
        this.setSlot();
    }

    public ChaosCubeMenu(int id, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenuReg.MODULAR_CHAOS_CUBE_MENU.get(), id, inv, entity, data);
        this.setSlot();
    }

    @Override
    protected int getAllSlots() {
        return this.getAutomationEntity().setInputSlots();
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlockReg.MODULAR_CHAOS_CUBE.get();
    }

    private void setSlot(){
        this.addSlot(new ModularChaosCubeSlot(this.getAutomationEntity().inputItemHandler,AUGMENT_SLOT, posX, posY));
    }

    public ChaosCubeEntity getAutomationEntity(){
        if(this.blockEntity instanceof ChaosCubeEntity modularChaosCubeEntity) return modularChaosCubeEntity;
        return null;
    }

}
