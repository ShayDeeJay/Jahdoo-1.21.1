package org.jahdoo.common.block.rune_table;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.common.block.wand_manager.WandManagerEntity.ADDITIONAL_RUNE_SLOTS;
import static org.jahdoo.common.block.wand_manager.WandManagerEntity.DEFAULT_SLOTS;

public class DivineForgeEntity extends AbstractBEInventory implements MenuProvider {

    public ArmorStand stand;

    public DivineForgeEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.RUNE_TABLE_BE.get(), pPos, pBlockState, 64);
    }

    public void tick(Level pLevel, BlockPos pos, BlockState pState) {

//        var getNearest = pLevel.players().stream().findFirst().get();
//        System.out.println(getNearest);
//        for(BlockPos blockpos : BOOKSHELF_OFFSETS) {
//            if (Random.nextInt(16) == 0) {
//                var center = pos.getCenter();
//                var playerP = getNearest.position();
//                var pPos = new Vec3(playerP.x - center.x, playerP.y - center.y, playerP.z - center.z);
//                level.addParticle(
//                    new GenericParticleOptions(ParticleStore.ENCHANT_PARTICLE, ElementReg.random().textColourA(), 0, 20, 3, false, 5),
//                    (double)pos.getX() + (double)0.5F,
//                    (double)pos.getY() + (double)2.0F,
//                    (double)pos.getZ() + (double)0.5F,
//                    (double)((float)pPos.x + Random.nextFloat()) - (double)0.5F,
//                    (double)((float)pPos.y - Random.nextFloat() - 0.0F),
//                    (double)((float)pPos.z + Random.nextFloat()) - (double)0.5F
//                );
//            }
//        }

        if(itemSlot().isEmpty()) privateTicks = 0; else {
            if(getLevel() instanceof ServerLevel){
                privateTicks++;
                updateBlock();
            }
        }
    }

    public boolean checkAndChargeCores(Item item, boolean charge){
        var handler = this.inputItemHandler;
        for (int i = 0; i < handler.getSlots(); i++){
            var runeTableItem = handler.getStackInSlot(i);

            if(runeTableItem.getItem().equals(item)){
                if(charge){
                    handler.getStackInSlot(i).shrink(1);
                }
                return true;
            }
        }
        return false;
    }

    public ArmorStand getStand(Level level){
        if(this.stand == null){
            stand = EntityType.ARMOR_STAND.create(level);
        }
        return stand;
    }

    public ItemStackHandler getItem(){
        return this.inputItemHandler;
    }

    public ItemStack itemSlot(){
        return this.inputItemHandler.getStackInSlot(0);
    }

    @Override
    public int setInputSlots() {
        return DEFAULT_SLOTS + ADDITIONAL_RUNE_SLOTS;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSizeInput() {
        return 64;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RuneTableMenu(i, inventory,this, this.data);
    }

    public void setItem(ItemStack item){
        if(this.getLevel() instanceof ServerLevel){
            getItem().setStackInSlot(0, item);
        }
    }

}
