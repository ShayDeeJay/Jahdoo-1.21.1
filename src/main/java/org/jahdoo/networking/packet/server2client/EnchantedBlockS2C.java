package org.jahdoo.networking.packet.server2client;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.ModHelpers;

public class EnchantedBlockS2C implements CustomPacketPayload{
    public static final Type<EnchantedBlockS2C> TYPE = new Type<>(ModHelpers.res("block_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantedBlockS2C> STREAM_CODEC = CustomPacketPayload.codec(EnchantedBlockS2C::toBytes, EnchantedBlockS2C::new);
    private BlockPos blockPos;
    private BlockState block;
    private int stage;
    private int chance;
    private int spreadChance;

    public EnchantedBlockS2C(BlockPos blockPos, BlockState block, int stage, int chance, int spreadChance) {
        this.blockPos = blockPos;
        this.block = block;
        this.stage = stage;
        this.chance = chance;
        this.spreadChance = spreadChance;
    }

    public EnchantedBlockS2C(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.block = buf.readJsonWithCodec(BlockState.CODEC);
        this.stage = buf.readInt();
        this.chance = buf.readInt();
        this.spreadChance = buf.readInt();

    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeBlockPos(this.blockPos);
        bug.writeJsonWithCodec(BlockState.CODEC, this.block);
        bug.writeInt(stage);
        bug.writeInt(chance);
        bug.writeInt(spreadChance);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level().isClientSide){
                    ctx.player().level().setBlockAndUpdate(blockPos, BlocksRegister.ENCHANTED_BLOCK.get().defaultBlockState());
                    var blockE = ctx.player().level().getBlockEntity(blockPos);
                    if(blockE instanceof EnchantedBlockEntity entity){
                        entity.stage = stage;
                        entity.block = block.getBlock();
                        entity.growthChance = chance;
                        entity.spreadChance = spreadChance;
                    }
                }
            }
        );
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
