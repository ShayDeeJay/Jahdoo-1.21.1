package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.client.gui.AbstractInternalContainer;
import org.jahdoo.client.gui.block.modular_chaos_cube.ModularChaosCubeSlot;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.MenusRegister;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity.AUGMENT_SLOT;

public class AugmentModificationMenu extends AbstractInternalContainer {
    public final int posX = 80;
    public final int posY = 30;
    public int offSetX = 0;
    public int offSetY = 30;

    public AugmentModificationMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(MenusRegister.AUGMENT_MODIFICATION_MENU.get(), pContainerId, inv, extraData);
    }

    public AugmentModificationMenu(int pContainerId, Inventory inv, AbstractBEInventory entity, ContainerData data) {
        super(MenusRegister.AUGMENT_MODIFICATION_MENU.get(), pContainerId, inv, entity, data);
    }

    public AugmentModificationEntity getAutomationEntity(){
        if(this.blockEntity instanceof AugmentModificationEntity augmentModification) return augmentModification;
        return null;
    }

    @Override
    protected int getAllSlots() {
        return this.getAutomationEntity().setInputSlots();
    }

    @Override
    public void addPlayerHotbar(Inventory playerInventory, int heightDiff) {}

    @Override
    public void addPlayerInventory(Inventory playerInventory, int heightDiff) {}

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {return ItemStack.EMPTY;}

    @Override
    protected Block getAssociatedBlock() {
        return BlocksRegister.AUGMENT_MODIFICATION_STATION.get();
    }



}
