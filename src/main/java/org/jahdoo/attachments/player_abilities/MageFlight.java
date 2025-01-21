package org.jahdoo.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.attachments.AbstractAttachment;
import org.jahdoo.attachments.CastingData;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.networking.packet.client2server.MageFlightPacketS2CPacket;
import org.jahdoo.networking.packet.server2client.MageFlightDataSyncS2CPacket;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.registers.AttachmentRegister.*;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;

public class MageFlight implements AbstractAttachment {

    public int jumpTickCounter;
    public boolean lastJumped;
    public boolean isFlying;
    public boolean jumpKeyDown;
    public static double manaCost = 0.5;

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
        var mageFlight = player.getData(MAGE_FLIGHT);
        mageFlight.serverFlight(player);
        if(player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new MageFlightPacketS2CPacket());
        }
    }

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

    public void serverFlight(Player player){
        if(player.isCreative() || player.isSpectator()) return;

        var wandItem = player.getItemInHand(player.getUsedItemHand());
        var manaSystem = player.getData(CASTER_DATA);
        if (cancelAttempt(player, wandItem)) return;

        if (!this.lastJumped && jumpKeyDown) {
            if (this.jumpTickCounter == 0) this.jumpTickCounter = 10; else this.isFlying = true;
        }

        if (this.jumpTickCounter > 0) this.jumpTickCounter--;
        this.lastJumped = this.jumpKeyDown;

        if (this.isFlying && this.jumpKeyDown) {
            flying(player, manaSystem, wandItem);
        } else {
            if(!player.isFallFlying()) player.getAbilities().mayfly = false;
        }

        if(player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MageFlightDataSyncS2CPacket(jumpTickCounter, lastJumped, isFlying, jumpKeyDown));
        }
    }

    private void flying(Player player, CastingData manaSystem, ItemStack wandItem) {
        if (manaSystem.getManaPool() > manaCost) {
//            player.getData(BOUNCY_FOOT).setEffectTimer(160);
            player.getAbilities().mayfly = true;
            var getPool = player.getAttribute(AttributesRegister.MANA_POOL);
            var manaCost = (getPool != null ? getPool.getValue() : 1) / 150;
            manaSystem.subtractMana(Math.min(manaCost, 2), player);
            var getDelta = player.getDeltaMovement();
            var speedModifier = 0.02;
            player.setDeltaMovement(player.getDeltaMovement().add(getDelta.x * speedModifier, 0.09, getDelta.z * speedModifier));
            mageFlightAnimation(wandItem, player);
        }
    }

    private boolean cancelAttempt(Player player, ItemStack wandItem) {
        if(!RuneData.RuneHelpers.canMageFlight(wandItem) || player.onGround() || player.isFallFlying()) {
            player.getAbilities().mayfly = false;
            this.isFlying = false;
            return true;
        }
        return false;
    }

    private void mageFlightAnimation(ItemStack wandItem, Player player){
        if(!(wandItem.getItem() instanceof WandItem)) return;
        var element = getElementByWandType(wandItem.getItem()).getFirst();
        var part1 = genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, element, 2, 0.2f, true);
        var part2 = bakedParticleOptions(getElementByWandType(wandItem.getItem()).getFirst().getTypeId(), 2, 1f, false);
        var getMovement = player.getDeltaMovement().y > -0.5;

        PositionGetters.getInnerRingOfRadiusRandom(player.position(), player.getBbWidth() - 0.3, getMovement ? 5 : 2,
            positions -> {
                player.level().addParticle(part1, positions.x, positions.y, positions.z, 0, -0.2, 0);
                player.level().addParticle(part2, positions.x, positions.y, positions.z, 0, -0.2, 0);
            }
        );

        if (player.tickCount % 3 == 0) {
            ModHelpers.getSoundWithPosition(
                player.level(), player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, 0.03f, (float) player.getDeltaMovement().y
            );
        }
    }

}
