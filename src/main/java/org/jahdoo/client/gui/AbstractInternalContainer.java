package org.jahdoo.client.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.registers.MenusRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public abstract class AbstractInternalContainer extends AbstractContainerMenu {

    protected static final int SLOTS_IN_ROW = 9;
    protected static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    protected static final int PLAYER_INVENTORY_SLOT_COUNT = SLOTS_IN_ROW * PLAYER_INVENTORY_ROW_COUNT;
    protected static final int VANILLA_SLOT_COUNT = SLOTS_IN_ROW + PLAYER_INVENTORY_SLOT_COUNT;
    protected static final int VANILLA_FIRST_SLOT_INDEX = 0;
    protected static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    protected final AbstractBEInventory blockEntity;
    protected final Level level;

    public AbstractInternalContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, (AbstractBEInventory) inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(0));
    }

    public AbstractInternalContainer(int pContainerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenusRegister.WAND_BLOCK_MENU.get(), pContainerId);
        this.blockEntity = entity;
        this.level = inv.player.level();
        int heightDiff = 55;
        this.addPlayerInventory(inv, heightDiff);
        this.addPlayerHotbar(inv, heightDiff);
        this.addDataSlots(data);
    }

    public int adjustInventoryY(){
        return 0;
    }

    public int adjustInventoryX(){
        return 0;
    }

    @Override
    public void initializeContents(int pStateId, List<ItemStack> pItems, ItemStack pCarried) {
        super.initializeContents(pStateId, pItems, pCarried);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        Slot sourceSlot = slots.get(slotIndex);
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM

        if (slotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + getAllSlots(), false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (slotIndex < TE_INVENTORY_FIRST_SLOT_INDEX + getAllSlots()) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) sourceSlot.set(ItemStack.EMPTY); else sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceStack);

        return copyOfSourceStack;
    }

    private void addPlayerInventory(Inventory playerInventory, int heightDiff) {
        for (int playerInvY = 0; playerInvY < 3; playerInvY++) {
            for (int playerInvX = 0; playerInvX < 9; playerInvX++) {
                this.addSlot(new Slot(playerInventory, playerInvX + playerInvY * 9 + 9, 8 + playerInvX * 18 + this.adjustInventoryX(), 84 + playerInvY * 18 + heightDiff + this.adjustInventoryY()));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory, int heightDiff) {
        for (int hotbarX = 0; hotbarX < 9; hotbarX++) {
            this.addSlot(new Slot(playerInventory, hotbarX, 8 + hotbarX * 18 + this.adjustInventoryX(), 142 + heightDiff + this.adjustInventoryY()));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, getAssociatedBlock());
    }

    protected abstract int getAllSlots();

    protected abstract Block getAssociatedBlock();
}
