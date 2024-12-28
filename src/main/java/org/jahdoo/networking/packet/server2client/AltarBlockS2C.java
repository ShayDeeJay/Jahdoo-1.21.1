package org.jahdoo.networking.packet.server2client;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.block.challange_altar.RoundGenerator;
import org.jahdoo.block.enchanted_block.EnchantedBlockEntity;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.ModHelpers;

public class AltarBlockS2C implements CustomPacketPayload{
    public static final Type<AltarBlockS2C> TYPE = new Type<>(ModHelpers.res("altar_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarBlockS2C> STREAM_CODEC = CustomPacketPayload.codec(AltarBlockS2C::toBytes, AltarBlockS2C::new);
    private final int baddies;
    private final BlockPos blockPos;

    public AltarBlockS2C(int baddies, BlockPos blockPos) {
        this.baddies = baddies;
        this.blockPos = blockPos;
    }

    public AltarBlockS2C(FriendlyByteBuf buf) {
        this.baddies = buf.readInt();
        this.blockPos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeInt(baddies);
        bug.writeBlockPos(this.blockPos);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level().isClientSide){
                    var blockE = ctx.player().level().getBlockEntity(blockPos);
                    if(blockE instanceof ChallengeAltarBlockEntity entity){
                        System.out.println(entity);
                        entity.totalEntities = baddies;
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
