package org.jahdoo.common.block.wand_manager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.Level;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.client.AbstractInternalContainer;
import org.jahdoo.common.client.slots.CoreItemSlot;
import org.jahdoo.common.client.slots.RuneSlot;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.MenuReg;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.block.wand_manager.WandManagerEntity.DEFAULT_SLOTS;
import static org.jahdoo.common.client.SharedUI.getCore;
import static org.jahdoo.common.client.SharedUI.handleSlotsInGridLayout;

public class WandManagerMenu extends AbstractInternalContainer {

    public int posX = 31;
    public int posY = 100;
    public int offSetX = 34;
    public int offSetY = 34;
    public int runeYSpacer = 33;

    public WandManagerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        super(MenuReg.WAND_MANAGER_MENU.get(), containerId, inv, extraData);
        this.addSlots();
    }

    public WandManagerMenu(int containerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenuReg.WAND_MANAGER_MENU.get(), containerId, inv, entity, data);
        this.addSlots();
    }

    private void insertWandSlot() {
        this.addSlot(new CoreItemSlot(getWandManagerEntity().getInputItemHandler(), 0, -1000, -1000, ItemReg.AUGMENT_CORE.get()));
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlockReg.WAND_MANAGER_TABLE.get();
    }

    public WandManagerEntity getWandManagerEntity(){
        if(this.blockEntity instanceof WandManagerEntity augmentModification) return augmentModification;
        return null;
    }

    @Override
    public void addPlayerInventory(Inventory inventory, int heightDiff) {
        super.addPlayerInventory(inventory, heightDiff);
    }

    @Override
    public void addPlayerHotbar(Inventory inventory, int heightDiff) {
        super.addPlayerHotbar(inventory, heightDiff);
    }

    @Override
    protected int getAllSlots() {
        var size = JahdooGearData.getGearData(getWandManagerEntity().getWandSlot()).runeSlots().size();
        return size + DEFAULT_SLOTS;
    }

    public void addSlots() {
        insertWandSlot();
        insertAugmentSlots();
        insertRuneSlots();
    }

    private void insertAugmentSlots() {
        var spacer = new AtomicInteger();
        var handler = getWandManagerEntity().getInputItemHandler();

        for (int i = 1; i < 4; i ++){
            this.addSlot(new CoreItemSlot(handler, i, posX - 75, posY + spacer.get() - 96, getCore().get(i-1)));
            spacer.set(spacer.get() + 28);
        }
    }

    private void insertRuneSlots() {
        try{

            var getAllSlots = this.getWandManagerEntity().getWandSlot();
            var getData = JahdooGearData.getGearData(getAllSlots);
            var iHandler = getWandManagerEntity().getInputItemHandler();
            var indexOne = new AtomicInteger(4);

            for (ItemStack itemStack : getData.runeSlots()) {
                iHandler.setStackInSlot(indexOne.get(), itemStack);
                indexOne.set(indexOne.get() + 1);
            }

            handleSlotsInGridLayout(
                (slotX, slotY, index) -> this.addSlot(new RuneSlot(iHandler, index + 4, slotX + posX, slotY - posY + 82, this.getWandManagerEntity(), null, 1)),
                getData.runeSlots().size(), 0,0, offSetX, offSetY
            );

        } catch (Exception e) {
            JahdooMod.LOGGER.log(Level.DEBUG, e);
        }
    }

}
