package org.jahdoo.common.block.wand_manager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.Level;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.client.gui.AbstractInternalContainer;
import org.jahdoo.common.client.slots.AugmentCoreSlot;
import org.jahdoo.common.client.slots.RuneSlot;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.registers.BlocksRegister;
import org.jahdoo.common.registers.ItemsRegister;
import org.jahdoo.common.registers.MenusRegister;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.block.wand_manager.WandManagerEntity.DEFAULT_SLOTS;
import static org.jahdoo.common.client.SharedUI.handleSlotsInGridLayout;

public class WandManagerMenu extends AbstractInternalContainer {

    public int posX = 31;
    public int posY = 100;
    public int offSetX = 34;
    public int offSetY = 34;
    public int runeYSpacer = 33;

    public WandManagerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        super(MenusRegister.WAND_MANAGER_MENU.get(), containerId, inv, extraData);
        this.addSlots();
    }

    public WandManagerMenu(int containerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenusRegister.WAND_MANAGER_MENU.get(), containerId, inv, entity, data);
        this.addSlots();
    }

    private void insertWandSlot() {
        this.addSlot(new AugmentCoreSlot(getWandManagerEntity().inputItemHandler, 0, -1000, -1000, ItemsRegister.AUGMENT_CORE.get()));
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlocksRegister.WAND_MANAGER_TABLE.get();
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
        int size = RuneHolder.getRuneholder(getWandManagerEntity().getWandSlot()).runeSlots().size();
        return size + DEFAULT_SLOTS;
    }

    public void addSlots() {
        insertWandSlot();
        insertAugmentSlots();
        insertRuneSlots();
    }

    public List<Item> getCore(){
        return List.of(
            ItemsRegister.AUGMENT_CORE.get(),
            ItemsRegister.ADVANCED_AUGMENT_CORE.get(),
            ItemsRegister.AUGMENT_HYPER_CORE.get()
        );
    }

    private void insertAugmentSlots() {
        var spacer = new AtomicInteger();

        for (int i = 1; i < 4; i ++){
            this.addSlot(new AugmentCoreSlot(getWandManagerEntity().inputItemHandler, i, posX - 75, posY + spacer.get() - 96, getCore().get(i-1)));
            spacer.set(spacer.get() + 28);
        }
    }

    private void insertRuneSlots() {
        try{
            var getAllSlots = this.getWandManagerEntity().getWandSlot();
            var getData = RuneHolder.getRuneholder(getAllSlots);
            var iHandler = getWandManagerEntity().inputItemHandler;
            var indexOne = new AtomicInteger(4);
            for (ItemStack itemStack : getData.runeSlots()) {
                iHandler.setStackInSlot(indexOne.get(), itemStack);
                indexOne.set(indexOne.get() + 1);
            }

            handleSlotsInGridLayout(
                (slotX, slotY, index) -> this.addSlot(new RuneSlot(iHandler, index + 4, slotX + posX, slotY - posY + 82, this.getWandManagerEntity(), 1)),
                getData.runeSlots().size(), 0,0, offSetX, offSetY
            );

        } catch (Exception e){
            JahdooMod.LOGGER.log(Level.DEBUG, e);
        }
    }

}
