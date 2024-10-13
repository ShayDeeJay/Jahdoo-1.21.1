package org.jahdoo.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.utils.GeneralHelpers;

public class SoundRegister {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, JahdooMod.MOD_ID);

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
    
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH_EFFECT = registerSoundEvent("dash_effect");
    public static final DeferredHolder<SoundEvent, SoundEvent> ORB_CREATE = registerSoundEvent("orb_create");
    public static final DeferredHolder<SoundEvent, SoundEvent> ORB_FIRE = registerSoundEvent("orb_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_EXPLOSION = registerSoundEvent("ice_explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_ATTACH = registerSoundEvent("ice_attach");
    public static final DeferredHolder<SoundEvent, SoundEvent> BOLT = registerSoundEvent("bolt");
    public static final DeferredHolder<SoundEvent, SoundEvent> TIMER = registerSoundEvent("timer");
    public static final DeferredHolder<SoundEvent, SoundEvent> EXPLOSION = registerSoundEvent("explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_EXPLOSION = registerSoundEvent("magic_explosion");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(GeneralHelpers.modResourceLocation(name)));
    }
}
