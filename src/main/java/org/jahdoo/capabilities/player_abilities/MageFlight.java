package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
//import org.assets.jahdoo.capabilities.AbstractCapability;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.capabilities.AbstractAttachment;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.networking.packet.MageFlightPacketS2CPacket;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.registers.AttachmentRegister.*;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;

public class MageFlight implements AbstractAttachment {

    public int jumpTickCounter;
    public boolean lastJumped;
    public boolean isFlying;
    public static double manaCost = 0.5;
    public boolean jumpKeyDown;

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
        if(player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new MageFlightPacketS2CPacket());
        }
        var mageFlight = player.getData(MAGE_FLIGHT);
        mageFlight.serverFlight(player);
    }

    public void setJumpKeyDown(boolean jumpKeyDown) {
        this.jumpKeyDown = jumpKeyDown;
    }

    public void serverFlight(Player player){
        var wandItem = player.getMainHandItem();
        if(player.isCreative() || !(wandItem.getItem() instanceof WandItem)) return;
        var manaSystem = player.getData(CASTER_DATA);
        if(player.onGround()) this.isFlying = false;
        if (!this.lastJumped && jumpKeyDown) {
            if (this.jumpTickCounter == 0) this.jumpTickCounter = 10; else this.isFlying = true;
        }

        if (this.jumpTickCounter > 0) this.jumpTickCounter--;

        this.lastJumped = jumpKeyDown;

        if (this.isFlying && jumpKeyDown) {
            if (manaSystem.getManaPool() > manaCost) {
                player.getAbilities().mayfly = true;
                player.getData(BOUNCY_FOOT).setEffectTimer(160);
                manaSystem.subtractMana(manaCost);
                player.setDeltaMovement(player.getDeltaMovement().add(0, 0.1, 0));
                mageFlightAnimation(wandItem, player);
            }
        } else {
            if(!player.isFallFlying()) player.getAbilities().mayfly = false;
        }
    }


    private void mageFlightAnimation(ItemStack wandItem, Player player){
        AbstractElement element = getElementByWandType(wandItem.getItem()).getFirst();

        GenericParticleOptions part1 = genericParticleOptions(
            ParticleStore.GENERIC_PARTICLE_SELECTION, element, 2, 0.2f, true
        );

        BakedParticleOptions part2 = new BakedParticleOptions(
            getElementByWandType(wandItem.getItem()).getFirst().getTypeId(),
            2, 1f, false
        );
        boolean getMovement = player.getDeltaMovement().y > -0.5;
        GeneralHelpers.getInnerRingOfRadiusRandom(player.position(), player.getBbWidth() - 0.3, getMovement ? 5 : 2,
            positions -> {
                player.level().addParticle(part1, positions.x, positions.y, positions.z, 0, -0.2, 0);
                player.level().addParticle(part2, positions.x, positions.y, positions.z, 0, -0.2, 0);
            }
        );

        if (player.tickCount % 3 == 0) {
            GeneralHelpers.getSoundWithPosition(
                player.level(),
                player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                0.03f,
                (float) player.getDeltaMovement().y
            );
        }
    }

}
