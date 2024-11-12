package org.jahdoo.block.automation_block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.block.AbstractTankUser;
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
import java.util.List;
import java.util.Objects;

import static org.jahdoo.registers.AttachmentRegister.BOOL;
import static org.jahdoo.registers.AttachmentRegister.POS;


public class AutomationBlockEntity extends AbstractTankUser implements MenuProvider, GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation NS = RawAnimation.begin().thenPlay("north_and_south");
    private static final RawAnimation EW = RawAnimation.begin().thenPlay("east_and_west");
    private static final RawAnimation UD = RawAnimation.begin().thenPlay("up_and_down");
    public static final int AUGMENT_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 0;

    public BlockPos direction;
    private int ticker;

    @Override
    public int setInputSlots() {
        return 2;
    }

    @Override
    public int setOutputSlots() {
        return 1;
    }

    @Override
    public int getMaxSlotSize() {
        return 64;
    }

    public AutomationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.AUTOMATION_BLOCK.get(), pPos, pBlockState, 1);
        this.setData(AttachmentRegister.POS, pPos.north());
        this.setData(AttachmentRegister.BOOL, false);
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
        if(this.hasData(POS)) direction = this.getData(POS);
    }

    @Override
    public void dropsAllInventory(Level level) {
        super.dropsAllInventory(level);
    }

    public List<Pair<String, BlockPos>> direction(){
        return List.of(
            Pair.of("North", this.getBlockPos().north()),
            Pair.of("South", this.getBlockPos().south()),
            Pair.of("East", this.getBlockPos().east()),
            Pair.of("West", this.getBlockPos().west()),
            Pair.of("Down", this.getBlockPos().below()),
            Pair.of("Up", this.getBlockPos().above())
        );
    }

    public void setDirection(String direction) {
        var pos = direction().stream()
            .filter(pair -> pair.getFirst().equals(direction))
            .findFirst();
        pos.ifPresent(pair -> this.direction = pair.getSecond());
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
        var hasDirection = this.direction != null;
        var isOff = Objects.equals(this.direction,this.getBlockPos());
        var isPowered = this.getData(AttachmentRegister.BOOL);
        if(!isPowered) return;
        if (isAugment && hasDirection && !isOff) {
            this.progress ++;
            if(this.ticker % 10 == 0){
                positionalParticles(level, 30, 0.7);
                extracted(0.05f,1.4f, level);
                var getAbilityId = DataComponentHelper.getAbilityTypeItemStack(augmentSlot);
                var getAbility = AbilityRegister.getFirstSpellByTypeId(getAbilityId);
                if(getAbility.isPresent()){
                    getAbility.get().invokeAbilityBlock(this.direction, this);
                    this.chargeTankFuel(this.setCraftingCost());
                }
            }
        } else this.progress = 0;
    }

    private void extracted(float volume, float pitch, Level level) {
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
                    var pos = this.getData(POS);
                    if(this.hasData(POS)){
                        System.out.println(pos);
                        if(pos == this.getBlockPos().north() || pos == this.getBlockPos().south()){

                            System.out.println(pos == this.getBlockPos().north());
                            System.out.println(pos == this.getBlockPos().south());
                            return state.setAndContinue(NS);
                        }

                        if(pos == this.getBlockPos().above() || pos == this.getBlockPos().below()){
                            return state.setAndContinue(UD);
                        }
                    }
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

