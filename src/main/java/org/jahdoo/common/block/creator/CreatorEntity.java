package org.jahdoo.common.block.creator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.block.creator.recipe.CreatorRecipes;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.particle.particle_options.BakedParticleOptions;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.CreatorRecipeReg;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.jahdoo.common.block.creator.CreatorBlock.particleBurst;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.common.registers.mod.ElementReg.utility;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class CreatorEntity extends AbstractTankUser implements RecipeInput {

    public static final GenericParticleOptions particleTypeA = genericParticle(SOFT_PARTICLE, utility(), 2, 0.08F, true);
    public static final BakedParticleOptions particleTypeB = bakedParticle(utility().id(), 2, 1F, false);
    public int ticker;
    private ItemStack getResult;
    private AbilityHolder holder;

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

    public ItemStack getResult(){
        return this.getResult;
    }

    public void setHolder(AbilityHolder holder){
        this.holder = holder;
    }

    public AbilityHolder getHolder(){
        return holder;
    }

    public static void successfulCraftVisual(Level level, BlockPos blockPos){
        particleBurst(level, blockPos);
//        SoundHelpers.getSoundWithPosition(level, blockPos, SoundReg.INCREASE_SCORE.get(), SoundSource.BLOCKS, 1f, 0.8f);
        SoundHelpers.getSoundWithPosition(level, blockPos, SoundReg.MYSTIC_ABILITY.get(), SoundSource.BLOCKS, 0.5f, 2f);

    }

    public boolean isCompletedCraft(){
        return this.progress == 360;
    }

    public int getProgress(){
        return progress;
    }

    public Optional<CreatorRecipes> getRecipe() {
        return CreatorRecipeReg.getSpellsByTypeId(getAllCraftables(), this);
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {
        animParticle(level, blockPos);
        this.assignTankBlockInRange(level, blockPos, this.getCraftingCost());

        if(this.canCraft()){
            if(progress == 0) this.ticker = 0;
            var creatorRecipes = this.getRecipe();
            if(creatorRecipes.isPresent()){
                var getRecipe = creatorRecipes.get();
                if(this.getResult == null) this.getResult = getRecipe.result(this);
                this.progress++;
                this.tableProcessingParticle(level);
                this.onCompleteCraft(level, blockPos);
            }
        } else {
            if(this.holder != null) this.setHolder(null);
            if(this.getResult != null) this.getResult = null;
        }

        if(ticker == Integer.MAX_VALUE) this.ticker = 0; else ticker ++;
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

    private void tableProcessingParticle(Level level){
        if(this.progress == 1 || this.progress % 25 == 0){
            SoundHelpers.getSoundWithPosition(level, this.getBlockPos(), SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.1F, Random.nextFloat(1.5F, 2F));
        }
        if(this.progress == 1 || this.progress % 5 == 0){
            SoundHelpers.getSoundWithPosition(level, this.getBlockPos(), SoundReg.LEVITATE.value(), SoundSource.BLOCKS, 0.5F, Random.nextFloat(0.4F, 0.8F));
        }
    }

    public void onCompleteCraft(Level level, BlockPos blockPos){
        if(!this.isCompletedCraft()) return;

        this.chargeTankFuel(getCraftingCost());
        this.getOutputItemHandler().insertItem(0, this.getResult.copy(), false);
        this.clearContentsOnCompletion();

        successfulCraftVisual(level, blockPos);
    }

    public void clearContentsOnCompletion(){
        var handler = this.getInputItemHandler();
        for(int i = 0; i < handler.getSlots(); i++) handler.getStackInSlot(i).shrink(1);
    }

    public int neededNexite(){
        if (getRecipe().isPresent()) return getRecipe().get().nexiteCost();
        return -1;
    }

    public boolean canCraft(){
        var a = !getAllCraftables().isEmpty();
        var b = getRecipe().isPresent();
        var c = this.hasTankAndFuel();
        var d = this.getOutputItemHandler().getStackInSlot(0).isEmpty();
        var e = b && getRecipe().get().secondaryCheck(this);
        return a && b && c && d & e;
    }

    public List<ItemStack> getAllCraftables(){
        var x = new ArrayList<ItemStack>();
        for (int i = 0; i < getInputItemHandler().getSlots(); i++){
            var stackInSlot = getInputItemHandler().getStackInSlot(i);
            if(!stackInSlot.isEmpty()) x.add(stackInSlot);
        }
        return x;
    }

    private int getCraftingCost(){
        var getRecipe = this.getRecipe();
        return getRecipe.map(CreatorRecipes::nexiteCost).orElse(65);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        if(this.holder != null) AbilityHolder.writeTag(holder, tag);
        tag.putInt("ticks", ticker);
        if(tankPosition != null){
            int[] array = {tankPosition.getX(), tankPosition.getY(), tankPosition.getZ()};
            tag.putIntArray("blockPos", array);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        this.ticker = tag.getInt("ticks");
        if(tag.contains("abilities")){
            this.holder = AbilityHolder.readTag(tag);
        }
        var array = tag.getIntArray("blockPos");
        if(!Arrays.stream(array).boxed().toList().isEmpty()){
            this.tankPosition = new BlockPos(array[0], array[1], array[2]);
        }
    }
}
