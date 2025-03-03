package org.jahdoo.ascension.ability.wand_perks.mage_flight;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.networking.client2server.FlyingC2SP;

import static org.jahdoo.common.registers.AttachmentReg.MAGE_FLIGHT;

public class MageFlightClient {
    public static void mageFlightClient(Player player){
        if(player instanceof LocalPlayer localPlayer){
            PacketDistributor.sendToServer(new FlyingC2SP(localPlayer.input.jumping));
            localPlayer.getData(MAGE_FLIGHT).setJumpKeyDown(localPlayer.input.jumping);
        }
    }
}
