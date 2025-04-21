package org.jahdoo.common.block.perk_table;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.client.screens.BoonSelectionScreen;
import org.jahdoo.common.client.screens.QuestSelectionScreen;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.QuestReg;

import java.util.Objects;

import static net.minecraft.sounds.SoundEvents.BREWING_STAND_BREW;
import static net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP;
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
    private String getQuestId;
    private int reRollCounter;

    public PerkTableEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.PERK_TABLE_BE.get(), pos, state);
        if(getQuestId == null) getQuestId = QuestReg.getRandomQuest().questName();
    }

    public boolean getUsed(){
        return this.hasUsed;
    }

    public String getGetQuestId() {
        return getQuestId;
    }

    public int getReRollCounter() {
        return reRollCounter;
    }

    public void reRollQuest() {
        var storeQuest = QuestReg.getRandomQuest().questName();

        while (Objects.equals(storeQuest, getQuestId)){
            storeQuest = QuestReg.getRandomQuest().questName();
        }

        reRollCounter++;
        getQuestId = storeQuest;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        idleEffect(level, pos.getCenter(), state);
        counter++;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("counter", this.counter);
        tag.putBoolean("used", this.hasUsed);
        tag.putString("quest_id", this.getQuestId);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.counter = tag.getInt("counter");
        this.hasUsed = tag.getBoolean("used");
        this.getQuestId = tag.getString("quest_id");
        super.loadAdditional(tag, registries);
    }

    private void idleEffect(Level level, Vec3 pos, BlockState state) {
        if(!level.isClientSide || getUsed() || counter % 4 != 0) return;
        var getPositions = innerRadiusRandom(pos.subtract(0, 0.45, 0), 0.38, 3);

        for (var vec3 : getPositions) {
            int bState = state.getValue(TEXTURE);
            var byState = bState == 0 ? color(207, 62, 62) : bState == 1 ? color(43, 193, 252) : bState == 2 ? color(252, 215, 3) : color(59, 173, 80) ;
            var genericParticle = ParticleHandlers.genericParticle(PLUS_PARTICLE, 16, 3, byState, byState, false);
            sendParticles(level, genericParticle, vec3, 0, 0, 0.5, 0, 12);
        }
    }

    public void setUsed(BlockState state, Player player){

        if(!getUsed() && level != null){
            int value = state.getValue(TEXTURE);

            switch (value){
                case 0 -> player.heal(player.getMaxHealth());
                case 1 -> player.getData(CASTER_DATA.get()).refillMana(player);
                case 2 -> {
                    if(level.isClientSide){
                        if(player.getData(AttachmentReg.RUN_DATA).getCurrentQuestId().isEmpty()){
                            Minecraft.getInstance().setScreen(new QuestSelectionScreen(this.worldPosition));
                            getSoundWithPosition(level, getBlockPos(), SoundReg.UNLOCK_NOTIFICATION.get(), 1, 2F);
                            getSoundWithPosition(level, getBlockPos(), SoundReg.UPGRADE_MODIFIER.get(), 0.5F, 1F);
                        } else {
                            player.displayClientMessage(Helpers.withStyleComponent("Quest Already Assigned", ColourStore.COOLDOWN_GREEN), true);
                        }
                        return;
                    }
                }
                case 3 -> {
                    if(level.isClientSide){
                        Minecraft.getInstance().setScreen(new BoonSelectionScreen());
                    }
                }
            }

            if(value != 2){
                this.updateBlock();
                idleEffect(player.level(), player.position(), state);
                this.hasUsed = true;
                level.removeBlock(getBlockPos().above(), false);
                level.removeBlock(getBlockPos(), false);
                getSoundWithPosition(level, getBlockPos(), value == 0 ? SoundReg.HEAL.get() : value == 1 ? BREWING_STAND_BREW : PLAYER_LEVELUP, 1, 0.8F);
            }
        }
    }
}

