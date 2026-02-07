package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.Map;

import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;

public class CooldownsSyncS2CP implements CustomPacketPayload {
    public static final Type<CooldownsSyncS2CP> TYPE = new Type<>(JahdooHelpers.res("player_cooldowns"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CooldownsSyncS2CP> STREAM_CODEC = CustomPacketPayload.codec(CooldownsSyncS2CP::toBytes, CooldownsSyncS2CP::new);

    private final Map<String, Integer> abilityCooldowns;
    private final Map<String, Integer> abilityCooldownsStatic;

    public CooldownsSyncS2CP(Map<String, Integer> abilityCooldowns, Map<String, Integer> abilityCooldownsStatic) {
        this.abilityCooldowns = abilityCooldowns;
        this.abilityCooldownsStatic = abilityCooldownsStatic;
    }

    public CooldownsSyncS2CP(FriendlyByteBuf buf) {
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
