package org.jahdoo.common.block.perk_table;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.client.screens.BoonSelectionScreen;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;

import static net.minecraft.sounds.SoundEvents.*;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.Helpers.getSoundWithPosition;
import static org.jahdoo.ascension.utils.PositionFinders.innerRadiusRandom;
import static org.jahdoo.common.block.perk_table.PerkTable.TEXTURE;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.particle.ParticleStore.PLUS_PARTICLE;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;


public class PerkTableEntity extends SyncedBlockEntity {

    public int counter;
    private boolean hasUsed;

    public PerkTableEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.PERK_TABLE_BE.get(), pos, state);
    }

    public boolean getUsed(){
        return this.hasUsed;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        idleEffect(level, pos.getCenter(), state);
        counter++;
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

    private void idleEffect(Level level, Vec3 pos, BlockState state) {
        if(!level.isClientSide || getUsed() || counter % 4 != 0) return;
        var getPositions = innerRadiusRandom(pos.subtract(0, 0.45, 0), 0.38, 3);

        for (var vec3 : getPositions) {
            int bState = state.getValue(TEXTURE);
            var byState = bState == 0 ? color(207, 62, 62) : bState == 1 ? color(43, 193, 252) : color(59, 173, 80) ;
            var genericParticle = ParticleHandlers.genericParticle(PLUS_PARTICLE, 16, 3, byState, byState, false);
            sendParticles(level, genericParticle, vec3, 0, 0, 0.5, 0, 35);
        }
    }

    public void setUsed(BlockState state, Player player){
        if(!getUsed() && level != null){
            this.hasUsed = true;
            int value = state.getValue(TEXTURE);

            switch (value){
                case 0 -> player.heal(player.getMaxHealth());
                case 1 -> player.getData(CASTER_DATA.get()).refillMana(player);
                default -> {
                    if(level.isClientSide){
                        Minecraft.getInstance().setScreen(new BoonSelectionScreen());
                    }
                }
            }

            this.updateBlock();
            idleEffect(player.level(), player.position(), state);
            getSoundWithPosition(level, getBlockPos(), value == 0 ? SoundReg.HEAL.get() : value == 1 ? BREWING_STAND_BREW : PLAYER_LEVELUP, 1, 0.8F);
            level.removeBlock(getBlockPos().above(), false);
            level.removeBlock(getBlockPos(), false);
        }
    }
}

