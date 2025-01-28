package org.jahdoo.client.gui.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.items.runes.rune_data.RuneHolder;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.networking.packet.client2server.ItemInBlockC2SPacket;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.items.runes.rune_data.RuneData.RuneHelpers.getCostFromRune;
import static org.jahdoo.registers.DataComponentRegistry.*;

public class RuneSlot extends SlotItemHandler {
    Item item;
    int maxStackSize;
    AbstractBEInventory bEntity;
    boolean isActive = true;

    public RuneSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        Item item,
        AbstractBEInventory bEntity,
        int maxStackSize
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.item = item;
        this.bEntity = bEntity;
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
        if(itemStack.getItem() instanceof RuneItem && isActive && this.getItem().isEmpty()){
            var wandManager = bEntity;
            var cost = getCostFromRune(itemStack);
            if(wandManager != null){
//                var potential = WandData.potential(wandManager.inputItemHandler.getStackInSlot(0));
//                if(cost > potential) return false;
                var level = wandManager.getLevel();
                var pos = wandManager.getBlockPos();
                if(level != null && this.getItem().isEmpty()){
                    if(level.isClientSide) return false;
                    ModHelpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_INSERT_ITEM, 0.4F, 1.2F);
                    ModHelpers.getSoundWithPosition(level, pos, SoundEvents.APPLY_EFFECT_TRIAL_OMEN, 0.4F, 2F);
//                    WandData.createRefinementPotential(wandManager.inputItemHandler.getStackInSlot(0), potential - cost);
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public boolean isHighlightable() {
        return this.isActive && !this.getItem().isEmpty();
    }

    @Override
    public void setChanged() {
        if(this.bEntity != null){
            var getAllSlots = this.bEntity.inputItemHandler.getStackInSlot(0);
            var getData = getAllSlots.get(RUNE_HOLDER);
            if (getData != null) {
                var index = new AtomicInteger(4);
                var list = new ArrayList<ItemStack>();
                for (ItemStack ignored : getData.runeSlots()) {
                    list.add(this.bEntity.inputItemHandler.getStackInSlot(index.get()));
                    index.set(index.get() + 1);
                }
                RuneHolder.updateRuneSlots(getAllSlots, list);
                    serverBoundPacket(getAllSlots);
            }
        }
    }

    private void serverBoundPacket(ItemStack getAllSlots) {
        PacketDistributor.sendToServer(new ItemInBlockC2SPacket(getAllSlots, this.bEntity.getBlockPos()));
    }
}