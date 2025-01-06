package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.client.gui.AbstractInternalContainer;
import org.jahdoo.client.gui.block.AugmentCoreSlot;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.registers.MenusRegister;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AugmentModificationMenu extends AbstractInternalContainer {
    public int posX = -42;
    public int posY = 41;
    public int offSetX = 0;
    public int offSetY = 30;

    public AugmentModificationMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(MenusRegister.AUGMENT_MODIFICATION_MENU.get(), pContainerId, inv, extraData);
        this.addSlots();
    }

    public AugmentModificationMenu(int pContainerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenusRegister.AUGMENT_MODIFICATION_MENU.get(), pContainerId, inv, entity, data);
        this.addSlots();
    }

    public void addSlots() {
        this.addSlot(new AugmentCoreSlot(getAugmentEntity().inputItemHandler, 0, -1000, -1000, ItemsRegister.AUGMENT_CORE.get()));
        var spacer = new AtomicInteger();
        for (int i = 1; i < 4; i ++){
            this.addSlot(new AugmentCoreSlot(getAugmentEntity().inputItemHandler, i, posX, posY + spacer.get(), getCore().get(i-1)));
            spacer.set(spacer.get() + 28);
        }
    }

    public List<Item> getCore(){
        return List.of(
            ItemsRegister.AUGMENT_CORE.get(),
            ItemsRegister.ADVANCED_AUGMENT_CORE.get(),
            ItemsRegister.AUGMENT_HYPER_CORE.get()
        );
    }

    public AugmentModificationEntity getAugmentEntity(){
        if(this.blockEntity instanceof AugmentModificationEntity augmentModification) return augmentModification;
        return null;
    }

    @Override
    protected int getAllSlots() {
        return this.getAugmentEntity().setInputSlots();
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
        return BlocksRegister.AUGMENT_MODIFICATION_STATION.get();
    }



}
