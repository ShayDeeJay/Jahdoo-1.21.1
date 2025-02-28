package org.jahdoo.common.block.wand;

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
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.items.wand.WandData;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AbilityRegister;
import org.jahdoo.common.registers.BlockEntitiesRegister;
import org.jahdoo.common.registers.DataComponentRegistry;
import org.jahdoo.common.registers.ItemsRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.items.augments.AugmentItemHelper.setAbilityToAugment;
import static org.jahdoo.common.registers.DataComponentRegistry.NUMBER;
import static org.jahdoo.common.registers.DataComponentRegistry.WAND_DATA;
import static org.jahdoo.common.registers.ElementRegistry.fromWand;

public class WandBlockEntity extends AbstractBEInventory implements MenuProvider, GeoBlockEntity {

    public static final int GET_WAND_SLOT = 0;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_block");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int tickCounter;

    public WandBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegister.WAND_BE.get(), pos, state, 1);
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
        if(getSlots.has(WAND_DATA.get())){
            var a = getSlots.get(WAND_DATA).abilitySlots();
            return Math.min(a, slotsWithoutWand());
        }
        return 4;
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
            PositionFinders.getInnerRingOfRadiusRandom(blockPos, 0.2, 2,
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

    public void updateView(){
        for(int i = 1; i < this.inputItemHandler.getSlots(); i++) {
            this.inputItemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }

        var actualAbilities =
            this.inputItemHandler.getStackInSlot(GET_WAND_SLOT)
                .get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());

        var storedAbilities = this.inputItemHandler
            .getStackInSlot(GET_WAND_SLOT)
            .get(WAND_DATA.get());

        AtomicInteger integer = new AtomicInteger(1);

        for (String key : storedAbilities.abilitySet()) {
            var abilityRegistrars = AbilityRegister.getSpellsByTypeId(key);
            if (!abilityRegistrars.isEmpty()) {
                var ability = abilityRegistrars.getFirst();
                var itemStack = new ItemStack(ItemsRegister.AUGMENT.get());
                itemStack.set(NUMBER, 4);
                var abilityHolder = actualAbilities.abilityProperties().get(key);
                setAbilityToAugment(itemStack, ability, actualAbilities);
                var newHolder = new WandAbilityHolder(new LinkedHashMap<>());
                newHolder.abilityProperties().put(key, abilityHolder);
                itemStack.set(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), newHolder);
                itemStack.set(DataComponentRegistry.JAHDOO_RARITY, ability.rarity().getId());
                this.inputItemHandler.setStackInSlot(integer.get(), itemStack);

            } else {
                this.inputItemHandler.setStackInSlot(integer.get(), ItemStack.EMPTY);
            }
            integer.set(integer.get() + 1);
        }
    }
}

