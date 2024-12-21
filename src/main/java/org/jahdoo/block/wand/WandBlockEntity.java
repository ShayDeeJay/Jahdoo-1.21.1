package org.jahdoo.block.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.components.ability_holder.AbilityHolder;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.client.gui.block.wand_block.WandBlockMenu;
import org.jahdoo.components.WandData;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.utils.PositionGetters;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.items.augments.AugmentItemHelper.setAbilityToAugment;
import static org.jahdoo.registers.DataComponentRegistry.NUMBER;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;

public class WandBlockEntity extends AbstractBEInventory implements MenuProvider, GeoBlockEntity {

    int tickCounter;
    public static final int GET_WAND_SLOT = 0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_block");

    public WandBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.WAND_BE.get(), pPos, pBlockState, 1);
    }

    public void updateView(){
        for(int i = 1; i < this.inputItemHandler.getSlots(); i++) {
            this.inputItemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }

        WandAbilityHolder actualAbilities =
            this.inputItemHandler.getStackInSlot(GET_WAND_SLOT)
            .get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());

        var storedAbilities = this.inputItemHandler.getStackInSlot(GET_WAND_SLOT)
                .get(WAND_DATA.get());

        AtomicInteger integer = new AtomicInteger(1);


        for (String key : storedAbilities.abilitySet()) {
            var abilityRegistrars = AbilityRegister.getSpellsByTypeId(key);
            if (!abilityRegistrars.isEmpty()) {
                var itemStack = new ItemStack(ItemsRegister.AUGMENT_ITEM.get());
                itemStack.set(NUMBER, 4);
                var abilityHolder = actualAbilities.abilityProperties().get(key);
                setAbilityToAugment(itemStack, abilityRegistrars.getFirst(), actualAbilities);
                var newHolder = new WandAbilityHolder(new LinkedHashMap<>());
                newHolder.abilityProperties().put(key, abilityHolder);
                itemStack.set(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), newHolder);
                this.inputItemHandler.setStackInSlot(integer.get(), itemStack);

            } else {
                this.inputItemHandler.setStackInSlot(integer.get(), ItemStack.EMPTY);
            }
            integer.set(integer.get() + 1);
        }

    }

    public void setAllAbilities(){
        var wandAbilityHolder = new WandAbilityHolder(new LinkedHashMap<>());
        var positions = new ArrayList<String>();
        ItemStack wandItem = this.getWandItemFromSlot().copy();

        for(int i = 0; i < this.getAllowedSlots(); i++){
            var augmentItem = this.inputItemHandler.getStackInSlot(i+1);
            var hasAbility = augmentItem.has(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            if(hasAbility){
                var getKeyFromAugment = DataComponentHelper.getAbilityTypeItemStack(augmentItem);
                var abilityHolder = augmentItem.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()).abilityProperties().get(getKeyFromAugment);
                positions.add(getKeyFromAugment);
                wandAbilityHolder.abilityProperties().put(getKeyFromAugment, abilityHolder);
            } else {
                positions.add("empty" + i);
                wandAbilityHolder.abilityProperties().put("empty" + i, new AbilityHolder(Collections.emptyMap()));
            }
        }

        if(!wandAbilityHolder.abilityProperties().containsKey(wandItem.get(WAND_DATA).selectedAbility())){
            wandItem.update(WAND_DATA, WandData.DEFAULT, data -> data.setSelectedAbility(""));
        }
        wandItem.update(WAND_DATA, WandData.DEFAULT, data -> data.setAbilityOrder(positions));
        wandItem.set(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), wandAbilityHolder);
        this.inputItemHandler.setStackInSlot(GET_WAND_SLOT, wandItem);
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {

        var getWandItem = inputItemHandler.getStackInSlot(GET_WAND_SLOT).getItem();
        var getType = getElementByWandType(getWandItem);
        if (getType.isEmpty()) return;

        tickCounter++;
        if (getType.getFirst() != null) {
            this.playIdleParticleType(level, blockPos, getType.getFirst());
        }
    }

    public void playIdleParticleType(Level level, BlockPos blockPos, AbstractElement getType){
        double difference = ModHelpers.Random.nextDouble(0.3,0.4);

        if(tickCounter % 4 == 0){
            PositionGetters.getInnerRingOfRadiusRandom(blockPos, 0.2, 2,
                positions -> {
                    ParticleHandlers.sendParticles(
                        level,
                        getType.getParticleGroup().bakedSlow(),
                        positions.subtract(0,difference,0),
                        1, 0,
                        ModHelpers.Random.nextDouble(0.0, 0.02),
                        0, 0.01
                    );

                    ParticleHandlers.sendParticles(
                        level,
                        getType.getParticleGroup().genericSlow(),
                        positions.subtract(0,difference,0),
                        1, 0, 0,
                        ModHelpers.Random.nextDouble(0.0, 0.02),
                        0.01
                    );
                }
            );
        }
    }

    public ItemStack getWandItemFromSlot(){
        return this.inputItemHandler.getStackInSlot(0);
    }

    public int slotsWithoutWand(){
        return this.setInputSlots() -1;
    }

    public int getAllowedSlots(){
        var getSlots = getWandItemFromSlot().has(WAND_DATA.get());
        if(getSlots){
            return Math.min(getWandItemFromSlot().get(WAND_DATA).abilitySlots(), slotsWithoutWand());
        }
        return 4;
    }

    @Override
    public void dropsAllInventory(Level level) {
        var inputInventory = new SimpleContainer(setInputSlots());
        inputInventory.setItem(0, this.getWandItemFromSlot());
        Containers.dropContents(level, this.worldPosition, inputInventory);
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this, state -> state.setAndContinue(IDLE))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.jahdoo.infusion_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new WandBlockMenu(pContainerId, pPlayerInventory, this, this.data);
    }
}

