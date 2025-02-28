package org.jahdoo.common.networking.packet.server2client;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.common.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.ascension.utils.Helpers;

public class AltarBlockS2C implements CustomPacketPayload{
    public static final Type<AltarBlockS2C> TYPE = new Type<>(Helpers.res("altar_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarBlockS2C> STREAM_CODEC = CustomPacketPayload.codec(AltarBlockS2C::toBytes, AltarBlockS2C::new);
    BlockPos blockPos;
    ChallengeLevelData properties;
    int privateTicks;

    public AltarBlockS2C(BlockPos blockPos, ChallengeLevelData properties, int privateTicks) {
        this.blockPos = blockPos;
        this.properties = properties;
        this.privateTicks = privateTicks;
    }

    public AltarBlockS2C(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.properties = buf.readJsonWithCodec(ChallengeLevelData.CODEC);
        this.privateTicks = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeJsonWithCodec(ChallengeLevelData.CODEC, properties);
        buf.writeInt(privateTicks);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                var level = ctx.player().level();
                if(level.isClientSide){
                    var blockE = level.getBlockEntity(blockPos);
                    if(blockE instanceof ChallengeAltarBlockEntity entity){
                        entity.setData(AttachmentReg.CHALLENGE_ALTAR, properties);
                        entity.setChanged();
                        entity.privateTicks = privateTicks;
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
