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
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.block.creator.recipe.CreatorRecipes;
import org.jahdoo.common.particle.particle_options.BakedParticleOptions;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.mod.CreatorRecipeReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.block.EnchantingTableBlock.BOOKSHELF_OFFSETS;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_MOVE_PARTICLE;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.common.registers.mod.ElementReg.utility;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class CreatorEntity extends AbstractTankUser implements RecipeInput {


    public static final GenericParticleOptions particleTypeA = genericParticle(SOFT_PARTICLE, utility(), 2, 0.08F, true);
    public static final BakedParticleOptions particleTypeB = bakedParticle(utility().id(), 2, 1F, false);
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



    public static void successfulCraftVisual(Level level, BlockPos blockPos){
        Helpers.getSoundWithPosition(level, blockPos, SoundEvents.BEACON_POWER_SELECT, 0.5f, 0.8f);
    }

    public boolean isCompletedCraft(){
        return this.progress == 360;
    }

    public void setAnimationTickerIncrement(double animationTickerIncrement){
        this.animationTickerIncrement = animationTickerIncrement;
    }

    public int getProgress(){
        return progress;
    }

    private Optional<CreatorRecipes> getRecipe() {
        return CreatorRecipeReg.getSpellsByTypeId(getAllCraftables());
    }

    private AbstractElement element(){
        return utility();
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {
        animParticle(level, blockPos);

        this.assignTankBlockInRange(level, blockPos, this.getCraftingCost());

        if(this.canCraft()){
            var creatorRecipes = this.getRecipe();
            if(this.getResult == null && creatorRecipes.isPresent()){
                this.getResult = creatorRecipes.get().result();
            }
            this.progress++;
            this.tableProcessingParticle();
            this.onCompleteCraft(level, blockPos);
            this.setAnimationTickerIncrement(Math.min(this.animationTickerIncrement + 0.1, 2.5));
            if(this.animateDistanceIncrement < 2.5) this.animateDistanceIncrement += 0.025;
            privateTicks++;
        } else {
            this.setAnimationTickerIncrement(Math.max(this.animationTickerIncrement - 0.1, 0.5));
            if(this.getResult != null) this.getResult = null;
            if(this.animateDistanceIncrement > 0.5) this.animateDistanceIncrement = 0.5;
            privateTicks--;
        }
    }

    private void animParticle(Level level, BlockPos blockPos) {
        var particleSpeed =  progress > 0 ? 0.12 : 0.08;
        PositionFinders.innerRadiusRandom(blockPos.getCenter().add(0, 0.3, 0), 0.2, progress > 0 ? 5 : 2,
            positions -> {
                level.addParticle(particleTypeA, positions.x, positions.y, positions.z, 0, -particleSpeed, 0);
                level.addParticle(particleTypeB, positions.x, positions.y, positions.z, 0, -particleSpeed, 0);
            }
        );
    }

    private void tableProcessingParticle(){
        if(!(this.level instanceof ServerLevel serverLevel)) return;

        if(this.progress % 4 == 0){
            PositionFinders.getOuterRingOfRadiusRandom(this.getBlockPos().getCenter(), this.animationTickerIncrement / 5, (double) this.progress / 20,
                worldPosition -> {
                    var directions = this.getBlockPos()
                        .getCenter()
                        .subtract(worldPosition)
                        .normalize()
                        .offsetRandom(RandomSource.create(), 2f);
                    var type = processingParticle(10, 0.85f, false, 0.1);
                    var add = worldPosition.add(0, 0.6f, 0);
                    var min = Math.min(0.06, (double) this.progress / (200 * 10));
                    sendParticles(serverLevel, type, add, 0, directions.x, directions.y, directions.z, min);
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
        for(BlockPos blockpos : BOOKSHELF_OFFSETS) {
            var lifetime = Random.nextInt(10, 50);
            var size = Random.nextFloat(1.8F, 3.2F);
            var partType = MAGIC_MOVE_PARTICLE;
            var particleData = new GenericParticleOptions(partType, utility().partColourB(), 0, lifetime, size, false, 5);
            level.addParticle(
                particleData,
                blockPos.getX() + 0.5,
                blockPos.getY() + 3.0,
                blockPos.getZ() + 0.5,
                (blockpos.getX() + Random.nextFloat()) - 0.5F,
                blockpos.getY() - Random.nextFloat() - 1.0F,
                (blockpos.getZ() + Random.nextFloat()) - 0.5F
            );
        }
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
        var a = !getAllCraftables().isEmpty();
        var b = getRecipe().isPresent();
        var b1 = this.hasTankAndFuel();
        var b2 = this.outputItemHandler.getStackInSlot(0).isEmpty();
        return a && b && b1 && b2;
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
