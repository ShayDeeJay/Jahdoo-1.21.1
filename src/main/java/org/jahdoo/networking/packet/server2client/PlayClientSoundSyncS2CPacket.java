package org.jahdoo.networking.packet.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.ModHelpers;

public class PlayClientSoundSyncS2CPacket implements CustomPacketPayload {
    public static final Type<PlayClientSoundSyncS2CPacket> TYPE = new Type<>(ModHelpers.res("play_local_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayClientSoundSyncS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(PlayClientSoundSyncS2CPacket::toBytes, PlayClientSoundSyncS2CPacket::new);

    SoundEvent soundEvents;
    float volume;
    float pitch;

    public PlayClientSoundSyncS2CPacket(SoundEvent soundEvents, float volume, float pitch) {
        this.soundEvents = soundEvents;
        this.volume = volume;
        this.pitch = pitch;
    }

    public PlayClientSoundSyncS2CPacket(FriendlyByteBuf buf) {
        this.soundEvents = buf.readJsonWithCodec(SoundEvent.CODEC).value();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeJsonWithCodec(SoundEvent.CODEC, new Holder.Direct<>(soundEvents));
        bug.writeFloat(volume);
        bug.writeFloat(pitch);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx.player() instanceof LocalPlayer localPlayer) {

                        localPlayer.playSound(soundEvents, volume, pitch);
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
