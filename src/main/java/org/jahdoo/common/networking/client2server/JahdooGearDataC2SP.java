package org.jahdoo.common.networking.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.registers.ComponentReg;

public class JahdooGearDataC2SP implements CustomPacketPayload {
    public static final Type<JahdooGearDataC2SP> TYPE = new Type<>(Helpers.res("gear_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, JahdooGearDataC2SP> STREAM_CODEC =
            CustomPacketPayload.codec(JahdooGearDataC2SP::toBytes, JahdooGearDataC2SP::new);

    JahdooGearData gearData;
    BlockPos blockPos;
    int index;

    public JahdooGearDataC2SP(JahdooGearData gearData, BlockPos blockPos, int index) {
        this.gearData = gearData;
        this.blockPos = blockPos;
        this.index = index;
    }

    public JahdooGearDataC2SP(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.gearData = buf.readJsonWithCodec(JahdooGearData.CODEC);
        this.index = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(JahdooGearData.CODEC, gearData);
        buf.writeInt(index);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof AbstractBEInventory wandBlock){
                        var original = wandBlock.inputItemHandler.getStackInSlot(index);
                        original.set(ComponentReg.JAHDOO_GEAR_DATA, gearData);
                        Helpers.repairDurability(original);
                        wandBlock.inputItemHandler.setStackInSlot(index, original);
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
