package org.jahdoo.client.gui.block.modular_chaos_cube;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.client.gui.AbstractInternalContainer;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.MenusRegister;

import static org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity.*;

public class ModularChaosCubeMenu extends AbstractInternalContainer {
    public final int posX = 80;
    public final int posY = 30;
    public int offSetX = 0;
    public int offSetY = 30;

    public ModularChaosCubeMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(MenusRegister.MODULAR_CHAOS_CUBE_MENU.get(), pContainerId, inv, extraData);
        this.setSlot();
    }

    public ModularChaosCubeMenu(int pContainerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenusRegister.MODULAR_CHAOS_CUBE_MENU.get(), pContainerId, inv, entity, data);
        this.setSlot();
    }

    private void setSlot(){
        this.addSlot(new ModularChaosCubeSlot(this.getAutomationEntity().inputItemHandler,AUGMENT_SLOT, posX, posY));
    }

    public ModularChaosCubeEntity getAutomationEntity(){
        if(this.blockEntity instanceof ModularChaosCubeEntity modularChaosCubeEntity) return modularChaosCubeEntity;
        return null;
    }

    @Override
    protected int getAllSlots() {
        return this.getAutomationEntity().setInputSlots();
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlocksRegister.MODULAR_CHAOS_CUBE.get();
    }



}
