package org.jahdoo.client.gui.wand_block;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.gui.AbstractInternalContainer;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static org.jahdoo.client.SharedUI.handleSlotsInGridLayout;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class WandBlockMenu extends AbstractInternalContainer {
    public int yOffset = 24;
    public int xOffset = 0;
    public int xSpacing = 34; // Adjust the spacing between slots horizontally
    public int ySpacing = 38; // Adjust the spacing between slots vertically
    public int sharedScreenWidth;
    public int shareScreenHeight;

    public WandBlockMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(pContainerId, inv, extraData);
        this.addSlotsInGridLayout();
    }

    public WandBlockMenu(int pContainerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(pContainerId, inv, entity, data);
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
    public int adjustInventoryY() {
        return yOffset - 33;
    }

    @Override
    public int adjustInventoryX() {
        return xOffset;
    }

    @Override
    protected Block getAssociatedBlock() {
        return BlocksRegister.WAND.get();
    }

    public void addSlotsInGridLayout() {
        handleSlotsInGridLayout(
            (slotX, slotY, index) -> {
                this.addSlot(new AugmentSlot(this.getWandBlockEntity().inputItemHandler,index + 1, slotX, slotY, this));
            },
            getWandBlockEntity().getAllowedSlots(),
            sharedScreenWidth,
            shareScreenHeight,
            xSpacing,
            ySpacing
        );
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        Slot sourceSlot = slots.get(slotIndex);
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack;
        int totalSlotsInWand = getWandBlockEntity().inputItemHandler.getSlots();

        if(!sourceStack.has(WAND_DATA)) return ItemStack.EMPTY;

        String sourceStackIndex = DataComponentHelper.getAbilityTypeItemStack(copyOfSourceStack);


        for(int i = 1; i < totalSlotsInWand; i++){
            if(slotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT){
                ItemStackHandler targetSlots = getWandBlockEntity().inputItemHandler;
                if (!targetSlots.getStackInSlot(i).isEmpty()) {
                    String abilityIndex = DataComponentHelper.getAbilityTypeItemStack(targetSlots.getStackInSlot(i));
                    if(sourceSlot.getItem().getCount() == 1){
                        if (Objects.equals(sourceStackIndex, abilityIndex)) {
                            ItemStack wandSlotItem = targetSlots.getStackInSlot(i).copyWithCount(1);
                            //set player slot
                            sourceSlot.set(wandSlotItem);
                            //set in wand
                            targetSlots.setStackInSlot(i, copyOfSourceStack.copyWithCount(1));

                            GeneralHelpers.getSoundWithPosition(level, getWandBlockEntity().getBlockPos(), SoundEvents.ARMOR_EQUIP_GENERIC.value());
                            this.getWandBlockEntity().setAllAbilities();
                        }
                    }
                }
            }
        }

        return super.quickMoveStack(player, slotIndex);
    }
}
