package org.jahdoo.common.registers;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.effects.*;
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
import static net.minecraft.world.effect.MobEffectCategory.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static org.jahdoo.common.registers.AttributesRegister.*;

public class EffectsRegister {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, JahdooMod.MOD_ID);

    //Other effects
    public static final DeferredHolder<MobEffect, MobEffect>  STEP_BOOST =
        mobEffectWithAttribute("step_boost", STEP_HEIGHT,  1.0, ADD_VALUE, GenericEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> MANA_REGENERATION =
        mobEffectWithAttribute("mana_regeneration", MANA_REGEN,  1.0, ADD_VALUE, GenericEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> FROST_EFFECT =
        mobEffectWithAttribute("frost_effect", MOVEMENT_SPEED, -0.15, ADD_MULTIPLIED_BASE, FrostEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> AMPLIFY_BLOCK_REACH =
        mobEffectWithAttribute("amplify_block_reach", BLOCK_INTERACTION_RANGE,  1.0, ADD_VALUE, GenericEffect::new);

    //Standard effects
    public static final DeferredHolder<MobEffect, MobEffect> REPLENISH_MANA =
        mobEffect("replenish_mana", ReplenishManaEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> ITEM_MAGNET =
        mobEffect("item_magnet", ItemMagnetEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> REBOUND =
        mobEffect("rebound", Rebound::new);

    public static final DeferredHolder<MobEffect, MobEffect> STUN_EFFECT =
        mobEffect("stun_effect", StunEffect::new);

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

    public static void register (IEventBus eventBus) { MOB_EFFECTS.register(eventBus);
    }

    public static DeferredHolder<MobEffect, MobEffect> mobEffectWithAttribute(
        String name,
        Holder<Attribute> attribute,
        Double value,
        AttributeModifier.Operation operation,
        Supplier<? extends MobEffect> sup
    ){
        sup.get().addAttributeModifier(attribute, withDefaultNamespace(name + "tag"), value, operation);
        return MOB_EFFECTS.register(name, sup);
    }
}
