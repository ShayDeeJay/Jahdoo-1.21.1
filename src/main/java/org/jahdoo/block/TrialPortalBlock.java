package org.jahdoo.block;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jahdoo.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.challenge.LevelGenerator;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.portal.DimensionTransition.DO_NOTHING;
import static org.jahdoo.challenge.LevelGenerator.*;
import static org.jahdoo.challenge.LevelGenerator.DimHandler.*;
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;
import static org.jahdoo.utils.ColourStore.COSMIC_PURPLE;
import static org.jahdoo.utils.ColourStore.PERK_GREEN;

public class TrialPortalBlock extends NetherPortalBlock {
    public static final IntegerProperty DIMENSION_KEY = BlockStateProperties.LEVEL;
    public static final int KEY_HOME = 0;
    public static final int KEY_TRADING_POST = 1;
    public static final int KEY_TRIAL = 2;

    public TrialPortalBlock() {
        super(
            BlockBehaviour.Properties.of()
                .noCollission()
                .randomTicks()
                .strength(-1.0F)
                .sound(SoundType.GLASS)
                .lightLevel(state -> 11)
                .pushReaction(PushReaction.BLOCK)
        );
        this.registerDefaultState(this.defaultBlockState().setValue(DIMENSION_KEY, 0));

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIMENSION_KEY);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {}

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {

        for(int i = 0; i < 4; ++i) {

            var d0 = (double)pos.getX() + random.nextDouble();
            var d1 = (double)pos.getY() + random.nextDouble();
            var d2 = (double)pos.getZ() + random.nextDouble();
            var d4 = ((double)random.nextFloat() - (double)0.5F) * (double)0.2F;
            var j = random.nextInt(2) * 2 - 1;
            var direction = !level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this);

            if (direction) {
                d0 = (double)pos.getX() + (double)0.5F + (double)0.25F * (double)j;
            } else {
                d2 = (double)pos.getZ() + (double)0.5F + (double)0.25F * (double)j;
            }

            var particleColour = state.getValue(DIMENSION_KEY) == 0 ? PERK_GREEN : COSMIC_PURPLE;
            var colourDarker = ModHelpers.getColourDarker(particleColour, 0.5F);
            var particleData = genericParticleOptions(MAGIC_PARTICLE_SELECTION, particleColour, colourDarker, 10, 1, false, 0);
            level.addParticle(particleData, d0, d1, d2, 0, d4, 0);

        }

    }

    @Override
    public @Nullable DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        if(!(entity instanceof Player player)) return null;
        var isContinueInstance = level instanceof CustomLevel customLevel;
        var getData = level.getData(AttachmentRegister.CHALLENGE_ALTAR);
        int dimId = level.getBlockState(pos).getValue(DIMENSION_KEY);


        if(dimId == KEY_HOME && player instanceof ServerPlayer serverPlayer){
            return serverPlayer.findRespawnPositionAndUseSpawnBlock(true, DO_NOTHING);
        }

        //Delete old level on leave.
        if(level instanceof CustomLevel customLevel) LevelGenerator.removeLevel(customLevel);
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        var getDim = dimId == KEY_TRADING_POST ? tradingPost() : trial();
        var challengeLevelData = ChallengeLevelData.newRound(1, getDim.id());
        return createNewWorld(player, level, isContinueInstance ? getData : challengeLevelData, getDim);
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
