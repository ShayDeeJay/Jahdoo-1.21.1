package org.jahdoo.common.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jahdoo.common.client.slots.InventorySlots;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

public abstract class AbstractInternalContainer extends AbstractContainerMenu {

    protected static final int SLOTS_IN_ROW = 9;
    protected static final int INV_ROWS = 3;
    protected static final int INV_SIZE = SLOTS_IN_ROW * INV_ROWS;
    protected static final int SLOT_SIZE = SLOTS_IN_ROW + INV_SIZE;
    protected static final int SLOT_A = 0;
    protected static final int INV_SLOT_A = SLOT_A + SLOT_SIZE;

    protected final AbstractBEInventory blockEntity;
    protected final Level level;

    public AbstractInternalContainer(MenuType<?> menuType, int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(
            menuType, containerId, inv, (AbstractBEInventory) inv.player.level().getBlockEntity(extraData.readBlockPos()),
            new SimpleContainerData(0)
        );
    }

    public AbstractInternalContainer(MenuType<?> menuType, int containerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(menuType, containerId);
        int heightDiff = 55;

        this.blockEntity = entity;
        this.level = inv.player.level();
        this.addDataSlots(data);
        this.addPlayerInventory(inv, heightDiff);
        this.addPlayerHotbar(inv, heightDiff);
    }

    protected abstract int getAllSlots();

    protected abstract Block getAssociatedBlock();

    public int adjustInventoryY() {
        return -33;
    }

    public int adjustInventoryX() {
        return 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, getAssociatedBlock());
    }

    public void addPlayerHotbar(Inventory playerInventory, int heightDiff) {
        for (int hotbarX = 0; hotbarX < 9; hotbarX++) {
            this.addSlot(new InventorySlots(playerInventory, hotbarX, 8 + hotbarX * 18 + this.adjustInventoryX(), 142 + heightDiff + this.adjustInventoryY()));
        }
    }

    public void addPlayerInventory(Inventory playerInventory, int heightDiff) {
        for (int playerInvY = 0; playerInvY < 3; playerInvY++) {
            for (int playerInvX = 0; playerInvX < 9; playerInvX++) {
                this.addSlot(new InventorySlots(playerInventory, playerInvX + playerInvY * 9 + 9, 8 + playerInvX * 18 + this.adjustInventoryX(), 84 + playerInvY * 18 + heightDiff + this.adjustInventoryY()));
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        var vanilla = SLOT_A + SLOT_SIZE;
        var beInventory = INV_SLOT_A + getAllSlots();
        var sourceSlot = slots.get(slotIndex);
        var sourceStack = sourceSlot.getItem();
        var copyOfSourceStack = sourceStack.copy();
        var empty = ItemStack.EMPTY;

        if(!sourceSlot.hasItem()){
            return empty;
        } else if (slotIndex < vanilla) {
            if (!moveItemStackTo(sourceStack, INV_SLOT_A, beInventory, false)) return empty;
        } else if (slotIndex < beInventory) {
            if (!moveItemStackTo(sourceStack, SLOT_A, vanilla, false)) return empty;
        } else {
            return empty;
        }

        if (sourceStack.getCount() == 0) sourceSlot.set(empty); else sourceSlot.setChanged();
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }
}
