package org.jahdoo.common.client.slots;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.block.wand_manager.WandManagerEntity;
import org.jahdoo.common.networking.packet.client2server.ItemInBlockC2SPacket;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.items.runes.rune_data.RuneHolder.*;
import static org.jahdoo.common.registers.ComponentReg.*;

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

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return maxStackSize == 0 ? 64 : maxStackSize;
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
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        if(itemStack.is(this.item) && isActive){
            if(this.wandManagerTableEntity != null){
                var level = wandManagerTableEntity.getLevel();
                var pos = wandManagerTableEntity.getBlockPos();
                if(level != null && this.getItem().isEmpty()){
                    Helpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_INSERT_ITEM, 0.4F, 1.2F);
                    Helpers.getSoundWithPosition(level, pos, SoundEvents.APPLY_EFFECT_TRIAL_OMEN, 0.4F, 2F);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setChanged() {
        var entity = this.wandManagerTableEntity;
        if(entity == null) return;
        var getAllSlots = entity.getWandSlot();
        var getData = getAllSlots.get(RUNE_HOLDER);

        if (getData == null) return;
        var index = new AtomicInteger(4);
        var list = new ArrayList<ItemStack>();

        for (ItemStack ignored : getData.runeSlots()) {
            list.add(entity.inputItemHandler.getStackInSlot(index.get()));
            index.set(index.get() + 1);
        }

        getAllSlots.update(RUNE_HOLDER.get(), DEFAULT, data -> data.insertNewHolder(list));
        getAllSlots.set(WAND_DATA, getAllSlots.get(WAND_DATA));
        PacketDistributor.sendToServer(new ItemInBlockC2SPacket(getAllSlots, entity.getBlockPos()));
    }
}