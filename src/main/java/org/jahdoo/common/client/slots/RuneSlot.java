package org.jahdoo.common.client.slots;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.networking.packet.client2server.ItemInBlockC2SPacket;
import org.jahdoo.ascension.utils.Helpers;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.items.runes.rune_data.RuneData.RuneHelpers.getCostFromRune;
import static org.jahdoo.common.registers.ComponentReg.*;

public class RuneSlot extends SlotItemHandler {

    private final int maxStackSize;
    private final AbstractBEInventory entity;
    private boolean isActive = true;

    public RuneSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        AbstractBEInventory entity,
        int maxStackSize
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.entity = entity;
        this.maxStackSize = maxStackSize;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return maxStackSize == 0 ? 64 : maxStackSize;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public boolean isHighlightable() {
        return this.isActive && !this.getItem().isEmpty();
    }

    private void serverBoundPacket(ItemStack getAllSlots) {
        PacketDistributor.sendToServer(new ItemInBlockC2SPacket(getAllSlots, this.entity.getBlockPos()));
    }

    @Override
    public void setChanged() {
        if(this.entity == null) return;
        var getAllSlots = this.entity.inputItemHandler.getStackInSlot(0);
        var getData = getAllSlots.get(RUNE_HOLDER);

        if (getData != null) {
            var index = new AtomicInteger(4);
            var list = new ArrayList<ItemStack>();
            for (ItemStack ignored : getData.runeSlots()) {
                list.add(this.entity.inputItemHandler.getStackInSlot(index.get()));
                index.set(index.get() + 1);
            }
            RuneHolder.updateRuneSlots(getAllSlots, list);
//            serverBoundPacket(getAllSlots);
        }
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        if(!(itemStack.getItem() instanceof RuneItem && isActive && this.getItem().isEmpty())) return false;

        var wandManager = entity;
        var cost = getCostFromRune(itemStack);

        if(wandManager == null) return false;
        var potential = RuneHolder.potential(wandManager.inputItemHandler.getStackInSlot(0));

        if(cost > potential) return false;
        var level = wandManager.getLevel();
        var pos = wandManager.getBlockPos();

        if(level == null || !this.getItem().isEmpty() || level.isClientSide) return false;

        Helpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_INSERT_ITEM, 0.4F, 1.2F);
        Helpers.getSoundWithPosition(level, pos, SoundEvents.APPLY_EFFECT_TRIAL_OMEN, 0.4F, 2F);
        RuneHolder.createRefinementPotential(wandManager.inputItemHandler.getStackInSlot(0), potential - cost);
        return true;
    }
}