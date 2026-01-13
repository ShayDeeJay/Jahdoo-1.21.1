package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.utils.Helpers;

public class SaveLoadoutS2CP implements CustomPacketPayload {

    public static final Type<SaveLoadoutS2CP> TYPE = new Type<>(Helpers.res("client_save_loadout"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveLoadoutS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(SaveLoadoutS2CP::toBytes, SaveLoadoutS2CP::new);

    private final int index;
    private final boolean isSaving;

    public SaveLoadoutS2CP(int index, boolean isSaving) {
        this.index = index;
        this.isSaving = isSaving;
    }

    public SaveLoadoutS2CP(FriendlyByteBuf buf) {
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
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    var casterData = localPlayer.getData(AttachmentReg.CASTER_DATA);
                    if(isSaving){
                        casterData.addLoadout(index);
                    } else {
                        casterData.initLoadout(index);
                    }
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
