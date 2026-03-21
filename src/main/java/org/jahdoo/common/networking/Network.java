package org.jahdoo.common.networking;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.networking.client2server.*;
import org.jahdoo.common.networking.server2client.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = JahdooMod.MOD_ID)
public class Network {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(JahdooMod.MOD_ID).versioned("1.0.0").optional();
        //C2S
        payloadRegistrar.playToServer(
            UseAbilityC2SP.TYPE,
            UseAbilityC2SP.STREAM_CODEC,
            UseAbilityC2SP::handle
        );

        payloadRegistrar.playToServer(
            SelectAbilityC2SP.TYPE,
            SelectAbilityC2SP.STREAM_CODEC,
            SelectAbilityC2SP::handle
        );

        payloadRegistrar.playToServer(
            BlinkC2SP.TYPE,
            BlinkC2SP.STREAM_CODEC,
            BlinkC2SP::handle
        );

        payloadRegistrar.playToServer(
            RushC2SP.TYPE,
            RushC2SP.STREAM_CODEC,
            RushC2SP::handle
        );

        payloadRegistrar.playToServer(
            BlinkManaAndCounterC2SP.TYPE,
            BlinkManaAndCounterC2SP.STREAM_CODEC,
            BlinkManaAndCounterC2SP::handle
        );

        payloadRegistrar.playToServer(
            PocketDimensionC2SP.TYPE,
            PocketDimensionC2SP.STREAM_CODEC,
            PocketDimensionC2SP::handle
        );

        payloadRegistrar.playToServer(
            PhantomJumpMagicCircleC2SP.TYPE,
            PhantomJumpMagicCircleC2SP.STREAM_CODEC,
            PhantomJumpMagicCircleC2SP::handle
        );

        payloadRegistrar.playToServer(
            FlyingC2SP.TYPE,
            FlyingC2SP.STREAM_CODEC,
            FlyingC2SP::handle
        );

        payloadRegistrar.playToServer(
            ChaosCubeC2SP.TYPE,
            ChaosCubeC2SP.STREAM_CODEC,
            ChaosCubeC2SP::handle
        );

        payloadRegistrar.playToServer(
            ChargeSealC2SP.TYPE,
            ChargeSealC2SP.STREAM_CODEC,
            ChargeSealC2SP::handle
        );

        payloadRegistrar.playToServer(
            DurabilityC2SP.TYPE,
            DurabilityC2SP.STREAM_CODEC,
            DurabilityC2SP::handle
        );

        payloadRegistrar.playToServer(
            PlayerExpC2SP.TYPE,
            PlayerExpC2SP.STREAM_CODEC,
            PlayerExpC2SP::handle
        );

        payloadRegistrar.playToServer(
            MagnetActiveC2SP.TYPE,
            MagnetActiveC2SP.STREAM_CODEC,
            MagnetActiveC2SP::handle
        );

        payloadRegistrar.playToServer(
            AttributeC2SP.TYPE,
            AttributeC2SP.STREAM_CODEC,
            AttributeC2SP::handle
        );

        payloadRegistrar.playToServer(
            EffectC2SP.TYPE,
            EffectC2SP.STREAM_CODEC,
            EffectC2SP::handle
        );

        payloadRegistrar.playToServer(
            AbilityHolderC2SP.TYPE,
            AbilityHolderC2SP.STREAM_CODEC,
            AbilityHolderC2SP::handle
        );

        payloadRegistrar.playToServer(
            RegretAbilitiesC2SP.TYPE,
            RegretAbilitiesC2SP.STREAM_CODEC,
            RegretAbilitiesC2SP::handle
        );

        payloadRegistrar.playToServer(
            AddAbilityC2SP.TYPE,
            AddAbilityC2SP.STREAM_CODEC,
            AddAbilityC2SP::handle
        );

        payloadRegistrar.playToServer(
            RemoveAbilityC2SP.TYPE,
            RemoveAbilityC2SP.STREAM_CODEC,
            RemoveAbilityC2SP::handle
        );

        payloadRegistrar.playToServer(
            MobEffectC2SP.TYPE,
            MobEffectC2SP.STREAM_CODEC,
            MobEffectC2SP::handle
        );

        payloadRegistrar.playToServer(
            UnlockedSkillsC2SP.TYPE,
            UnlockedSkillsC2SP.STREAM_CODEC,
            UnlockedSkillsC2SP::handle
        );

        payloadRegistrar.playToServer(
            AbilityPointC2SP.TYPE,
            AbilityPointC2SP.STREAM_CODEC,
            AbilityPointC2SP::handle
        );

        payloadRegistrar.playToServer(
            PlayerTrialDataC2SP.TYPE,
            PlayerTrialDataC2SP.STREAM_CODEC,
            PlayerTrialDataC2SP::handle
        );

        payloadRegistrar.playToServer(
            AddQuestC2SP.TYPE,
            AddQuestC2SP.STREAM_CODEC,
            AddQuestC2SP::handle
        );

        payloadRegistrar.playToServer(
            WalletSyncC2SP.TYPE,
            WalletSyncC2SP.STREAM_CODEC,
            WalletSyncC2SP::handle
        );

        payloadRegistrar.playToServer(
            PerkTableSyncC2SP.TYPE,
            PerkTableSyncC2SP.STREAM_CODEC,
            PerkTableSyncC2SP::handle
        );

        payloadRegistrar.playToServer(
            JahdooGearDataC2SP.TYPE,
            JahdooGearDataC2SP.STREAM_CODEC,
            JahdooGearDataC2SP::handle
        );

        payloadRegistrar.playToServer(
            QuestTrackerC2SP.TYPE,
            QuestTrackerC2SP.STREAM_CODEC,
            QuestTrackerC2SP::handle
        );

        payloadRegistrar.playToServer(
            GivePlayerItemsC2SP.TYPE,
            GivePlayerItemsC2SP.STREAM_CODEC,
            GivePlayerItemsC2SP::handle
        );

        payloadRegistrar.playToServer(
            SaveLoadoutC2SP.TYPE,
            SaveLoadoutC2SP.STREAM_CODEC,
            SaveLoadoutC2SP::handle
        );

        //S2C

        payloadRegistrar.playToClient(
            CastingUnlocksSyncS2CP.TYPE,
            CastingUnlocksSyncS2CP.STREAM_CODEC,
            CastingUnlocksSyncS2CP::handle
        );

        payloadRegistrar.playToClient(
            PlayerTrialDataS2CP.TYPE,
            PlayerTrialDataS2CP.STREAM_CODEC,
            PlayerTrialDataS2CP::handle
        );

        payloadRegistrar.playToClient(
            OwnerSyncS2CP.TYPE,
            OwnerSyncS2CP.STREAM_CODEC,
            OwnerSyncS2CP::handle
        );

        payloadRegistrar.playToClient(
            ManaSyncS2CP.TYPE,
            ManaSyncS2CP.STREAM_CODEC,
            ManaSyncS2CP::handle
        );

        payloadRegistrar.playToClient(
            CooldownsSyncS2CP.TYPE,
            CooldownsSyncS2CP.STREAM_CODEC,
            CooldownsSyncS2CP::handle
        );

        payloadRegistrar.playToClient(
            MageFlightC2SP.TYPE,
            MageFlightC2SP.STREAM_CODEC,
            MageFlightC2SP::handle
        );

        payloadRegistrar.playToClient(
            MageFlightSyncS2CP.TYPE,
            MageFlightSyncS2CP.STREAM_CODEC,
            MageFlightSyncS2CP::handle
        );

        payloadRegistrar.playToClient(
            BouncyFootS2CP.TYPE,
            BouncyFootS2CP.STREAM_CODEC,
            BouncyFootS2CP::handle
        );

        payloadRegistrar.playToClient(
            NovaSmashS2CP.TYPE,
            NovaSmashS2CP.STREAM_CODEC,
            NovaSmashS2CP::handle
        );

        payloadRegistrar.playToClient(
            ClientSoundS2CP.TYPE,
            ClientSoundS2CP.STREAM_CODEC,
            ClientSoundS2CP::handle
        );

        payloadRegistrar.playToClient(
            MoveClientEntityS2CP.TYPE,
            MoveClientEntityS2CP.STREAM_CODEC,
            MoveClientEntityS2CP::handle
        );

        payloadRegistrar.playToClient(
            BlinkS2CP.TYPE,
            BlinkS2CP.STREAM_CODEC,
            BlinkS2CP::handle
        );

        payloadRegistrar.playToClient(
            InstanceSyncS2CP.TYPE,
            InstanceSyncS2CP.STREAM_CODEC,
            InstanceSyncS2CP::handle
        );

        payloadRegistrar.playToClient(
            AbilityHolderS2CP.TYPE,
            AbilityHolderS2CP.STREAM_CODEC,
            AbilityHolderS2CP::handle
        );

        payloadRegistrar.playToClient(
            CastingDataSyncS2CP.TYPE,
            CastingDataSyncS2CP.STREAM_CODEC,
            CastingDataSyncS2CP::handle
        );

        payloadRegistrar.playToClient(
            RunDataS2CP.TYPE,
            RunDataS2CP.STREAM_CODEC,
            RunDataS2CP::handle
        );

        payloadRegistrar.playToClient(
            QuestTrackerS2CP.TYPE,
            QuestTrackerS2CP.STREAM_CODEC,
            QuestTrackerS2CP::handle
        );

        payloadRegistrar.playToClient(
            ClearPlayerTrialDataS2CP.TYPE,
            ClearPlayerTrialDataS2CP.STREAM_CODEC,
            ClearPlayerTrialDataS2CP::handle
        );

        payloadRegistrar.playToClient(
            JahdooToastS2CP.TYPE,
            JahdooToastS2CP.STREAM_CODEC,
            JahdooToastS2CP::handle
        );
    }

}

