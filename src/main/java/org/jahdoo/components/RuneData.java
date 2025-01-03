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

import static net.minecraft.util.FastColor.*;
import static net.minecraft.util.FastColor.ARGB32.*;
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
    int rarityId,
    int tier
){
    public static final String SUFFIX = "Rune";
    public static final String DEFAULT_NAME = "Generic " + SUFFIX;
    public static final int NO_ELEMENT = -1;
    public static final RuneData DEFAULT = new RuneData(NO_ELEMENT, DEFAULT_NAME, "", -1, 0, 0);

    public RuneData insertNewName(String name){
        return new RuneData(this.elementId, name, this.description, this.colour, this.rarityId, this.tier);
    }

    public RuneData insertNewDescription(String description){
        return new RuneData(this.elementId, this.name, description, this.colour, this.rarityId, this.tier);
    }

    public RuneData insertNewElement(int elementId){
        return new RuneData(elementId, this.name, this.description, this.colour, this.rarityId, this.tier);
    }

    public RuneData insertNewRarity(int rarityId){
        return new RuneData(this.elementId, this.name, this.description, this.colour, rarityId, this.tier);
    }

    public RuneData insertNewColour(int colour){
        return new RuneData(this.elementId, this.name, this.description, colour, this.rarityId, this.tier);
    }


    public RuneData insertNewTier(int tier){
        return new RuneData(this.elementId, this.name, this.description, colour, this.rarityId, tier);
    }

    public int getTypeColourPrimary(){
        var element = getElementOptional(this.elementId);
        return element.map(AbstractElement::textColourPrimary).orElse(colour);
    }

    public int getTypeColourSecondary(){
        var element = getElementOptional(this.elementId);
        return colour;
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(this.elementId);
        friendlyByteBuf.writeUtf(this.name);
        friendlyByteBuf.writeUtf(this.description);
        friendlyByteBuf.writeInt(this.colour);
        friendlyByteBuf.writeInt(this.rarityId);
        friendlyByteBuf.writeInt(this.tier);
    }

    public static RuneData deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new RuneData(
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readInt(),
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
            Codec.INT.fieldOf("rarity_id").forGetter(RuneData::rarityId),
            Codec.INT.fieldOf("tier").forGetter(RuneData::tier)
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
            return withStyleComponent(getName(stack), getLighterColor(getRuneData(stack).getTypeColourSecondary()));
        }

        public static Component standAloneAttributes(ItemStack itemStack) {
            var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();

            if(!attributes.isEmpty()){
                var data = getRuneData(itemStack);
                var colourPre = color(121, 187, 67);
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

        public static void generateFullRune(ItemStack stack, Pair<String, Holder<Attribute>> type, double value, String name, JahdooRarity rarity, String description, int elementId, int tier, int colour) {
            replaceOrAddAttribute(stack,type.getFirst(),type.getSecond(), value, EquipmentSlot.MAINHAND, true);
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewRarity(rarity.getId()));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewName(name + " " + RuneData.SUFFIX));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewColour(colour));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewDescription(description));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewElement(elementId));
            stack.update(RUNE_DATA.get(), RuneData.DEFAULT, data -> data.insertNewTier(tier));
        }

        public static int getLighterColor(int color) {
            // Extract ARGB components from the integer
            int alpha = (color >> 24) & 0xFF;
            int red = (color >> 16) & 0xFF;
            int green = (color >> 8) & 0xFF;
            int blue = color & 0xFF;

            // Calculate the lighter shade by increasing the brightness
            var v = 1.6;
            red = Math.min((int) (red / v), 255);
            green = Math.min((int) (green / v), 255);
            blue = Math.min((int) (blue / v), 255);

            // Combine the components back into an integer
            return (alpha << 24) | (red << 16) | (green << 8) | blue;
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

                var randomIndex2 = Random.nextInt(0, allManaAttributes.size());
                var randomIndex = Random.nextInt(0, allTypeAttributes.size());
                var getTypeAttribute = allTypeAttributes.get(randomIndex);
                var getMultiAttribute = allMultiRunes.get(randomIndex);
                var getManaAttributes = allManaAttributes.get(randomIndex2);
                var getSkillAttributes = skillRunes.get(randomIndex2);


                switch (getRarity()){
                    case COMMON -> {
                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
                        generateFullRune(stack, getTypeAttribute.getFirst(), getTypeAttribute.getSecond(), "Elemental", COMMON, "", getElement.getTypeId(), rarity.getId(),color(224, 224, 224));
                    }
                    case EPIC -> {
                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(2));
                        generateFullRune(stack, getSkillAttributes.getFirst(), 1, "Perk", EPIC, getSkillAttributes.getSecond(), NO_ELEMENT, rarity.getId(), color(38, 224, 200));
                    }
                    case LEGENDARY -> {
                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0));
                        generateFullRune(stack, getManaAttributes.getFirst(), getManaAttributes.getSecond(), "Aether", LEGENDARY, "", NO_ELEMENT, rarity.getId(), color(0, 169, 247));
                    }
                    case ETERNAL -> {
                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(5));
                        generateFullRune(stack, getMultiAttribute.getFirst(), getMultiAttribute.getSecond(), "Cosmic", ETERNAL, "", NO_ELEMENT, rarity.getId(), color(171, 87, 194));
                    }
                }
            }
        }
    }

}
