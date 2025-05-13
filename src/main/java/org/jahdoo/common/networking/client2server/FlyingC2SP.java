package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.Helpers;

import static org.jahdoo.common.registers.AttachmentReg.MAGE_FLIGHT;

public class FlyingC2SP implements CustomPacketPayload{
    public static final Type<FlyingC2SP> TYPE = new Type<>(Helpers.res("send_flying_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FlyingC2SP> STREAM_CODEC = CustomPacketPayload.codec(FlyingC2SP::toBytes, FlyingC2SP::new);
    private final boolean isJumpKeyDown;

    public FlyingC2SP(boolean isJumpKeyDown) {
        this.isJumpKeyDown = isJumpKeyDown;
    }

    public FlyingC2SP(FriendlyByteBuf buf) {
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
