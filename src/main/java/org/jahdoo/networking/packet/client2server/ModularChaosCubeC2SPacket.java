package org.jahdoo.networking.packet.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.capabilities.player_abilities.ModularChaosCubeProperties;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.registers.AttachmentRegister.MODULAR_CHAOS_CUBE;

public class ModularChaosCubeC2SPacket implements CustomPacketPayload {
    public static final Type<ModularChaosCubeC2SPacket> TYPE = new Type<>(ModHelpers.res("modular_chaos_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ModularChaosCubeC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(ModularChaosCubeC2SPacket::toBytes, ModularChaosCubeC2SPacket::new);

    BlockPos blockPos;
    ModularChaosCubeProperties autoBlock;

    public ModularChaosCubeC2SPacket(BlockPos blockPos, ModularChaosCubeProperties autoBlock) {
        this.blockPos = blockPos;
        this.autoBlock = autoBlock;
    }

    public ModularChaosCubeC2SPacket(FriendlyByteBuf buf) {
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
