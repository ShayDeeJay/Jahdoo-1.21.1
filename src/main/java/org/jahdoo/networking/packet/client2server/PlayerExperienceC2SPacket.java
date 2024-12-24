package org.jahdoo.networking.packet.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.block.wand_block_manager.WandManagerTableEntity;
import org.jahdoo.utils.ModHelpers;

public class PlayerExperienceC2SPacket implements CustomPacketPayload {
    public static final Type<PlayerExperienceC2SPacket> TYPE = new Type<>(ModHelpers.res("experience_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerExperienceC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(PlayerExperienceC2SPacket::toBytes, PlayerExperienceC2SPacket::new);
    int level;

    public PlayerExperienceC2SPacket(int level) {
        this.level = level;
    }

    public PlayerExperienceC2SPacket(FriendlyByteBuf buf) {
        this.level = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(level);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    serverPlayer.setExperienceLevels(level);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
