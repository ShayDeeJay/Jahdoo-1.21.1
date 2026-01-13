package org.jahdoo.common.networking.server2client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.Helpers;

public class ClientSoundS2CP implements CustomPacketPayload {
    public static final Type<ClientSoundS2CP> TYPE = new Type<>(Helpers.res("play_local_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientSoundS2CP> STREAM_CODEC = CustomPacketPayload.codec(ClientSoundS2CP::toBytes, ClientSoundS2CP::new);

    SoundEvent soundEvents;
    float volume;
    float pitch;
    boolean isLooping;

    public ClientSoundS2CP(SoundEvent soundEvents, float volume, float pitch, boolean isBatched) {
        this.soundEvents = soundEvents;
        this.volume = volume;
        this.pitch = pitch;
        this.isLooping = isBatched;
    }

    public ClientSoundS2CP(FriendlyByteBuf buf) {
        this.soundEvents = buf.readJsonWithCodec(SoundEvent.CODEC).value();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.isLooping = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeJsonWithCodec(SoundEvent.CODEC, new Holder.Direct<>(soundEvents));
        bug.writeFloat(volume);
        bug.writeFloat(pitch);
        bug.writeBoolean(isLooping);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                @Override
                public void run() {
                    if (ctx.player() instanceof LocalPlayer localPlayer) {
                        if(!isLooping){
                            localPlayer.playSound(soundEvents, volume, pitch);
                        } else {
                            var clientLevel = localPlayer.level();
                            if(clientLevel.isClientSide){
                                var sound = new SimpleSoundInstance(
                                    soundEvents.getLocation(),
                                    SoundSource.MUSIC,
                                    volume, pitch,
                                    SoundInstance.createUnseededRandom(),
                                    true,
                                    0,
                                    SoundInstance.Attenuation.NONE,
                                    0.0F, 0.0F, 0.0F,
                                    true
                                );

                                var soundManager = Minecraft.getInstance().getSoundManager();
                                soundManager.stop();
                                soundManager.play(sound);
                            }
                        }
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
