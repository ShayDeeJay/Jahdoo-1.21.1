package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class SaveLoadoutC2SP implements CustomPacketPayload {


    public static final Type<SaveLoadoutC2SP> TYPE = new Type<>(JahdooHelpers.res("save_loadout"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveLoadoutC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(SaveLoadoutC2SP::toBytes, SaveLoadoutC2SP::new);

    private final int index;
    private final boolean shiftDown;
    private final boolean newLoadout;
    private final boolean canPress;
    private final boolean leftClick;

    public SaveLoadoutC2SP(int index, boolean shiftDown, boolean newLoadout, boolean canPress, boolean leftClick) {
        this.index = index;
        this.shiftDown = shiftDown;
        this.newLoadout = newLoadout;
        this.canPress = canPress;
        this.leftClick = leftClick;
    }

    public SaveLoadoutC2SP(FriendlyByteBuf buf) {
        this.index = buf.readInt();
        this.shiftDown = buf.readBoolean();
        this.newLoadout = buf.readBoolean();
        this.canPress = buf.readBoolean();
        this.leftClick = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(index);
        buf.writeBoolean(shiftDown);
        buf.writeBoolean(newLoadout);
        buf.writeBoolean(canPress);
        buf.writeBoolean(leftClick);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if (ctx.player() instanceof ServerPlayer serverPlayer) {
                    var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);

                    if(leftClick) {
                        casterData.removeLoadout(index);
                        return;
                    }

                    if (newLoadout && shiftDown) {
                        casterData.addLoadout(index);
                    } else if(canPress) {
                        casterData.initLoadout(index);
                    }

                    PacketDistributor.sendToPlayer(serverPlayer, new CastingDataSyncS2CP(casterData));
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
