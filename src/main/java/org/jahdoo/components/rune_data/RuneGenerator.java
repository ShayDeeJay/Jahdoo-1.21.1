package org.jahdoo.components.rune_data;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.ability.rarity.RarityAttributes;

import java.util.List;
import java.util.function.UnaryOperator;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ability.rarity.JahdooRarity.*;
import static org.jahdoo.ability.rarity.RarityAttributes.*;
import static org.jahdoo.components.rune_data.RuneData.*;
import static org.jahdoo.components.rune_data.RuneGenerator.RuneCategories.*;
import static org.jahdoo.utils.ColourStore.*;

public class RuneGenerator {

    private final Holder<Attribute> type;
    private final double value;
    private final String name;
    private final JahdooRarity rarity;
    private final String description;
    private final int elementId;
    private final int tier;
    private final int colour;
    private final int modelData;
    private final double percentage;

    private RuneGenerator(Builder builder) {
        this.type = builder.type;
        this.value = builder.value;
        this.name = builder.name;
        this.rarity = builder.rarity;
        this.description = builder.description;
        this.elementId = builder.elementId;
        this.tier = builder.tier;
        this.colour = builder.colour;
        this.modelData = builder.modelData;
        this.percentage = builder.percentage;
    }

    public Holder<Attribute> getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getName() {
        return name;
    }

    public JahdooRarity getRarity() {
        return rarity;
    }

    public String getDescription() {
        return description;
    }

    public int getElementId() {
        return elementId;
    }

    public int getTier() {
        return tier;
    }

    public int getColour() {
        return colour;
    }

    public int getModelData() {
        return modelData;
    }

    public static class Builder {
        private Holder<Attribute> type;

        private double value = NO_VALUE;
        private String name = "";
        private JahdooRarity rarity = COMMON;
        private String description = NO_DESCRIPTION;
        private int elementId = NO_ELEMENT;
        private int tier = 1;
        private int colour = -1;
        private int modelData = 4;
        private double percentage = 0;

        public Builder(Holder<Attribute> type) {
            if (type == null) {
                throw new IllegalArgumentException("Type cannot be null.");
            }
            this.type = type;
        }

        public Builder setConvertPercentage(double percentage) {
            this.percentage = percentage;
            return this;
        }

        public Builder setType(Holder<Attribute> type) {
            this.type = type;
            return this;
        }

        public Builder setValue(double value) {
            this.value = value;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setRarity(JahdooRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setElementId(int elementId) {
            this.elementId = elementId;
            return this;
        }

        public Builder setTier(int tier) {
            this.tier = tier;
            return this;
        }

        public Builder setColour(int colour) {
            this.colour = colour;
            return this;
        }

        public Builder setModelData(int modelData) {
            this.modelData = modelData;
            return this;
        }

        public RuneGenerator build() {
            return new RuneGenerator(this);
        }
    }

    public static RuneGenerator generateElementalRune(Holder<Attribute> type, double value, JahdooRarity rarity, int elementId, int tier) {
        return new RuneGenerator.Builder(type)
            .setValue(value)
            .setName(ELEMENTAL.getName())
            .setRarity(rarity)
            .setElementId(elementId)
            .setTier(tier)
            .setModelData(elementId)
            .build();
    }

    public static RuneGenerator generatePerkRune(Holder<Attribute> type, double value, JahdooRarity rarity, String description, int tier, double percent) {
        return new RuneGenerator.Builder(type)
            .setValue(value)
            .setName(PERK.getName())
            .setRarity(rarity)
            .setDescription(description)
            .setTier(tier)
            .setColour(PERK_GREEN)
            .setModelData(PERK.getModel())
            .setConvertPercentage(percent)
            .build();
    }

    public static RuneGenerator.Builder generateCosmicRune(Holder<Attribute> type, double value, int tier) {
        return new RuneGenerator.Builder(type)
            .setValue(value)
            .setName(COSMIC.getName())
            .setRarity(ETERNAL)
            .setTier(tier)
            .setColour(COSMIC_PURPLE)
            .setModelData(COSMIC.getModel());
    }

    public static RuneGenerator generateAetherRune(Holder<Attribute> type, double value, int tier) {
        return new RuneGenerator.Builder(type)
            .setValue(value)
            .setName(AETHER.getName())
            .setRarity(LEGENDARY)
            .setTier(tier)
            .setColour(AETHER_BLUE)
            .setModelData(AETHER.getModel())
            .build();
    }

    public enum RuneCategories {
        ELEMENTAL("Elemental", 5, -1),
        PERK("Perk", 15, 8),
        AETHER("Aether", 25, 0),
        COSMIC("Cosmic", 50, 10),
        EMPTY("Blank", 500, -1);

        private final String name;
        private final int cost;
        private final int model;

        RuneCategories(String name, int cost, int model) {
            this.name = name;
            this.cost = cost;
            this.model = model;
        }

        public String getName() {
            return name;
        }

        public int getCost() {
            return cost;
        }

        public int getModel() {
            return model;
        }

        public static int getCostByName(String name) {
            for (RuneCategories category : RuneCategories.values()) {
                if (category.name.equalsIgnoreCase(name)) return category.cost;
            }
            return -1;
        }

        @Override
        public String toString() {
            return String.format("RuneCategories{name='%s', cost=%d, model=%d}", name, cost, model);
        }

        public static RuneCategories fromName(String name) {
            for (RuneCategories category : RuneCategories.values()) {
                if (category.name.equalsIgnoreCase(name)) {
                    return category;
                }
            }
            return EMPTY;
        }
    }
}