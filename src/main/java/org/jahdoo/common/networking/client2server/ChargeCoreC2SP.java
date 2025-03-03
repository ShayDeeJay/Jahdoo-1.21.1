package org.jahdoo.common.networking.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.ascension.utils.Helpers;

public class ChargeCoreC2SP implements CustomPacketPayload{

    public static final Type<ChargeCoreC2SP> TYPE =
        new Type<>(Helpers.res("augment_modification_packets"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeCoreC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(ChargeCoreC2SP::toBytes, ChargeCoreC2SP::new);

    private final BlockPos blockPos;
    private final ItemStack itemStack;

    public ChargeCoreC2SP(BlockPos blockPos, ItemStack itemStack) {
        this.blockPos = blockPos;
        this.itemStack = itemStack;
    }

    public ChargeCoreC2SP(FriendlyByteBuf buf) {
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
                if(ctx.player().level() instanceof ServerLevel level){
                    var entity = level.getBlockEntity(this.blockPos);

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
