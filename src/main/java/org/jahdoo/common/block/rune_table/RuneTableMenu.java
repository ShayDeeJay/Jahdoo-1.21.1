package org.jahdoo.common.block.rune_table;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.client.AbstractInternalContainer;
import org.jahdoo.common.client.slots.GeneralItemSlot;
import org.jahdoo.common.client.slots.RuneSlot;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.MenuReg;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.block.wand_manager.WandManagerEntity.DEFAULT_SLOTS;
import static org.jahdoo.common.client.SharedUI.getCore;
import static org.jahdoo.common.client.SharedUI.handleSlotsInGridLayout;

public class RuneTableMenu extends AbstractInternalContainer  {

    public int posX = 34;
    public int posY = 104;
    public int offSetX = 34;
    public int offSetY = 34;
    public int runeYSpacer = 33;
    public boolean hideSlot = true;

    public RuneTableMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(MenuReg.RUNE_TABLE_MENU.get(), id, inv, extraData);
        this.addSlots();
    }

    public RuneTableMenu(int id, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenuReg.RUNE_TABLE_MENU.get(), id, inv, entity, data);
        this.addSlots();
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlockReg.RUNE_TABLE.get();
    }

    public RuneTableEntity tableEntity(){
        if(this.blockEntity instanceof RuneTableEntity runeTable) return runeTable;
        return null;
    }

    @Override
    protected int getAllSlots() {
        int size = JahdooGearData.getGearData(tableEntity().getItem().getStackInSlot(0)).runeSlots().size();
        return size + DEFAULT_SLOTS -1;
    }

    public void switchVisibility(boolean switchC) {
        this.hideSlot = switchC;
    }

    public void addSlots() {
        insertAugmentSlots();
        insertRuneSlots();
    }

    private void insertAugmentSlots() {
        var spacer = new AtomicInteger();
        for (int i = 1; i < 4; i ++){
            this.addSlot(new GeneralItemSlot(tableEntity().inputItemHandler, i, posX - 75, posY + spacer.get() - 96, getCore().get(i-1)));
            spacer.set(spacer.get() + 28);
        }
    }

    private void insertRuneSlots() {
        var getAllSlots = this.tableEntity().getItem().getStackInSlot(0);
        var getData = JahdooGearData.getGearData(getAllSlots);
        var iHandler = tableEntity().inputItemHandler;
        var indexOne = new AtomicInteger(4);
        for (ItemStack itemStack : getData.runeSlots()) {
            iHandler.setStackInSlot(indexOne.get(), itemStack);
            indexOne.set(indexOne.get() + 1);
        }

        handleSlotsInGridLayout(
            (slotX, slotY, index) -> this.addSlot(new RuneSlot(iHandler, index + 4, slotX + posX - 3, slotY - posY + 90, this.tableEntity(), this, 1)),
            getData.runeSlots().size(), 0,0, offSetX, offSetY
        );
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        var vanilla = SLOT_A + SLOT_SIZE;
        var beInventory = INV_SLOT_A + getAllSlots();
        var sourceSlot = slots.get(slotIndex);
        var sourceStack = sourceSlot.getItem();
        var copyOfSourceStack = sourceStack.copy();
        var empty = ItemStack.EMPTY;

        if(!sourceSlot.hasItem() || sourceStack.getItem() instanceof RuneItem){
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
