package org.jahdoo.common.registers;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.element.*;
import org.jahdoo.ascension.utils.Helpers;

import java.util.*;
import java.util.function.Supplier;

public class ElementReg {

    public static final ResourceKey<Registry<AbstractElement>> ELEMENT_REGISTRY_KEY = ResourceKey.createRegistryKey(Helpers.res("element"));
    private static final DeferredRegister<AbstractElement> ELEMENT = DeferredRegister.create(ELEMENT_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractElement> REGISTRY =  new RegistryBuilder<>(ELEMENT_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("Element.RegisterRegistry");
        event.register(REGISTRY);
    }

    public static Optional<AbstractElement> fromWand(Item wand) {
        var element = REGISTRY
            .stream()
            .filter(a -> a.getWand() == wand)
            .toList();
        return element.isEmpty() ? Optional.empty() : Optional.of(element.getFirst());
    }

    public static Optional<AbstractElement> fromId(int typeId) {
        var element = REGISTRY
            .stream()
            .filter(a -> a.id() == typeId)
            .toList();
        return element.isEmpty() ? Optional.empty() : Optional.of(element.getFirst());
    }

    public static List<AbstractElement> getWithout(AbstractElement...elements) {
        return REGISTRY.stream()
            .filter(e -> !Arrays.stream(elements).toList().contains(e))
            .filter(e -> e != utility())
            .toList();
    }

    public static AbstractElement random() {
        var list = REGISTRY.stream()
            .filter(e -> e != utility())
            .toList();
        return Helpers.listRandom(list);
    }

    private static DeferredHolder<AbstractElement, AbstractElement> registerElement(Supplier<AbstractElement> element) {
        return ELEMENT.register(element.get().setAbilityId(), element);
    }

    public static AbstractElement frost(){
        return FROST.get();
    }

    public static AbstractElement mystic(){
        return MYSTIC.get();
    }

    public static AbstractElement inferno(){
        return INFERNO.get();
    }

    public static AbstractElement utility(){
        return UTILITY.get();
    }

    public static AbstractElement vitality(){
        return VITALITY.get();
    }

    public static final DeferredHolder<AbstractElement, AbstractElement> FROST =
        registerElement(Frost::new);

    public static final DeferredHolder<AbstractElement, AbstractElement> MYSTIC =
        registerElement(Mystic::new);

    public static final DeferredHolder<AbstractElement, AbstractElement> INFERNO =
        registerElement(Inferno::new);

    public static final DeferredHolder<AbstractElement, AbstractElement> UTILITY =
        registerElement(Utility::new);

    public static final DeferredHolder<AbstractElement, AbstractElement> VITALITY =
        registerElement(Vitality::new);

    public static void register(IEventBus eventBus) {
        ELEMENT.register(eventBus);
    }

}
