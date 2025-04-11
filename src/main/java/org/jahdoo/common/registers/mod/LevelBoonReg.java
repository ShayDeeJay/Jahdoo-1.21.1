package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.ascension.boon.level_boons.negative.*;
import org.jahdoo.ascension.boon.level_boons.positive.*;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.jahdoo.ascension.level_manager.StructureManager.SEED;

public class LevelBoonReg {

    public static final ResourceKey<Registry<AbstractLevelBoon>> LEVEL_BOON_REGISTRY_KEY = ResourceKey.createRegistryKey(Helpers.res("level_boon"));
    private static final DeferredRegister<AbstractLevelBoon> LEVEL_BOON = DeferredRegister.create(LEVEL_BOON_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractLevelBoon> REGISTRY =  new RegistryBuilder<>(LEVEL_BOON_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("Element.RegisterRegistry");
        event.register(REGISTRY);
    }

    private static DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> registerElement(Supplier<AbstractLevelBoon> levelBoon) {
        return LEVEL_BOON.register(levelBoon.get().id(), levelBoon);
    }

    public static AbstractLevelBoon randomPositive() {
        var element = REGISTRY
            .stream()
            .filter(AbstractLevelBoon::isPositive)
            .toList();
        return Helpers.listRandom(element, SEED);
    }

    public static AbstractLevelBoon randomNegative() {
        var element = REGISTRY
            .stream()
            .filter(a -> !a.isPositive())
            .toList();
        return Helpers.listRandom(element, SEED);
    }

    public static AbstractLevelBoon withRarityNegative(JahdooRarity rarity) {
        var element = REGISTRY
            .stream()
            .filter(a -> !a.isPositive())
            .filter(a -> a.rarity() == rarity)
            .toList();
        return Helpers.listRandom(element, SEED);
    }

    public static AbstractLevelBoon withRarityPositive(JahdooRarity rarity) {
        var element = REGISTRY
            .stream()
            .filter(AbstractLevelBoon::isPositive)
            .filter(a -> a.rarity() == rarity)
            .toList();
        return Helpers.listRandom(element, SEED);
    }

    public static Optional<AbstractLevelBoon> fromId(String typeId) {
        var element = REGISTRY
            .stream()
            .filter(a -> Objects.equals(a.id(), typeId))
            .toList();
        return element.isEmpty() ? Optional.empty() : Optional.of(element.getFirst());
    }

    public static AbstractLevelBoon random() {
        var list = REGISTRY.stream().toList();
        return Helpers.listRandom(list);
    }

    //Negative
    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> HEALTH =
        registerElement(MobHealth::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> SPEED =
        registerElement(MobSpeed::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> DAMAGE =
        registerElement(MobDamage::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> ARMOR =
        registerElement(MobArmor::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> HORDE =
        registerElement(Horde::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> SKELETON =
        registerElement(Skeleton::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> VOID_SPIDER =
        registerElement(VoidSpider::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> ETERNAL_WIZARD =
        registerElement(EternalWizard::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> INFERNO_CREEPER =
        registerElement(InfernoCreeper::new);

    //Positive
    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> TIME =
        registerElement(Time::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> BRONZE_COIN =
        registerElement(BronzeCoins::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> FILLER_BRONZE_COIN =
        registerElement(FillerBronzeCoins::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> SILVER_COIN =
        registerElement(SilverCoins::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> GOLD_COIN =
        registerElement(GoldCoins::new);

    public static void register(IEventBus eventBus) {
        LEVEL_BOON.register(eventBus);
    }

}
