package org.jahdoo.common.block.augment_modification_station;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.client.gui.AbstractInternalContainer;
import org.jahdoo.common.client.slots.AugmentCoreSlot;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.MenuReg;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.client.SharedUI.getCore;

public class AugmentModificationMenu extends AbstractInternalContainer {

    public int posX = -42;
    public int posY = 41;
    public int offSetX = 0;
    public int offSetY = 30;

    public AugmentModificationMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(MenuReg.AUGMENT_MODIFICATION_MENU.get(), pContainerId, inv, extraData);
        this.addSlots();
    }

    public AugmentModificationMenu(int pContainerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenuReg.AUGMENT_MODIFICATION_MENU.get(), pContainerId, inv, entity, data);
        this.addSlots();
    }

    @Override
    protected int getAllSlots() {
        return this.getAugmentEntity().setInputSlots();
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlockReg.AUGMENT_MODIFICATION_STATION.get();
    }

    public AugmentModificationEntity getAugmentEntity(){
        if(this.blockEntity instanceof AugmentModificationEntity augmentEntity) return augmentEntity;
        return null;
    }

    public void addSlots() {
        this.addSlot(new AugmentCoreSlot(getAugmentEntity().inputItemHandler, 0, -1000, -1000, ItemReg.AUGMENT_CORE.get()));
        var spacer = new AtomicInteger();
        for (int i = 1; i < 4; i ++){
            this.addSlot(new AugmentCoreSlot(getAugmentEntity().inputItemHandler, i, posX, posY + spacer.get(), getCore().get(i-1)));
            spacer.set(spacer.get() + 28);
        }
    }

}
