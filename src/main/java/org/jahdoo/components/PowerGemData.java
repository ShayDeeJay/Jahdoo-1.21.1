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
import static org.jahdoo.registers.AttributesRegister.replaceOrAddAttribute;
import static org.jahdoo.registers.DataComponentRegistry.POWER_GEM_DATA;
import static org.jahdoo.registers.ElementRegistry.getElementByTypeId;
import static org.jahdoo.utils.ModHelpers.*;

public record PowerGemData(
    int elementId,
    String name,
    String description,
    int colour,
    int rarityId
){
    public static final String SUFFIX = "Power Gem";
    public static final String DEFAULT_NAME = "Generic " + SUFFIX;
    public static final PowerGemData DEFAULT = new PowerGemData(-1, DEFAULT_NAME, "Has no power", -1, 0);

    public PowerGemData insertNewName(String name){
        return new PowerGemData(this.elementId, name, this.description, this.colour, this.rarityId);
    }

    public PowerGemData insertNewDescription(String description){
        return new PowerGemData(this.elementId, this.name, description, this.colour, this.rarityId);
    }

    public PowerGemData insertNewElement(int elementId){
        return new PowerGemData(elementId, this.name, this.description, this.colour, this.rarityId);
    }

    public PowerGemData insertNewRarity(int rarityId){
        return new PowerGemData(this.elementId, this.name, this.description, this.colour, rarityId);
    }

    public PowerGemData insertNewColour(int colour){
        return new PowerGemData(this.elementId, this.name, this.description, colour, this.rarityId);
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

    public static PowerGemData deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new PowerGemData(
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readInt()
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, PowerGemData> STREAM_CODEC = StreamCodec.ofMember(
        PowerGemData::serialise,
        PowerGemData::deserialise
    );

    public static final Codec<PowerGemData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("element_id").forGetter(PowerGemData::elementId),
            Codec.STRING.fieldOf("name").forGetter(PowerGemData::name),
            Codec.STRING.fieldOf("description").forGetter(PowerGemData::description),
            Codec.INT.fieldOf("colour").forGetter(PowerGemData::colour),
            Codec.INT.fieldOf("rarity_id").forGetter(PowerGemData::rarityId)
            ).apply(instance, PowerGemData::new)
    );


    public static class PowerGemHelpers {
        public static String getName(ItemStack itemStack){
            var data = getGemData(itemStack);
            if(data.isPresent() && !Objects.equals(data.get().name(), DEFAULT_NAME)) return data.get().name();
            return DEFAULT_NAME;
        }

        public static String getDescription(ItemStack itemStack){
            var data = getGemData(itemStack);
            if(data.isPresent()) return data.get().description();
            return "";
        }

        public static Component getNameWithStyle(ItemStack stack){
            return withStyleComponent(getName(stack), getGemData(stack).orElse(DEFAULT).getTypeColourSecondary());
        }

        public static Component standAloneAttributes(ItemStack itemStack) {
            var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
            if(!attributes.isEmpty()){
                var data = getGemData(itemStack).orElse(DEFAULT);
                var colourPre = FastColor.ARGB32.color(121, 187, 67);
                var entry = attributes.getFirst();
                var value = roundNonWholeString(doubleFormattedDouble(entry.modifier().amount()));
                var valueWithFix = "+" + value + "%";
                var descriptionId = entry.attribute().value().getDescriptionId();
                var typeColour = data.elementId < 1 ? data.colour : getElementByTypeId(data.elementId()).getFirst().textColourPrimary();
                var compName = withStyleComponentTrans(descriptionId, typeColour);
                return withStyleComponent(compName.getString().equals("Mana") ? "+" + value + " " : valueWithFix + " ", colourPre).copy()
                    .append(compName);
            }
            return Component.empty();
        }

        public static Optional<PowerGemData> getGemData(ItemStack stack){
            return Optional.ofNullable(stack.get(DataComponentRegistry.POWER_GEM_DATA.get()));
        }

        public static void generateTypeGem(ItemStack stack, Pair<String, Holder<Attribute>> type, double value, AbstractElement getElement, JahdooRarity rarity) {
            replaceOrAddAttribute(stack,type.getFirst(),type.getSecond(), value, EquipmentSlot.MAINHAND, true);
            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewElement(getElement.getTypeId()));
            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewRarity(rarity.getId()));
            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewName(getElement.getElementName() + " " + PowerGemData.SUFFIX));
            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewColour(getElement.textColourSecondary()));
        }

        public static void generateMultiGem(ItemStack stack, Pair<String, Holder<Attribute>> type, double value, String name, JahdooRarity rarity, int colour) {
            replaceOrAddAttribute(stack,type.getFirst(),type.getSecond(), value, EquipmentSlot.MAINHAND, true);
            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewRarity(rarity.getId()));
            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewName(name + " " + PowerGemData.SUFFIX));
            stack.update(POWER_GEM_DATA.get(), PowerGemData.DEFAULT, data -> data.insertNewColour(colour));
        }

        public static void generateRandomTypAttribute(ItemStack stack) {
            if(stack.getAttributeModifiers().modifiers().isEmpty()){
                var getElement = ElementRegistry.getRandomElement();
                var rarity = JahdooRarity.getRarity();

                var allManaAttributes = List.of(
                    Pair.of(Pair.of(AttributesRegister.MANA_REGEN_PREFIX, AttributesRegister.MANA_REGEN.getDelegate()), JahdooRarity.getManaRegenRange(rarity)),
                    Pair.of(Pair.of(AttributesRegister.MANA_POOL_PREFIX, AttributesRegister.MANA_POOL.getDelegate()), JahdooRarity.getManaRange(rarity))
                );

                var allMultiGems = List.of(
                    Pair.of(Pair.of(AttributesRegister.MAGIC_DAMAGE_MULTIPLIER_PREFIX, AttributesRegister.MAGIC_DAMAGE_MULTIPLIER.getDelegate()), JahdooRarity.getDamageRange(rarity)),
                    Pair.of(Pair.of(AttributesRegister.COOLDOWN_REDUCTION_PREFIX, AttributesRegister.COOLDOWN_REDUCTION.getDelegate()), JahdooRarity.getCooldownRange(rarity)),
                    Pair.of(Pair.of(AttributesRegister.MANA_COST_REDUCTION_PREFIX, AttributesRegister.MANA_COST_REDUCTION.getDelegate()), JahdooRarity.getManaReductionRange(rarity))
                );

                var allTypeAttributes = List.of(
                    Pair.of(getElement.getDamageTypeAmplifier(), JahdooRarity.getDamageRange(rarity)),
                    Pair.of(getElement.getTypeCooldownReduction(), JahdooRarity.getCooldownRange(rarity)),
                    Pair.of(getElement.getTypeManaReduction(), JahdooRarity.getManaReductionRange(rarity))
                );

                var randomIndex = Random.nextInt(0, allTypeAttributes.size());
                var getTypeAttribute = allTypeAttributes.get(randomIndex);
                var getMultiAttribute = allMultiGems.get(randomIndex);
                var getManaAttributes = allManaAttributes.get( Random.nextInt(0, allManaAttributes.size()));
                stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(randomIndex));


                switch (getRarity()){
                    case COMMON, RARE, EPIC -> generateTypeGem(stack, getTypeAttribute.getFirst(), getTypeAttribute.getSecond(), getElement, rarity);
                    case LEGENDARY -> generateMultiGem(stack, getMultiAttribute.getFirst(), getMultiAttribute.getSecond(), "Versatile", rarity, FastColor.ARGB32.color(69, 100, 142));
                    case ETERNAL -> generateMultiGem(stack, getManaAttributes.getFirst(), getManaAttributes.getSecond(), "Assistant", rarity, FastColor.ARGB32.color(71, 145, 243));
                }
            }
        }

    }

}
