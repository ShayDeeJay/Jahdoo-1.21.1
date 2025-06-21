package org.jahdoo.common.block.creator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.block.creator.recipe.CreatorRecipes;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.mod.CreatorRecipeReg;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CreatorEntity extends AbstractTankUser implements RecipeInput {

    public double animationTicker;
    public double animateDistanceIncrement = 0.5f;
    private double animationTickerIncrement = 0.5f;
    private ItemStack getResult;

    public CreatorEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.CREATOR_BE.get(), pPos, pBlockState, 1);
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
    public int getMaxSlotSizeInput() {
        return 1;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 64;
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

    public double getAnimationTicker(){
        return this.animationTicker;
    }

    public static void successfulCraftVisual(Level level, BlockPos blockPos){
        Helpers.getSoundWithPosition(level, blockPos, SoundEvents.BEACON_POWER_SELECT, 0.5f, 0.8f);
    }

    public boolean isCompletedCraft(){
        return this.progress == 200;
    }

    public void setAnimationTickerIncrement(double animationTickerIncrement){
        this.animationTickerIncrement = animationTickerIncrement;
    }

    private Optional<CreatorRecipes> getRecipe() {
        return CreatorRecipeReg.getSpellsByTypeId(getAllCraftables());
    }

    public int getProgress(){
        return this.progress;
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {
        if(this.canCraft()){
            var creatorRecipes = this.getRecipe();
            if(this.getResult == null && creatorRecipes.isPresent()){
                this.getResult = creatorRecipes.get().result();
            }
            this.progress++;
            this.tableProcessingParticle();
            this.onCompleteCraft(level, blockPos);
            this.setAnimationTickerIncrement(Math.min(this.animationTickerIncrement + 0.1, 2.5));
        } else {
            this.setAnimationTickerIncrement(Math.max(this.animationTickerIncrement - 0.1, 0.5));
            if(this.getResult != null) this.getResult = null;
//            if(this.progress > 0 && this.outputItemHandler.getStackInSlot(0).isEmpty()) this.progress = 0;
        }

        this.assignTankBlockInRange(level, blockPos, this.getCraftingCost());
    }

    private void tableProcessingParticle(){
        if(!(this.level instanceof ServerLevel serverLevel)) return;

        if(this.progress % 4 == 0){
            PositionFinders.getOuterRingOfRadiusRandom(this.getBlockPos().getCenter(), this.animationTickerIncrement / 5, (double) this.progress / 2,
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
            Helpers.getSoundWithPosition(serverLevel, this.getBlockPos(), SoundEvents.BEACON_AMBIENT, 0.5f, 2f);
        }
    }

    public void onCompleteCraft(Level level, BlockPos blockPos){
        if(!this.isCompletedCraft()) return;

        this.chargeTankFuel(getCraftingCost());
        this.outputItemHandler.insertItem(0, this.getResult.copy(), false);
        this.clearContentsOnCompletion();
        successfulCraftVisual(level, blockPos);
        //ADD COMPLETION PARTICLES
//        this.progress = Math.max(0, progress - 20);
    }

    public void clearContentsOnCompletion(){
        var handler = this.inputItemHandler;
        for(int i = 0; i < handler.getSlots(); i++) handler.getStackInSlot(i).shrink(1);
    }

    public ItemStack getOutputResult(){
        var recipe = this.getRecipe();
        return recipe.isPresent() ? recipe.get().result() : ItemStack.EMPTY;
    }

    public boolean canCraft(){
        var b = getRecipe().isPresent();
        var b1 = this.hasTankAndFuel();
        var b2 = this.outputItemHandler.getStackInSlot(0).isEmpty();
        return b && b1 && b2;
    }

    public List<ItemStack> getAllCraftables(){
        var x = new ArrayList<ItemStack>();
        for (int i = 0; i < inputItemHandler.getSlots(); i++){
            var stackInSlot = inputItemHandler.getStackInSlot(i);
            if(!stackInSlot.isEmpty()) x.add(stackInSlot);
        }
        return x;
    }

    private int getCraftingCost(){
        var getRecipe = this.getRecipe();
        return getRecipe.map(CreatorRecipes::nexiteCost).orElse(65);
    }

    public void setAnimator(){
        if(this.animationTicker >= 360) {
            this.animationTicker = 0;
        } else {
            this.animationTicker += this.animationTickerIncrement;
        }
    }

    public void setAnimatedDistance(){
        if(this.canCraft()){
            if(this.animateDistanceIncrement < 2.5) this.animateDistanceIncrement += 0.025;
        } else {
            if(this.animateDistanceIncrement > 0.5) this.animateDistanceIncrement -= 0.025;
        }
    }

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
}
