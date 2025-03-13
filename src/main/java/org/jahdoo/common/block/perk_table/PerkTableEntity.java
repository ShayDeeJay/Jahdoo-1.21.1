package org.jahdoo.common.block.perk_table;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;

import static net.minecraft.sounds.SoundEvents.*;
import static net.minecraft.util.FastColor.ARGB32.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.PositionFinders.*;
import static org.jahdoo.common.block.perk_table.PerkTable.TEXTURE;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.PLUS_PARTICLE;
import static org.jahdoo.common.registers.AttachmentReg.*;


public class PerkTableEntity extends SyncedBlockEntity {

    public int counter;
    private boolean hasUsed;

    public PerkTableEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.PERK_TABLE_BE.get(), pos, state);
    }

    public boolean getUsed(){
        return this.hasUsed;
    }

    public void setUsed(BlockState state, Player player){
        if(!getUsed() && level != null){
            this.hasUsed = true;
            int value = state.getValue(TEXTURE);

            if(value == 0){
                player.heal(player.getMaxHealth());
            } else {
                player.getData(CASTER_DATA.get()).refillMana(player);
            }

            idleEffect(player.level(), player.position(), state);
            getSoundWithPosition(level, getBlockPos(), PLAYER_LEVELUP, 1, 0.8F);
            this.updateBlock();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("counter", this.counter);
        tag.putBoolean("used", this.hasUsed);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.counter = tag.getInt("counter");
        this.hasUsed = tag.getBoolean("used");
        super.loadAdditional(tag, registries);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        idleEffect(level, pos.getCenter(), state);
        counter++;
    }

    private void idleEffect(Level level, Vec3 pos, BlockState state) {
        if(!level.isClientSide || getUsed() || counter % 4 != 0) return;
        var getPositions = innerRadiusRandom(pos.subtract(0, 0.45, 0), 0.38, 3);

        for (var vec3 : getPositions) {
            var byState = state.getValue(TEXTURE) == 0 ? color(207, 62, 62) : color(43, 193, 252);
            var genericParticle = ParticleHandlers.genericParticle(PLUS_PARTICLE, 16, 3, byState, byState, false);
            sendParticles(level, genericParticle, vec3, 0, 0, 0.5, 0, 35);
        }
    }

}

