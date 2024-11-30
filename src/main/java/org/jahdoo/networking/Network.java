package org.jahdoo.networking;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jahdoo.JahdooMod;
import org.jahdoo.networking.packet.client2server.*;
import org.jahdoo.networking.packet.server2client.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = JahdooMod.MOD_ID)
public class Network {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(JahdooMod.MOD_ID).versioned("1.0.0").optional();
        payloadRegistrar.playToServer(UseAbilityC2SPacket.TYPE, UseAbilityC2SPacket.STREAM_CODEC, UseAbilityC2SPacket::handle);
        payloadRegistrar.playToServer(SelectedAbilityC2SPacket.TYPE, SelectedAbilityC2SPacket.STREAM_CODEC, SelectedAbilityC2SPacket::handle);
        payloadRegistrar.playToServer(StopUsingC2SPacket.TYPE, StopUsingC2SPacket.STREAM_CODEC, StopUsingC2SPacket::handle);
        payloadRegistrar.playToServer(FlyingPacketC2SPacket.TYPE, FlyingPacketC2SPacket.STREAM_CODEC, FlyingPacketC2SPacket::handle);
        payloadRegistrar.playToServer(SyncComponentC2S.TYPE, SyncComponentC2S.STREAM_CODEC, SyncComponentC2S::handle);
        payloadRegistrar.playToServer(FallDistanceSyncC2SPacket.TYPE, FallDistanceSyncC2SPacket.STREAM_CODEC, FallDistanceSyncC2SPacket::handle);
        payloadRegistrar.playToServer(SyncComponentBlockC2S.TYPE, SyncComponentBlockC2S.STREAM_CODEC, SyncComponentBlockC2S::handle);
        payloadRegistrar.playToServer(ModularChaosCubeC2SPacket.TYPE, ModularChaosCubeC2SPacket.STREAM_CODEC, ModularChaosCubeC2SPacket::handle);
        payloadRegistrar.playToServer(AugmentModificationChargeC2S.TYPE, AugmentModificationChargeC2S.STREAM_CODEC, AugmentModificationChargeC2S::handle);

        payloadRegistrar.playToClient(ManaDataSyncS2CPacket.TYPE, ManaDataSyncS2CPacket.STREAM_CODEC, ManaDataSyncS2CPacket::handle);
        payloadRegistrar.playToClient(CooldownsDataSyncS2CPacket.TYPE, CooldownsDataSyncS2CPacket.STREAM_CODEC, CooldownsDataSyncS2CPacket::handle);
        payloadRegistrar.playToClient(MageFlightPacketS2CPacket.TYPE, MageFlightPacketS2CPacket.STREAM_CODEC, MageFlightPacketS2CPacket::handle);
        payloadRegistrar.playToClient(MageFlightDataSyncS2CPacket.TYPE, MageFlightDataSyncS2CPacket.STREAM_CODEC, MageFlightDataSyncS2CPacket::handle);
        payloadRegistrar.playToClient(BouncyFootDataSyncS2CPacket.TYPE, BouncyFootDataSyncS2CPacket.STREAM_CODEC, BouncyFootDataSyncS2CPacket::handle);
        payloadRegistrar.playToClient(NovaSmashS2CPacket.TYPE, NovaSmashS2CPacket.STREAM_CODEC, NovaSmashS2CPacket::handle);
        payloadRegistrar.playToClient(PlayClientSoundSyncS2CPacket.TYPE, PlayClientSoundSyncS2CPacket.STREAM_CODEC, PlayClientSoundSyncS2CPacket::handle);
        payloadRegistrar.playToClient(EffectSyncS2CPacket.TYPE, EffectSyncS2CPacket.STREAM_CODEC, EffectSyncS2CPacket::handle);
        payloadRegistrar.playToClient(EnchantedBlockS2C.TYPE, EnchantedBlockS2C.STREAM_CODEC, EnchantedBlockS2C::handle);
    }

}

