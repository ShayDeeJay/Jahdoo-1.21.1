package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.block.wand_block_manager.WandManagerTableEntity;
import org.jahdoo.components.WandData;
import org.jahdoo.networking.packet.client2server.WandDataC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class AugmentCoreSlot extends SlotItemHandler {
    Item item;
    int maxStackSize;
    WandManagerTableEntity wandManagerTableEntity;
    boolean isActive = true;

    public AugmentCoreSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        Item item
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.item = item;
    }
    public void setActive(boolean active) {
        isActive = active;
    }



    public AugmentCoreSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        Item item,
        WandManagerTableEntity wandManagerTableEntity,
        int maxStackSize
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.item = item;
        this.wandManagerTableEntity = wandManagerTableEntity;
        this.maxStackSize = maxStackSize;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return maxStackSize == 0 ? 64 : maxStackSize;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return itemStack.is(this.item) && isActive;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public void setChanged() {
        if(this.wandManagerTableEntity != null){
            var getAllSlots = this.wandManagerTableEntity.getWandSlot();
            var getData = getAllSlots.get(WAND_DATA);
            if (getData != null) {
                var index = new AtomicInteger(4);
                var list = new ArrayList<ItemStack>();
                for (ItemStack ignored : getData.upgradeSlots()) {
                    list.add(this.wandManagerTableEntity.inputItemHandler.getStackInSlot(index.get()));
                    index.set(index.get() + 1);
                }
                getAllSlots.update(WAND_DATA.get(), WandData.DEFAULT, data -> data.setUpgradeSlots(list));
                var getData2 = getAllSlots.get(WAND_DATA);
                PacketDistributor.sendToServer(new WandDataC2SPacket(getData2, this.wandManagerTableEntity.getBlockPos()));
            }
        }
    }
}