package org.jahdoo.items.runes.rune_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.wand.WandItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static net.minecraft.util.FastColor.ARGB32.*;
import static org.jahdoo.ability.rarity.JahdooRarity.*;
import static org.jahdoo.challenge.LocalLootBeamData.attachLootBeamComponent;
import static org.jahdoo.items.runes.rune_data.RuneGenerator.*;
import static org.jahdoo.items.runes.rune_data.RuneGenerator.RuneCategories.fromName;
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
    public static final String DEFAULT_NAME = "Unidentified";
    public static final int NO_ELEMENT = -1;
    public static final int NO_VALUE = -1;
    public static final String NO_DESCRIPTION = "";
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
        var element = getElementById(this.elementId);
        return element.map(AbstractElement::textColourPrimary).orElse(colour);
    }

    public int getTypeColourSecondary(){
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
        public static int getTier(ItemStack itemStack){
            var data = getRuneData(itemStack);
            if(!Objects.equals(data.name(), DEFAULT_NAME)) return data.tier();
            return -1;
        }

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
            if(itemStack.getItem() instanceof WandItem){
                var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
                return attributes.stream().anyMatch(attribute -> attribute.attribute().value().getDescriptionId().contains(MAGE_FLIGHT_PREFIX));
            }
            return false;
        }

        public static boolean canTripleJump(ItemStack itemStack){
            if(itemStack.getItem() instanceof WandItem){
                var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
                return attributes.stream().anyMatch(attribute -> attribute.attribute().value().getDescriptionId().contains(TRIPLE_JUMP_PREFIX));
            }
            return false;
        }

        public static boolean hasDestinyBond(ItemStack itemStack){
            var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
            return attributes.stream().anyMatch(attribute -> attribute.attribute().value().getDescriptionId().contains(DESTINY_BOND_PREFIX));
        }

        public static Component getNameWithStyle(ItemStack stack){
            return withStyleComponent(getName(stack)+ " " + RuneData.SUFFIX, getColourDarker(getRuneData(stack).getTypeColourSecondary(), 1.6));
        }

        public static int getCostFromRune(@NotNull ItemStack itemStack) {
            var name = RuneData.RuneHelpers.getName(itemStack);
            return fromName(name).getCost();
        }

        public static Component standAloneAttributes(ItemStack itemStack) {
            var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
            if (attributes.isEmpty()) return Component.empty();

            var data = getRuneData(itemStack);
            var colourPre = color(121, 187, 67);
            var entry = attributes.getFirst();
            var descriptionId = entry.attribute().value().getDescriptionId();
            var amount = entry.modifier().amount();
            var typeColour = data.elementId < 1 ? data.colour : getElementByTypeId(data.elementId()).getFirst().textColourPrimary();
            var compName = withStyleComponentTrans(descriptionId, typeColour);
            var isAbsorption = descriptionId.contains("absorption");
            var isMaxHealth = descriptionId.contains("health");
            var isSpeed = descriptionId.contains("speed");

            if(isSpeed) amount = amount * 1000;
            if(isAbsorption || isMaxHealth) amount = (amount/2);

            var value = roundNonWholeString(singleFormattedDouble(amount));

            if(descriptionId.contains(FIXED_VALUE) || isAbsorption || isMaxHealth) {
                return withStyleComponent("+" + value + " ", colourPre).copy().append(compName);
            }

            if(descriptionId.contains("skills")){
                return withStyleComponent("", colourPre).copy().append(compName);
            }

            return withStyleComponent("+" + value + "%" + " ", colourPre).copy().append(compName);
        }

        public static RuneData getRuneData(ItemStack stack){
            return stack.getOrDefault(RUNE_DATA, DEFAULT);
        }

        public static void generateFullRune(ItemStack stack, RuneGenerator runGen) {
            var isPercentage = runGen.getPercentage() > 0 ? (runGen.getValue() * runGen.getPercentage()) / 100 : runGen.getValue();
            replaceOrAddAttribute(stack, runGen.getType().getRegisteredName(), runGen.getType(), isPercentage, EquipmentSlot.MAINHAND, true);
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(runGen.getModelData()));
            var value = new RuneData(runGen.getElementId(), runGen.getName(), runGen.getDescription(), runGen.getColour(), runGen.getRarity().getId(), runGen.getTier());
            stack.set(RUNE_DATA, value);
        }

        public static void generateRandomTypAttribute(ItemStack stack, @Nullable JahdooRarity withRarity) {
            if(stack.getAttributeModifiers().modifiers().isEmpty()){
                var getElement = getRandomElement();
                var rarity = withRarity != null ? withRarity : JahdooRarity.getRarity();

                final var attributes = rarity.getAttributes();
                final var id = rarity.getId();

                var getList = switch (getRarity()){
                    case COMMON ->
                        List.of(
                            generateElementalRune(getElement.getTypeManaReduction(), attributes.getRandomManaReduction(), COMMON, getElement.getTypeId(), id),
                            generatePerkRune(DESTINY_BOND.getDelegate(), NO_VALUE, COMMON, "Keep your item on death.", NO_VALUE, NO_VALUE)
                        );
                    case RARE ->
                        List.of(
                            generateElementalRune(getElement.getDamageTypeAmplifier(), attributes.getRandomDamage(), RARE, getElement.getTypeId(), id),
                            generatePerkRune(Attributes.MOVEMENT_SPEED, attributes.getRandomDamage(), RARE, NO_DESCRIPTION, id, 0.1)
                        );
                    case EPIC ->
                        List.of(
                            generateElementalRune(getElement.getTypeCooldownReduction(), attributes.getRandomCooldown(), EPIC, getElement.getTypeId(), id),
                            generatePerkRune(TRIPLE_JUMP, NO_VALUE, RARE, "Allows player to jump up to 3 times", NO_VALUE, NO_VALUE)
                        );
                    case LEGENDARY ->
                        List.of(
                            generateAetherRune(MANA_REGEN.getDelegate(), attributes.getRandomManaRegen(), id),
                            generateAetherRune(MANA_POOL.getDelegate(), attributes.getRandomManaPool(), id),
                            generatePerkRune(Attributes.MAX_HEALTH, attributes.getRandomMaxHealth(), LEGENDARY, "Increase max health", id, NO_VALUE),
                            generatePerkRune(Attributes.MAX_ABSORPTION, attributes.getRandomMaxAbsorption(), LEGENDARY, "Increase absorption heart capacity", id, NO_VALUE),
                            generatePerkRune(MAGE_FLIGHT.getDelegate(), NO_VALUE, LEGENDARY, "Allows the player to fly, at the cost of mana.", NO_VALUE, NO_VALUE)
                        );
                    case ETERNAL ->
                        List.of(
                            generateCosmicRune(MAGIC_DAMAGE_MULTIPLIER.getDelegate(), attributes.getRandomDamage(), id).build(),
                            generateCosmicRune(COOLDOWN_REDUCTION.getDelegate(), attributes.getRandomCooldown(), id).build(),
                            generateCosmicRune(MANA_COST_REDUCTION.getDelegate(), attributes.getRandomManaReduction(), id).build(),
                            generateSympathiserRune(CAST_HEAL.getDelegate(), attributes.getRandomHealChance(), id),
                            generateSympathiserRune(ABSORPTION_HEARTS.getDelegate(), attributes.getRandomHealChance(), id),
                            generateSympathiserRune(SKIP_MANA.getDelegate(), attributes.getRandomHealChance(), id),
                            generateSympathiserRune(SKIP_COOLDOWN.getDelegate(), attributes.getRandomHealChance(), id)
                        );
                };

                attachLootBeamComponent(stack, rarity);
                generateFullRune(stack, getRandomListElement(getList));
            }
        }
    }


}
