package org.jahdoo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.registers.AttachmentRegister.MAGE_FLIGHT;

public class FlyingPacketC2SPacket implements CustomPacketPayload{
    public static final Type<FlyingPacketC2SPacket> TYPE = new Type<>(GeneralHelpers.modResourceLocation("send_flying_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FlyingPacketC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(FlyingPacketC2SPacket::toBytes, FlyingPacketC2SPacket::new);
    private boolean isJumpKeyDown;

    public FlyingPacketC2SPacket(boolean isJumpKeyDown) {
        this.isJumpKeyDown = isJumpKeyDown;
    }

    public FlyingPacketC2SPacket(FriendlyByteBuf buf) {
        this.isJumpKeyDown = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeBoolean(this.isJumpKeyDown);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var mageFlight = serverPlayer.getData(MAGE_FLIGHT);
                    mageFlight.setJumpKeyDown(isJumpKeyDown);
                }
            }
        );
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
