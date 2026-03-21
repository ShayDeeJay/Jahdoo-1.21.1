package org.jahdoo.trial_nexus.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.networking.client2server.MageFlightC2SP;
import org.jahdoo.common.networking.server2client.MageFlightSyncS2CP;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.IAttachment;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.level_manager.PlayerHomeDim;
import org.jahdoo.trial_nexus.magic.skills.MageFlightSkill;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static net.minecraft.sounds.SoundSource.PLAYERS;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.common.registers.AttachmentReg.MAGE_FLIGHT;
import static org.jahdoo.common.registers.mod.ElementReg.fromWand;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class MageFlight implements IAttachment {

    public int jumpTickCounter;
    public boolean lastJumped;
    public boolean isFlying;
    public boolean jumpKeyDown;
    public static double manaCost = 0.5;

    public void setJumpTickCounter(int jumpTickCounter) {
        this.jumpTickCounter = jumpTickCounter;
    }

    public void setLastJumped(boolean lastJumped) {
        this.lastJumped = lastJumped;
    }

    public void setIsFlying(boolean playerFlying) {
        this.isFlying = playerFlying;
    }

    public void setJumpKeyDown(boolean jumpKeyDown) {
        this.jumpKeyDown = jumpKeyDown;
    }

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("jumpTickCounter", jumpTickCounter);
        nbt.putBoolean("lastJumped", lastJumped);
        nbt.putBoolean("isFlying", isFlying);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        jumpTickCounter = nbt.getInt("jumpTickCounter");
        lastJumped = nbt.getBoolean("lastJumped");
        isFlying = nbt.getBoolean("isFlying");
    }

    public static void mageFlightTickEvent(Player player){
        if(PlayerHomeDim.isPlayerHome(player)){
            player.getAbilities().mayfly = true;
            return;
        }

        if(!player.getData(CASTER_DATA).isAbilityOnCooldown(MageFlightSkill.MAGE_FLIGHT)){
            var mageFlight = player.getData(MAGE_FLIGHT);
            mageFlight.serverFlight(player);

            if (player instanceof ServerPlayer serverPlayer)
                PacketDistributor.sendToPlayer(serverPlayer, new MageFlightC2SP());
        }
    }

    private boolean cancelAttempt(Player player) {
        var casterData = CasterData.hasSkill(player, SkillReg.MAGE_FLIGHT.get().id());
        if(!casterData || player.onGround() || player.isFallFlying()) {
            player.getAbilities().mayfly = false;
            this.isFlying = false;
            return true;
        }
        return false;
    }

    private void flying(Player player, CasterData casterData, ItemStack wandItem) {
        if (CastHelper.sufficientMana(player, casterData, (float) manaCost, null)) {
            var getPool = player.getAttribute(AttributeReg.MANA_POOL);
            var manaCost = (getPool != null ? getPool.getValue() : 1) / 200;
            var getDelta = player.getDeltaMovement();
            var mod = player.getAttribute(AttributeReg.MAGE_FLIGHT);
            if(mod == null) return;

            player.getAbilities().mayfly = true;
            casterData.subtractMana(Math.min(manaCost, 2), player);

            var v = mod.getValue() - 1;
            var min = Math.min(getDelta.y + 0.090, 0.1 * v);
            player.setDeltaMovement(getDelta.x, min, getDelta.z);

            var usedItem = CastHelper.hasValidCasterItem(player).getItem();
            var element = fromWand(usedItem).orElse(ElementReg.random());
            mageFlightAnimation(element, player);
        }
    }

    private void mageFlightAnimation(AbstractElement element, Player player){
        var part1 = ParticleHandlers.genericParticle(ParticleStore.GENERIC_PARTICLE, element, 2, 0.2f, true);
        var part2 = bakedParticle(element.id(), 2, 1f, false);
        var getMovement = player.getDeltaMovement().y > -0.5;

        PositionFinders.innerRadiusRandom(player.position(), player.getBbWidth() - 0.3, getMovement ? 5 : 2,
            positions -> {
                player.level().addParticle(part1, positions.x, positions.y, positions.z, 0, -0.2, 0);
                player.level().addParticle(part2, positions.x, positions.y, positions.z, 0, -0.2, 0);
            }
        );

        if (player.tickCount % 6 == 0) {
            SoundHelpers.getSoundWithPosition(
                player.level(), player.position(), SoundReg.LEVITATE.get(), PLAYERS, 1f, Random.nextFloat(1, 1.5F)
            );
            SoundHelpers.getSoundWithPosition(
                player.level(), player.position(), SoundReg.HEAL.get(), PLAYERS, 0.5f, Random.nextFloat(1, 1.5F)
            );
        }
    }

    public void serverFlight(Player player){
        if(player.isCreative() || player.isSpectator()) return;

        var wandItem = CastHelper.hasValidCasterItem(player);
        var manaSystem = player.getData(CASTER_DATA);
        if (cancelAttempt(player)) return;

        if (!this.lastJumped && jumpKeyDown) {
            if (this.jumpTickCounter == 0) this.jumpTickCounter = 5; else this.isFlying = true;
        }

        if (this.jumpTickCounter > 0) this.jumpTickCounter--;
        this.lastJumped = this.jumpKeyDown;

        if (this.isFlying && this.jumpKeyDown) {
            flying(player, manaSystem, wandItem);
        } else {
            if(!player.isFallFlying()) player.getAbilities().mayfly = false;
        }

        if(player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MageFlightSyncS2CP(jumpTickCounter, lastJumped, isFlying, jumpKeyDown));
        }
    }

}
