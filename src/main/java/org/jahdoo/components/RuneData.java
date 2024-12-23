package org.jahdoo.components;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;

import java.util.List;
import java.util.Objects;

import static org.jahdoo.ability.rarity.JahdooRarity.*;
import static org.jahdoo.registers.AttributesRegister.*;
import static org.jahdoo.registers.DataComponentRegistry.RUNE_DATA;
import static org.jahdoo.registers.ElementRegistry.*;
import static org.jahdoo.registers.ElementRegistry.getElementByTypeId;
import static org.jahdoo.utils.ModHelpers.*;

public record RuneData(
    int elementId,
    String name,
    String description,
    int colour,
    int rarityId
){
    public static final String SUFFIX = "Rune";
    public static final String DEFAULT_NAME = "Generic " + SUFFIX;
    public static final RuneData DEFAULT = new RuneData(-1, DEFAULT_NAME, "", -1, 0);

    public RuneData insertNewName(String name){
        return new RuneData(this.elementId, name, this.description, this.colour, this.rarityId);
    }

    public RuneData insertNewDescription(String description){
        return new RuneData(this.elementId, this.name, description, this.colour, this.rarityId);
    }

    public RuneData insertNewElement(int elementId){
        return new RuneData(elementId, this.name, this.description, this.colour, this.rarityId);
    }

    public RuneData insertNewRarity(int rarityId){
        return new RuneData(this.elementId, this.name, this.description, this.colour, rarityId);
    }

    public RuneData insertNewColour(int colour){
        return new RuneData(this.elementId, this.name, this.description, colour, this.rarityId);
    }

    public int getTypeColourPrimary(){
        var element = getElementOptional(this.elementId);
        return element.map(AbstractElement::textColourPrimary).orElse(colour);
    }

    public int getTypeColourSecondary(){
        var element = getElementOptional(this.elementId);
        return element.map(AbstractElement::textColourSecondary).orElse(colour);
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(this.elementId);
        friendlyByteBuf.writeUtf(this.name);
        friendlyByteBuf.writeUtf(this.description);
        friendlyByteBuf.writeInt(this.colour);
        friendlyByteBuf.writeInt(this.rarityId);
    }

    public static RuneData deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new RuneData(
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readInt()
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RuneData> STREAM_CODEC = StreamCodec.ofMember(
        RuneData::serialise,
        RuneData::deserialise
    );

    public static final Codec<RuneData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("element_id").forGetter(RuneData::elementId),
            Codec.STRING.fieldOf("name").forGetter(RuneData::name),
            Codec.STRING.fieldOf("description").forGetter(RuneData::description),
            Codec.INT.fieldOf("colour").forGetter(RuneData::colour),
            Codec.INT.fieldOf("rarity_id").forGetter(RuneData::rarityId)
            ).apply(instance, RuneData::new)
    );


    public static class RuneHelpers {
        public static String getName(ItemStack itemStack){
            var data = getRuneData(itemStack);
            if(!Objects.equals(data.name(), DEFAULT_NAME)) return data.name();
            return DEFAULT_NAME;
        }

        public static Component getDescription(ItemStack itemStack){
            var data = getRuneData(itemStack);
            return Component.literal(data.description());
        }

        public static boolean canMageFlight(ItemStack itemStack){
            var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
            return attributes.stream().anyMatch(attribute -> attribute.attribute().value().getDescriptionId().contains(MAGE_FLIGHT_PREFIX));
        }

        public static boolean hasDestinyBond(ItemStack itemStack){
            var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
            return attributes.stream().anyMatch(attribute -> attribute.attribute().value().getDescriptionId().contains(DESTINY_BOND_PREFIX));
        }

        public static Component getNameWithStyle(ItemStack stack){
            return withStyleComponent(getName(stack), getRuneData(stack).getTypeColourSecondary());
        }

        public static Component standAloneAttributes(ItemStack itemStack) {
            var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();

            if(!attributes.isEmpty()){
                var data = getRuneData(itemStack);
                var colourPre = FastColor.ARGB32.color(121, 187, 67);
                var entry = attributes.getFirst();
                var value = roundNonWholeString(singleFormattedDouble(entry.modifier().amount()));
                var valueWithFix = "+" + value + "%";
                var valueWithout = "+" + value + " ";
                var descriptionId = entry.attribute().value().getDescriptionId();
                var typeColour = data.elementId < 1 ? data.colour : getElementByTypeId(data.elementId()).getFirst().textColourPrimary();
                var compName = withStyleComponentTrans(descriptionId, typeColour);
                var mana = compName.getString().equals("Mana");
                var skill = descriptionId.contains("skills");
                return withStyleComponent(mana ? valueWithout : skill ? "" : valueWithFix + " ", colourPre).copy()
                    .append(compName);
            }
            return Component.empty();
        }

        public static RuneData getRuneData(ItemStack stack){
            return stack.getOrDefault(RUNE_DATA, DEFAULT);
        }

        public static void generateTypeRune(ItemStack stack, Pair<String, Holder<Attribute>> type, double value, AbstractElement getElement, JahdooRarity rarity) {
            replaceOrAddAttribute(stack,type.getFirst(),type.getSecond(), value, EquipmentSlot.MAINHAND, true);
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewElement(getElement.getTypeId()));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewRarity(rarity.getId()));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewName(getElement.getElementName() + " " + RuneData.SUFFIX));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewColour(getElement.textColourSecondary()));
        }

        public static void generateMultiRune(ItemStack stack, Pair<String, Holder<Attribute>> type, double value, String name, JahdooRarity rarity, int colour) {
            replaceOrAddAttribute(stack,type.getFirst(),type.getSecond(), value, EquipmentSlot.MAINHAND, true);
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewRarity(rarity.getId()));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewName(name + " " + RuneData.SUFFIX));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewColour(colour));
        }

        public static void generateWithDescription(ItemStack stack, Pair<String, Holder<Attribute>> type, double value, String name, JahdooRarity rarity, int colour, String description) {
            replaceOrAddAttribute(stack,type.getFirst(),type.getSecond(), value, EquipmentSlot.MAINHAND, true);
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewRarity(rarity.getId()));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewName(name + " " + RuneData.SUFFIX));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewColour(colour));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewDescription(description));
        }

        public static void generateRandomTypAttribute(ItemStack stack) {
            if(stack.getAttributeModifiers().modifiers().isEmpty()){
                var getElement = getRandomElement();
                var rarity = JahdooRarity.getRarity();

                var allManaAttributes = List.of(
                    Pair.of(Pair.of(MANA_REGEN_PREFIX, MANA_REGEN.getDelegate()), rarity.getAttributes().getRandomManaRegen()),
                    Pair.of(Pair.of(MANA_POOL_PREFIX, MANA_POOL.getDelegate()), rarity.getAttributes().getRandomManaPool())
                );

                var allMultiRunes = List.of(
                    Pair.of(Pair.of(MAGIC_DAMAGE_MULTIPLIER_PREFIX, MAGIC_DAMAGE_MULTIPLIER.getDelegate()), rarity.getAttributes().getRandomDamage()),
                    Pair.of(Pair.of(COOLDOWN_REDUCTION_PREFIX, COOLDOWN_REDUCTION.getDelegate()), rarity.getAttributes().getRandomCooldown()),
                    Pair.of(Pair.of(MANA_COST_REDUCTION_PREFIX, MANA_COST_REDUCTION.getDelegate()), rarity.getAttributes().getRandomManaReduction())
                );

                var skillRunes = List.of(
                    Pair.of(Pair.of(MAGE_FLIGHT_PREFIX, MAGE_FLIGHT.getDelegate()), "Allows the player to fly, at the cost of mana."),
                    Pair.of(Pair.of(DESTINY_BOND_PREFIX, DESTINY_BOND.getDelegate()), "Keep your wand on death")
                );

                var allTypeAttributes = List.of(
                    Pair.of(getElement.getDamageTypeAmplifier(), rarity.getAttributes().getRandomDamage()),
                    Pair.of(getElement.getTypeCooldownReduction(), rarity.getAttributes().getRandomCooldown()),
                    Pair.of(getElement.getTypeManaReduction(), rarity.getAttributes().getRandomManaReduction())
                );

                var randomIndex = Random.nextInt(0, allTypeAttributes.size());
                var getTypeAttribute = allTypeAttributes.get(randomIndex);
                var getMultiAttribute = allMultiRunes.get(randomIndex);
                var getManaAttributes = allManaAttributes.get(Random.nextInt(0, allManaAttributes.size()));
                var getSkillAttributes = skillRunes.get(Random.nextInt(0, allManaAttributes.size()));


                switch (getRarity()){
                    case COMMON, RARE -> {
                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(randomIndex));
                        generateTypeRune(stack, getTypeAttribute.getFirst(), getTypeAttribute.getSecond(), getElement, rarity);
                    }
                    case EPIC -> {
                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(3));
                        generateWithDescription(stack, getSkillAttributes.getFirst(), 1, "Skill", LEGENDARY, FastColor.ARGB32.color(234, 82, 68), getSkillAttributes.getSecond());
                    }
                    case LEGENDARY -> {
                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(4));
                        generateMultiRune(stack, getMultiAttribute.getFirst(), getMultiAttribute.getSecond(), "Versatile", rarity, FastColor.ARGB32.color(69, 100, 142));
                    }
                    case ETERNAL -> {
                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(5));
                        generateMultiRune(stack, getManaAttributes.getFirst(), getManaAttributes.getSecond(), "Assistant", rarity, FastColor.ARGB32.color(71, 145, 243));
                    }
                }
            }
        }
    }

}
