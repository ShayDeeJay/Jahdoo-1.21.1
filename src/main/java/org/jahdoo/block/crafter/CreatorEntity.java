package org.jahdoo.block.crafter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.recipe.CreatorRecipe;
import org.jahdoo.recipe.RecipeRegistry;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.Arrays;
import java.util.Optional;

public class CreatorEntity extends AbstractTankUser implements RecipeInput {

    public double animationTicker;
    public double animateDistanceIncrement = 0.5f;
    private double animationTickerIncrement = 0.5f;

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        this.progress = pTag.getInt("progress");
        if(tankPosition != null){
            int[] array = {tankPosition.getX(), tankPosition.getY(), tankPosition.getZ()};
            pTag.putIntArray("blockPos", array);
        }
        super.loadAdditional(pTag, pRegistries);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("progress", this.progress);
        var array = pTag.getIntArray("blockPos");
        if(!Arrays.stream(array).boxed().toList().isEmpty()){
            this.tankPosition = new BlockPos(array[0], array[1], array[2]);
        }
        super.saveAdditional(pTag, pRegistries);
    }

    public CreatorEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.CREATOR_BE.get(), pPos, pBlockState, 1);
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {
        this.assignTankBlockInRange(level, blockPos, this.getCraftingCost());
        if(this.canCraft()){
            this.progress++;
            this.tableProcessingParticle();
            this.onCompleteCraft(level, blockPos);
            this.setAnimationTickerIncrement(Math.min(this.animationTickerIncrement + 0.1, 2.5));
        } else {
            this.setAnimationTickerIncrement(Math.max(this.animationTickerIncrement - 0.1, 0.5));
            if(this.progress > 0) this.progress = 0;
        }
    }

    private void tableProcessingParticle(){
        if(!(this.level instanceof ServerLevel serverLevel)) return;

        if(this.progress % 4 == 0){
            PositionGetters.getOuterRingOfRadiusRandom(this.getBlockPos().getCenter(), this.animationTickerIncrement / 5, 20,
                worldPosition -> {
                    Vec3 directions = this.getBlockPos()
                        .getCenter()
                        .subtract(worldPosition)
                        .normalize()
                        .offsetRandom(RandomSource.create(), 2f);
                        ParticleHandlers.sendParticles(serverLevel, processingParticle(10, 0.45f, false, 0.1),
                        worldPosition.add(0, 0.6f, 0), 0, directions.x, directions.y, directions.z, Math.min(0.06, (double) this.progress /(200 * 10))
                    );
                }
            );
        }

        if(this.progress % 25 == 0){
            ModHelpers.getSoundWithPosition(serverLevel, this.getBlockPos(), SoundEvents.BEACON_AMBIENT, 0.5f, 2f);
        }
    }

    public void onCompleteCraft(Level level, BlockPos blockPos){
        if(!this.isCompletedCraft()) return;

        this.chargeTankFuel(getCraftingCost());
        this.successfulCraftVisual(level, blockPos, this.getOutputResult());
        this.outputItemHandler.setStackInSlot(0, this.getOutputResult());
        this.clearContentsOnCompletion();

        this.progress = 0;
    }

    public void clearContentsOnCompletion(){
        for(int i = 0; i < this.inputItemHandler.getSlots(); i++) this.inputItemHandler.getStackInSlot(i).shrink(1);
    }

    public ItemStack getOutputResult(){
        if(getCurrentRecipe(level).isEmpty()) return ItemStack.EMPTY;
        return getCurrentRecipe(level).get().value().getResultItem(level.registryAccess());
    }

    private void successfulCraftVisual(Level level, BlockPos blockPos, ItemStack itemStack){
        if(!(level instanceof ServerLevel serverLevel)) return;
        if(itemStack.getItem() instanceof WandItem) {
            ParticleHandlers.particleBurst(
                serverLevel, blockPos.getCenter().add(0, 0.5f, 0), 10,
                ParticleHandlers.genericParticleOptions(
                    ParticleStore.SOFT_PARTICLE_SELECTION,
                    ElementRegistry.getElementByWandType(itemStack.getItem()).getFirst(), 30, 0.6f
                ),
                0, 0.5, 0, 0.1f
            );
        };

        ModHelpers.getSoundWithPosition(level, blockPos, SoundEvents.BEACON_POWER_SELECT, 0.5f, 0.8f);
    }

    public boolean isCompletedCraft(){
        return this.progress == 200;
    }

    public double getAnimationTickerIncrement(){
        return this.animationTickerIncrement;
    }

    public void setAnimationTickerIncrement(double animationTickerIncrement){
        this.animationTickerIncrement = animationTickerIncrement;
    }

    public boolean canCraft(){
        return getCurrentRecipe(level).isPresent()
            && this.hasTankAndFuel()
            && this.outputItemHandler.getStackInSlot(0).isEmpty();
    }

    private int getCraftingCost(){
        if(this.getCurrentRecipe(level).isPresent()){
            return this.getCurrentRecipe(level).get().value().getCraftingCost();
        }
        return 1000;
    }

    private Optional<RecipeHolder<CreatorRecipe>> getCurrentRecipe(Level level) {
        SimpleContainer inventory = new SimpleContainer(this.inputItemHandler.getSlots());
        for(int i = 0; i < inputItemHandler.getSlots(); i++) {
            inventory.setItem(i, inputItemHandler.getStackInSlot(i));
        }

        return level.getRecipeManager().getRecipeFor(RecipeRegistry.CREATOR_TYPE.get(), CraftingInput.of(3, 2, inventory.getItems()), level);
    }

    public void setAnimator(){
        if(this.animationTicker >= 360) {
            this.animationTicker = 0;
        } else {
            this.animationTicker += this.getAnimationTickerIncrement();
        }
    }

    public double getAnimationTicker(){
        return this.animationTicker;
    }

    public void setAnimatedDistance(){
        if(this.canCraft()){
            if(this.animateDistanceIncrement < 2.5) this.animateDistanceIncrement += 0.025;
        } else {
            if(this.animateDistanceIncrement > 0.5) this.animateDistanceIncrement -= 0.025;
        }
    }

    @Override
    public int setInputSlots() {
        return 8;
    }

    @Override
    public int setOutputSlots() {
        return 1;
    }

    @Override
    public int getMaxSlotSize() {
        return 1;
    }

    @Override
    public ItemStack getItem(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int setCraftingCost() {
        return getCraftingCost();
    }
}
