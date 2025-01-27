package org.jahdoo.registers;

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
import org.jahdoo.utils.ModHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class  AttributesRegister {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, JahdooMod.MOD_ID);
    public static final String MOD = "jahdoo";
    public static final String FIXED_VALUE = ".fixed";

    //Base Attributes
    public static final String MANA_POOL_PREFIX = MOD + FIXED_VALUE + ".mana.mana_pool";
    public static final String MANA_REGEN_PREFIX= MOD + ".mana.mana_regen";

    //Skill Attributes
    public static final String MAGE_FLIGHT_PREFIX = MOD + ".skills.mage_flight";
    public static final String DESTINY_BOND_PREFIX = MOD + ".skills.destiny_bond";
    public static final String TRIPLE_JUMP_PREFIX = MOD + ".skills.triple_jump";

    //Cast Attributes
    public static final String SKIP_MANA_PREFIX = MOD + ".mana.no_mana_cost";
    public static final String SKIP_COOLDOWN_PREFIX = MOD + ".no_cooldown_cost";
    public static final String HEAL_PREFIX= MOD + ".cast.healer";
    public static final String ABSORPTION_PREFIX = MOD + ".cast.absorption";

    //Element Attributes
    public static final String MANA_COST_REDUCTION_PREFIX = MOD + ".mana.cost_reduction";
    public static final String COOLDOWN_REDUCTION_PREFIX = MOD + ".cooldown.cooldown_reduction";
    public static final String MAGIC_DAMAGE_MULTIPLIER_PREFIX = MOD + ".damage.damage_multiplier";

    public static final String INFERNO_MANA_COST_REDUCTION_PREFIX = MOD + ".inferno_mana.cost_reduction";
    public static final String INFERNO_COOLDOWN_REDUCTION_PREFIX = MOD + ".inferno_cooldown.cooldown_reduction";
    public static final String INFERNO_MAGIC_DAMAGE_MULTIPLIER_PREFIX = MOD + ".inferno_damage.damage_multiplier";

    public static final String MYSTIC_MANA_COST_REDUCTION_PREFIX = MOD + ".mystic_mana.cost_reduction";
    public static final String MYSTIC_COOLDOWN_REDUCTION_PREFIX = MOD + ".mystic_cooldown.cooldown_reduction";
    public static final String MYSTIC_MAGIC_DAMAGE_MULTIPLIER_PREFIX = MOD + ".mystic_damage.damage_multiplier";

    public static final String LIGHTNING_MANA_COST_REDUCTION_PREFIX = MOD + ".lightning_mana.cost_reduction";
    public static final String LIGHTNING_COOLDOWN_REDUCTION_PREFIX = MOD + ".lightning_cooldown.cooldown_reduction";
    public static final String LIGHTNING_MAGIC_DAMAGE_MULTIPLIER_PREFIX = MOD + ".lightning_damage.damage_multiplier";

    public static final String FROST_MANA_COST_REDUCTION_PREFIX = MOD + ".frost_mana.cost_reduction";
    public static final String FROST_COOLDOWN_REDUCTION_PREFIX = MOD + ".frost_cooldown.cooldown_reduction";
    public static final String FROST_MAGIC_DAMAGE_MULTIPLIER_PREFIX = MOD + ".frost_damage.damage_multiplier";

    public static final String VITALITY_MANA_COST_REDUCTION_PREFIX = MOD + ".vitality_mana.cost_reduction";
    public static final String VITALITY_COOLDOWN_REDUCTION_PREFIX = MOD + ".vitality_cooldown.cooldown_reduction";
    public static final String VITALITY_MAGIC_DAMAGE_MULTIPLIER_PREFIX = MOD + ".vitality_damage.damage_multiplier";

    //Base attribute
    public static final DeferredHolder<Attribute, Attribute> MANA_POOL = register(MANA_POOL_PREFIX, 100);
    public static final DeferredHolder<Attribute, Attribute> MANA_REGEN = register(MANA_REGEN_PREFIX, 0);

    //Infinity Attributes
    public static final DeferredHolder<Attribute, Attribute> SKIP_MANA = register(SKIP_MANA_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> SKIP_COOLDOWN = register(SKIP_COOLDOWN_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> CAST_HEAL = register(HEAL_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> ABSORPTION_HEARTS = register(ABSORPTION_PREFIX, 0);

    //Skill Attributes
    public static final DeferredHolder<Attribute, Attribute> DESTINY_BOND = register(DESTINY_BOND_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> MAGE_FLIGHT = register(MAGE_FLIGHT_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> TRIPLE_JUMP = register(TRIPLE_JUMP_PREFIX, 0);

    //Cosmic attributes
    public static final DeferredHolder<Attribute, Attribute> COOLDOWN_REDUCTION = register(COOLDOWN_REDUCTION_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> MAGIC_DAMAGE_MULTIPLIER = register(MAGIC_DAMAGE_MULTIPLIER_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> MANA_COST_REDUCTION = register(MANA_COST_REDUCTION_PREFIX, 0);

    //Elemental attributes
    public static final DeferredHolder<Attribute, Attribute> INFERNO_COOLDOWN_REDUCTION = register(INFERNO_COOLDOWN_REDUCTION_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> INFERNO_MAGIC_DAMAGE_MULTIPLIER = register(INFERNO_MAGIC_DAMAGE_MULTIPLIER_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> INFERNO_MANA_COST_REDUCTION = register(INFERNO_MANA_COST_REDUCTION_PREFIX, 0);

    public static final DeferredHolder<Attribute, Attribute> MYSTIC_COOLDOWN_REDUCTION = register(MYSTIC_COOLDOWN_REDUCTION_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> MYSTIC_MAGIC_DAMAGE_MULTIPLIER = register(MYSTIC_MAGIC_DAMAGE_MULTIPLIER_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> MYSTIC_MANA_COST_REDUCTION = register(MYSTIC_MANA_COST_REDUCTION_PREFIX, 0);

    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_COOLDOWN_REDUCTION = register(LIGHTNING_COOLDOWN_REDUCTION_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_MAGIC_DAMAGE_MULTIPLIER = register(LIGHTNING_MAGIC_DAMAGE_MULTIPLIER_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_MANA_COST_REDUCTION = register(LIGHTNING_MANA_COST_REDUCTION_PREFIX, 0);

    public static final DeferredHolder<Attribute, Attribute> FROST_COOLDOWN_REDUCTION = register(FROST_COOLDOWN_REDUCTION_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> FROST_MAGIC_DAMAGE_MULTIPLIER = register(FROST_MAGIC_DAMAGE_MULTIPLIER_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> FROST_MANA_COST_REDUCTION = register(FROST_MANA_COST_REDUCTION_PREFIX, 0);

    public static final DeferredHolder<Attribute, Attribute> VITALITY_COOLDOWN_REDUCTION = register(VITALITY_COOLDOWN_REDUCTION_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> VITALITY_MAGIC_DAMAGE_MULTIPLIER = register(VITALITY_MAGIC_DAMAGE_MULTIPLIER_PREFIX, 0);
    public static final DeferredHolder<Attribute, Attribute> VITALITY_MANA_COST_REDUCTION = register(VITALITY_MANA_COST_REDUCTION_PREFIX, 0);

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
        attributeList.add(setAttribute(name, attribute, value, equipmentSlot, isRandomId));
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
        var resourcelocation = isRandomId ? ModHelpers.res(String.valueOf(UUID.randomUUID())) : ResourceLocation.parse(name);
        var attributes = new AttributeModifier(resourcelocation, value,  AttributeModifier.Operation.ADD_VALUE);
        return new ItemAttributeModifiers.Entry(attribute, attributes,  EquipmentSlotGroup.bySlot(equipmentSlot));
    }

    public static void attachAttribute(EntityAttributeModificationEvent event){
        event.add(EntityType.PLAYER, AttributesRegister.MANA_POOL);
        event.add(EntityType.PLAYER, AttributesRegister.MANA_REGEN);
        event.add(EntityType.PLAYER, AttributesRegister.MAGE_FLIGHT);
        event.add(EntityType.PLAYER, AttributesRegister.TRIPLE_JUMP);

        event.add(EntityType.PLAYER, AttributesRegister.SKIP_MANA);
        event.add(EntityType.PLAYER, AttributesRegister.SKIP_COOLDOWN);

        event.add(EntityType.PLAYER, AttributesRegister.COOLDOWN_REDUCTION);
        event.add(EntityType.PLAYER, AttributesRegister.MAGIC_DAMAGE_MULTIPLIER);
        event.add(EntityType.PLAYER, AttributesRegister.MANA_COST_REDUCTION);

        event.add(EntityType.PLAYER, AttributesRegister.INFERNO_COOLDOWN_REDUCTION);
        event.add(EntityType.PLAYER, AttributesRegister.INFERNO_MAGIC_DAMAGE_MULTIPLIER);
        event.add(EntityType.PLAYER, AttributesRegister.INFERNO_MANA_COST_REDUCTION);

        event.add(EntityType.PLAYER, AttributesRegister.LIGHTNING_COOLDOWN_REDUCTION);
        event.add(EntityType.PLAYER, AttributesRegister.LIGHTNING_MAGIC_DAMAGE_MULTIPLIER);
        event.add(EntityType.PLAYER, AttributesRegister.LIGHTNING_MANA_COST_REDUCTION);

        event.add(EntityType.PLAYER, AttributesRegister.VITALITY_COOLDOWN_REDUCTION);
        event.add(EntityType.PLAYER, AttributesRegister.VITALITY_MAGIC_DAMAGE_MULTIPLIER);
        event.add(EntityType.PLAYER, AttributesRegister.VITALITY_MANA_COST_REDUCTION);

        event.add(EntityType.PLAYER, AttributesRegister.MYSTIC_COOLDOWN_REDUCTION);
        event.add(EntityType.PLAYER, AttributesRegister.MYSTIC_MAGIC_DAMAGE_MULTIPLIER);
        event.add(EntityType.PLAYER, AttributesRegister.MYSTIC_MANA_COST_REDUCTION);

        event.add(EntityType.PLAYER, AttributesRegister.FROST_COOLDOWN_REDUCTION);
        event.add(EntityType.PLAYER, AttributesRegister.FROST_MAGIC_DAMAGE_MULTIPLIER);
        event.add(EntityType.PLAYER, AttributesRegister.FROST_MANA_COST_REDUCTION);
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}

