package org.jahdoo.networking.packet.server2client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.registers.AttachmentRegister.BOUNCY_FOOT;

public class MoveClientEntitySyncS2CPacket implements CustomPacketPayload {
    public static final Type<MoveClientEntitySyncS2CPacket> TYPE = new Type<>(ModHelpers.res("move_client_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MoveClientEntitySyncS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(MoveClientEntitySyncS2CPacket::toBytes, MoveClientEntitySyncS2CPacket::new);

    private final double x;
    private final double y;
    private final double z;
    private final int id;

    public MoveClientEntitySyncS2CPacket(
        double x,
        double y,
        double z,
        int id
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
    }

    public MoveClientEntitySyncS2CPacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.id = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeDouble(this.x);
        bug.writeDouble(this.y);
        bug.writeDouble(this.z);
        bug.writeInt(this.id);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                @Override
                public void run() {
                    if(ctx.player().level() instanceof ClientLevel clientLevel) {
                        var foundEntity = clientLevel.getEntity(id);
                        if(foundEntity != null){
                            foundEntity.setDeltaMovement(x,y,z);
                        }
                    }
                }
            }
        );
        return true;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
