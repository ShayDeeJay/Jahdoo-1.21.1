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
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.ability.abilities.BurningSkullsAbility;
import org.jahdoo.ascension.ability.abilities.EscapeDecoyAbility;
import org.jahdoo.ascension.ability.abilities.vital_rejuvenation.*;
import org.jahdoo.ascension.ability.abilities.ancient_golem.SummonAncientGolemAbility;
import org.jahdoo.ascension.ability.abilities.arcane_shift.ArcaneShiftAbility;
import org.jahdoo.ascension.ability.abilities.armageddon.ArmageddonAbility;
import org.jahdoo.ascension.ability.abilities.block_bomb.BlockBombAbility;
import org.jahdoo.ascension.ability.abilities.block_breaker.BlockBreakerAbility;
import org.jahdoo.ascension.ability.abilities.block_placer.BlockPlacerAbility;
import org.jahdoo.ascension.ability.abilities.dimensional_recall.DimensionalRecallAbility;
import org.jahdoo.ascension.ability.abilities.elemental_shooter.ElementalShooterAbility;
import org.jahdoo.ascension.ability.abilities.enchanted_fusion.EnchantedFusionAbility;
import org.jahdoo.ascension.ability.abilities.eternal_wizard.SummonEternalWizardAbility;
import org.jahdoo.ascension.ability.abilities.farmers_touch.FarmersTouchAbility;
import org.jahdoo.ascension.ability.abilities.fetch.FetchAbility;
import org.jahdoo.ascension.ability.abilities.fireball.FireballAbility;
import org.jahdoo.ascension.ability.abilities.frostbolts.FrostboltsAbility;
import org.jahdoo.ascension.ability.abilities.hammer.HammerAbility;
import org.jahdoo.ascension.ability.abilities.hellfire.HellfireAbility;
import org.jahdoo.ascension.ability.abilities.ice_bomb.IceBombAbility;
import org.jahdoo.ascension.ability.abilities.life_siphon.LifeSiphonAbility;
import org.jahdoo.ascension.ability.abilities.light_placer.LightPlacerAbility;
import org.jahdoo.ascension.ability.abilities.mystical_semtex.MysticalSemtexAbility;
import org.jahdoo.ascension.ability.abilities.nova_smash.NovaSmashAbility;
import org.jahdoo.ascension.ability.abilities.permafrost.PermafrostAbility;
import org.jahdoo.ascension.ability.abilities.quantum_destroyer.QuantumDestroyerAbility;
import org.jahdoo.ascension.ability.abilities.storm_rush.StormRushAbility;
import org.jahdoo.ascension.ability.abilities.vein_miner.VeinMinerAbility;
import org.jahdoo.ascension.ability.abilities.wall_placer.WallPlacerAbility;
import org.jahdoo.ascension.rarity.JahdooRarity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.minecraft.resources.ResourceKey.*;
import static net.neoforged.neoforge.registries.DeferredRegister.*;
import static org.jahdoo.common.registers.ComponentReg.WAND_ABILITY_HOLDER;
import static org.jahdoo.common.registers.ElementReg.utility;

public class AbilityReg {

    public static final ResourceKey<Registry<AbilityRegistrar>> ABILITY_REGISTRY_KEY =
        createRegistryKey(ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID,"ability"));

    private static final DeferredRegister<AbilityRegistrar> ABILITIES =
        create(ABILITY_REGISTRY_KEY, JahdooMod.MOD_ID);

    public static final Registry<AbilityRegistrar> REGISTRY =
        new RegistryBuilder<>(ABILITY_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("SpellRegistry.registerRegistry");
        event.register(REGISTRY);
    }

    public static List<AbilityRegistrar> getSpellsByTypeId(String typeId) {
        return AbilityReg.REGISTRY
            .stream()
            .filter(a -> Objects.equals(a.setAbilityId(), typeId))
            .toList();
    }

    public static List<AbilityRegistrar> getMatchingRarity(JahdooRarity rarity) {
        return AbilityReg.REGISTRY
            .stream()
            .filter(a -> a.rarity().getId() == rarity.getId())
            .toList();
    }

    public static List<AbilityRegistrar> getMatchingRarityNoUtil(JahdooRarity rarity) {
        return AbilityReg.REGISTRY
            .stream()
            .filter(a -> a.rarity().getId() == rarity.getId())
            .filter(a -> a.getElemenType() != utility())
            .toList();
    }

    public static List<AbilityRegistrar> getMatchingRarityUtilOnly(JahdooRarity rarity) {
        return AbilityReg.REGISTRY
                .stream()
                .filter(a -> a.rarity().getId() == rarity.getId())
                .filter(a -> a.getElemenType() == utility())
                .toList();
    }

    public static Optional<AbilityRegistrar> getFirstSpellByTypeId(String typeId) {
        return AbilityReg.REGISTRY
            .stream()
            .filter(a -> Objects.equals(a.setAbilityId(), typeId))
            .findFirst();  // Lazy and returns an Optional
    }

    public static Optional<AbilityRegistrar> getFirstSpellFromAugment(ItemStack itemStack) {
        if(itemStack.isEmpty()) return Optional.empty();
        var wandAbilityHolder = itemStack.get(WAND_ABILITY_HOLDER);

        if(wandAbilityHolder != null) {
            var typeId = wandAbilityHolder.abilityProperties().keySet().stream().findFirst();
            return typeId.flatMap(s ->
                AbilityReg.REGISTRY
                    .stream()
                    .filter(a -> Objects.equals(a.setAbilityId(), s))
                    .findFirst()
            );
        }

        return Optional.empty();
    }

    //Volt
//    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BOLTZ = registerSpell(new BoltzAbility());

    //Multi-Type
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ELEMENTAL_SHOOTER =
        registerSpell(new ElementalShooterAbility());

    //Inferno
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ARMAGEDDON =
        registerSpell(new ArmageddonAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> FIREBALL =
        registerSpell(new FireballAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> HELLFIRE =
        registerSpell(new HellfireAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BURNING_SKULLS =
        registerSpell(new BurningSkullsAbility());

    //Mystic
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ARCANE_SHIFT =
        registerSpell(new ArcaneShiftAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> MYSTICAL_SEMTEX =
        registerSpell(new MysticalSemtexAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> QUANTUM_DESTROYER =
        registerSpell(new QuantumDestroyerAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> NOVA_SMASH =
        registerSpell(new NovaSmashAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> DIMENSIONAL_RECALL =
        registerSpell(new DimensionalRecallAbility());

    //Frost
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> PERMAFROST =
        registerSpell(new PermafrostAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ICE_BOMB =
        registerSpell(new IceBombAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> FROST_BOLTS =
        registerSpell(new FrostboltsAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> STORM_RUSH =
        registerSpell(new StormRushAbility());

    //Vitality
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> SUMMON_ETERNAL_WIZARD =
        registerSpell(new SummonEternalWizardAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ESCAPE_DECOY =
        registerSpell(new EscapeDecoyAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> VITAL_REJUVENATION =
        registerSpell(new VitalRejuvenationAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> SUMMON_ANCIENT_GOLEM =
        registerSpell(new SummonAncientGolemAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> LIFE_SIPHON =
        registerSpell(new LifeSiphonAbility());

    //Utility
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BLOCK_BOMB =
        registerSpell(new BlockBombAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BLOCK_BREAKER =
        registerSpell(new BlockBreakerAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BLOCK_PLACER =
        registerSpell(new BlockPlacerAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> FARMERS_TOUCH =
        registerSpell(new FarmersTouchAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> HAMMER =
        registerSpell(new HammerAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> LIGHT_PLACER =
        registerSpell(new LightPlacerAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> FETCH =
        registerSpell(new FetchAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> VEIN_MINER =
        registerSpell(new VeinMinerAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> WALL_PLACER =
        registerSpell(new WallPlacerAbility());

    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ENCHANTED_FUSION =
        registerSpell(new EnchantedFusionAbility());

    private static DeferredHolder<AbilityRegistrar, AbilityRegistrar> registerSpell(AbilityRegistrar spell) {
        return ABILITIES.register(spell.setAbilityId(), () -> spell);
    }

    public static void register(IEventBus eventBus) {
        ABILITIES.register(eventBus);
    }
}
