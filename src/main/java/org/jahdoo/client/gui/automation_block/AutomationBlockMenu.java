package org.jahdoo.client.gui.automation_block;

import com.ibm.icu.text.SpoofChecker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.client.gui.AbstractInternalContainer;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.MenusRegister;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.block.automation_block.AutomationBlockEntity.*;

public class AutomationBlockMenu extends AbstractInternalContainer {
    public final int posX = 80;
    public final int posY = 40;
    public int offSetX = 0;
    public int offSetY = 30;

    public AutomationBlockMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(MenusRegister.AUTOMATION_BLOCK_MENU.get(), pContainerId, inv, extraData);
        this.setSlot();
    }

    public AutomationBlockMenu(int pContainerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenusRegister.AUTOMATION_BLOCK_MENU.get(), pContainerId, inv, entity, data);
        this.setSlot();
    }

    private void setSlot(){
        this.addSlot(new AutomationSlot(this.getAutomationEntity().inputItemHandler,AUGMENT_SLOT, posX, posY, this));
        this.addSlot(new SlotItemHandler(this.getAutomationEntity().inputItemHandler,INPUT_SLOT, posX - 49 + offSetX, posY + offSetY));
        this.addSlot(new SlotItemHandler(this.getAutomationEntity().outputItemHandler,OUTPUT_SLOT, posX + 49 + offSetX, posY+ offSetY));
    }

    public AutomationBlockEntity getAutomationEntity(){
        if(this.blockEntity instanceof AutomationBlockEntity automationBlockEntity) return automationBlockEntity;
        return null;
    }

    @Override
    protected int getAllSlots() {
        return this.getAutomationEntity().setInputSlots();
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlocksRegister.AUTOMATION_BLOCK.get();
    }



}
