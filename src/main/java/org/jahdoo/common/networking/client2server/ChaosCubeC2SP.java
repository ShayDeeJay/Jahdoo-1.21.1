package org.jahdoo.common.networking.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.trial_nexus.attachments.ChaosCubeData;
import org.jahdoo.trial_nexus.utils.Helpers;

public class ChaosCubeC2SP implements CustomPacketPayload {
    public static final Type<ChaosCubeC2SP> TYPE = new Type<>(Helpers.res("modular_chaos_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChaosCubeC2SP> STREAM_CODEC = CustomPacketPayload.codec(ChaosCubeC2SP::toBytes, ChaosCubeC2SP::new);

    BlockPos blockPos;
    ChaosCubeData autoBlock;

    public ChaosCubeC2SP(BlockPos blockPos, ChaosCubeData autoBlock) {
        this.blockPos = blockPos;
        this.autoBlock = autoBlock;
    }

    public ChaosCubeC2SP(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.autoBlock = buf.readJsonWithCodec(ChaosCubeData.CODEC);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(ChaosCubeData.CODEC, autoBlock);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork( () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof ChaosCubeEntity entity){
                        entity.updateData(autoBlock);
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
