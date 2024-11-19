package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;

public class BlockPlacer extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.res("block_placer_property");
    Level level;

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
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(level.getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        var player = (Player) genericProjectile.getOwner();
        var pos = this.genericProjectile.blockEntityPos;
        var blockPos = blockHitResult.getBlockPos();
        var side = blockHitResult.getDirection();
        if (level.isClientSide) return;
        var targetBlock = ItemStack.EMPTY;
        var replaceBlock = Blocks.AIR;

        if(player != null){
            targetBlock = new ItemStack(WandItem.getStoredBlock(level, player.getMainHandItem()));
            replaceBlock = WandItem.getStoredBlock(level, player.getMainHandItem());
        } else {
            if(pos != null) {
                if(this.level.getBlockEntity(BlockPos.containing(pos)) instanceof ModularChaosCubeEntity entity){
                    if(!entity.externalInputInventory(level).isEmpty()){
                        targetBlock = entity.externalInputInventory(level);
                        replaceBlock = Block.byItem(targetBlock.getItem());
                    }
                }
            }
        }

        removeItemsFromInv(this.genericProjectile, blockPos, side, replaceBlock, player, targetBlock, pos);
        super.onBlockBlockHit(blockHitResult);
        genericProjectile.discard();
    }

    public static void removeItemsFromInv(Projectile projectile, BlockPos blockPos, Direction side, Block replaceBlock, Player player, ItemStack targetBlock, Vec3 pos) {
        var level = projectile.level();
        if (level.getBlockState(blockPos.relative(side)).canBeReplaced() && replaceBlock != Blocks.AIR) {
            if(player != null ){
                for (ItemStack itemStack : player.getInventory().items) {
                    if (itemStack.is(targetBlock.getItem()) && player.getInventory().selected != player.getInventory().items.indexOf(itemStack)) {
                        if(!player.isCreative()) itemStack.shrink(1);
                        extracted(blockPos, side, replaceBlock, level);
                        break;
                    }
                }
            } else {
                if(level.getBlockEntity(BlockPos.containing(pos)) instanceof ModularChaosCubeEntity entity){
                    var localStack = entity.externalInputInventory(level);
                    if(!localStack.isEmpty()) entity.externalInputInventory(level).shrink(1);
                    extracted(blockPos, side, Block.byItem(localStack.getItem()), level);
                }
            }
        }
    }

    private static void extracted(BlockPos blockPos, Direction side, Block replaceBlock, Level level) {
        BlockState state = replaceBlock.defaultBlockState();
        level.setBlockAndUpdate(blockPos.relative(side), state);
        ModHelpers.getSoundWithPosition(level, blockPos, state.getSoundType().getBreakSound());
    }
}
