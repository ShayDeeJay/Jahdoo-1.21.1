package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.all_magic.AbilityBuilder.RANGE;
import static org.jahdoo.all_magic.all_abilities.abilities.Utility.FarmersTouchAbility.GROWTH_CHANCE;
import static org.jahdoo.all_magic.all_abilities.abilities.Utility.FarmersTouchAbility.HARVEST_CHANCE;

public class BlockPlacer extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.modResourceLocation("block_placer_property");
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
        if(level.getBlockEntity(blockHitResult.getBlockPos()) instanceof AutomationBlockEntity) return;
        var player = (Player) genericProjectile.getOwner();
        var pos = this.genericProjectile.blockEntityPos;
        var blockPos = blockHitResult.getBlockPos();
        var side = blockHitResult.getDirection();
        if (level.isClientSide) return;
        var targetBlock = ItemStack.EMPTY;
        var replaceBlock = Blocks.AIR;

        if(player != null){
            targetBlock = player.getOffhandItem();
            replaceBlock = Block.byItem(targetBlock.getItem());
        } else {
            if(pos != null) {
                if(this.level.getBlockEntity(BlockPos.containing(pos)) instanceof AutomationBlockEntity entity){
                    if(!entity.externalInputInventory(level).isEmpty()){
                        targetBlock = entity.externalInputInventory(level);
                        replaceBlock = Block.byItem(targetBlock.getItem());
                    }
                }
            }
        }

        if (level.getBlockState(blockPos.relative(side)).canBeReplaced() && replaceBlock != Blocks.AIR) {
            level.setBlockAndUpdate(blockPos.relative(side), replaceBlock.defaultBlockState());
            ModHelpers.getSoundWithPosition(level, blockPos, replaceBlock.defaultBlockState().getSoundType().getBreakSound());
            if(player != null ){
                if(!player.isCreative()){
                    boolean itemFound = false;
                    for (ItemStack itemStack : player.getInventory().items) {
                        if (itemStack.is(targetBlock.getItem()) && player.getInventory().selected != player.getInventory().items.indexOf(itemStack)) {
                            itemStack.shrink(1);
                            itemFound = true;
                            break;
                        }
                    }

                    if (!itemFound && player.getInventory().selected != -1) {
                        ItemStack selectedSlotItem = player.getInventory().getItem(player.getInventory().selected);
                        (selectedSlotItem.is(targetBlock.getItem()) ? selectedSlotItem : targetBlock).shrink(1);
                    }
                }
            } else {
                if(this.level.getBlockEntity(BlockPos.containing(pos)) instanceof AutomationBlockEntity entity){
                    entity.externalInputInventory(level).shrink(1);
                }
            }
        }
        super.onBlockBlockHit(blockHitResult);
        genericProjectile.discard();
    }
}
