package org.jahdoo.common.networking.server2client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.NotNull;

public class MoveClientEntityS2CP implements CustomPacketPayload {
    public static final Type<MoveClientEntityS2CP> TYPE = new Type<>(JahdooHelpers.res("move_client_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MoveClientEntityS2CP> STREAM_CODEC = CustomPacketPayload.codec(MoveClientEntityS2CP::toBytes, MoveClientEntityS2CP::new);

    private final double x;
    private final double y;
    private final double z;
    private final int id;

    public MoveClientEntityS2CP(
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

    public MoveClientEntityS2CP(FriendlyByteBuf buf) {
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
                        if(foundEntity != null) foundEntity.setDeltaMovement(x,y,z);
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
