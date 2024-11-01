package org.jahdoo.all_magic.wand_perks.mage_flight;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.networking.packet.client2server.FlyingPacketC2SPacket;

import static org.jahdoo.registers.AttachmentRegister.MAGE_FLIGHT;

public class MageFlightClient {
    public static void mageFlightClient(Player player){
        if(player instanceof LocalPlayer localPlayer){
            PacketDistributor.sendToServer(new FlyingPacketC2SPacket(localPlayer.input.jumping));
            localPlayer.getData(MAGE_FLIGHT).setJumpKeyDown(localPlayer.input.jumping);
        }
    }
}
