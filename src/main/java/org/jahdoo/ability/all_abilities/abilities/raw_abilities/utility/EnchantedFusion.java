package org.jahdoo.ability.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ability.AbstractUtilityProjectile;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.UtilityHelpers;
import org.jahdoo.ability.all_abilities.abilities.Utility.BlockBreakerAbility;
import org.jahdoo.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.networking.packet.server2client.EnchantedBlockS2C;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.ElementRegistry.UTILITY;

public class EnchantedFusion extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.res("enchanted_fusion_property");

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        var level = getLevel();
        if(level.getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        var pos = blockHitResult.getBlockPos();
        var state = level.getBlockState(pos);
        if(level instanceof ServerLevel serverLevel){
            if(EnchantedBlockEntity.ConverterValues.isConvertibleBlock(state.getBlock())){
                level.setBlockAndUpdate(pos, BlocksRegister.ENCHANTED_BLOCK.get().defaultBlockState());
                if (level.getBlockEntity(pos) instanceof EnchantedBlockEntity enchantedBlockEntity) {
                    if (!state.isAir()) {
                        ModHelpers.sendPacketsToPlayer(serverLevel, new EnchantedBlockS2C(pos, state, 0, 1000, 0));
                        enchantedBlockEntity.setBlockType(state.getBlock(), 0);
                    }
                }

                PositionGetters.getCubeCornersAndFaceCenters(
                    pos,0.8, pos1 -> {
                        var directions = pos.getCenter().subtract(pos1).normalize();
                        var particle = genericParticleOptions(MAGIC_PARTICLE_SELECTION, UTILITY.get(), 20, 1f);
                        sendParticles(serverLevel, particle, pos1, 0, directions.x, directions.y, directions.z, 0.1);
                    }
                );
            }
        }
        super.onBlockBlockHit(blockHitResult);
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
