package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.trackable.level_modifiers.AbstractLevelBoon;
import org.jahdoo.trial_nexus.trackable.level_modifiers.negative.*;
import org.jahdoo.trial_nexus.trackable.level_modifiers.positive.*;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.Helpers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class LevelBoonReg {

    public static final ResourceKey<Registry<AbstractLevelBoon>> LEVEL_BOON_REGISTRY_KEY = ResourceKey.createRegistryKey(JahdooHelpers.res("level_boon"));
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
        return Helpers.listRandom(getAllPositive());
    }

    public static AbstractLevelBoon randomNegative() {
        return Helpers.listRandom(getAllNegative());
    }

    public static List<AbstractLevelBoon> getAllPositive() {
        return REGISTRY
            .stream()
            .filter(AbstractLevelBoon::isPositive)
            .toList();
    }

    public static List<AbstractLevelBoon> getAllNegative() {
        return REGISTRY
            .stream()
            .filter(a -> !a.isPositive())
            .toList();
    }

    public static AbstractLevelBoon getStampBoons(JahdooRarity rarity) {
        var element = REGISTRY
            .stream()
            .filter(a -> a.getStampIndex() != -1)
            .filter(a -> a.rarity() == rarity)
            .toList();
        return Helpers.listRandom(element);
    }

    public static AbstractLevelBoon withRarityNegative(JahdooRarity rarity) {
        var element = REGISTRY
            .stream()
            .filter(a -> !a.isPositive())
            .filter(a -> a.rarity() == rarity)
            .toList();
        return Helpers.listRandom(element);
    }

    public static AbstractLevelBoon withRarityPositive(JahdooRarity rarity) {
        var element = REGISTRY
            .stream()
            .filter(AbstractLevelBoon::isPositive)
            .filter(a -> a.rarity() == rarity)
            .toList();
        return Helpers.listRandom(element);
    }

    public static Optional<AbstractLevelBoon> fromId(String typeId) {
        var element = REGISTRY
            .stream()
            .filter(a -> Objects.equals(a.id(), typeId))
            .toList();
        return element.isEmpty() ? Optional.empty() : Optional.of(element.getFirst());
    }

    public static Optional<AbstractLevelBoon> positiveFromId(String typeId) {
        var element = REGISTRY
            .stream()
            .filter(AbstractLevelBoon::isPositive)
            .filter(a -> Objects.equals(a.id(), typeId))
            .toList();
        return element.isEmpty() ? Optional.empty() : Optional.of(element.getFirst());
    }


    public static Optional<AbstractLevelBoon> negativeFromId(String typeId) {
        var element = REGISTRY
            .stream()
            .filter(a -> !a.isPositive())
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

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> ARMOR =
        registerElement(MobArmor::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> DAMAGE =
        registerElement(MobDamage::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> SPEED =
        registerElement(MobSpeed::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> KNOCKBACK =
        registerElement(MobKnockback::new);

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

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> TRAIL_EXPERIENCE =
        registerElement(Experience::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> QUEST_MULTIPLIER =
        registerElement(QuestLootMultiplier::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> BRONZE_COIN =
        registerElement(BronzeCoins::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> SILVER_COIN =
        registerElement(SilverCoins::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> GOLD_COIN =
        registerElement(GoldCoins::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> SAFE_LOOT_MULTIPLIER =
        registerElement(SafeLootMultiplier::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> LOOT_POT_MULTIPLIER =
        registerElement(LootPotMultiplier::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> COMMON_LOOT_MULTIPLIER =
        registerElement(CommonLootMultiplier::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> RARE_LOOT_MULTIPLIER =
        registerElement(RareLootMultiplier::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> LEGENDARY_LOOT_MULTIPLIER =
        registerElement(LegendaryLootMultiplier::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> MYTHIC_LOOT_MULTIPLIER =
        registerElement(MythicLootMultiplier::new);

    public static final DeferredHolder<AbstractLevelBoon, AbstractLevelBoon> ORE_MULTIPLIER =
        registerElement(OreMultiplier::new);

    public static void register(IEventBus eventBus) {
        LEVEL_BOON.register(eventBus);
    }

}
