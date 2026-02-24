package org.jahdoo.common.block.divine_forge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.common.client.AbstractInternalContainer;
import org.jahdoo.common.client.slots.CoreItemSlot;
import org.jahdoo.common.client.slots.ModifierSlot;
import org.jahdoo.common.client.slots.RuneSlot;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.MenuReg;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

import static org.jahdoo.common.block.divine_forge.DivineForgeEntity.*;
import static org.jahdoo.common.client.SharedUI.getCore;
import static org.jahdoo.common.client.SharedUI.handleSlotsInGridLayout;

public class DivineForgeMenu extends AbstractInternalContainer  {

    public int posX = 34;
    public int posY = 104;
    public int offSetX = 34;
    public int offSetY = 34;
    public int runeYSpacer = 33;
    public boolean hideRuneSlots = true;
    public boolean hideModifierSlot = true;

    public DivineForgeMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(MenuReg.RUNE_TABLE_MENU.get(), id, inv, extraData);
        this.addSlots();
    }

    public DivineForgeMenu(int id, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenuReg.RUNE_TABLE_MENU.get(), id, inv, entity, data);
        this.addSlots();
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlockReg.RUNE_TABLE.get();
    }

    public DivineForgeEntity tableEntity(){
        if(this.blockEntity instanceof DivineForgeEntity runeTable) return runeTable;
        return null;
    }

    @Override
    protected int getAllSlots() {
        int size = JahdooGearData.getGearData(tableEntity().getItem().getStackInSlot(0)).runeSlots().size();
        return DEFAULT_SLOTS + MODIFICATION_SLOTS + size;
    }

    public void switchModifierVisibility(boolean switchC) {
        this.hideModifierSlot = switchC;
    }

    public void switchRuneVisibility(boolean switchC) {
        this.hideRuneSlots = switchC;
    }

    public void addSlots() {
        insertAugmentSlots();
        insertModificationSlot();
        insertRuneSlots();
    }

    private void insertAugmentSlots() {
        var spacer = 0;
        for (int i = 1; i < DEFAULT_SLOTS; i ++){
            this.addSlot(new CoreItemSlot(tableEntity().getInputItemHandler(), i, posX - 75, posY + spacer - 96, getCore().get(i-1)));
            spacer += 28;
        }
    }

    private void insertModificationSlot() {
        var spacer = 0;
        for(int i = 0; i < MODIFICATION_SLOTS; i++){
            this.addSlot(new ModifierSlot(tableEntity().getInputItemHandler(), i + MODIFICATION_SLOTS, posX + 30 + spacer, posY - 73, this));
            spacer += 32;
        }
    }

    public void insertRuneSlots() {
        var getAllSlots = this.tableEntity().getItem().getStackInSlot(0);
        var getData = JahdooGearData.getGearData(getAllSlots);
        var iHandler = tableEntity().getInputItemHandler();
        var indexOne = DivineForgeEntity.DEFAULT_SLOTS + DivineForgeEntity.MODIFICATION_SLOTS;

        for (ItemStack itemStack : getData.runeSlots()) {
            iHandler.setStackInSlot(indexOne, itemStack);
            indexOne++;
        }

        handleSlotsInGridLayout(
            (slotX, slotY, index) -> getAddSlot(slotX, slotY, index, iHandler),
            getData.runeSlots().size(), 0, 0, offSetX - 6, offSetY - 2
        );
    }

    private void getAddSlot(Integer slotX, Integer slotY, Integer index, ItemStackHandler iHandler) {
        var slot = new RuneSlot(iHandler, index + 8, slotX + posX - 3, slotY - posY + 90, this.tableEntity(), this, 1);
        this.addSlot(slot);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        var vanilla = SLOT_A + SLOT_SIZE;
        var slotA = INV_SLOT_A;
        var beInventory = slotA + getAllSlots();
        var sourceSlot = slots.get(slotIndex);
        var sourceStack = sourceSlot.getItem();
        var copyOfSourceStack = sourceStack.copy();
        var empty = ItemStack.EMPTY;

        if(!sourceSlot.hasItem() || sourceStack.getItem() instanceof RuneItem){
            return empty;
        } else if (slotIndex < vanilla) {
            if (!moveItemStackTo(sourceStack, slotA, beInventory, false)) return empty;
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
