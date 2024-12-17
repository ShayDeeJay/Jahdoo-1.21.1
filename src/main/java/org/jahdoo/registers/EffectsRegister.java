package org.jahdoo.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.ability.effects.custom_effects.*;
import org.jahdoo.ability.effects.custom_effects.type_effects.*;
import org.jahdoo.ability.effects.custom_effects.type_effects.mystic_effect.MysticEffect;

public class EffectsRegister {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, JahdooMod.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect>  STEP_BOOST = MOB_EFFECTS.register("step_boost",
        () -> new GenericEffect(MobEffectCategory.BENEFICIAL, 3434234).addAttributeModifier(Attributes.STEP_HEIGHT, ResourceLocation.withDefaultNamespace("step_height"), 1.0, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, MobEffect> AMPLIFY_BLOCK_REACH = MOB_EFFECTS.register("amplify_block_reach",
        () -> new GenericEffect(MobEffectCategory.BENEFICIAL, 3436524).addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, ResourceLocation.withDefaultNamespace("player_reach"),1.0D, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, MobEffect> MANA_REGENERATION = MOB_EFFECTS.register("mana_regeneration",
        () -> new GenericEffect(MobEffectCategory.BENEFICIAL, 3436524).addAttributeModifier(AttributesRegister.MANA_REGEN, ResourceLocation.withDefaultNamespace("man_regen"),1.0D, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, MobEffect> REPLENISH_MANA = MOB_EFFECTS.register("replenish_mana",
        () -> new ReplenishManaEffect(MobEffectCategory.BENEFICIAL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> ITEM_MAGNET = MOB_EFFECTS.register("item_magnet",
        () -> new ItemMagnetEffect(MobEffectCategory.BENEFICIAL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> STUN_EFFECT = MOB_EFFECTS.register("stun_effect",
        () -> new StunEffect(MobEffectCategory.HARMFUL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> ICE_EFFECT = MOB_EFFECTS.register("frost_effect",
        () -> new FrostEffect(MobEffectCategory.HARMFUL, 3436524).addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.withDefaultNamespace("frost.speed"), -0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredHolder<MobEffect, MobEffect> MYSTIC_EFFECT = MOB_EFFECTS.register("mystic_effect",
        () -> new MysticEffect(MobEffectCategory.HARMFUL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> INFERNO_EFFECT = MOB_EFFECTS.register("inferno_effect",
        () -> new FireEffect(MobEffectCategory.HARMFUL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> LIGHTNING_EFFECT = MOB_EFFECTS.register("lightning_effect",
        () -> new LightningEffect(MobEffectCategory.HARMFUL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> VITALITY_EFFECT = MOB_EFFECTS.register("vitality_effect",
        () -> new VitalityEffect(MobEffectCategory.HARMFUL, 3436524)
    );

    public static void register (IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
