package org.jahdoo.registers;

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
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.abilities.ability_data.*;
import org.jahdoo.ability.abilities.ability_data.Utility.*;
import org.jahdoo.ability.rarity.JahdooRarity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.registers.ElementRegistry.UTILITY;

public class AbilityRegister {

    public static final ResourceKey<Registry<AbilityRegistrar>> ABILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID,"ability"));
    private static final DeferredRegister<AbilityRegistrar> ABILITIES = DeferredRegister.create(ABILITY_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbilityRegistrar> REGISTRY =  new RegistryBuilder<>(ABILITY_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("SpellRegistry.registerRegistry");
        event.register(REGISTRY);
    }

    public static List<AbilityRegistrar> getSpellsByTypeId(String typeId) {
        return AbilityRegister.REGISTRY
            .stream()
            .filter(ability -> Objects.equals(ability.setAbilityId(), typeId))
            .toList();
    }

    public static List<AbilityRegistrar> getAllRangeRarity(JahdooRarity rarity) {
        return AbilityRegister.REGISTRY
            .stream()
            .filter(ability -> ability.rarity().getId() <= rarity.getId())
            .toList();
    }

    public static List<AbilityRegistrar> getMatchingRarity(JahdooRarity rarity) {
        return AbilityRegister.REGISTRY
            .stream()
            .filter(ability -> ability.rarity().getId() == rarity.getId())
            .toList();
    }

    public static List<AbilityRegistrar> getMatchingRarityNoUtil(JahdooRarity rarity) {
        return AbilityRegister.REGISTRY
            .stream()
            .filter(ability -> ability.rarity().getId() == rarity.getId())
            .filter(ability -> ability.getElemenType() != UTILITY.get())
            .toList();
    }

    public static List<AbilityRegistrar> getMatchingRarityUtilOnly(JahdooRarity rarity) {
        return AbilityRegister.REGISTRY
                .stream()
                .filter(ability -> ability.rarity().getId() == rarity.getId())
                .filter(ability -> ability.getElemenType() == UTILITY.get())
                .toList();
    }

    public static Optional<AbilityRegistrar> getFirstSpellByTypeId(String typeId) {
        return AbilityRegister.REGISTRY
            .stream()
            .filter(ability -> Objects.equals(ability.setAbilityId(), typeId))
            .findFirst();  // Lazy and returns an Optional
    }

    public static Optional<AbilityRegistrar> getFirstSpellFromAugment(ItemStack itemStack) {
        if(itemStack.isEmpty()) return Optional.empty();

        var wandAbilityHolder = itemStack.get(WAND_ABILITY_HOLDER);
        if(wandAbilityHolder != null) {
            var typeId = wandAbilityHolder.abilityProperties().keySet().stream().findFirst();
            return typeId.flatMap(
            s -> AbilityRegister.REGISTRY
                    .stream()
                    .filter(ability -> Objects.equals(ability.setAbilityId(), s))
                    .findFirst()
            );
        }

        return Optional.empty();
    }

    private static DeferredHolder<AbilityRegistrar, AbilityRegistrar> registerSpell(AbilityRegistrar spell) {
        return ABILITIES.register(spell.setAbilityId(), () -> spell);
    }

    //Multi-Type
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ELEMENTAL_SHOOTER = registerSpell(new ElementalShooterAbility());

    //Volt
//    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BOLTZ = registerSpell(new BoltzAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> STATIC = registerSpell(new StaticAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> THUNDER_BURST = registerSpell(new ThunderBurstAbility());

    //Inferno
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ARMAGEDDON = registerSpell(new ArmageddonAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> FIREBALL = registerSpell(new FireballAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> HELLFIRE = registerSpell(new HellfireAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BURNING_SKULLS = registerSpell(new BurningSkullsAbility());

    //Mystic
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ARCANE_SHIFT = registerSpell(new ArcaneShiftAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> MYSTICAL_SEMTEX = registerSpell(new MysticalSemtexAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> QUANTUM_DESTROYER = registerSpell(new QuantumDestroyerAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> NOVA_SMASH = registerSpell(new NovaSmashAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> DIMENSIONAL_RECALL = registerSpell(new DimensionalRecallAbility());

    //Frost
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> PERMAFROST = registerSpell(new PermafrostAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ICE_BOMB = registerSpell(new IceBombAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> FROST_BOLTS = registerSpell(new FrostboltsAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> STORM_RUSH = registerSpell(new StormRushAbility());

    //Vitality
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> SUMMON_ETERNAL_WIZARD = registerSpell(new SummonEternalWizardAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ESCAPE_DECOY = registerSpell(new EscapeDecoyAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> VITAL_REJUVENATION = registerSpell(new VitalRejuvenationAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> SUMMON_ANCIENT_GOLEM = registerSpell(new SummonAncientGolemAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> LIFE_SIPHON = registerSpell(new LifeSiphonAbility());

    //Utility
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BLOCK_BOMB = registerSpell(new BlockBombAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BLOCK_BREAKER = registerSpell(new BlockBreakerAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> BLOCK_PLACER = registerSpell(new BlockPlacerAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> FARMERS_TOUCH = registerSpell(new FarmersTouchAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> HAMMER = registerSpell(new HammerAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> LIGHT_PLACER = registerSpell(new LightPlacerAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> FETCH = registerSpell(new FetchAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> VEIN_MINER = registerSpell(new VeinMinerAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> PLAYER_SCALE = registerSpell(new PlayerScaleAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> WALL_PLACER = registerSpell(new WallPlacerAbility());
    public static final DeferredHolder<AbilityRegistrar, AbilityRegistrar> ENCHANTED_FUSION = registerSpell(new EnchantedFusionAbility());

    public static void register(IEventBus eventBus) {
        ABILITIES.register(eventBus);
    }
}
