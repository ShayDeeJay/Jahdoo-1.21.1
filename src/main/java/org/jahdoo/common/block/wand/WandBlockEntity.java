package org.jahdoo.common.block.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;

import static org.jahdoo.common.registers.ComponentReg.WAND_DATA;
import static org.jahdoo.common.registers.ElementReg.fromWand;

public class WandBlockEntity extends AbstractBEInventory implements MenuProvider, GeoBlockEntity {

    public static final int GET_WAND_SLOT = 0;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_block");
    private static final Logger log = LoggerFactory.getLogger(WandBlockEntity.class);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int tickCounter;

    public WandBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.WAND_BE.get(), pos, state, 1);
    }

    public ItemStack getWandItemFromSlot(){
        return this.inputItemHandler.getStackInSlot(0);
    }

    public int slotsWithoutWand(){
        return this.setInputSlots() -1;
    }

    @Override
    public int setInputSlots() {
        return 11;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSize() {
        return 1;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.jahdoo.infusion_table");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WandBlockMenu(id, inventory, this, this.data);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public void dropsAllInventory(Level level) {
        var inputInventory = new SimpleContainer(setInputSlots());
        inputInventory.setItem(0, this.getWandItemFromSlot());
        Containers.dropContents(level, this.worldPosition, inputInventory);
    }

    public int getAllowedSlots(){
        var getSlots = getWandItemFromSlot();
        var a = getSlots.get(WAND_DATA);

        return a != null ? Math.min(a.abilitySlots(), slotsWithoutWand()) : 4;
    }

    public void tick(Level level, BlockPos blockPos, BlockState state) {

        var getWandItem = inputItemHandler.getStackInSlot(GET_WAND_SLOT).getItem();
        var getType = fromWand(getWandItem);
        if (getType.isEmpty()) return;

        tickCounter++;
        getType.ifPresent(type -> this.playIdleParticleType(level, blockPos, type));
    }

    public void playIdleParticleType(Level level, BlockPos blockPos, AbstractElement getType){
        double difference = Helpers.Random.nextDouble(0.3,0.4);

        if(tickCounter % 4 == 0){
            PositionFinders.innerRadiusRandom(blockPos, 0.2, 2,
                positions -> {
                    ParticleHandlers.sendParticles(
                        level,
                        getType.getParticleGroup().bakedSlow(),
                        positions.subtract(0,difference,0),
                        1, 0,
                        Helpers.Random.nextDouble(0.0, 0.02),
                        0, 0.01
                    );

                    ParticleHandlers.sendParticles(
                        level,
                        getType.getParticleGroup().genericSlow(),
                        positions.subtract(0,difference,0),
                        1, 0, 0,
                        Helpers.Random.nextDouble(0.0, 0.02),
                        0.01
                    );
                }
            );
        }
    }

    public void setAllAbilities(){
        var wandItem = this.getWandItemFromSlot().copy();

        var bundleContents = wandItem.get(DataComponents.BUNDLE_CONTENTS);
        if(bundleContents == null) return;

        var newComponents = new ArrayList<ItemStack>();

        for(int i = 0; i < this.getAllowedSlots(); i++){
            var augmentItem = this.inputItemHandler.getStackInSlot(i+1);
            if(!augmentItem.isEmpty()){
                newComponents.add(augmentItem);
            }
        }

        wandItem.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newComponents));
        this.inputItemHandler.setStackInSlot(GET_WAND_SLOT, wandItem);

    }

    public void updateView(){
        var list = this.getWandItemFromSlot().get(DataComponents.BUNDLE_CONTENTS);

        var counter = 1;
        if(list != null){
            for (var itemStack : list.itemCopyStream().toList()) {
                if(!itemStack.isEmpty()){
                    this.inputItemHandler.setStackInSlot(counter, itemStack);
                    counter++;
                }
            }
        }
    }
}

