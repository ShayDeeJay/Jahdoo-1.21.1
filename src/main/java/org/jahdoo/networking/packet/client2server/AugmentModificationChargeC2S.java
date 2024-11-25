package org.jahdoo.networking.packet.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.utils.ModHelpers;

public class AugmentModificationChargeC2S implements CustomPacketPayload{
    public static final Type<AugmentModificationChargeC2S> TYPE = new Type<>(ModHelpers.res("augment_modification_packets"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AugmentModificationChargeC2S> STREAM_CODEC = CustomPacketPayload.codec(AugmentModificationChargeC2S::toBytes, AugmentModificationChargeC2S::new);
    private final BlockPos blockPos;
    private final ItemStack itemStack;

    public AugmentModificationChargeC2S(BlockPos blockPos, ItemStack itemStack) {
        this.blockPos = blockPos;
        this.itemStack = itemStack;
    }

    public AugmentModificationChargeC2S(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.itemStack = buf.readJsonWithCodec(ItemStack.CODEC);
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeBlockPos(this.blockPos);
        bug.writeJsonWithCodec(ItemStack.CODEC, this.itemStack);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var entity = serverLevel.getBlockEntity(this.blockPos);
                    if(entity instanceof AugmentModificationEntity entity1){
                        chargeCoreSides(entity1, this.itemStack);
                    }
                }
            }
        );
        return true;
    }

    public static void chargeCoreSides(AugmentModificationEntity entity1, ItemStack itemStack) {
        var inputItemHandler = entity1.inputItemHandler;
        for(int i = 1; i < inputItemHandler.getSlots(); i++){
            if(inputItemHandler.getStackInSlot(i).getItem() == itemStack.getItem()){
                inputItemHandler.getStackInSlot(i).shrink(1);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
