package org.jahdoo.trial_nexus.magic.abilities_utility.enchanted_fusion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.block.enchanted_block.ConverterValues;
import org.jahdoo.common.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.trial_nexus.magic.AbstractUtilityProjectile;
import org.jahdoo.trial_nexus.magic.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;

import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_PARTICLE;
import static org.jahdoo.common.registers.mod.ElementReg.utility;

public class EnchantedFusion extends AbstractUtilityProjectile {

    private static final ResourceLocation abilityId = JahdooHelpers.res("enchanted_fusion_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new EnchantedFusion();
    }

    @Override
    public String abilityId() {
        return EnchantedFusionAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        var level = getLevel();
        if(LevelGenerator.isNexus(level)) return;

        if(level.getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        var pos = blockHitResult.getBlockPos();
        var state = level.getBlockState(pos);

        if(level instanceof ServerLevel serverLevel){
            if(ConverterValues.isConvertibleBlock(state.getBlock())){
                level.setBlockAndUpdate(pos, BlockReg.ENCHANTED_BLOCK.get().defaultBlockState());
                if (level.getBlockEntity(pos) instanceof EnchantedBlockEntity enchantedBlockEntity) {
                    if (!state.isAir()) {
//                        enchantedBlockEntity.setBlockType(state.getBlock(), 0);
                    }
                }

                PositionFinders.getCubeCornersAndFaceCenters(
                    pos,0.8, pos1 -> {
                        var directions = pos.getCenter().subtract(pos1).normalize();
                        var particle = ParticleHandlers.genericParticle(MAGIC_PARTICLE, utility(), 20, 1f);
                        sendParticles(serverLevel, particle, pos1, 0, directions.x, directions.y, directions.z, 0.1);
                    }
                );
            }
        }

        generic.discard();
    }

}
