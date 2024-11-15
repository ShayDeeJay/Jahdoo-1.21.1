package org.jahdoo.block.automation_block;

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
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.capabilities.player_abilities.AutoBlock;
import org.jahdoo.client.gui.automation_block.AutomationBlockMenu;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.jahdoo.capabilities.player_abilities.AutoBlock.*;
import static org.jahdoo.client.gui.IconLocations.*;
import static org.jahdoo.registers.AttachmentRegister.*;


public class AutomationBlockEntity extends AbstractTankUser implements MenuProvider, GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation NS = RawAnimation.begin().thenPlay("north_and_south");
    private static final RawAnimation EW = RawAnimation.begin().thenPlay("east_and_west");
    private static final RawAnimation UD = RawAnimation.begin().thenPlay("up_and_down");
    public static final int AUGMENT_SLOT = 0;
    private int ticker;

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

    public AutomationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.AUTOMATION_BLOCK.get(), pPos, pBlockState, 1);
        this.setData(AUTO_BLOCK, AutoBlock.initData(this.getBlockPos()));
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
            Pair.of(NORTH, this.getBlockPos().north()),
            Pair.of(SOUTH, this.getBlockPos().south()),
            Pair.of(EAST, this.getBlockPos().east()),
            Pair.of(WEST, this.getBlockPos().west()),
            Pair.of(DOWN, this.getBlockPos().below()),
            Pair.of(UP, this.getBlockPos().above())
        );
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        this.ticker++;
        this.assignTankBlockInRange(pLevel, pPos, 1);
        var augmentSlot = this.inputItemHandler.getStackInSlot(AUGMENT_SLOT);
        particleAnimation(pLevel, hasTankAndFuel());
        useAugment(pLevel, hasTankAndFuel(), augmentSlot);
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
            var speed  = this.getData(AUTO_BLOCK).speed();
            if(this.ticker % (speed == 0 ? 100 : speed) == 0){
                positionalParticles(level, 30, 0.7);
                useSound(0.05f,1.4f, level);
                var getAbilityId = DataComponentHelper.getAbilityTypeItemStack(augmentSlot);
                var getAbility = AbilityRegister.getFirstSpellByTypeId(getAbilityId);
                if(getAbility.isPresent()){
                    getAbility.get().invokeAbilityBlock(getActionDirection(this), this);
                    this.chargeTankFuel(this.setCraftingCost());
                }
            }
        } else this.progress = 0;
        this.setChanged();
    }

    public void externalOutputInventory(Level level, ItemEntity itemEntity){
        var getPos = this.getData(AUTO_BLOCK).output();
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
        var getPos = this.getData(AUTO_BLOCK).input();
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
        if (ticker % 10 != 0 || pLevel.isClientSide) return;
        positionalParticles(pLevel, (hasTank ? 5 : 1), 0.45);
    }
    
    private void positionalParticles(Level pLevel,int positions, double radius) {
        if(pLevel.isClientSide) return;
        PositionGetters.getRandomSphericalPositions(this.getBlockPos().getCenter(), radius, positions,
            pos -> {
                var directions = this.getBlockPos().getCenter().subtract(pos).normalize();
                var particle = ParticleHandlers.genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, ElementRegistry.UTILITY.get(), (int) (radius * 5), 0.8f);
                ParticleHandlers.sendParticles(pLevel, particle, pos, 0, directions.x, directions.y, directions.z, radius / 5);
            }
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(
                this,
                state -> {
                    var posData = AutoBlock.getActionDirection(this);
                    if(this.hasData(AUTO_BLOCK)){
                        var pos = this.getBlockPos();
                        if(posData.equals(pos.north()) || posData.equals(pos.south())) return state.setAndContinue(NS);
                        if(posData.equals(pos.above()) || posData.equals(pos.below())) return state.setAndContinue(UD);
                    }
                    this.setChanged();
                    return state.setAndContinue(EW);
                }
            )
        );
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
        return new AutomationBlockMenu(i, inventory, this, this.data);
    }

    @Override
    public int setCraftingCost() {
        return 1;
    }
}

