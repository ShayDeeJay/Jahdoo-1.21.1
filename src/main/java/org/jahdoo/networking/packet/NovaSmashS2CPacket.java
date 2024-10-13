package org.jahdoo.networking.packet;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.registers.AttachmentRegister.NOVA_SMASH;

public class NovaSmashS2CPacket implements CustomPacketPayload {

    public static final Type<NovaSmashS2CPacket> TYPE = new Type<>(GeneralHelpers.modResourceLocation("nova_smash_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NovaSmashS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(NovaSmashS2CPacket::toBytes, NovaSmashS2CPacket::new);

    int highestDelta;
    boolean canSmash;

    public NovaSmashS2CPacket(int highestDelta, boolean canSmash) {
        this.canSmash = canSmash;
        this.highestDelta = highestDelta;
    }

    public NovaSmashS2CPacket(FriendlyByteBuf buf) {
        this.highestDelta = buf.readInt();
        this.canSmash = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.highestDelta);
        buf.writeBoolean(this.canSmash);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork( () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    var novaSmash = localPlayer.getData(NOVA_SMASH);
                    novaSmash.setCanSmash(this.canSmash);
                    novaSmash.setHighestDelta(this.highestDelta);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
