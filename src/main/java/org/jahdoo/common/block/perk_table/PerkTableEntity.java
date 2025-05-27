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
import org.jahdoo.common.client.screens.QuestSelectionScreen;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jahdoo.trial_nexus.boon.player_boons.Boon;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.minecraft.sounds.SoundEvents.BREWING_STAND_BREW;
import static net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.block.perk_table.PerkTable.TEXTURE;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.particle.ParticleStore.PLUS_PARTICLE;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.trial_nexus.utils.Helpers.getSoundWithPosition;
import static org.jahdoo.trial_nexus.utils.PositionFinders.innerRadiusRandom;


public class PerkTableEntity extends SyncedBlockEntity {
    private String getQuestId;
    private int reRollCounter;
    private List<UUID> usedBy = new ArrayList<>();
    public final List<Boon> boonsPositive = new ArrayList<>();
    public final List<Boon> boonsNegative = new ArrayList<>();

    public PerkTableEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.PERK_TABLE_BE.get(), pos, state);
        if(getQuestId == null) getQuestId = QuestReg.getRandomQuest().questName();
        for (var i = 0; i < 3; i++){
//            boonsPositive.add(getPositiveBoon());
//            boonsNegative.add(getNegativeBoon());
        }
    }

    public boolean interacted(Player player){
        return this.usedBy.contains(player.getUUID());
    }

    public void addUsedBy(UUID playerUUID){
        this.usedBy.add(playerUUID);
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
        privateTicks++;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("counter", this.privateTicks);
        tag.putString("quest_id", this.getQuestId);

        var tags = new CompoundTag();
        for (var uuid : this.usedBy) tags.putUUID(uuid.toString(), uuid);
        tag.put("interacted", tags);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.privateTicks = tag.getInt("counter");
        this.getQuestId = tag.getString("quest_id");

        var getUsed = tag.getCompound("interacted");
        for (var allKey : getUsed.getAllKeys()) this.usedBy.add(getUsed.getUUID(allKey));

        super.loadAdditional(tag, registries);
    }

    private void idleEffect(Level level, Vec3 pos, BlockState state) {
        if(!level.isClientSide || privateTicks % 4 != 0) return;
        var getPositions = innerRadiusRandom(pos.subtract(0, 0.45, 0), 0.38, 3);

        for (var vec3 : getPositions) {
            int bState = state.getValue(TEXTURE);
            var byState = bState == 0 ? color(207, 62, 62) : bState == 1 ? color(43, 193, 252) : bState == 2 ? color(252, 215, 3) : color(59, 173, 80) ;
            var genericParticle = ParticleHandlers.genericParticle(PLUS_PARTICLE, 16, 3, byState, byState, false);
            sendParticles(level, genericParticle, vec3, 0, 0, 0.5, 0, 12);
        }
    }

    public void setUsed(BlockState state, Player player){
        if(level == null) return;

        switch (state.getValue(TEXTURE)) {
            case 0 -> {
                if (!this.usedBy.contains(player.getUUID())) {
                    player.heal(player.getMaxHealth());
                    getSoundWithPosition(level, getBlockPos(), SoundReg.VITALITY_ABILITY.get(), 1, 0.8F);
                    getSoundWithPosition(level, getBlockPos(), SoundReg.HEAL.get(), 1, 1.8F);
                    this.usedBy.add(player.getUUID());
                    this.updateBlock();
                } else {
                    usedMessage(player, ColourStore.NEGATIVE_RED);
                }
            }
            case 1 -> {
                if (!this.usedBy.contains(player.getUUID())) {
                    player.getData(CASTER_DATA.get()).refillMana(player);
                    getSoundWithPosition(level, getBlockPos(), SoundReg.ORB_CREATE.get(), 0.6F, 1.8F);
                    getSoundWithPosition(level, getBlockPos(), BREWING_STAND_BREW, 1, 0.8F);
                    this.usedBy.add(player.getUUID());
                } else {
                    usedMessage(player, ColourStore.AETHER_BLUE);
                }
            }
            case 2 -> {
                if (!this.usedBy.contains(player.getUUID())) {
                    if(level.isClientSide){
                        Minecraft.getInstance().setScreen(new QuestSelectionScreen(this.worldPosition));
                    }
                } else {
                    usedMessage(player, ColourStore.ABSORPTION_YELLOW);
                }
            }
            case 3 -> {
                if (!this.usedBy.contains(player.getUUID())) {
                    if(level.isClientSide){
                        Minecraft.getInstance().setScreen(new BoonSelectionScreen(worldPosition, player));
                        getSoundWithPosition(level, getBlockPos(), PLAYER_LEVELUP, 1, 0.8F);
                    }
                } else {
                    usedMessage(player, ColourStore.PERK_GREEN);
                }
            }
        }
    }

    private static void usedMessage(Player player, int colour) {
        if(player.level().isClientSide){
            player.displayClientMessage(Helpers.withStyleComponent("You've Already Used This", colour), false);
            player.playSound(SoundReg.REJECT.get(), 0.25f, 1.4F);
        }
    }
}

