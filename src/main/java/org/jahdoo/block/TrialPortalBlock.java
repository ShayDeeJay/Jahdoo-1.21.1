package org.jahdoo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.challenge.LevelGenerator;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.AttachmentRegister.*;
import static org.jahdoo.utils.ColourStore.PERK_GREEN;

public class TrialPortalBlock extends NetherPortalBlock {
    public TrialPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {}

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            var z = (double) pos.getZ() + (double) 0.5F;
            var y = (double) pos.getY() + (double) 0.5F;
            var x = (double) pos.getX() + (double) 0.5F;
            var pitch = random.nextFloat() * 0.4F + 0.8F;
            level.playLocalSound(x, y, z, SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.BLOCKS, 0.5F, pitch - 0.8f, false);
        }

        for(int i = 0; i < 4; ++i) {
            double d0 = (double)pos.getX() + random.nextDouble();
            double d1 = (double)pos.getY() + random.nextDouble();
            double d2 = (double)pos.getZ() + random.nextDouble();
            double d4 = ((double)random.nextFloat() - (double)0.5F) * (double)0.2F;
            int j = random.nextInt(2) * 2 - 1;
            if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
                d0 = (double)pos.getX() + (double)0.5F + (double)0.25F * (double)j;
            } else {
                d2 = (double)pos.getZ() + (double)0.5F + (double)0.25F * (double)j;
            }

            var colourDarker = ModHelpers.getColourDarker(PERK_GREEN, 0.5F);
            var particleData = genericParticleOptions(MAGIC_PARTICLE_SELECTION, PERK_GREEN, colourDarker, 10, 1, false, 0);
            level.addParticle(particleData, d0, d1, d2, 0, d4, 0);
        }
    }


    @Override
    public @Nullable DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        if(!(entity instanceof Player player)) return null;
        return LevelGenerator.createNewWorld(player, level);
    }

    @Override
    public Transition getLocalTransition() {
        return Transition.NONE;
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        return 0;
    }
}
