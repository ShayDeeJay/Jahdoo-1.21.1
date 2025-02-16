package org.jahdoo.ability.abilities.ability.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ability.AbstractUtilityProjectile;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.Utility.WallPlacerAbility;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.ability.AbilityBuilder.OFFSET;
import static org.jahdoo.ability.AbilityBuilder.SIZE;
import static org.jahdoo.ability.abilities.ability.utility.BlockPlacer.removeItemsFromInv;
import static org.jahdoo.items.wand.WandItemHelper.getStoredBlock;

public class WallPlacer extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.res("wall_placer_property");
    Level level;
    double breakerSize;
    int size;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.breakerSize = this.getTagUtility(SIZE);
        var offset = (int) this.getTagUtility(OFFSET);
        this.size = (int) ((breakerSize/2) - offset);
        this.level = genericProjectile.level();
    }

    @Override
    public String abilityId() {
        return WallPlacerAbility.abilityId.getPath().intern();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new WallPlacer();
    }


    public int getthig(boolean isLookingUpAndDown, BlockHitResult blockHitResult, int breakerSize){
        var direction = blockHitResult.getDirection();
        if(isLookingUpAndDown){
            if(direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) return 0; else return -1;
        } else {
            return -breakerSize-1;
        }
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        var owner = (LivingEntity) genericProjectile.getOwner();
        if(owner == null && genericProjectile.blockEntityPos == null) return;

        var radius = (int) (this.breakerSize / 2);
        var pos = blockHitResult.getBlockPos();
        var pDirection = owner == null ? genericProjectile.getDirection() : owner.getDirection();
        var lookAngleY = genericProjectile.getLookAngle().y;
        var isLookingUpOrDown = lookAngleY < -0.8 || lookAngleY > 0.8;
        var axisZ = pDirection.getAxis() == Direction.Axis.Z;
        var axisX = pDirection.getAxis() == Direction.Axis.X;
        var player = (Player) genericProjectile.getOwner();
        var pos1 = this.genericProjectile.blockEntityPos;
        var side = blockHitResult.getDirection();
        var targetBlock = ItemStack.EMPTY;
        var replaceBlock = Blocks.AIR;

        if(player != null){
            targetBlock = new ItemStack(getStoredBlock(level, player.getMainHandItem()));
            replaceBlock = getStoredBlock(level, player.getMainHandItem());
        } else {
            if(pos1 != null) {
                if(this.level.getBlockEntity(BlockPos.containing(pos1)) instanceof ModularChaosCubeEntity entity){
                    if(!entity.externalInputInventory(level).isEmpty()){
                        targetBlock = entity.externalInputInventory(level);
                        replaceBlock = Block.byItem(targetBlock.getItem());
                    }
                }
            }
        }

        var isPos = this.genericProjectile.blockEntityPos != null;
        pos = pos.relative(lookAngleY < -0.8 ? pDirection.getOpposite() : pDirection, !isLookingUpOrDown || isPos ? 0 : size).above(isLookingUpOrDown || isPos ? 0 : size);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos offsetPos = pos.offset(
                        x * (isLookingUpOrDown || axisZ ? 1 : 0),
                        y * (isLookingUpOrDown ? 0 : 1),
                        z * (isLookingUpOrDown || axisX ? 1 : 0)
                    );
                    removeItemsFromInv(genericProjectile, offsetPos, side, replaceBlock, player, targetBlock, pos1, false);
                }
            }
        }
        ModHelpers.getSoundWithPosition(level, pos, replaceBlock.getSoundType(replaceBlock.defaultBlockState(), level, pos, null).getPlaceSound(), 1, 1);
        genericProjectile.discard();
    }


}
