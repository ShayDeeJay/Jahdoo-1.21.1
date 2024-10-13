package org.jahdoo.all_magic.wand_perks.mage_flight;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
//import org.assets.jahdoo.capabilities.AbstractCapability;
import org.jahdoo.items.wand.CastHelper;
import org.jahdoo.networking.packet.MageFlightPacketS2CPacket;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;
import static org.jahdoo.registers.AttachmentRegister.MAGE_FLIGHT;

public class MageFlight {

    public int jumpTickCounter;
    public boolean lastJumped;
    public boolean isFlying;
    public boolean chargeMana;
    public static double manaCost = 0.5;

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("jumpTickCounter", jumpTickCounter);
        nbt.putBoolean("lastJumped", lastJumped);
        nbt.putBoolean("isFlying", isFlying);
    }

    public void loadNBTData(CompoundTag nbt) {
        jumpTickCounter = nbt.getInt("jumpTickCounter");
        lastJumped = nbt.getBoolean("lastJumped");
        isFlying = nbt.getBoolean("isFlying");
    }

    public static void mageFlightTickEvent(ServerPlayer serverPlayer){
        serverPlayer.getData(MAGE_FLIGHT).serverFlight(serverPlayer);
    }

    public void serverFlight(ServerPlayer serverPlayer){
        PacketDistributor.sendToPlayer(serverPlayer, new MageFlightPacketS2CPacket(this.jumpTickCounter, this.isFlying, this.lastJumped));

        if(this.chargeMana) {
            this.playMageFlightSound(serverPlayer);
            var manaSystem = serverPlayer.getData(CASTER_DATA);
            manaSystem.subtractMana(manaCost);
        }

    }

    public int getJumpTickCounter(){
        return this.jumpTickCounter;
    }

    public boolean getLastJumped(){
        return this.lastJumped;
    }

    public boolean getIsFlying(){
        return this.isFlying;
    }

    public void setJumpTickCounter(int jumpTickCounter){
        this.jumpTickCounter = jumpTickCounter;
    }

    public void setLastJumped(boolean lastJumped){
        this.lastJumped = lastJumped;
    }

    public void setFlying(boolean isFlying){
        this.isFlying = isFlying;
    }

    public void setChargeMana(boolean chargeMana){
        this.chargeMana = chargeMana;
    }

    private void playMageFlightSound(ServerPlayer serverPlayer){
        if (serverPlayer.tickCount % 3 == 0) {
            GeneralHelpers.getSoundWithPosition(
                serverPlayer.level(),
                serverPlayer.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                0.03f,
                (float) serverPlayer.getDeltaMovement().y
            );
        }
    }

}
