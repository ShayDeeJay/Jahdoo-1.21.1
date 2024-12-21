package org.jahdoo.networking.packet.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.ModHelpers;

import java.util.Map;

import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;

public class CooldownsDataSyncS2CPacket implements CustomPacketPayload {
    public static final Type<CooldownsDataSyncS2CPacket> TYPE = new Type<>(ModHelpers.res("player_cooldowns"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CooldownsDataSyncS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(CooldownsDataSyncS2CPacket::toBytes, CooldownsDataSyncS2CPacket::new);

    private final Map<String, Integer> abilityCooldowns;
    private final Map<String, Integer> abilityCooldownsStatic;

    public CooldownsDataSyncS2CPacket(Map<String, Integer> abilityCooldowns, Map<String, Integer> abilityCooldownsStatic) {
        this.abilityCooldowns = abilityCooldowns;
        this.abilityCooldownsStatic = abilityCooldownsStatic;
    }

    public CooldownsDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.abilityCooldowns = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
        this.abilityCooldownsStatic = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(this.abilityCooldowns, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
        buf.writeMap(this.abilityCooldownsStatic, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx.player() instanceof LocalPlayer localPlayer) {
                        localPlayer.getData(CASTER_DATA).setLocalCooldowns(abilityCooldowns);
                        localPlayer.getData(CASTER_DATA).setLocalCooldownsStatic(abilityCooldownsStatic);
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
