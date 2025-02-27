package org.jahdoo.common.registers;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.AbstractEntityProperty;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.abilities.ancient_golem.SummonAncientGolem;
import org.jahdoo.ascension.ability.abilities.armageddon.Armageddon;
import org.jahdoo.ascension.ability.abilities.block_bomb.BlockBomb;
import org.jahdoo.ascension.ability.abilities.block_breaker.BlockBreaker;
import org.jahdoo.ascension.ability.abilities.block_placer.BlockPlacer;
import org.jahdoo.ascension.ability.abilities.boltz.Boltz;
import org.jahdoo.ascension.ability.abilities.elemental_shooter.ElementalShooter;
import org.jahdoo.ascension.ability.abilities.enchanted_fusion.EnchantedFusion;
import org.jahdoo.ascension.ability.abilities.eternal_wizard.SummonEternalWizard;
import org.jahdoo.ascension.ability.abilities.farmers_touch.FarmersTouch;
import org.jahdoo.ascension.ability.abilities.fetch.Fetch;
import org.jahdoo.ascension.ability.abilities.fireball.FireBall;
import org.jahdoo.ascension.ability.abilities.frostbolts.FrostBolts;
import org.jahdoo.ascension.ability.abilities.hammer.Hammer;
import org.jahdoo.ascension.ability.abilities.hellfire.HellFire;
import org.jahdoo.ascension.ability.abilities.life_siphon.LifeSiphon;
import org.jahdoo.ascension.ability.abilities.life_siphon.LifeSiphonNova;
import org.jahdoo.ascension.ability.abilities.light_placer.LightPlacer;
import org.jahdoo.ascension.ability.abilities.mob_abilities.Barrage;
import org.jahdoo.ascension.ability.abilities.mystical_semtex.MysticalSemtex;
import org.jahdoo.ascension.ability.abilities.permafrost.Permafrost;
import org.jahdoo.ascension.ability.abilities.quantum_destroyer.QuantumDestroyer;
import org.jahdoo.ascension.ability.abilities.vein_miner.VeinMiner;
import org.jahdoo.ascension.ability.abilities.wall_placer.WallPlacer;
import org.jahdoo.ascension.ability.abilities.armageddon.ArmageddonModule;
import org.jahdoo.ascension.ability.abilities.EtherealArrow;
import org.jahdoo.ascension.ability.abilities.ice_bomb.IceBomb;
import org.jahdoo.ascension.utils.Helpers;

import java.util.function.Supplier;

public class EntityPropertyRegister {

    public static final ResourceKey<Registry<AbstractEntityProperty>> PROJECTILE_PROPERTY_REGISTRY_KEY =
        ResourceKey.createRegistryKey(Helpers.res("projectile_properties"));

    private static final DeferredRegister<AbstractEntityProperty> PROJECTILE_PROPERTY = DeferredRegister.create(PROJECTILE_PROPERTY_REGISTRY_KEY, JahdooMod.MOD_ID);

    public static final Registry<AbstractEntityProperty> REGISTRY = new RegistryBuilder<>(PROJECTILE_PROPERTY_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("EntityProperty.registerRegistry");
        event.register(REGISTRY);
    }

    private static Supplier<AbstractEntityProperty> registerProjectileProperty(AbstractEntityProperty entityProperty) {
        return PROJECTILE_PROPERTY.register(entityProperty.setAbilityId(), () -> entityProperty);
    }

    public static DefaultEntityBehaviour getProperty(String location){
        return EntityPropertyRegister.REGISTRY.get(Helpers.res(location)).getEntityProperty();
    }

    //USED FOR ELEMENT PROJECTILE
    public static final Supplier <AbstractEntityProperty> FIRE_BALL =
        registerProjectileProperty(new FireBall());
    public static final Supplier <AbstractEntityProperty> FROST_BOLT =
        registerProjectileProperty(new FrostBolts());
    public static final Supplier <AbstractEntityProperty> ICE_NEEDLER =
        registerProjectileProperty(new IceBomb());
    public static final Supplier <AbstractEntityProperty> MYSTICAL_SEMTEX =
        registerProjectileProperty(new MysticalSemtex());
    public static final Supplier <AbstractEntityProperty> QUANTUM_DESTROYER =
        registerProjectileProperty(new QuantumDestroyer());
    public static final Supplier <AbstractEntityProperty> OVERCHARGED =
        registerProjectileProperty(new LifeSiphon());
    public static final Supplier <AbstractEntityProperty> BOLTZ =
        registerProjectileProperty(new Boltz());

    //USED FOR GENERIC PROJECTILE
    public static final Supplier <AbstractEntityProperty> ELEMENTAL_SHOOTER =
        registerProjectileProperty(new ElementalShooter());
    public static final Supplier <AbstractEntityProperty> BLOCK_BREAKER =
        registerProjectileProperty(new BlockBreaker());
    public static final Supplier <AbstractEntityProperty> BLOCK_EXPLODER =
        registerProjectileProperty(new BlockBomb());
    public static final Supplier <AbstractEntityProperty> LIGHT_PLACER =
        registerProjectileProperty(new LightPlacer());
    public static final Supplier <AbstractEntityProperty> VEIN_MINER =
        registerProjectileProperty(new VeinMiner());
    public static final Supplier <AbstractEntityProperty> BLOCK_PLACER =
        registerProjectileProperty(new BlockPlacer());
    public static final Supplier <AbstractEntityProperty> HAMMER =
        registerProjectileProperty(new Hammer());
    public static final Supplier <AbstractEntityProperty> BONE_MEAL =
        registerProjectileProperty(new FarmersTouch());
    public static final Supplier <AbstractEntityProperty> ETHEREAL_ARROW =
        registerProjectileProperty(new EtherealArrow());
    public static final Supplier <AbstractEntityProperty> ENCHANTED_FUSION =
        registerProjectileProperty(new EnchantedFusion());
    public static final Supplier <AbstractEntityProperty> FETCH =
        registerProjectileProperty(new Fetch());
    public static final Supplier <AbstractEntityProperty> WALL_PLACER =
        registerProjectileProperty(new WallPlacer());

    //USED FOR AOE ENTITY
    public static final Supplier <AbstractEntityProperty> ARCTIC_STORM =
        registerProjectileProperty(new Permafrost());
    public static final Supplier <AbstractEntityProperty> ARMAGEDDON_MODULE =
        registerProjectileProperty(new ArmageddonModule());
    public static final Supplier <AbstractEntityProperty> SOUL_SIPHON_NOVA =
        registerProjectileProperty(new LifeSiphonNova());
    public static final Supplier <AbstractEntityProperty> ARMAGEDDON =
        registerProjectileProperty(new Armageddon());
    public static final Supplier <AbstractEntityProperty> HELLFIRE =
        registerProjectileProperty(new HellFire());
    public static final Supplier <AbstractEntityProperty> SUMMON_ETERNAL_WIZARD =
        registerProjectileProperty(new SummonEternalWizard());
    public static final Supplier <AbstractEntityProperty> SUMMON_ANCIENT_GOLEM =
        registerProjectileProperty(new SummonAncientGolem());

    //MOB ABILITIES
    public static final Supplier <AbstractEntityProperty> BARRAGE =
        registerProjectileProperty(new Barrage());

    public static void register(IEventBus eventBus) {
        PROJECTILE_PROPERTY.register(eventBus);
    }
}
