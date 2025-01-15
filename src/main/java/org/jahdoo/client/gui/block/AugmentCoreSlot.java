package org.jahdoo.client.gui.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.block.wand_block_manager.WandManagerEntity;
import org.jahdoo.items.runes.rune_data.RuneHolder;
import org.jahdoo.networking.packet.client2server.ItemInBlockC2SPacket;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.registers.DataComponentRegistry.*;

public class AugmentCoreSlot extends SlotItemHandler {
    Item item;
    int maxStackSize;
    WandManagerEntity wandManagerTableEntity;
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

    public AugmentCoreSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        Item item,
        WandManagerEntity wandManagerTableEntity,
        int maxStackSize
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.item = item;
        this.wandManagerTableEntity = wandManagerTableEntity;
        this.maxStackSize = maxStackSize;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return maxStackSize == 0 ? 64 : maxStackSize;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        if(itemStack.is(this.item) && isActive){
            if(this.wandManagerTableEntity != null){
                var level = wandManagerTableEntity.getLevel();
                var pos = wandManagerTableEntity.getBlockPos();
                if(level != null && this.getItem().isEmpty()){
                    ModHelpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_INSERT_ITEM, 0.4F, 1.2F);
                    ModHelpers.getSoundWithPosition(level, pos, SoundEvents.APPLY_EFFECT_TRIAL_OMEN, 0.4F, 2F);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public void set(ItemStack stack) {
        super.set(stack);
    }

    @Override
    public boolean isHighlightable() {
        return this.isActive && !this.getItem().isEmpty();
    }

    @Override
    public void setChanged() {
        if(this.wandManagerTableEntity != null){
            var getAllSlots = this.wandManagerTableEntity.getWandSlot();
            var getData = getAllSlots.get(RUNE_HOLDER);
            if (getData != null) {
                var index = new AtomicInteger(4);
                var list = new ArrayList<ItemStack>();
                for (ItemStack ignored : getData.runeSlots()) {
                    list.add(this.wandManagerTableEntity.inputItemHandler.getStackInSlot(index.get()));
                    index.set(index.get() + 1);
                }
                getAllSlots.update(RUNE_HOLDER.get(), RuneHolder.DEFAULT, data -> data.insertNewHolder(list));
//                getAllSlots.update(WAND_DATA.get(), WandData.DEFAULT, data -> data.setRuneSlots(list));
                var getData2 = getAllSlots.get(WAND_DATA);
                getAllSlots.set(WAND_DATA, getData2);
                PacketDistributor.sendToServer(new ItemInBlockC2SPacket(getAllSlots, this.wandManagerTableEntity.getBlockPos()));
            }
        }
    }
}