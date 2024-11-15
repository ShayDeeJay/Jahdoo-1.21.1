package org.jahdoo.networking.packet.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.capabilities.player_abilities.AutoBlock;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.registers.AttachmentRegister.AUTO_BLOCK;

public class AutoBlockC2SPacket implements CustomPacketPayload {
    public static final Type<AutoBlockC2SPacket> TYPE = new Type<>(ModHelpers.modResourceLocation("auto_block_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AutoBlockC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(AutoBlockC2SPacket::toBytes, AutoBlockC2SPacket::new);

    BlockPos blockPos;
    AutoBlock autoBlock;

    public AutoBlockC2SPacket(BlockPos blockPos, AutoBlock autoBlock) {
        this.blockPos = blockPos;
        this.autoBlock = autoBlock;
    }

    public AutoBlockC2SPacket(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.autoBlock = buf.readJsonWithCodec(AutoBlock.CODEC);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(AutoBlock.CODEC, autoBlock);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork( () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof AutomationBlockEntity entity){
                        entity.setData(AUTO_BLOCK, this.autoBlock);
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
