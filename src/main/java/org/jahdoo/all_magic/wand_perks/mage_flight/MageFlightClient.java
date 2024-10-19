package org.jahdoo.all_magic.wand_perks.mage_flight;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.capabilities.player_abilities.MageFlight;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.networking.packet.FlyingPacketC2SPacket;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.capabilities.player_abilities.MageFlight.manaCost;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;
import static org.jahdoo.registers.AttachmentRegister.MAGE_FLIGHT;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;

public class MageFlightClient {
    public static void mageFlightClient(Player player){
        if(player.isCreative()) return;
        if(player instanceof LocalPlayer localPlayer){
            var mageFlight = localPlayer.getData(MAGE_FLIGHT);
            var manaSystem = localPlayer.getData(CASTER_DATA);
            var flyingAttribute = localPlayer.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
            var wandItem = localPlayer.getMainHandItem();

            if (!(wandItem.getItem() instanceof WandItem)) return;
            if (localPlayer.onGround()) mageFlight.isFlying = false;

            if (!mageFlight.lastJumped && localPlayer.input.jumping) {
                if (mageFlight.jumpTickCounter == 0) mageFlight.jumpTickCounter = 10; else mageFlight.isFlying = true;
            }

            if (mageFlight.jumpTickCounter > 0) mageFlight.jumpTickCounter--;

            mageFlight.lastJumped = localPlayer.input.jumping;

            if (mageFlight.isFlying && localPlayer.input.jumping) {
                if(flyingAttribute != null) flyingAttribute.setBaseValue(1);
                mageFlight.chargeMana = true;
                if (manaSystem.getManaPool() > manaCost) {
                    localPlayer.setDeltaMovement(localPlayer.getDeltaMovement().add(0, 0.1, 0));
                    mageFlightAnimation(wandItem, localPlayer);
                }
            } else {
                if(flyingAttribute != null) flyingAttribute.setBaseValue(0);
                mageFlight.chargeMana = false;
            }

            mageFlightC2S(mageFlight);
        }
    }

    public static void mageFlightC2S(MageFlight mageFlight){
        PacketDistributor.sendToServer(new FlyingPacketC2SPacket(mageFlight.chargeMana, mageFlight.lastJumped, mageFlight.isFlying, mageFlight.jumpTickCounter));
    }

    public static void mageFlightAnimation(ItemStack wandItem, LocalPlayer localPlayer){
        AbstractElement element = getElementByWandType(wandItem.getItem()).getFirst();

        GenericParticleOptions part1 = genericParticleOptions(
            ParticleStore.GENERIC_PARTICLE_SELECTION, element, 2, 0.2f, true
        );

        BakedParticleOptions part2 = new BakedParticleOptions(
            getElementByWandType(wandItem.getItem()).getFirst().getTypeId(),
            2, 1f, false
        );
        boolean getMovement = localPlayer.getDeltaMovement().y > -0.5;
        GeneralHelpers.getInnerRingOfRadiusRandom(localPlayer.position(), localPlayer.getBbWidth() - 0.3, getMovement ? 5 : 2,
            positions -> {
                localPlayer.level().addParticle(part1, positions.x, positions.y, positions.z, 0, -0.2, 0);
                localPlayer.level().addParticle(part2, positions.x, positions.y, positions.z, 0, -0.2, 0);
            }
        );
    }

}
