package org.jahdoo.common.block.chaos_cube;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.AbstractBlockAbility;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.ChaosCubeData;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.core.Direction.byName;
import static org.jahdoo.common.block.BlockInteractionHandler.getItemHandlerAt;
import static org.jahdoo.common.entities.EntityAnimations.*;


public class ChaosCubeEntity extends AbstractTankUser implements MenuProvider, GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int ticker;
    private int entityTicker;
    private AbilityHolder holder;
    public ChaosCubeData getData = ChaosCubeData.initData();

    public ChaosCubeEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.MODULAR_CHAOS_CUBE_BE.get(), pos, state, 1);
    }


    @Override
    public void saveToItem(ItemStack stack, HolderLookup.Provider registries) {
        if(this.holder != null) stack.set(ComponentReg.ABILITY_HOLDER, holder);
        super.saveToItem(stack, registries);
    }

    public void updateData(ChaosCubeData data) {
        this.getData = data;
    }

    @Override
    public int setInputSlots() {
        return 0;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSizeInput() {
        return 0;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 0;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public AbilityHolder getHolder() {
        return holder;
    }

    public void setHolder(AbilityHolder holder) {
        this.holder = holder;
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    private void useSound(float volume, float pitch, Level level) {
        Ability.utilitySpellCastSound(level, getBlockPos().getCenter());
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ChaosCubeMenu(i, inventory, this, this.getData());
    }

    private void particleAnimation(Level level, boolean hasTank) {
        if (entityTicker % 10 != 0 || level.isClientSide) return;
        positionalParticles(level, (hasTank ? 5 : 1), 0.45);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("infuser.progress", progress);
        AbilityHolder.writeTag(holder == null ? AbilityHolder.DEFAULT : holder, tag);
        if(this.getData != null) ChaosCubeData.saveChaosData(tag, this.getData);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("infuser.progress");
        this.holder = AbilityHolder.readTag(tag);
        var data1 = ChaosCubeData.loadChaosData(tag);
        this.getData = data1 == null ? ChaosCubeData.initData() : data1;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 10, state -> state.setAndContinue(IDLE_BLOCK)));
        controllers.add(new AnimationController<>(this, "side_anim", 10, this::getPlayState));
    }

    @Override
    public int setCraftingCost() {
        if(holder == null) return -1;
        return (int) CasterData.getSpecificValue(holder, AbilityBuilder.MANA_COST);
    }

    private void positionalParticles(Level level, int positions, double radius) {
        if(level.isClientSide) return;
        PositionFinders.getRandomSphericalPositions(this.getBlockPos().getCenter(), radius, positions,
            pos -> {
                var directions = this.getBlockPos().getCenter().subtract(pos).normalize();
                var particle = ParticleHandlers.genericParticle(ParticleStore.SOFT_PARTICLE, ElementReg.utility(), (int) (radius * 5), 0.6f);
                ParticleHandlers.sendParticles(level, particle, pos, 0, directions.x, directions.y, directions.z, radius / 5);
            }
        );
    }

    public List<Pair<ResourceLocation, BlockPos>> direction(){
        return List.of(
            Pair.of(Icons.NORTH, this.getBlockPos().north()),
            Pair.of(Icons.WEST, this.getBlockPos().west()),
            Pair.of(Icons.UP, this.getBlockPos().above()),
            Pair.of(Icons.EAST, this.getBlockPos().east()),
            Pair.of(Icons.SOUTH, this.getBlockPos().south()),
            Pair.of(Icons.DOWN, this.getBlockPos().below())
        );
    }

    public void activateConnectedBlocks() {
        if (this.getLevel() == null) return;
        var visited = new HashSet<BlockPos>();
        for (Pair<ResourceLocation, BlockPos> posPair : this.direction()) {
            BlockPos blockPos = posPair.getSecond();
            if (!this.getData.chained()) return;
            if (!visited.contains(blockPos) && this.getLevel().getBlockEntity(blockPos) instanceof ChaosCubeEntity blockE) {
                if(!blockE.getData.chained()) continue;
                visited.add(blockPos);
                triggerBlock(blockE, visited);
            }
        }
    }

    private void triggerBlock(ChaosCubeEntity blockE, Set<BlockPos> visited) {
        var active = this.getData.active();
        var active1 = blockE.getData.active();
        if (active != active1) ChaosCubeHelpers.togglePower(blockE);
        if (blockE.getLevel() == null) return;

        for (Pair<ResourceLocation, BlockPos> posPair : blockE.direction()) {
            BlockPos blockPos = posPair.getSecond();
            if (!visited.contains(blockPos) && blockE.getLevel().getBlockEntity(blockPos) instanceof ChaosCubeEntity blockD) {
                if(!blockD.getData.chained()) continue;
                visited.add(blockPos);
                triggerBlock(blockD, visited);
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if(holder == null && this.ticker > 0) this.ticker = 0; this.progress = 0;

        this.entityTicker ++;
        useAugment(level, false);

        if(this.getData != null && this.getData.active()){
            var speed = this.getData.speed();
            if (this.ticker >= (speed == 0 ? 100 : speed)) this.ticker = 0;
            if(holder != null) this.ticker++;
        } else {
            if (this.ticker > 0) this.ticker = 0;
        }

        this.assignTankBlockInRange(level, pos, 1);
        particleAnimation(level, hasTankAndFuel());

    }

    public ItemStack externalInputInventory(Level level){
        var getNewPos = getDirectionPos(getData.input());
        var handler = getItemHandlerAt(level, getNewPos.getX(), getNewPos.getY(), getNewPos.getZ(), Direction.DOWN);
        AtomicReference<ItemStack> itemStack = new AtomicReference<>(ItemStack.EMPTY);
        handler.ifPresent(
            iItemHandlerObjectPair -> {
                var itemHandler = iItemHandlerObjectPair.getKey();
                for(int i = 0; i < itemHandler.getSlots(); i++){
                    var slotStack = itemHandler.getStackInSlot(i);
                    if(!slotStack.isEmpty()) {
                        itemStack.set(slotStack);
                        return;
                    }
                }
            }
        );
        return itemStack.get();
    }

    public @NotNull BlockPos getDirectionPos(String directionName) {
        var direction = byName(directionName);
        return this.getBlockPos().relative(direction == null ? Direction.UP : direction);
    }

    private PlayState getPlayState(AnimationState<ChaosCubeEntity> state) {
        var data = this.getData;
        if (!data.active()) return PlayState.STOP;

        state.setControllerSpeed(2.1f - (data.speed() / 50f));

        var direction = byName(data.action());
        if (direction == null) return PlayState.STOP;

        return state.setAndContinue(
            switch (direction) {
                case NORTH -> MYS_NORTH;
                case SOUTH -> MYS_SOUTH;
                case EAST  -> MYS_EAST;
                case WEST  -> MYS_WEST;
                case UP    -> MYS_UP;
                case DOWN  -> MYS_DOWN;
            }
        );
    }

    public void useAugment(Level level, boolean ignorePower) {
        if(!hasTankAndFuel()) return;

        var actionDirection = this.getDirectionPos(getData.action());
        var isPowered = getData.active();
        if(!ignorePower) if (!isPowered) return;

        this.progress++;
        if(this.ticker == 1 || ignorePower){
            positionalParticles(level, 30, 0.7);
            useSound(0.05f,1.4f, level);
            var getAbility = AbilityReg.getFirstSpellByTypeId(holder.abilityName());
            if(getAbility.isPresent()){
                if(getAbility.get() instanceof AbstractBlockAbility abstractBlockAbility){
                    abstractBlockAbility.invokeAbilityBlock(actionDirection, this, this.holder);
                }
                this.chargeTankFuel(this.setCraftingCost());
            }
        }

        this.setChanged();
    }

    public boolean externalOutputInventory(Level level, ItemEntity itemEntity){
        var pos = getDirectionPos(this.getData.output());

        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return false;

        var itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, byName(this.getData.output()));
        if (itemHandler == null) return false;

        var sourceStack = itemEntity.getItem();
        var remaining = sourceStack.copy();
        var originalCount = remaining.getCount();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            var slotStack = itemHandler.getStackInSlot(i);

            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, remaining)) {
                remaining = itemHandler.insertItem(i, remaining, false);
                if (remaining.isEmpty()) break;
            }
        }

        for (int i = 0; i < itemHandler.getSlots() && !remaining.isEmpty(); i++) {
            if (itemHandler.getStackInSlot(i).isEmpty()) {
                remaining = itemHandler.insertItem(i, remaining, false);
            }
        }

        int inserted = originalCount - remaining.getCount();
        if (inserted > 0) {
            sourceStack.shrink(inserted);
            if (sourceStack.isEmpty()) itemEntity.discard();
            return true;
        }
        return false;
    }

}

