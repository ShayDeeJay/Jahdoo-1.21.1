package org.jahdoo.common.networking.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.trial_nexus.utils.Helpers;

public class ItemInBlockC2SP implements CustomPacketPayload {
    public static final Type<ItemInBlockC2SP> TYPE = new Type<>(Helpers.res("wand_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemInBlockC2SP> STREAM_CODEC =
            CustomPacketPayload.codec(ItemInBlockC2SP::toBytes, ItemInBlockC2SP::new);

    BlockPos blockPos;
    ItemStack itemStack;
    int index;

    public ItemInBlockC2SP(ItemStack itemStack, BlockPos blockPos, int index) {
        this.itemStack = itemStack;
        this.blockPos = blockPos;
        this.index = index;
    }

    public ItemInBlockC2SP(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.itemStack = buf.readJsonWithCodec(ItemStack.OPTIONAL_CODEC);
        this.index = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(ItemStack.OPTIONAL_CODEC, itemStack);
        buf.writeInt(index);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof AbstractBEInventory abstractBEntity){
                        abstractBEntity.inputItemHandler.setStackInSlot(index, itemStack);
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
