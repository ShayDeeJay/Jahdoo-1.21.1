package org.jahdoo.networking;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jahdoo.JahdooMod;
import org.jahdoo.networking.packet.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = JahdooMod.MOD_ID)
public class Network {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(JahdooMod.MOD_ID).versioned("1.0.0").optional();

        payloadRegistrar.playToServer(UseAbilityC2SPacket.TYPE, UseAbilityC2SPacket.STREAM_CODEC, UseAbilityC2SPacket::handle);
        payloadRegistrar.playToServer(SelectedAbilityC2SPacket.TYPE, SelectedAbilityC2SPacket.STREAM_CODEC, SelectedAbilityC2SPacket::handle);
        payloadRegistrar.playToServer(StopUsingC2SPacket.TYPE, StopUsingC2SPacket.STREAM_CODEC, StopUsingC2SPacket::handle);
        payloadRegistrar.playToServer(FlyingPacketC2SPacket.TYPE, FlyingPacketC2SPacket.STREAM_CODEC, FlyingPacketC2SPacket::handle);
        payloadRegistrar.playToServer(SyncPlayerItemComponentsPacket.TYPE, SyncPlayerItemComponentsPacket.STREAM_CODEC, SyncPlayerItemComponentsPacket::handle);

        payloadRegistrar.playToClient(ManaDataSyncS2CPacket.TYPE, ManaDataSyncS2CPacket.STREAM_CODEC, ManaDataSyncS2CPacket::handle);
        payloadRegistrar.playToClient(CooldownsDataSyncS2CPacket.TYPE, CooldownsDataSyncS2CPacket.STREAM_CODEC, CooldownsDataSyncS2CPacket::handle);
        payloadRegistrar.playToClient(MageFlightPacketS2CPacket.TYPE, MageFlightPacketS2CPacket.STREAM_CODEC, MageFlightPacketS2CPacket::handle);
//        payloadRegistrar.playToClient(NovaSmashS2CPacket.TYPE, NovaSmashS2CPacket.STREAM_CODEC, NovaSmashS2CPacket::handle);
    }

}
