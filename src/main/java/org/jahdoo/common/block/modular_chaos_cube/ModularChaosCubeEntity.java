package org.jahdoo.common.block.modular_chaos_cube;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbstractBlockAbility;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.ascension.attachments.player_abilities.ModularChaosCubeProperties;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.items.augments.Augment;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.jahdoo.common.block.BlockInteractionHandler.getItemHandlerAt;
import static org.jahdoo.ascension.attachments.player_abilities.ModularChaosCubeProperties.*;
import static org.jahdoo.common.components.DataComponentHelper.getKeyFromAugment;
import static org.jahdoo.common.components.DataComponentHelper.getSpecificValue;
import static org.jahdoo.common.entities.EntityAnimations.*;
import static org.jahdoo.common.registers.AttachmentReg.*;


public class ModularChaosCubeEntity extends AbstractTankUser implements MenuProvider, GeoBlockEntity {

    public static final int AUGMENT_SLOT = 0;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int ticker;
    private int entityTicker;

    public ModularChaosCubeEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.MODULAR_CHAOS_CUBE_BE.get(), pos, state, 1);
        this.setData(MODULAR_CHAOS_CUBE, ModularChaosCubeProperties.initData(this.getBlockPos()));
    }

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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    private void useSound(float volume, float pitch, Level level) {
        Helpers.getSoundWithPosition(level, this.getBlockPos(), SoundEvents.BREEZE_CHARGE, volume, pitch);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ModularChaosCubeMenu(i, inventory, this, this.data);
    }

    private void particleAnimation(Level level, boolean hasTank) {
        if (entityTicker % 10 != 0 || level.isClientSide) return;
        positionalParticles(level, (hasTank ? 5 : 1), 0.45);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("infuser.progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("infuser.progress");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 10, state -> state.setAndContinue(IDLE_BLOCK)));
        controllers.add(new AnimationController<>(this, "side_anim", 10, this::getPlayState));
    }

    @Override
    public int setCraftingCost() {
        var getHolder = this.augmentSlot().get(ComponentReg.WAND_ABILITY_HOLDER);
        if(getHolder == null) return -1;
        return (int) getSpecificValue(getKeyFromAugment(this.augmentSlot()),  getHolder, AbilityBuilder.MANA_COST);
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
            if (!this.getData(MODULAR_CHAOS_CUBE).chained()) return;
            if (!visited.contains(blockPos) && this.getLevel().getBlockEntity(blockPos) instanceof ModularChaosCubeEntity blockE) {
                if(!blockE.getData(MODULAR_CHAOS_CUBE).chained()) continue;
                visited.add(blockPos);
                triggerBlock(blockE, visited);
            }
        }
    }

    private void triggerBlock(ModularChaosCubeEntity blockE, Set<BlockPos> visited) {
        var active = this.getData(MODULAR_CHAOS_CUBE).active();
        var active1 = blockE.getData(MODULAR_CHAOS_CUBE).active();
        if (active != active1) ModularChaosCubeData.togglePower(blockE);
        if (blockE.getLevel() == null) return;

        for (Pair<ResourceLocation, BlockPos> posPair : blockE.direction()) {
            BlockPos blockPos = posPair.getSecond();
            if (!visited.contains(blockPos) && blockE.getLevel().getBlockEntity(blockPos) instanceof ModularChaosCubeEntity blockD) {
                if(!blockD.getData(MODULAR_CHAOS_CUBE).chained()) continue;
                visited.add(blockPos);
                triggerBlock(blockD, visited);
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if(this.augmentSlot().isEmpty() && this.ticker > 0) this.ticker = 0; this.progress = 0;
        this.entityTicker ++;
        useAugment(level, hasTankAndFuel(), augmentSlot());
        if(this.getData(MODULAR_CHAOS_CUBE).active()){
            var speed = this.getData(MODULAR_CHAOS_CUBE).speed();
            if (this.ticker >= (speed == 0 ? 100 : speed)) this.ticker = 0;
            if(!this.augmentSlot().isEmpty()) this.ticker++;
        } else {
            if (this.ticker > 0) this.ticker = 0;
        }
        this.assignTankBlockInRange(level, pos, 1);
        particleAnimation(level, hasTankAndFuel());
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
                var getAbility = AbilityReg.getFirstSpellByTypeId(getAbilityId);
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
}

