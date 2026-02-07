package org.jahdoo.common.block.perk_table;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.client.screens.BoonSelectionScreen;
import org.jahdoo.common.client.screens.QuestSelectionScreen;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jahdoo.trial_nexus.boon.player_boons.Boon;
import org.jahdoo.trial_nexus.level_manager.BlockSetupManager;
import org.jahdoo.trial_nexus.level_manager.StructureManager;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.block.SyncedBlockEntity;

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
import static org.jahdoo.trial_nexus.utils.PositionFinders.innerRadiusRandom;


public class PerkTableEntity extends SyncedBlockEntity {
    private String getQuestId;
    private int reRollCounter;
    private final List<UUID> usedBy = new ArrayList<>();
    public final List<Boon> boonsPositive = new ArrayList<>();
    public final List<Boon> boonsNegative = new ArrayList<>();
    public BlockPos returnLocation = null;
    public static final BlockPos restPos = new BlockPos(23, 105, 27);

    public PerkTableEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.PERK_TABLE_BE.get(), pos, state);
        if(getQuestId == null) getQuestId = QuestReg.getRandomQuest().questName();
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
        incrementPrivateTicks();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("counter", this.getPrivateTicks());
        tag.putString("quest_id", this.getQuestId);

        var tags = new CompoundTag();
        for (var uuid : this.usedBy) tags.putUUID(uuid.toString(), uuid);
        tag.put("interacted", tags);

        if(this.returnLocation != null){
            Helpers.saveBlockPosNBT(tag, this.returnLocation);
        }

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.setPrivateTicks(tag.getInt("counter"));
        this.getQuestId = tag.getString("quest_id");

        var getUsed = tag.getCompound("interacted");
        for (var allKey : getUsed.getAllKeys()) this.usedBy.add(getUsed.getUUID(allKey));

        this.returnLocation = Helpers.loadBlockPosNBT(tag);

        super.loadAdditional(tag, registries);
    }

    private void idleEffect(Level level, Vec3 pos, BlockState state) {
        if(!level.isClientSide || getPrivateTicks() % 4 != 0) return;
        var getPositions = innerRadiusRandom(pos.subtract(0, 0.45, 0), 0.38, 3);

        for (var vec3 : getPositions) {
            int bState = state.getValue(TEXTURE);
            var byState = bState == 0 ? color(207, 62, 62) : bState == 1 ? color(43, 193, 252) : bState == 2 ? color(252, 215, 3) : color(59, 173, 80) ;
            var genericParticle = ParticleHandlers.genericParticle(PLUS_PARTICLE, 16, 3, byState, byState, false);
            sendParticles(level, genericParticle, vec3, 0, 0, 0.5, 0, 12);
        }
    }
    public void sharedSound(SoundEvent sEvent, Float volume, Float pitch){
        Helpers.getSoundWithPosition(level, this.getBlockPos(), sEvent, SoundSource.BLOCKS, volume, pitch);
    }
    public void setUsed(BlockState state, Player player){
        if(level == null) return;

        switch (state.getValue(TEXTURE)) {
            case 0 -> {
                if (!this.usedBy.contains(player.getUUID())) {
                    player.heal(player.getMaxHealth());
                    sharedSound(SoundReg.VITALITY_ABILITY.get(), 1F, 0.8F);
                    sharedSound(SoundReg.HEAL.get(), 1F, 1.8F);
                    this.usedBy.add(player.getUUID());
                    this.updateBlock();
                } else {
                    usedMessage(player, org.shaydee.shaydeeapi.Colours.getNegativeRed());
                }
            }
            case 1 -> {
                if (!this.usedBy.contains(player.getUUID())) {
                    player.getData(CASTER_DATA.get()).refillMana(player);
                    sharedSound(SoundReg.ORB_CREATE.get(), 0.6F, 1.8F);
                    sharedSound(BREWING_STAND_BREW, 1F, 0.8F);
                    this.usedBy.add(player.getUUID());
                } else {
                    usedMessage(player, org.shaydee.shaydeeapi.Colours.getAetherBlue());
                }
            }
            case 2 -> {
                if (!this.usedBy.contains(player.getUUID())) {
                    if(level.isClientSide){
                        Minecraft.getInstance().setScreen(new QuestSelectionScreen(this.worldPosition));
                    }
                } else {
                    usedMessage(player, org.shaydee.shaydeeapi.Colours.getAbsorptionYellow());
                }
            }
            case 3 -> {
                if (!this.usedBy.contains(player.getUUID())) {
                    if(level.isClientSide){
                        Minecraft.getInstance().setScreen(new BoonSelectionScreen(worldPosition, player));
                        sharedSound(PLAYER_LEVELUP, 1F, 0.8F);
                    }
                } else {
                    usedMessage(player, org.shaydee.shaydeeapi.Colours.getPerkGreen());
                }
            }
            case 4 ->{
                if(level instanceof ServerLevel cLevel){
                    if(player instanceof ServerPlayer serverPlayer){
                        JahdooHelpers.sendClientSound(serverPlayer, SoundReg.REST.get(), 0.1F, 1, true);
                    }
                    StructureManager.generateCooldownRoom(cLevel);
                    var findBlock = cLevel.getBlockState(restPos);

                    if(findBlock.is(Blocks.DIAMOND_BLOCK)) BlockSetupManager.setPerkTable(cLevel, restPos, 5);

                    if(cLevel.getBlockEntity(restPos) instanceof PerkTableEntity perkTableEntity){
                        perkTableEntity.returnLocation = player.blockPosition();
                    }

                    var home = restPos.north(3).getCenter();
                    player.teleportTo(home.x, home.y, home.z);
                }
            }
            case 5 ->{
                if(level instanceof ServerLevel){
                    var rLoc = this.returnLocation;
                    if(rLoc != null){
                        if(player instanceof ServerPlayer serverPlayer){
                            JahdooHelpers.sendClientSound(serverPlayer, SoundReg.LOOP.get(), 0.4F, 1, true);
                        }
                        player.teleportTo(rLoc.getX() + 0.5, rLoc.getY(), rLoc.getZ() + 0.5);
                    }
                }
            }
        }
    }

    private static void usedMessage(Player player, int colour) {
        if(player.level().isClientSide){
            player.displayClientMessage(JahdooHelpers.withStyleComponent("You've Already Used This", colour), false);
            player.playSound(SoundReg.REJECT.get(), 0.25f, 1.4F);
        }
    }
}

