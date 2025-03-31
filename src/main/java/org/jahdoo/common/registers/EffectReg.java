package org.jahdoo.common.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.effects.GenericEffect;
import org.jahdoo.ascension.ability.effects.Rebound;
import org.jahdoo.ascension.ability.effects.ReplenishManaEffect;
import org.jahdoo.ascension.ability.effects.StunEffect;
import org.jahdoo.ascension.ability.effects.type_effects.frost.FrostEffect;
import org.jahdoo.ascension.ability.effects.type_effects.frost.GreaterFrostEffect;
import org.jahdoo.ascension.ability.effects.type_effects.inferno.GreaterInfernoEffect;
import org.jahdoo.ascension.ability.effects.type_effects.inferno.InfernoEffect;
import org.jahdoo.ascension.ability.effects.type_effects.mystic.GreaterMysticEffect;
import org.jahdoo.ascension.ability.effects.type_effects.mystic.MysticEffect;
import org.jahdoo.ascension.ability.effects.type_effects.vitality.GreaterVitalityEffect;
import org.jahdoo.ascension.ability.effects.type_effects.vitality.VitalityEffect;

import java.util.function.Supplier;

import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class EffectReg {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, JahdooMod.MOD_ID);

    //Other effects
    public static final DeferredHolder<MobEffect, MobEffect> STEP_BOOST = mobEffect("step_boost",
        () -> new GenericEffect().addAttributeModifier(STEP_HEIGHT, withDefaultNamespace("step_boost_tag"), 1.0F, ADD_VALUE)
    );

    public static final DeferredHolder<MobEffect, MobEffect> AMPLIFY_BLOCK_REACH = mobEffect("amplify_block_reach",
        () -> new GenericEffect().addAttributeModifier(BLOCK_INTERACTION_RANGE, withDefaultNamespace("amplify_block_reach_tag"), 1.0F, ADD_VALUE)
    );

    //Standard effects
    public static final DeferredHolder<MobEffect, MobEffect> REPLENISH_MANA =
        mobEffect("replenish_mana", ReplenishManaEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> REBOUND =
        mobEffect("rebound", Rebound::new);

    public static final DeferredHolder<MobEffect, MobEffect> STUN_EFFECT =
        mobEffect("stun_effect", StunEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> HEXED  =
        mobEffect("hexed_effect", GenericEffect::new);


    public static final DeferredHolder<MobEffect, MobEffect> FROST_EFFECT = mobEffect("frost_effect",
        () -> new FrostEffect().addAttributeModifier(MOVEMENT_SPEED, withDefaultNamespace("frost_effect_tag"), -0.05F, ADD_MULTIPLIED_TOTAL)
    );

    public static final DeferredHolder<MobEffect, MobEffect> GREATER_FROST_EFFECT =
        mobEffect("greater_frost_effect", GreaterFrostEffect::new);


    public static final DeferredHolder<MobEffect, MobEffect> MYSTIC_EFFECT =
        mobEffect("mystic_effect", MysticEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> GREATER_MYSTIC_EFFECT =
        mobEffect("greater_mystic_effect", GreaterMysticEffect::new);


    public static final DeferredHolder<MobEffect, MobEffect> INFERNO_EFFECT =
        mobEffect("inferno_effect", InfernoEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> GREATER_INFERNO_EFFECT =
        mobEffect("greater_inferno_effect", GreaterInfernoEffect::new);


    public static final DeferredHolder<MobEffect, MobEffect> VITALITY_EFFECT =
        mobEffect("vitality_effect", VitalityEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> GREATER_VITALITY_EFFECT =
        mobEffect("greater_vitality_effect", GreaterVitalityEffect::new);

    public static DeferredHolder<MobEffect, MobEffect> mobEffect(String name, Supplier<? extends MobEffect> sup){
        return MOB_EFFECTS.register(name, sup);
    }

    public static void register (IEventBus eventBus) { MOB_EFFECTS.register(eventBus); }

}
