package org.jahdoo.ascension.ability.abilities.enchanted_fusion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ascension.ability.AbstractUtilityProjectile;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.abilities.block_breaker.BlockBreakerAbility;
import org.jahdoo.common.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.common.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.common.networking.packet.server2client.EnchantedBlockS2C;
import org.jahdoo.common.registers.BlocksRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;

import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;
import static org.jahdoo.common.registers.ElementRegistry.UTILITY;
import static org.jahdoo.common.registers.ElementRegistry.utility;

public class EnchantedFusion extends AbstractUtilityProjectile {
    ResourceLocation abilityId = Helpers.res("enchanted_fusion_property");

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        var level = getLevel();
        if(level.getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        var pos = blockHitResult.getBlockPos();
        var state = level.getBlockState(pos);
        if(level instanceof ServerLevel serverLevel){
            if(EnchantedBlockEntity.ConverterValues.isConvertibleBlock(state.getBlock())){
                level.setBlockAndUpdate(pos, BlocksRegister.ENCHANTED_BLOCK.get().defaultBlockState());
                if (level.getBlockEntity(pos) instanceof EnchantedBlockEntity enchantedBlockEntity) {
                    if (!state.isAir()) {
                        Helpers.sendPacketsToPlayer(serverLevel, new EnchantedBlockS2C(pos, state, 0, 1000, 0));
                        enchantedBlockEntity.setBlockType(state.getBlock(), 0);
                    }
                }

                PositionFinders.getCubeCornersAndFaceCenters(
                    pos,0.8, pos1 -> {
                        var directions = pos.getCenter().subtract(pos1).normalize();
                        var particle = genericParticleOptions(MAGIC_PARTICLE_SELECTION, utility(), 20, 1f);
                        sendParticles(serverLevel, particle, pos1, 0, directions.x, directions.y, directions.z, 0.1);
                    }
                );
            }
        }
        genericProjectile.discard();
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
    }

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
        return BlockBreakerAbility.abilityId.getPath().intern();
    }
}
