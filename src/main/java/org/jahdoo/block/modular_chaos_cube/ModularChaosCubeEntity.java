package org.jahdoo.block.modular_chaos_cube;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jahdoo.ability.all_abilities.ability_components.AbstractBlockAbility;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.capabilities.player_abilities.ModularChaosCubeProperties;
import org.jahdoo.client.gui.IconLocations;
import org.jahdoo.client.gui.modular_chaos_cube.ModularChaosCubeData;
import org.jahdoo.client.gui.modular_chaos_cube.ModularChaosCubeMenu;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.jahdoo.capabilities.player_abilities.ModularChaosCubeProperties.*;
import static org.jahdoo.registers.AttachmentRegister.*;


public class ModularChaosCubeEntity extends AbstractTankUser implements MenuProvider, GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation WEST = RawAnimation.begin().thenPlay("direction1");
    private static final RawAnimation EAST = RawAnimation.begin().thenPlay("direction2");
    private static final RawAnimation SOUTH = RawAnimation.begin().thenPlay("direction3");
    private static final RawAnimation UP = RawAnimation.begin().thenPlay("direction4");
    private static final RawAnimation DOWN = RawAnimation.begin().thenPlay("direction5");
    private static final RawAnimation NORTH = RawAnimation.begin().thenPlay("direction6");

    public static final int AUGMENT_SLOT = 0;
    private int ticker;
    private int entityTicker;

    @Override
    public int setInputSlots() {
        return 1;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSize() {
        return 64;
    }

    public ItemStack augmentSlot(){
        return this.inputItemHandler.getStackInSlot(AUGMENT_SLOT);
    }

    public ModularChaosCubeEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.MODULAR_CHAOS_CUBE.get(), pPos, pBlockState, 1);
        this.setData(MODULAR_CHAOS_CUBE, ModularChaosCubeProperties.initData(this.getBlockPos()));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("infuser.progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        progress = pTag.getInt("infuser.progress");
    }

    @Override
    public void dropsAllInventory(Level level) {
        super.dropsAllInventory(level);
    }

    public List<Pair<ResourceLocation, BlockPos>> direction(){
        return List.of(
            Pair.of(IconLocations.NORTH, this.getBlockPos().north()),
            Pair.of(IconLocations.WEST, this.getBlockPos().west()),
            Pair.of(IconLocations.UP, this.getBlockPos().above()),
            Pair.of(IconLocations.EAST, this.getBlockPos().east()),
            Pair.of(IconLocations.SOUTH, this.getBlockPos().south()),
            Pair.of(IconLocations.DOWN, this.getBlockPos().below())
        );
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        this.entityTicker ++;
        var augmentSlot = this.inputItemHandler.getStackInSlot(AUGMENT_SLOT);
        useAugment(pLevel, hasTankAndFuel(), augmentSlot);
        if(this.getData(MODULAR_CHAOS_CUBE).active()){
            var speed = this.getData(MODULAR_CHAOS_CUBE).speed();
            if (this.ticker >= (speed == 0 ? 100 : speed)) this.ticker = 0;
            this.ticker++;
        } else {
            if (this.ticker > 0) this.ticker = 0;
        }
        this.assignTankBlockInRange(pLevel, pPos, 1);
        particleAnimation(pLevel, hasTankAndFuel());
    }

    private void useAugment(Level level, boolean hasTank, ItemStack augmentSlot) {
        if(!hasTank) return;
        var isAugment = augmentSlot.getItem() instanceof Augment;
        var hasDirection = getActionDirection(this) != null;
        var isOff = Objects.equals(getActionDirection(this),this.getBlockPos());
        var isPowered = getActive(this);
        if(!isPowered) return;
        if (isAugment && hasDirection && !isOff) {
            this.progress ++;
            if(this.ticker == 1){
                positionalParticles(level, 30, 0.7);
                useSound(0.05f,1.4f, level);
                var getAbilityId = DataComponentHelper.getAbilityTypeItemStack(augmentSlot);
                var getAbility = AbilityRegister.getFirstSpellByTypeId(getAbilityId);
                if(getAbility.isPresent()){
                    if(getAbility.get() instanceof AbstractBlockAbility abstractBlockAbility){
                        abstractBlockAbility.invokeAbilityBlock(getActionDirection(this), this);
                    }
                    this.chargeTankFuel(this.setCraftingCost());
                }
            }

        } else this.progress = 0;
        this.setChanged();
    }

    public void externalOutputInventory(Level level, ItemEntity itemEntity){
        var getPos = this.getData(MODULAR_CHAOS_CUBE).output();
        var handler = getItemHandlerAt(level, getPos.getX(), getPos.getY(), getPos.getZ(), Direction.UP);
        handler.ifPresent(
            iItemHandlerObjectPair -> {
                var entityStack = itemEntity.getItem();
                int remainingAmount = entityStack.getCount();
                var itemHandler = iItemHandlerObjectPair.getKey();
                for(int i = 0; i < itemHandler.getSlots(); i++){
                    int maxStackSize = itemHandler.getSlotLimit(i) == 99 ? 64 : itemHandler.getSlotLimit(i);
                    if (remainingAmount <= 0) break;
                    var slotStack = itemHandler.getStackInSlot(i);
                    int slotSpace = maxStackSize - slotStack.getCount(); // Use maxStackSize variable
                    boolean isStack = slotStack.getItem() == entityStack.getItem() && slotStack.getCount() < maxStackSize;
                    boolean emptySlot = slotStack.isEmpty();

                    if (isStack) {
                        int addAmount = Math.min(remainingAmount, slotSpace);
                        slotStack.grow(addAmount);
                        remainingAmount -= addAmount;
                        entityStack.shrink(addAmount);
                    } else if (emptySlot) {
                        int addAmount = Math.min(remainingAmount, maxStackSize);
                        itemHandler.insertItem(i, entityStack.copy().split(addAmount), false);
                        remainingAmount -= addAmount;
                        entityStack.shrink(addAmount);
                    }
                }
            }
        );
    }

    public ItemStack externalInputInventory(Level level){
        var getPos = this.getData(MODULAR_CHAOS_CUBE).input();
        var handler = getItemHandlerAt(level, getPos.getX(), getPos.getY(), getPos.getZ(), Direction.DOWN);
        AtomicReference<ItemStack> itemStack = new AtomicReference<>(ItemStack.EMPTY);
        handler.ifPresent(
            iItemHandlerObjectPair -> {
                var itemHandler = iItemHandlerObjectPair.getKey();
                for(int i = 0; i < itemHandler.getSlots(); i++){
                    var slotStack = itemHandler.getStackInSlot(i);
                    if(!slotStack.isEmpty()) {
                        itemStack.set(slotStack);
                        return;
                    };
                }
            }
        );
        return itemStack.get();
    }

    public void activateConnectedBlocks() {
        if (this.getLevel() == null) return;
        Set<BlockPos> visited = new HashSet<>(); // Keep track of visited blocks

        for (Pair<ResourceLocation, BlockPos> posPair : this.direction()) {
            BlockPos blockPos = posPair.getSecond();
            if (!this.getData(MODULAR_CHAOS_CUBE).chained()) return;
            if (!visited.contains(blockPos) && this.getLevel().getBlockEntity(blockPos) instanceof ModularChaosCubeEntity blockE) {
                if(!blockE.getData(MODULAR_CHAOS_CUBE).chained()) continue;
                visited.add(blockPos); // Mark as visited
                triggerBlock(blockE, visited); // Pass visited set
            }
        }
    }

    private void triggerBlock(ModularChaosCubeEntity blockE, Set<BlockPos> visited) {
        boolean active = this.getData(MODULAR_CHAOS_CUBE).active();
        boolean active1 = blockE.getData(MODULAR_CHAOS_CUBE).active();
        if (active != active1) ModularChaosCubeData.togglePower(blockE);
        if (blockE.getLevel() == null) return;

        for (Pair<ResourceLocation, BlockPos> posPair : blockE.direction()) {
            BlockPos blockPos = posPair.getSecond();
            if (!visited.contains(blockPos) && blockE.getLevel().getBlockEntity(blockPos) instanceof ModularChaosCubeEntity blockD) {
                if(!blockD.getData(MODULAR_CHAOS_CUBE).chained()) continue;
                visited.add(blockPos); // Mark as visited
                triggerBlock(blockD, visited); // Recurse for the next block
            }
        }
    }

    public static Optional<org.apache.commons.lang3.tuple.Pair<IItemHandler, Object>> getItemHandlerAt(Level worldIn, double x, double y, double z, Direction side) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        BlockState state = worldIn.getBlockState(blockpos);
        BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(blockpos) : null;
        IItemHandler blockCap = worldIn.getCapability(Capabilities.ItemHandler.BLOCK, blockpos, state, blockEntity, side);
        if (blockCap != null) {
            return Optional.of(ImmutablePair.of(blockCap, blockEntity));
        } else {
            List<Entity> list = worldIn.getEntities((Entity)null, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelector.ENTITY_STILL_ALIVE);
            if (!list.isEmpty()) {
                Collections.shuffle(list);
                for (Entity entity : list) {
                    IItemHandler entityCap = entity.getCapability(Capabilities.ItemHandler.ENTITY_AUTOMATION, side);
                    if (entityCap != null) {
                        return Optional.of(ImmutablePair.of(entityCap, entity));
                    }
                }
            }
            return Optional.empty();
        }
    }

    private void useSound(float volume, float pitch, Level level) {
        ModHelpers.getSoundWithPosition(level, this.getBlockPos(), SoundEvents.BREEZE_CHARGE, volume, pitch);
    }

    private void particleAnimation(Level pLevel, boolean hasTank) {
        if (entityTicker % 10 != 0 || pLevel.isClientSide) return;
        positionalParticles(pLevel, (hasTank ? 5 : 1), 0.45);
    }
    
    private void positionalParticles(Level pLevel,int positions, double radius) {
        if(pLevel.isClientSide) return;
        PositionGetters.getRandomSphericalPositions(this.getBlockPos().getCenter(), radius, positions,
            pos -> {
                var directions = this.getBlockPos().getCenter().subtract(pos).normalize();
                var particle = ParticleHandlers.genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, ElementRegistry.UTILITY.get(), (int) (radius * 5), 0.6f);
                ParticleHandlers.sendParticles(pLevel, particle, pos, 0, directions.x, directions.y, directions.z, radius / 5);
            }
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 10, state -> state.setAndContinue(IDLE)));
        controllers.add(new AnimationController<>(this, "side_anim", 10, this::getPlayState));
    }

    private PlayState getPlayState(AnimationState<ModularChaosCubeEntity> state) {
        var getPos = this.getData(MODULAR_CHAOS_CUBE);
        if(getPos.active()){
            float speed = (float) getPos.speed() /50;
            state.setControllerSpeed(2.1f - speed);
            if (getPos.action().equals(this.getBlockPos().north())) {
                return state.setAndContinue(NORTH);
            } else if (getPos.action().equals(this.getBlockPos().south())) {
                return state.setAndContinue(SOUTH);
            } else if (getPos.action().equals(this.getBlockPos().east())) {
                return state.setAndContinue(EAST);
            } else if (getPos.action().equals(this.getBlockPos().west())) {
                return state.setAndContinue(WEST);
            } else if (getPos.action().equals(this.getBlockPos().above())) {
                return state.setAndContinue(UP);
            } else if(getPos.action().equals(this.getBlockPos().below())){
                return state.setAndContinue(DOWN);
            }
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ModularChaosCubeMenu(i, inventory, this, this.data);
    }

    @Override
    public int setCraftingCost() {
        return 1;
    }
}

