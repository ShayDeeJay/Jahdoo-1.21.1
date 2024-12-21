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
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.jahdoo.ability.JahdooRarity.*;
import static org.jahdoo.registers.AttributesRegister.*;
import static org.jahdoo.registers.DataComponentRegistry.RUNE_DATA;
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
    public static final RuneData DEFAULT = new RuneData(-1, DEFAULT_NAME, "Has no power", -1, 0);

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
        var element = ElementRegistry.getElementOptional(this.elementId);
        return element.map(AbstractElement::textColourPrimary).orElse(colour);
    }

    public int getTypeColourSecondary(){
        var element = ElementRegistry.getElementOptional(this.elementId);
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
            if(data.isPresent() && !Objects.equals(data.get().name(), DEFAULT_NAME)) return data.get().name();
            return DEFAULT_NAME;
        }

        public static String getDescription(ItemStack itemStack){
            var data = getRuneData(itemStack);
            if(data.isPresent()) return data.get().description();
            return "";
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
            return withStyleComponent(getName(stack), getRuneData(stack).orElse(DEFAULT).getTypeColourSecondary());
        }

        public static Component standAloneAttributes(ItemStack itemStack) {
            var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
            if(!attributes.isEmpty()){
                var data = getRuneData(itemStack).orElse(DEFAULT);
                var colourPre = FastColor.ARGB32.color(121, 187, 67);
                var entry = attributes.getFirst();
                var value = roundNonWholeString(doubleFormattedDouble(entry.modifier().amount()));
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

        public static Optional<RuneData> getRuneData(ItemStack stack){
            return Optional.ofNullable(stack.get(DataComponentRegistry.RUNE_DATA.get()));
        }

        public static RuneData getRuneDataOpen(ItemStack stack){
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

        public static void generateRandomTypAttribute(ItemStack stack) {
            if(stack.getAttributeModifiers().modifiers().isEmpty()){
                var getElement = ElementRegistry.getRandomElement();
                var rarity = JahdooRarity.getRarity();

                var allManaAttributes = List.of(
                    Pair.of(Pair.of(AttributesRegister.MANA_REGEN_PREFIX, AttributesRegister.MANA_REGEN.getDelegate()), JahdooRarity.getManaRegenRange(rarity)),
                    Pair.of(Pair.of(AttributesRegister.MANA_POOL_PREFIX, AttributesRegister.MANA_POOL.getDelegate()), JahdooRarity.getManaRange(rarity))
                );

                var allMultiRunes = List.of(
                    Pair.of(Pair.of(AttributesRegister.MAGIC_DAMAGE_MULTIPLIER_PREFIX, AttributesRegister.MAGIC_DAMAGE_MULTIPLIER.getDelegate()), JahdooRarity.getDamageRange(rarity)),
                    Pair.of(Pair.of(AttributesRegister.COOLDOWN_REDUCTION_PREFIX, AttributesRegister.COOLDOWN_REDUCTION.getDelegate()), JahdooRarity.getCooldownRange(rarity)),
                    Pair.of(Pair.of(AttributesRegister.MANA_COST_REDUCTION_PREFIX, AttributesRegister.MANA_COST_REDUCTION.getDelegate()), JahdooRarity.getManaReductionRange(rarity))
                );

                var skillRunes = List.of(
                    Pair.of(Pair.of(AttributesRegister.MAGE_FLIGHT_PREFIX, AttributesRegister.MAGE_FLIGHT.getDelegate()), 1),
                    Pair.of(Pair.of(AttributesRegister.DESTINY_BOND_PREFIX, AttributesRegister.DESTINY_BOND.getDelegate()), 1)
                );

                var allTypeAttributes = List.of(
                    Pair.of(getElement.getDamageTypeAmplifier(), JahdooRarity.getDamageRange(rarity)),
                    Pair.of(getElement.getTypeCooldownReduction(), JahdooRarity.getCooldownRange(rarity)),
                    Pair.of(getElement.getTypeManaReduction(), JahdooRarity.getManaReductionRange(rarity))
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
                        generateMultiRune(stack, getSkillAttributes.getFirst(), getSkillAttributes.getSecond(), "Skill", LEGENDARY, FastColor.ARGB32.color(234, 82, 68));
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
