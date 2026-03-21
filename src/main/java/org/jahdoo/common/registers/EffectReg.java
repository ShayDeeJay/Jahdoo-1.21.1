package org.jahdoo.common.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.magic.effects.ChampionEffect;
import org.jahdoo.trial_nexus.magic.effects.GenericEffect;
import org.jahdoo.trial_nexus.magic.effects.InfiniteManaEffect;
import org.jahdoo.trial_nexus.magic.effects.ReplenishManaEffect;

import java.util.function.Supplier;

public class EffectReg {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, JahdooMod.MOD_ID);

    //Standard effects
    public static final DeferredHolder<MobEffect, MobEffect> HEXED =
        mobEffect("hexed_effect", GenericEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> CHAMPION_EFFECT =
        mobEffect("champion_effect", ChampionEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> REPLENISH_MANA =
        mobEffect("replenish_mana", ReplenishManaEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> INFINITE_MANA  =
        mobEffect("infinite_mana", InfiniteManaEffect::new);


    public static DeferredHolder<MobEffect, MobEffect> mobEffect(String name, Supplier<? extends MobEffect> sup){
        return MOB_EFFECTS.register(name, sup);
    }

    public static void register (IEventBus eventBus) { MOB_EFFECTS.register(eventBus); }

}
