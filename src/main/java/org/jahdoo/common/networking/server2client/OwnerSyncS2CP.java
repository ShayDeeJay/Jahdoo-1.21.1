package org.jahdoo.common.networking.server2client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.entities.ITamableEntity;

import java.util.UUID;

public class OwnerSyncS2CP implements CustomPacketPayload {
    public static final Type<OwnerSyncS2CP> TYPE = new Type<>(JahdooHelpers.res("set_client_owner"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OwnerSyncS2CP> STREAM_CODEC = CustomPacketPayload.codec(OwnerSyncS2CP::toBytes, OwnerSyncS2CP::new);

    int id;
    UUID uuid;

    public OwnerSyncS2CP(int id, UUID uuid) {
        this.id = id;
        this.uuid = uuid;
    }

    public OwnerSyncS2CP(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.uuid = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(id);
        byteBuf.writeUUID(uuid);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    var level = Minecraft.getInstance().level;
                    if(level != null) {
                        var entity = level.getEntity(id);
                        if(entity instanceof ITamableEntity iTamable) {
                            iTamable.setOwner(level.getPlayerByUUID(uuid));
                        }
                    }
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
