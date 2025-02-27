package org.jahdoo.common.registers;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.utils.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.minecraft.world.entity.EntityType.*;

public class  AttributesRegister {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, JahdooMod.MOD_ID);
    public static final String MOD = "jahdoo";
    public static final String FIXED_VALUE = ".fixed";

    //Base attribute
    public static final DeferredHolder<Attribute, Attribute> MANA_POOL =
        register(MOD + FIXED_VALUE + ".mana.mana_pool", 100);
    public static final DeferredHolder<Attribute, Attribute> MANA_REGEN =
        register(MOD + ".mana.mana_regen", 0);

    //Infinity Attributes
    public static final DeferredHolder<Attribute, Attribute> SKIP_MANA =
        register(MOD + ".mana.no_mana_cost", 0);
    public static final DeferredHolder<Attribute, Attribute> SKIP_COOLDOWN =
        register(MOD + ".no_cooldown_cost", 0);
    public static final DeferredHolder<Attribute, Attribute> CAST_HEAL =
        register(MOD + ".cast.healer", 0);
    public static final DeferredHolder<Attribute, Attribute> ABSORPTION_HEARTS =
        register(MOD + ".cast.absorption", 0);

    //Skill Attributes
    public static final DeferredHolder<Attribute, Attribute> DESTINY_BOND =
        register(MOD + ".skills.destiny_bond", 0);
    public static final DeferredHolder<Attribute, Attribute> MAGE_FLIGHT =
        register(MOD + ".skills.mage_flight", 0);
    public static final DeferredHolder<Attribute, Attribute> TRIPLE_JUMP =
        register(MOD + ".skills.triple_jump", 0);

    //Cosmic attributes
    public static final DeferredHolder<Attribute, Attribute> COOLDOWN_REDUCTION =
        register(MOD + ".cooldown.cooldown_reduction", 0);
    public static final DeferredHolder<Attribute, Attribute> MAGIC_DAMAGE_MULTIPLIER =
        register(MOD + ".damage.damage_multiplier", 0);
    public static final DeferredHolder<Attribute, Attribute> MANA_COST_REDUCTION =
        register(MOD + ".mana.cost_reduction", 0);

    //Elemental attributes
    public static final DeferredHolder<Attribute, Attribute> INFERNO_COOLDOWN_REDUCTION =
        register(MOD + ".inferno_cooldown.cooldown_reduction", 0);
    public static final DeferredHolder<Attribute, Attribute> INFERNO_MAGIC_DAMAGE_MULTIPLIER =
        register(MOD + ".inferno_damage.damage_multiplier", 0);
    public static final DeferredHolder<Attribute, Attribute> INFERNO_MANA_COST_REDUCTION =
        register(MOD + ".inferno_mana.cost_reduction", 0);

    public static final DeferredHolder<Attribute, Attribute> MYSTIC_COOLDOWN_REDUCTION =
        register(MOD + ".mystic_cooldown.cooldown_reduction", 0);
    public static final DeferredHolder<Attribute, Attribute> MYSTIC_MAGIC_DAMAGE_MULTIPLIER =
        register(MOD + ".mystic_damage.damage_multiplier", 0);
    public static final DeferredHolder<Attribute, Attribute> MYSTIC_MANA_COST_REDUCTION =
        register(MOD + ".mystic_mana.cost_reduction", 0);

    public static final DeferredHolder<Attribute, Attribute> FROST_COOLDOWN_REDUCTION =
        register(MOD + ".frost_cooldown.cooldown_reduction", 0);
    public static final DeferredHolder<Attribute, Attribute> FROST_MAGIC_DAMAGE_MULTIPLIER =
        register( MOD + ".frost_damage.damage_multiplier", 0);
    public static final DeferredHolder<Attribute, Attribute> FROST_MANA_COST_REDUCTION =
        register(MOD + ".frost_mana.cost_reduction", 0);

    public static final DeferredHolder<Attribute, Attribute> VITALITY_COOLDOWN_REDUCTION =
        register(MOD + ".vitality_cooldown.cooldown_reduction", 0);
    public static final DeferredHolder<Attribute, Attribute> VITALITY_MAGIC_DAMAGE_MULTIPLIER =
        register(MOD + ".vitality_damage.damage_multiplier", 0);
    public static final DeferredHolder<Attribute, Attribute> VITALITY_MANA_COST_REDUCTION =
        register(MOD + ".vitality_mana.cost_reduction", 0);

    public static DeferredHolder<Attribute, Attribute> register (String name, double defaultVal){
        var rangedAttribute = new RangedAttribute("attribute.name."+name, defaultVal, 0.0, 2048.0);
        return ATTRIBUTES.register(name, () -> rangedAttribute.setSyncable(true));
    }

    public static void replaceOrAddAttribute(
        ItemStack itemStack,
        String name,
        Holder<Attribute> attribute,
        double value,
        EquipmentSlot equipmentSlot,
        boolean isRandomId
    ){
        var attributeList = new ArrayList<>(itemStack.getAttributeModifiers().modifiers().stream().toList());
        var itemAttributes = new ItemAttributeModifiers(attributeList, false);
        var existingModifier = attributeList
            .stream()
            .filter(att -> att.modifier().id().getPath().equals(name))
            .findFirst();
        existingModifier.ifPresent(attributeList::remove);
        var attributes = setAttribute(name, attribute, value, equipmentSlot, isRandomId);

        attributeList.add(attributes);
        itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, itemAttributes);
    }

    public static ItemAttributeModifiers replaceOrAddAttribute(
        List<ItemAttributeModifiers.Entry> attributeList,
        String name,
        Holder<Attribute> attribute,
        double value,
        EquipmentSlot equipmentSlot
    ){
        var itemAttributes = new ItemAttributeModifiers(attributeList, false);
        var existingModifier = attributeList
            .stream()
            .filter(att -> att.modifier().id().getPath().equals(name))
            .findFirst();
        existingModifier.ifPresent(attributeList::remove);

        attributeList.add(setAttribute(name, attribute, value, equipmentSlot, true));
        return itemAttributes;
    }

    public static ItemAttributeModifiers.Entry setAttribute(String name, Holder<Attribute> attribute, double value, EquipmentSlot equipmentSlot, boolean isRandomId){
        var resourcelocation = isRandomId ? Helpers.res(String.valueOf(UUID.randomUUID())) : ResourceLocation.parse(name);
        var attributes = new AttributeModifier(resourcelocation, value,  AttributeModifier.Operation.ADD_VALUE);
        
        return new ItemAttributeModifiers.Entry(attribute, attributes,  EquipmentSlotGroup.bySlot(equipmentSlot));
    }

    public static void attachAttribute(EntityAttributeModificationEvent event){
        event.add(PLAYER, MANA_POOL);
        event.add(PLAYER, MANA_REGEN);
        event.add(PLAYER, MAGE_FLIGHT);
        event.add(PLAYER, TRIPLE_JUMP);

        event.add(PLAYER, SKIP_MANA);
        event.add(PLAYER, SKIP_COOLDOWN);

        event.add(PLAYER, COOLDOWN_REDUCTION);
        event.add(PLAYER, MAGIC_DAMAGE_MULTIPLIER);
        event.add(PLAYER, MANA_COST_REDUCTION);

        event.add(PLAYER, INFERNO_COOLDOWN_REDUCTION);
        event.add(PLAYER, INFERNO_MAGIC_DAMAGE_MULTIPLIER);
        event.add(PLAYER, INFERNO_MANA_COST_REDUCTION);

        event.add(PLAYER, VITALITY_COOLDOWN_REDUCTION);
        event.add(PLAYER, VITALITY_MAGIC_DAMAGE_MULTIPLIER);
        event.add(PLAYER, VITALITY_MANA_COST_REDUCTION);

        event.add(PLAYER, MYSTIC_COOLDOWN_REDUCTION);
        event.add(PLAYER, MYSTIC_MAGIC_DAMAGE_MULTIPLIER);
        event.add(PLAYER, MYSTIC_MANA_COST_REDUCTION);

        event.add(PLAYER, FROST_COOLDOWN_REDUCTION);
        event.add(PLAYER, FROST_MAGIC_DAMAGE_MULTIPLIER);
        event.add(PLAYER, FROST_MANA_COST_REDUCTION);
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}

