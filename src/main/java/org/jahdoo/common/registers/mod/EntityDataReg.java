package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.ability.AbstractEntityProperty;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.ability.abilities_combat.ancient_golem.SummonAncientGolem;
import org.jahdoo.trial_nexus.ability.abilities_combat.armageddon.Armageddon;
import org.jahdoo.trial_nexus.ability.abilities_utility.block_bomb.BlockBomb;
import org.jahdoo.trial_nexus.ability.abilities_utility.block_breaker.BlockBreaker;
import org.jahdoo.trial_nexus.ability.abilities_utility.block_placer.BlockPlacer;
import org.jahdoo.trial_nexus.ability.abilities_combat.boltz.Boltz;
import org.jahdoo.trial_nexus.ability.abilities_combat.elemental_missile.ElementalMissile;
import org.jahdoo.trial_nexus.ability.abilities_utility.enchanted_fusion.EnchantedFusion;
import org.jahdoo.trial_nexus.ability.abilities_combat.eternal_wizard.SummonEternalWizard;
import org.jahdoo.trial_nexus.ability.abilities_utility.farmers_touch.FarmersTouch;
import org.jahdoo.trial_nexus.ability.abilities_utility.fetch.Fetch;
import org.jahdoo.trial_nexus.ability.abilities_combat.fireball.FireBall;
import org.jahdoo.trial_nexus.ability.abilities_combat.frostbolts.FrostBolts;
import org.jahdoo.trial_nexus.ability.abilities_utility.hammer.Hammer;
import org.jahdoo.trial_nexus.ability.abilities_combat.hellfire.HellFire;
import org.jahdoo.trial_nexus.ability.abilities_combat.life_siphon.LifeSiphon;
import org.jahdoo.trial_nexus.ability.abilities_combat.life_siphon.LifeSiphonNova;
import org.jahdoo.trial_nexus.ability.abilities_utility.light_placer.LightPlacer;
import org.jahdoo.trial_nexus.ability.abilities_combat.mob_abilities.Barrage;
import org.jahdoo.trial_nexus.ability.abilities_combat.mystical_semtex.MysticalSemtex;
import org.jahdoo.trial_nexus.ability.abilities_combat.permafrost.Permafrost;
import org.jahdoo.trial_nexus.ability.abilities_combat.quantum_destroyer.QuantumDestroyer;
import org.jahdoo.trial_nexus.ability.abilities_utility.vein_miner.VeinMiner;
import org.jahdoo.trial_nexus.ability.abilities_utility.wall_placer.WallPlacer;
import org.jahdoo.trial_nexus.ability.abilities_combat.armageddon.ArmageddonModule;
import org.jahdoo.trial_nexus.ability.abilities_combat.EtherealArrow;
import org.jahdoo.trial_nexus.ability.abilities_combat.ice_bomb.IceBomb;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.Objects;
import java.util.function.Supplier;

public class EntityDataReg {

    public static final ResourceKey<Registry<AbstractEntityProperty>> PROJECTILE_PROPERTY_REGISTRY_KEY =
        ResourceKey.createRegistryKey(Helpers.res("projectile_properties"));

    private static final DeferredRegister<AbstractEntityProperty> PROJECTILE_PROPERTY =
        DeferredRegister.create(PROJECTILE_PROPERTY_REGISTRY_KEY, JahdooMod.MOD_ID);

//----------------------------------------------------------------------------------------------------------------------

    //USED FOR ELEMENT PROJECTILE
    public static final Supplier <AbstractEntityProperty> FIRE_BALL =
        registerProperty(FireBall::new);

    public static final Supplier <AbstractEntityProperty> FROST_BOLT =
        registerProperty(FrostBolts::new);

    public static final Supplier <AbstractEntityProperty> ICE_BOMB =
        registerProperty(IceBomb::new);

    public static final Supplier <AbstractEntityProperty> MYSTICAL_SEMTEX =
        registerProperty(MysticalSemtex::new);

    public static final Supplier <AbstractEntityProperty> QUANTUM_DESTROYER =
        registerProperty(QuantumDestroyer::new);

    public static final Supplier <AbstractEntityProperty> OVERCHARGED =
        registerProperty(LifeSiphon::new);

    public static final Supplier <AbstractEntityProperty> BOLTZ =
        registerProperty(Boltz::new);

//----------------------------------------------------------------------------------------------------------------------

    //USED FOR GENERIC PROJECTILE
    public static final Supplier <AbstractEntityProperty> ELEMENTAL_SHOOTER =
        registerProperty(ElementalMissile::new);

    public static final Supplier <AbstractEntityProperty> BLOCK_BREAKER =
        registerProperty(BlockBreaker::new);

    public static final Supplier <AbstractEntityProperty> BLOCK_EXPLODER =
        registerProperty(BlockBomb::new);

    public static final Supplier <AbstractEntityProperty> LIGHT_PLACER =
        registerProperty(LightPlacer::new);

    public static final Supplier <AbstractEntityProperty> VEIN_MINER =
        registerProperty(VeinMiner::new);

    public static final Supplier <AbstractEntityProperty> BLOCK_PLACER =
        registerProperty(BlockPlacer::new);

    public static final Supplier <AbstractEntityProperty> HAMMER =
        registerProperty(Hammer::new);

    public static final Supplier <AbstractEntityProperty> BONE_MEAL =
        registerProperty(FarmersTouch::new);

    public static final Supplier <AbstractEntityProperty> ETHEREAL_ARROW =
        registerProperty(EtherealArrow::new);

    public static final Supplier <AbstractEntityProperty> ENCHANTED_FUSION =
        registerProperty(EnchantedFusion::new);

    public static final Supplier <AbstractEntityProperty> FETCH =
        registerProperty(Fetch::new);

    public static final Supplier <AbstractEntityProperty> WALL_PLACER =
        registerProperty(WallPlacer::new);

//----------------------------------------------------------------------------------------------------------------------

    //USED FOR AOE ENTITY
    public static final Supplier <AbstractEntityProperty> ARCTIC_STORM =
        registerProperty(Permafrost::new);

    public static final Supplier <AbstractEntityProperty> ARMAGEDDON_MODULE =
        registerProperty(ArmageddonModule::new);

    public static final Supplier <AbstractEntityProperty> SOUL_SIPHON_NOVA =
        registerProperty(LifeSiphonNova::new);

    public static final Supplier <AbstractEntityProperty> ARMAGEDDON =
        registerProperty(Armageddon::new);

    public static final Supplier <AbstractEntityProperty> HELLFIRE =
        registerProperty(HellFire::new);

    public static final Supplier <AbstractEntityProperty> SUMMON_ETERNAL_WIZARD =
        registerProperty(SummonEternalWizard::new);

    public static final Supplier <AbstractEntityProperty> SUMMON_ANCIENT_GOLEM =
        registerProperty(SummonAncientGolem::new);

//----------------------------------------------------------------------------------------------------------------------
    //MOB ABILITIES
    public static final Supplier <AbstractEntityProperty> BARRAGE =
        registerProperty(Barrage::new);

//----------------------------------------------------------------------------------------------------------------------
    public static final Registry<AbstractEntityProperty> REGISTRY =
        new RegistryBuilder<>(PROJECTILE_PROPERTY_REGISTRY_KEY).create();

    private static Supplier<AbstractEntityProperty> registerProperty(Supplier<AbstractEntityProperty> entityProperty) {
        return PROJECTILE_PROPERTY.register(entityProperty.get().setAbilityId(), entityProperty);
    }

    public static DefaultEntityBehaviour getProperty(String location){
        return Objects.requireNonNull(REGISTRY.get(Helpers.res(location))).getEntityProperty();
    }

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("EntityProperty.registerRegistry");
        event.register(REGISTRY);
    }

    public static void register(IEventBus eventBus) {
        PROJECTILE_PROPERTY.register(eventBus);
    }
}
