package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.ability.skills.*;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.List;
import java.util.function.Supplier;

public class SkillReg {

    public static final ResourceKey<Registry<AbstractSkill>> SKILL_REGISTRY_KEY = ResourceKey.createRegistryKey(Helpers.res("skill"));
    private static final DeferredRegister<AbstractSkill> ELEMENT = DeferredRegister.create(SKILL_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractSkill> REGISTRY =  new RegistryBuilder<>(SKILL_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("SKill.RegisterRegistry");
        event.register(REGISTRY);
    }

    private static DeferredHolder<AbstractSkill, AbstractSkill> registerElement(Supplier<AbstractSkill> skill) {
        return ELEMENT.register(skill.get().id(), skill);
    }

    public static List<AbstractSkill> getAllSkills() {
        return REGISTRY.stream().toList();
    }

    public static final DeferredHolder<AbstractSkill, AbstractSkill> CLIMBER =
        registerElement(ClimberSkill::new);

    public static final DeferredHolder<AbstractSkill, AbstractSkill> REBOUND =
        registerElement(ReboundSkill::new);

    public static final DeferredHolder<AbstractSkill, AbstractSkill> TRIPLE_JUMP =
        registerElement(TripleJumpSkill::new);

    public static final DeferredHolder<AbstractSkill, AbstractSkill> DRIP_WALK =
        registerElement(DripWalkSkill::new);

    public static final DeferredHolder<AbstractSkill, AbstractSkill> MAGE_FLIGHT =
        registerElement(MageFlightSkill::new);

    public static void register(IEventBus eventBus) {
        ELEMENT.register(eventBus);
    }

}
