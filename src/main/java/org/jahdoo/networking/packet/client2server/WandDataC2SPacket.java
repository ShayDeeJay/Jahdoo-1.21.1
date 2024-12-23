package org.jahdoo.networking.packet.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.attachments.player_abilities.ModularChaosCubeProperties;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.block.wand.WandBlock;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.block.wand_block_manager.WandManagerTableEntity;
import org.jahdoo.components.WandData;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.registers.AttachmentRegister.MODULAR_CHAOS_CUBE;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class WandDataC2SPacket implements CustomPacketPayload {
    public static final Type<WandDataC2SPacket> TYPE = new Type<>(ModHelpers.res("wand_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WandDataC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(WandDataC2SPacket::toBytes, WandDataC2SPacket::new);

    BlockPos blockPos;
    ItemStack itemStack;

    public WandDataC2SPacket(ItemStack itemStack, BlockPos blockPos) {
        this.itemStack = itemStack;
        this.blockPos = blockPos;
    }

    public WandDataC2SPacket(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.itemStack = buf.readJsonWithCodec(ItemStack.CODEC);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(ItemStack.CODEC, itemStack);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof WandManagerTableEntity wandBlock){
                        wandBlock.inputItemHandler.setStackInSlot(0, itemStack);
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
