package org.jahdoo.common.registers.mod;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.BlankRune;
import org.jahdoo.common.items.runes.aether_rune.ManaPoolRune;
import org.jahdoo.common.items.runes.aether_rune.ManaRegenRune;
import org.jahdoo.common.items.runes.cosmic_rune.CooldownReductionRune;
import org.jahdoo.common.items.runes.cosmic_rune.MagicDamageRune;
import org.jahdoo.common.items.runes.cosmic_rune.ManaCostReductionRune;
import org.jahdoo.common.items.runes.elemental_rune.frost_runes.FrostCooldownRune;
import org.jahdoo.common.items.runes.elemental_rune.frost_runes.FrostDamageRune;
import org.jahdoo.common.items.runes.elemental_rune.frost_runes.FrostEffectRune;
import org.jahdoo.common.items.runes.elemental_rune.frost_runes.FrostManaRune;
import org.jahdoo.common.items.runes.elemental_rune.inferno_runes.InfernoCooldownRune;
import org.jahdoo.common.items.runes.elemental_rune.inferno_runes.InfernoDamageRune;
import org.jahdoo.common.items.runes.elemental_rune.inferno_runes.InfernoEffectRune;
import org.jahdoo.common.items.runes.elemental_rune.inferno_runes.InfernoManaRune;
import org.jahdoo.common.items.runes.elemental_rune.mystic_runes.MysticCooldownRune;
import org.jahdoo.common.items.runes.elemental_rune.mystic_runes.MysticDamageRune;
import org.jahdoo.common.items.runes.elemental_rune.mystic_runes.MysticEffectRune;
import org.jahdoo.common.items.runes.elemental_rune.mystic_runes.MysticManaRune;
import org.jahdoo.common.items.runes.elemental_rune.vitality_runes.VitalityCooldownRune;
import org.jahdoo.common.items.runes.elemental_rune.vitality_runes.VitalityDamageRune;
import org.jahdoo.common.items.runes.elemental_rune.vitality_runes.VitalityEffectRune;
import org.jahdoo.common.items.runes.elemental_rune.vitality_runes.VitalityManaRune;
import org.jahdoo.common.items.runes.perk_rune.DestinyBondRune;
import org.jahdoo.common.items.runes.perk_rune.MaxAbsorptionRune;
import org.jahdoo.common.items.runes.perk_rune.MaxHealthRune;
import org.jahdoo.common.items.runes.resilience_rune.FlurryRune;
import org.jahdoo.common.items.runes.resilience_rune.ResilienceRune;
import org.jahdoo.common.items.runes.resilience_rune.StrikerRune;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;
import org.jahdoo.common.items.runes.skill_rune.*;
import org.jahdoo.common.items.runes.sympathiser_rune.AbsorptionHeartRune;
import org.jahdoo.common.items.runes.sympathiser_rune.CastHealRune;
import org.jahdoo.common.items.runes.sympathiser_rune.SkipCooldownRune;
import org.jahdoo.common.items.runes.sympathiser_rune.SkipManaRune;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.Helpers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class RuneReg {

    public static final ResourceKey<Registry<AbstractRune>> RUNE_REGISTRY_KEY = ResourceKey.createRegistryKey(JahdooHelpers.res("rune"));
    private static final DeferredRegister<AbstractRune> RUNE = DeferredRegister.create(RUNE_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractRune> REGISTRY =  new RegistryBuilder<>(RUNE_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("Rune.RegisterRegistry");
        event.register(REGISTRY);
    }

    private static DeferredHolder<AbstractRune, AbstractRune> registerRune(Supplier<AbstractRune> rune) {
        return RUNE.register(rune.get().runeId(), rune);
    }

    public static Optional<AbstractRune> getByAttributeId(String attributeId) {
        return REGISTRY.stream().filter(s -> s.attributeHolder().value().getDescriptionId().equals(attributeId)).findFirst();
    }

    public static List<AbstractRune> getAllAttributes() {
        return REGISTRY.stream().toList();
    }

    public static List<AbstractRune> getAllEffectAttributes() {
        return REGISTRY.stream().filter(s -> s.runeCategory().equals(RuneCategories.EFFECT)).toList();
    }

    public static List<AbstractRune> getAllSkillAttributes() {
        return REGISTRY.stream().filter(s -> s.runeCategory().equals(RuneCategories.SKILL)).toList();
    }

    public static List<AbstractRune> getAllRunes() {
        return REGISTRY.stream().filter(s -> !s.runeCategory().getIsDummy()).toList();
    }

    public static AbstractRune getRuneFromId(String runeId) {
        var list = getAllRunes().stream().filter(s -> s.runeId().equals(runeId)).toList();
        return list.isEmpty() ? BLANK_RUNE.get() : list.getFirst();
    }

    public static AbstractRune getRuneFromAttribute(Holder<Attribute> attributeHolder) {
        var list = getAllAttributes().stream().filter(s -> s.attributeHolder().equals(attributeHolder)).toList();
        return list.isEmpty() ? null : Helpers.listRandom(list);
    }

    public static Optional<AbstractRune> getRuneWithRarity(JahdooRarity rarity) {
        return Optional.of(Helpers.listRandom(getAllRunes().stream().filter(s -> s.runeRarity().equals(rarity)).toList()));
    }

    public static List<AbstractRune> getAllRuneWithRarity(JahdooRarity...rarity) {
        return getAllRunes().stream().filter(s -> Arrays.stream(rarity).toList().contains(s.runeRarity())).toList();
    }

    public static List<AbstractRune> runesWithoutCategoryAndRarity(RuneCategories...categories) {
        return getAllRunes()
            .stream()
            .filter(s -> !Arrays.stream(categories).toList().contains(s.runeCategory()))
            .filter(s -> s != DESTINY_BOND_RUNE.get())
            .toList();
    }

    //Aether Runes
    public static final DeferredHolder<AbstractRune, AbstractRune> MANA_POOL_RUNE =
        registerRune(ManaPoolRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> MANA_REGEN_RUNE =
        registerRune(ManaRegenRune::new);

    //Cosmic Runes
    public static final DeferredHolder<AbstractRune, AbstractRune> COOLDOWN_REDUCTION_RUNE =
        registerRune(CooldownReductionRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> MAGIC_DAMAGE_RUNE =
        registerRune(MagicDamageRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> MANA_COST_REDUCTION_RUNE =
        registerRune(ManaCostReductionRune::new);

    //Inferno Runes
    public static final DeferredHolder<AbstractRune, AbstractRune> INFERNO_EFFECT_RUNE =
        registerRune(InfernoEffectRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> INFERNO_COOLDOWN_RUNE =
        registerRune(InfernoCooldownRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> INFERNO_MANA_RUNE =
        registerRune(InfernoManaRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> INFERNO_DAMAGE_RUNE =
        registerRune(InfernoDamageRune::new);

    //Frost Runes
    public static final DeferredHolder<AbstractRune, AbstractRune> FROST_EFFECT_RUNE =
        registerRune(FrostEffectRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> FROST_COOLDOWN_RUNE =
        registerRune(FrostCooldownRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> FROST_MANA_RUNE =
        registerRune(FrostManaRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> FROST_DAMAGE_RUNE =
        registerRune(FrostDamageRune::new);

    //Mystic Runes
    public static final DeferredHolder<AbstractRune, AbstractRune> MYSTIC_EFFECT_RUNE =
        registerRune(MysticEffectRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> MYSTIC_COOLDOWN_RUNE =
        registerRune(MysticCooldownRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> MYSTIC_MANA_RUNE =
        registerRune(MysticManaRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> MYSTIC_DAMAGE_RUNE =
        registerRune(MysticDamageRune::new);

    //Vitality Runes
    public static final DeferredHolder<AbstractRune, AbstractRune> VITALITY_EFFECT_RUNE =
        registerRune(VitalityEffectRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> VITALITY_COOLDOWN_RUNE =
        registerRune(VitalityCooldownRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> VITALITY_MANA_RUNE =
        registerRune(VitalityManaRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> VITALITY_DAMAGE_RUNE =
        registerRune(VitalityDamageRune::new);

    //Perk Runes
    public static final DeferredHolder<AbstractRune, AbstractRune> DESTINY_BOND_RUNE =
        registerRune(DestinyBondRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> MAX_ABSORPTION_RUNE =
        registerRune(MaxAbsorptionRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> MAX_HEALTH_RUNE =
        registerRune(MaxHealthRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> RESILIENCE =
        registerRune(ResilienceRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> STRIKER =
        registerRune(StrikerRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> FLURRY =
        registerRune(FlurryRune::new);

    //Sympathiser Rune
    public static final DeferredHolder<AbstractRune, AbstractRune> ABSORPTION_HEART_RUNE =
        registerRune(AbsorptionHeartRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> CAST_HEAL_RUNE =
        registerRune(CastHealRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> SKIP_COOLDOWN_RUNE =
        registerRune(SkipCooldownRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> SKIP_MANA_RUNE =
        registerRune(SkipManaRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> BLANK_RUNE =
        registerRune(BlankRune::new);

    //Skill Rune
    public static final DeferredHolder<AbstractRune, AbstractRune> SWIFT_RUNE =
        registerRune(SwiftRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> CLIMBER_RUNE =
        registerRune(ClimberRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> RUSH_RUNE =
        registerRune(RushRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> BLINK_RUNE =
        registerRune(BlinkRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> MAGE_FLIGHT_RUNE =
        registerRune(MageFlightRune::new);

    public static final DeferredHolder<AbstractRune, AbstractRune> PHANTOM_JUMP_RUNE =
        registerRune(PhantomJumpRune::new);


    public static void register(IEventBus eventBus) {
        RUNE.register(eventBus);
    }

}
