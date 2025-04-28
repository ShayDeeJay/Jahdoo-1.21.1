package org.jahdoo.common.items.runes.rune_data;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

import static org.jahdoo.common.items.runes.rune_data.RuneData.NO_VALUE;

public class RuneGenerator {

    private final Holder<Attribute> type;
    private final double value;
    private final String name;
    private final int tier;

    private RuneGenerator(Builder builder) {
        this.type = builder.type;
        this.value = builder.value;
        this.name = builder.name;
        this.tier = builder.tier;
    }

    public Holder<Attribute> getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int getTier() {
        return tier;
    }

    public static class Builder {
        private Holder<Attribute> type;

        private double value = NO_VALUE;
        private String name = "";
        private int tier = 1;

        public Builder(Holder<Attribute> type) {
            if (type == null) {
                throw new IllegalArgumentException("Type cannot be null.");
            }
            this.type = type;
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

        public Builder setTier(int tier) {
            this.tier = tier;
            return this;
        }

        public RuneGenerator build() {
            return new RuneGenerator(this);
        }
    }


}