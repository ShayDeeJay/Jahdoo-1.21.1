package org.jahdoo.trial_nexus.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.ComposterBlock.LEVEL;
import static net.minecraft.world.level.block.ComposterBlock.getValue;
import static org.jahdoo.trial_nexus.ability.abilities_combat.permafrost.PermafrostAbility.abilityId;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class MixinMethods {

    public static void onTargetHit(Vec3 pos, Level level){
        var aoeCloud = new AoeCloud(level, null, 0f, EntityDataReg.BARRAGE.get().setAbilityId(), WAND_ABILITY_HOLDER_BARRAGE, abilityId.getPath().intern());
        aoeCloud.setPos(pos.x, pos.y, pos.z);
        level.addFreshEntity(aoeCloud);
    }

    private static final AbilityHolder WAND_ABILITY_HOLDER_BARRAGE =
        new AbilityBuilder(null, abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(1200)
            .setEffectDurationWithValue(300, 100, 100)
            .setEffectStrengthWithValue(10, 5,5)
            .setModifierWithoutBounds(AbilityBuilder.LIFETIME, 100)
            .setModifierWithoutBounds(AbilityBuilder.AOE, 2)
            .buildAndReturn();

    public static boolean hasBlockNeeded(Level level, BlockPos pos){
        var directions = Direction.stream().toList();
        for (var direction : directions) {
            if(level.getBlockState(pos.relative(direction)).is(BlockReg.MYSTICAL_AUGMENTER)){
                return true;
            }
        }

        return false;
    }

    public static ItemStack getCorrectDrop(BlockPos pos, Level level){
        return hasBlockNeeded(level, pos) ? new ItemStack(ItemReg.NEXITE_POWDER) : new ItemStack(Items.BONE_MEAL);
    }

    public static void onTickComposter(Level level, BlockPos pos){
        for(int i = 0; i < 4; i++){
            var dustPlume = ParticleHandlers.genericParticle(ParticleStore.MAGIC_PARTICLE, ElementReg.utility(), 10, 2, false, 1);
            var posX = (double) pos.getX() + Random.nextDouble(0.20, 0.80);
            var posY = (double) pos.getY() + Random.nextDouble(0.1, 1);
            var posZ = (double) pos.getZ() + Random.nextDouble(0.20, 0.80);
            level.addParticle(dustPlume, posX, posY, posZ, 0, Random.nextDouble(0.05, 0.20), 0F);
        }
    }

    public static class OutputContainer extends SimpleContainer implements WorldlyContainer {
        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;

        public OutputContainer(BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
            super(stack);
            this.state = state;
            this.level = level;
            this.pos = pos;
        }

        public int getMaxStackSize() {
            return 1;
        }

        public int[] getSlotsForFace(Direction side) {
            return side == Direction.DOWN ? new int[]{0} : new int[0];
        }

        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return false;
        }

        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            var boneMeal = Items.BONE_MEAL;
            var nexite = ItemReg.NEXITE_POWDER;
            var isValidItem = stack.is(boneMeal) || stack.is(nexite);
            return !this.changed && direction == Direction.DOWN && isValidItem;
        }

        public void setChanged() {
            empty(null, this.state, this.level, this.pos);
            this.changed = true;
        }
    }

    public static class InputContainer extends SimpleContainer implements WorldlyContainer {
        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;

        public InputContainer(BlockState state, LevelAccessor level, BlockPos pos) {
            super(1);
            this.state = state;
            this.level = level;
            this.pos = pos;
        }

        public int getMaxStackSize() {
            return 1;
        }

        public int[] getSlotsForFace(Direction side) {
            return side == Direction.UP ? new int[]{0} : new int[0];
        }

        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return !this.changed && direction == Direction.UP && getValue(itemStack) > 0.0F;
        }

        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return false;
        }

        public void setChanged() {
            ItemStack itemstack = this.getItem(0);
            if (!itemstack.isEmpty()) {
                this.changed = true;
                BlockState blockstate = MixinMethods.addItem(this.state, this.level, this.pos, itemstack);
                this.level.levelEvent(1500, this.pos, blockstate != this.state ? 1 : 0);
                this.removeItemNoUpdate(0);
            }

        }
    }

    public static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
        public EmptyContainer() {
            super(0);
        }

        public int[] getSlotsForFace(Direction side) {
            return new int[0];
        }

        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return false;
        }

        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return false;
        }
    }

    public static BlockState addItem(BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
        int i = state.getValue(LEVEL);
        float f = getValue(stack);
        if ((i != 0 || !(f > 0.0F)) && !(level.getRandom().nextDouble() < (double)f)) {
            return state;
        } else {
            int j = i + 1;
            BlockState blockstate = state.setValue(LEVEL, j);
            level.setBlock(pos, blockstate, 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, blockstate));
            if (j == 7) {
                level.scheduleTick(pos, state.getBlock(), 20);
            }

            return blockstate;
        }
    }

    public static BlockState empty(@Nullable Entity entity, BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState blockstate = state.setValue(LEVEL, 0);
        level.setBlock(pos, blockstate, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
        return blockstate;
    }

}
