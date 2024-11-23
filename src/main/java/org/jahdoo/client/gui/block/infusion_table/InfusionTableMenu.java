package org.jahdoo.client.gui.block.infusion_table;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.block.wandBlockManager.WandManagerTableEntity;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.MenusRegister;

public class InfusionTableMenu extends AbstractContainerMenu {
    public final WandManagerTableEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final Container accept = new SimpleContainer(1);

    private static final int TOTAL_SLOTS = 7;  // must be the number of slots you have!

    public InfusionTableMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(TOTAL_SLOTS));
    }

    @Override
    public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        if(pSlotId == 43) {}
        super.clicked(pSlotId, pButton, pClickType, pPlayer);
    }


    public InfusionTableMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        // Call superclass constructor with the specified container type and container ID
        super(MenusRegister.CRYSTAL_INFUSION_MENU.get(), pContainerId);

        // Check if the inventory size is as expected (in this case, 3 slots)
        checkContainerSize(inv, TOTAL_SLOTS + 1);

        // Cast the BlockEntity to CrystalInfusionTableEntity and assign it to the blockEntity field
        blockEntity = ((WandManagerTableEntity) entity);

        // Get the level (world) associated with the player's inventory
        this.level = inv.player.level();

        // Store the ContainerData object
        this.data = data;

        int heightDiff = 42;
        int newSlotsDiff = 45;

        // player's 3 inventory rows
//        for (int playerInvY = 0; playerInvY < 3; playerInvY++) {
//
//            for (int playerInvX = 0; playerInvX < 9; playerInvX++) {
//
//                this.addSlot(new Slot(inv, playerInvX + playerInvY * 8 + 9, 8 + playerInvX * 18, 84 + playerInvY * 18 + heightDiff));
//            }
//        }
//
        // Add hot-bar slots.
//        for (int hotbarX = 0; hotbarX < 9; hotbarX++) {
//            this.addSlot(new Slot(inv, hotbarX, 8 + hotbarX * 18, 142 + heightDiff));
//        }
//
//        // Retrieve the IItemHandler capability from the blockEntity
//        this.blockEntity
//            .getCapability()
//            .ifPresent(
//                iItemHandler -> {
//                    int startPositionY = 31;
//                    int startPositionX = 40;
//                    int itemStack = isBookOrTablet(iItemHandler.getStackInSlot(6));
//
//
//                    for(int i = 0; i < (TOTAL_SLOTS-1) / 2; i++) {
//                        this.addSlot(new CrystalSlots(iItemHandler,i,56 - startPositionX,startPositionY - newSlotsDiff, i <= itemStack));
//                        startPositionY += 42;
//                    }
//
//                    startPositionY = 31;
//
//                    for(int i = 0; i < (TOTAL_SLOTS-1) / 2; i++) {
//                        this.addSlot(new CrystalSlots(iItemHandler,i + 3,184 - startPositionX,startPositionY - newSlotsDiff, i <= itemStack));
//                        startPositionY += 42;
//                    }
//
//                    this.addSlot(new BookSlot(iItemHandler,6,80,73 - newSlotsDiff));
//                }
//            );
//        this.addSlot(
//            new Slot(this.accept, 0,80,111 - newSlotsDiff){
//                @Override
//                public int getMaxStackSize() {
//                    return 0;
//                }
//
//                @Override
//                public boolean isHighlightable() {
//                    return false;
//                }
//            }
//        );
//        addDataSlots(data);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TOTAL_SLOTS, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TOTAL_SLOTS) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }


    @Override
    public boolean stillValid(Player pPlayer) {
        // Check if the container is still valid using the ContainerLevelAccess.create method
        // This method requires the level (world) and the position of the block entity
        // It also checks if the player is still interacting with the container
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            pPlayer, BlocksRegister.WAND_MANAGER_TABLE.get());
    }
}
