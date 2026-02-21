package org.jahdoo.common.networking.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

public class ItemInBlockC2SP implements CustomPacketPayload {
    public static final Type<ItemInBlockC2SP> TYPE = new Type<>(JahdooHelpers.res("wand_data_sync"));
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
                        System.out.println(abstractBEntity.getInputItemHandler().getStackInSlot(index));
                        var stackInSlot = abstractBEntity.getInputItemHandler().getStackInSlot(index);

                        var all = abstractBEntity.getInputItemHandler().getSlots();

                        for (var i = 0; i < all; i++) {
                            var item = abstractBEntity.getInputItemHandler().getStackInSlot(i);
                            if (item == itemStack) {
                                item.shrink(1);
                            }
                        }

                        stackInSlot.shrink(1);
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
