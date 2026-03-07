package org.jahdoo.trial_nexus.magic.abilities_utility.deprecated.block_placer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.trial_nexus.magic.AbstractUtilityProjectile;
import org.jahdoo.trial_nexus.magic.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static org.jahdoo.common.items.caster_item.CasterItemHelper.getStoredBlock;

public class BlockPlacer extends AbstractUtilityProjectile {

    private static final ResourceLocation abilityId = JahdooHelpers.res("block_placer_property");
    private Level level;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.level = genericProjectile.level();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BlockPlacer();
    }

    @Override
    public String abilityId() {
        return BlockPlacerAbility.abilityId.getPath().intern();
    }

    private static void placeSound(BlockPos blockPos, Direction side, Block replaceBlock, Level level, boolean playSound) {
        var state = replaceBlock.defaultBlockState();
        level.setBlockAndUpdate(blockPos.relative(side), state);
        if(playSound){
            SoundHelpers.getSoundWithPosition(level, blockPos, state.getSoundType().getBreakSound());
        }
    }

    public static void removeItemsFromInv(
        Projectile projectile,
        BlockPos blockPos,
        Direction side,
        Block replaceBlock,
        Player player,
        ItemStack targetBlock,
        Vec3 pos,
        boolean playSound
    ) {
        var level = projectile.level();
        if (level.getBlockState(blockPos.relative(side)).canBeReplaced() && replaceBlock != Blocks.AIR) {
            if(player != null ){
                for (ItemStack itemStack : player.getInventory().items) {
                    if (itemStack.is(targetBlock.getItem()) && player.getInventory().selected != player.getInventory().items.indexOf(itemStack)) {
                        if(!player.isCreative()) itemStack.shrink(1);
                        placeSound(blockPos, side, replaceBlock, level, playSound);
                        break;
                    }
                }
            } else {
                if(level.getBlockEntity(BlockPos.containing(pos)) instanceof ChaosCubeEntity entity){
                    var localStack = entity.externalInputInventory(level);
                    if(!localStack.isEmpty()) entity.externalInputInventory(level).shrink(1);
                    placeSound(blockPos, side, Block.byItem(localStack.getItem()), level, playSound);
                }
            }
        }
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(level.getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        var player = (Player) generic.getOwner();
        var pos = this.generic.blockEntityPos;
        var blockPos = blockHitResult.getBlockPos();
        var side = blockHitResult.getDirection();
        if (level.isClientSide) return;
        var targetBlock = ItemStack.EMPTY;
        var replaceBlock = Blocks.AIR;

        if(player != null){
            var mainHandItem = JahdooHelpers.getUsedItem(player);
            targetBlock = new ItemStack(getStoredBlock(level, mainHandItem));
            replaceBlock = getStoredBlock(level, mainHandItem);
        } else {
            if(pos != null) {
                if(this.level.getBlockEntity(BlockPos.containing(pos)) instanceof ChaosCubeEntity entity){
                    if(!entity.externalInputInventory(level).isEmpty()){
                        targetBlock = entity.externalInputInventory(level);
                        replaceBlock = Block.byItem(targetBlock.getItem());
                    }
                }
            }
        }

        removeItemsFromInv(this.generic, blockPos, side, replaceBlock, player, targetBlock, pos, true);
        generic.discard();
    }

}
