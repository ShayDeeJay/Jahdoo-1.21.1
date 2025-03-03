package org.jahdoo.common.networking.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.ascension.attachments.player_abilities.ModularChaosCubeProperties;
import org.jahdoo.ascension.utils.Helpers;

import static org.jahdoo.common.registers.AttachmentReg.MODULAR_CHAOS_CUBE;

public class ChaosCubeC2SP implements CustomPacketPayload {
    public static final Type<ChaosCubeC2SP> TYPE = new Type<>(Helpers.res("modular_chaos_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChaosCubeC2SP> STREAM_CODEC = CustomPacketPayload.codec(ChaosCubeC2SP::toBytes, ChaosCubeC2SP::new);

    BlockPos blockPos;
    ModularChaosCubeProperties autoBlock;

    public ChaosCubeC2SP(BlockPos blockPos, ModularChaosCubeProperties autoBlock) {
        this.blockPos = blockPos;
        this.autoBlock = autoBlock;
    }

    public ChaosCubeC2SP(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.autoBlock = buf.readJsonWithCodec(ModularChaosCubeProperties.CODEC);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(ModularChaosCubeProperties.CODEC, autoBlock);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork( () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof ModularChaosCubeEntity entity){
                        entity.setData(MODULAR_CHAOS_CUBE, this.autoBlock);
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
