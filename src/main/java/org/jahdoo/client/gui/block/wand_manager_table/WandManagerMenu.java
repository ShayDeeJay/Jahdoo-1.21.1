package org.jahdoo.client.gui.block.wand_manager_table;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.wand_block_manager.WandManagerTableEntity;
import org.jahdoo.client.gui.AbstractInternalContainer;
import org.jahdoo.client.gui.block.augment_modification_station.AugmentCoreSlot;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.registers.MenusRegister;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WandManagerMenu extends AbstractInternalContainer {
    public int posX = -42;
    public int posY = 41;
    public int offSetX = 0;
    public int offSetY = 30;

    public WandManagerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(MenusRegister.WAND_MANAGER_MENU.get(), pContainerId, inv, extraData);
        this.addSlots();
    }

    public WandManagerMenu(int pContainerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenusRegister.WAND_MANAGER_MENU.get(), pContainerId, inv, entity, data);
        this.addSlots();
    }

    public void addSlots() {
        this.addSlot(new AugmentCoreSlot(getWandManagerEntity().inputItemHandler, 0, -1000, -1000, ItemsRegister.AUGMENT_CORE.get()));
        var spacer = new AtomicInteger();
        for (int i = 1; i < 4; i ++){
            this.addSlot(new AugmentCoreSlot(getWandManagerEntity().inputItemHandler, i, posX, posY + spacer.get(), getCore().get(i-1)));
            spacer.set(spacer.get() + 30);
        }
    }

    public List<Item> getCore(){
        return List.of(
            ItemsRegister.AUGMENT_CORE.get(),
            ItemsRegister.ADVANCED_AUGMENT_CORE.get(),
            ItemsRegister.AUGMENT_HYPER_CORE.get()
        );
    }

    public WandManagerTableEntity getWandManagerEntity(){
        if(this.blockEntity instanceof WandManagerTableEntity augmentModification) return augmentModification;
        return null;
    }

    @Override
    protected int getAllSlots() {
        return this.getWandManagerEntity().setInputSlots();
    }

    @Override
    public void addPlayerInventory(Inventory playerInventory, int heightDiff) {
        super.addPlayerInventory(playerInventory, heightDiff);
    }

    @Override
    public void addPlayerHotbar(Inventory playerInventory, int heightDiff) {
        super.addPlayerHotbar(playerInventory, heightDiff);
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlocksRegister.WAND_MANAGER_TABLE.get();
    }



}
