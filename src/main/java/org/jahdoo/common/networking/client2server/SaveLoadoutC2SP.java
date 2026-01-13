package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.networking.server2client.SaveLoadoutS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.utils.Helpers;

public class SaveLoadoutC2SP implements CustomPacketPayload {

    public static final Type<SaveLoadoutC2SP> TYPE = new Type<>(Helpers.res("save_loadout"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveLoadoutC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(SaveLoadoutC2SP::toBytes, SaveLoadoutC2SP::new);

    private final int index;
    private final boolean isSaving;

    public SaveLoadoutC2SP(int index, boolean isSaving) {
        this.index = index;
        this.isSaving = isSaving;
    }

    public SaveLoadoutC2SP(FriendlyByteBuf buf) {
        this.index = buf.readInt();
        this.isSaving = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(index);
        buf.writeBoolean(isSaving);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
                    if(isSaving){
                        System.out.println("saving");
                        casterData.addLoadout(index);
                    } else {
                        casterData.initLoadout(index);
                    }
                    PacketDistributor.sendToPlayer(serverPlayer, new SaveLoadoutS2CP(index, isSaving));
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
