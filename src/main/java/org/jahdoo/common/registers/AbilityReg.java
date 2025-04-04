package org.jahdoo.common.registers;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.abilities_combat.BurningSkullsAbility;
import org.jahdoo.ascension.ability.abilities_combat.EscapeDecoyAbility;
import org.jahdoo.ascension.ability.abilities_combat.ancient_golem.SummonAncientGolemAbility;
import org.jahdoo.ascension.ability.abilities_combat.arcane_shift.ArcaneShiftAbility;
import org.jahdoo.ascension.ability.abilities_combat.armageddon.ArmageddonAbility;
import org.jahdoo.ascension.ability.abilities_combat.dimensional_recall.DimensionalRecallAbility;
import org.jahdoo.ascension.ability.abilities_combat.elemental_shooter.ElementalShooterAbility;
import org.jahdoo.ascension.ability.abilities_combat.eternal_wizard.SummonEternalWizardAbility;
import org.jahdoo.ascension.ability.abilities_combat.fireball.FireballAbility;
import org.jahdoo.ascension.ability.abilities_combat.frostbolts.FrostboltsAbility;
import org.jahdoo.ascension.ability.abilities_combat.hellfire.HellfireAbility;
import org.jahdoo.ascension.ability.abilities_combat.ice_bomb.IceBombAbility;
import org.jahdoo.ascension.ability.abilities_combat.life_siphon.LifeSiphonAbility;
import org.jahdoo.ascension.ability.abilities_combat.mystical_semtex.MysticalSemtexAbility;
import org.jahdoo.ascension.ability.abilities_combat.nova_smash.NovaSmashAbility;
import org.jahdoo.ascension.ability.abilities_combat.permafrost.PermafrostAbility;
import org.jahdoo.ascension.ability.abilities_combat.quantum_destroyer.QuantumDestroyerAbility;
import org.jahdoo.ascension.ability.abilities_combat.storm_rush.StormRushAbility;
import org.jahdoo.ascension.ability.abilities_combat.vital_rejuvenation.VitalRejuvenationAbility;
import org.jahdoo.ascension.ability.abilities_utility.block_bomb.BlockBombAbility;
import org.jahdoo.ascension.ability.abilities_utility.block_breaker.BlockBreakerAbility;
import org.jahdoo.ascension.ability.abilities_utility.block_placer.BlockPlacerAbility;
import org.jahdoo.ascension.ability.abilities_utility.enchanted_fusion.EnchantedFusionAbility;
import org.jahdoo.ascension.ability.abilities_utility.farmers_touch.FarmersTouchAbility;
import org.jahdoo.ascension.ability.abilities_utility.fetch.FetchAbility;
import org.jahdoo.ascension.ability.abilities_utility.hammer.HammerAbility;
import org.jahdoo.ascension.ability.abilities_utility.light_placer.LightPlacerAbility;
import org.jahdoo.ascension.ability.abilities_utility.vein_miner.VeinMinerAbility;
import org.jahdoo.ascension.ability.abilities_utility.wall_placer.WallPlacerAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.minecraft.resources.ResourceKey.createRegistryKey;
import static net.neoforged.neoforge.registries.DeferredRegister.create;
import static org.jahdoo.common.registers.ComponentReg.ABILITY_HOLDER;
import static org.jahdoo.common.registers.ElementReg.utility;

public class AbilityReg {

    public static final ResourceKey<Registry<Ability>> ABILITY_REGISTRY_KEY =
        createRegistryKey(ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID,"ability"));

    private static final DeferredRegister<Ability> ABILITIES =
        create(ABILITY_REGISTRY_KEY, JahdooMod.MOD_ID);

    public static final Registry<Ability> REGISTRY =
        new RegistryBuilder<>(ABILITY_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("SpellRegistry.registerRegistry");
        event.register(REGISTRY);
    }

    public static List<Ability> getSpellsByTypeId(String typeId) {
        return AbilityReg.REGISTRY
            .stream()
            .filter(a -> Objects.equals(a.setAbilityId(), typeId))
            .toList();
    }

    public static List<Ability> getMatchingRarity(JahdooRarity rarity) {
        return AbilityReg.REGISTRY
            .stream()
            .filter(a -> a.rarity().getId() == rarity.getId())
            .toList();
    }

    public static List<Ability> getMatchingRarityNoUtil(JahdooRarity rarity) {
        return AbilityReg.REGISTRY
            .stream()
            .filter(a -> a.rarity().getId() == rarity.getId())
            .filter(a -> a.getElemenType() != utility())
            .toList();
    }

    public static List<Ability> getMatchingRarityUtilOnly(JahdooRarity rarity) {
        return AbilityReg.REGISTRY
                .stream()
                .filter(a -> a.rarity().getId() == rarity.getId())
                .filter(a -> a.getElemenType() == utility())
                .toList();
    }

    public static Optional<Ability> getFirstSpellByTypeId(String typeId) {
        return AbilityReg.REGISTRY
            .stream()
            .filter(a -> Objects.equals(a.setAbilityId(), typeId))
            .findFirst();  // Lazy and returns an Optional
    }

    public static List<Ability> getWithElement(AbstractElement element) {
        return AbilityReg.REGISTRY
            .stream()
            .filter(a -> a.getElemenType() == element)
            .toList();  // Lazy and returns an Optional
    }

    public static Optional<Ability> getFirstSpellFromAugment(ItemStack itemStack) {
        if(itemStack.isEmpty()) return Optional.empty();
        var abilityHolder = itemStack.get(ABILITY_HOLDER);

        if(abilityHolder != null) {
            return AbilityReg.REGISTRY
                .stream()
                .filter(a -> Objects.equals(a.setAbilityId(), abilityHolder.abilityName()))
                .findFirst();
        }

        return Optional.empty();
    }

    //Volt
//    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BOLTZ = registerSpell(new BoltzAbility());

    //Multi-Type
    public static final DeferredHolder<Ability, Ability> ELEMENTAL_SHOOTER =
        registerSpell(new ElementalShooterAbility());

    //Inferno
    public static final DeferredHolder<Ability, Ability> ARMAGEDDON =
        registerSpell(new ArmageddonAbility());

    public static final DeferredHolder<Ability, Ability> FIREBALL =
        registerSpell(new FireballAbility());

    public static final DeferredHolder<Ability, Ability> HELLFIRE =
        registerSpell(new HellfireAbility());

    public static final DeferredHolder<Ability, Ability> BURNING_SKULLS =
        registerSpell(new BurningSkullsAbility());

    //Mystic
    public static final DeferredHolder<Ability, Ability> ARCANE_SHIFT =
        registerSpell(new ArcaneShiftAbility());

    public static final DeferredHolder<Ability, Ability> MYSTICAL_SEMTEX =
        registerSpell(new MysticalSemtexAbility());

    public static final DeferredHolder<Ability, Ability> QUANTUM_DESTROYER =
        registerSpell(new QuantumDestroyerAbility());

    public static final DeferredHolder<Ability, Ability> NOVA_SMASH =
        registerSpell(new NovaSmashAbility());

    public static final DeferredHolder<Ability, Ability> DIMENSIONAL_RECALL =
        registerSpell(new DimensionalRecallAbility());

    //Frost
    public static final DeferredHolder<Ability, Ability> PERMAFROST =
        registerSpell(new PermafrostAbility());

    public static final DeferredHolder<Ability, Ability> ICE_BOMB =
        registerSpell(new IceBombAbility());

    public static final DeferredHolder<Ability, Ability> FROST_BOLTS =
        registerSpell(new FrostboltsAbility());

    public static final DeferredHolder<Ability, Ability> STORM_RUSH =
        registerSpell(new StormRushAbility());

    //Vitality
    public static final DeferredHolder<Ability, Ability> SUMMON_ETERNAL_WIZARD =
        registerSpell(new SummonEternalWizardAbility());

    public static final DeferredHolder<Ability, Ability> ESCAPE_DECOY =
        registerSpell(new EscapeDecoyAbility());

    public static final DeferredHolder<Ability, Ability> VITAL_REJUVENATION =
        registerSpell(new VitalRejuvenationAbility());

    public static final DeferredHolder<Ability, Ability> SUMMON_ANCIENT_GOLEM =
        registerSpell(new SummonAncientGolemAbility());

    public static final DeferredHolder<Ability, Ability> LIFE_SIPHON =
        registerSpell(new LifeSiphonAbility());

    //Utility
    public static final DeferredHolder<Ability, Ability> BLOCK_BOMB =
        registerSpell(new BlockBombAbility());

    public static final DeferredHolder<Ability, Ability> BLOCK_BREAKER =
        registerSpell(new BlockBreakerAbility());

    public static final DeferredHolder<Ability, Ability> BLOCK_PLACER =
        registerSpell(new BlockPlacerAbility());

    public static final DeferredHolder<Ability, Ability> FARMERS_TOUCH =
        registerSpell(new FarmersTouchAbility());

    public static final DeferredHolder<Ability, Ability> HAMMER =
        registerSpell(new HammerAbility());

    public static final DeferredHolder<Ability, Ability> LIGHT_PLACER =
        registerSpell(new LightPlacerAbility());

    public static final DeferredHolder<Ability, Ability> FETCH =
        registerSpell(new FetchAbility());

    public static final DeferredHolder<Ability, Ability> VEIN_MINER =
        registerSpell(new VeinMinerAbility());

    public static final DeferredHolder<Ability, Ability> WALL_PLACER =
        registerSpell(new WallPlacerAbility());

    public static final DeferredHolder<Ability, Ability> ENCHANTED_FUSION =
        registerSpell(new EnchantedFusionAbility());

    private static DeferredHolder<Ability, Ability> registerSpell(Ability spell) {
        return ABILITIES.register(spell.setAbilityId(), () -> spell);
    }

    public static void register(IEventBus eventBus) {
        ABILITIES.register(eventBus);
    }
}
