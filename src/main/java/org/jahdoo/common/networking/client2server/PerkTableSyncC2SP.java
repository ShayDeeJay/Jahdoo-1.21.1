package org.jahdoo.common.networking.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.block.perk_table.PerkTableEntity;

import java.util.UUID;

public class PerkTableSyncC2SP implements CustomPacketPayload {
    public static final Type<PerkTableSyncC2SP> TYPE = new Type<>(Helpers.res("perk_table_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PerkTableSyncC2SP> STREAM_CODEC =
            CustomPacketPayload.codec(PerkTableSyncC2SP::toBytes, PerkTableSyncC2SP::new);

    UUID playerUUID;
    BlockPos blockPos;

    public PerkTableSyncC2SP(UUID uuid, BlockPos blockPos) {
        this.playerUUID = uuid;
        this.blockPos = blockPos;
    }

    public PerkTableSyncC2SP(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.playerUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeUUID(playerUUID);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof PerkTableEntity perkTable){
                        perkTable.addUsedBy(playerUUID);
                        perkTable.updateBlock();
                    }
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
