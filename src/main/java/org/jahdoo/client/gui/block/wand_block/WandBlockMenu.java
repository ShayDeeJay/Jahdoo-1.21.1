package org.jahdoo.client.gui.block.wand_block;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.gui.AbstractInternalContainer;
import org.jahdoo.client.gui.block.augment_modification_station.InventorySlots;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.MenusRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static org.jahdoo.client.SharedUI.handleSlotsInGridLayout;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class WandBlockMenu extends AbstractInternalContainer {
    public int slotsY = -7;
    public int yOffset = 24;
    public int xOffset = 0;
    public int xSpacing = 26;
    public int ySpacing = 30;

    public WandBlockMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(MenusRegister.WAND_BLOCK_MENU.get(), pContainerId, inv, extraData);
        this.addSlotsInGridLayout();

    }

    public WandBlockMenu(int pContainerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenusRegister.WAND_BLOCK_MENU.get(), pContainerId, inv, entity, data);
        this.addSlotsInGridLayout();
    }

    public WandBlockEntity getWandBlockEntity(){
        if(this.blockEntity instanceof WandBlockEntity wandBlockEntity) return wandBlockEntity;
        return null;
    }

    @Override
    protected int getAllSlots() {
        return this.getWandBlockEntity().getAllowedSlots();
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlocksRegister.WAND.get();
    }

    public void addSlotsInGridLayout() {
        handleSlotsInGridLayout(
            (slotX, slotY, index) -> this.addSlot(new AugmentSlot(this.getWandBlockEntity().inputItemHandler,index + 1, slotX, slotY + slotsY, this)),
            getWandBlockEntity().getAllowedSlots(),
            0,0,
            xSpacing,
            ySpacing
        );
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        var sourceSlot = slots.get(slotIndex);
        var sourceStack = sourceSlot.getItem();
        var copyOfSourceStack = sourceStack.copy();
        int totalSlotsInWand = getWandBlockEntity().inputItemHandler.getSlots();
        if(!sourceStack.has(WAND_DATA)) return ItemStack.EMPTY;
        String sourceStackIndex = DataComponentHelper.getAbilityTypeItemStack(copyOfSourceStack);
        for(int i = 1; i < totalSlotsInWand; i++){
            if(slotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT){
                var targetSlots = getWandBlockEntity().inputItemHandler;
                if (!targetSlots.getStackInSlot(i).isEmpty()) {
                    var abilityIndex = DataComponentHelper.getAbilityTypeItemStack(targetSlots.getStackInSlot(i));
                    if(sourceSlot.getItem().getCount() == 1){
                        if (Objects.equals(sourceStackIndex, abilityIndex)) {
                            var wandSlotItem = targetSlots.getStackInSlot(i).copyWithCount(1);
                            //set player slot
                            sourceSlot.set(wandSlotItem);
                            //set in wand
                            targetSlots.setStackInSlot(i, copyOfSourceStack.copyWithCount(1));

                            ModHelpers.getSoundWithPosition(level, getWandBlockEntity().getBlockPos(), SoundEvents.VAULT_OPEN_SHUTTER);
                            this.getWandBlockEntity().setAllAbilities();
                        }
                    }
                }
            }
        }

        return super.quickMoveStack(player, slotIndex);
    }
}
