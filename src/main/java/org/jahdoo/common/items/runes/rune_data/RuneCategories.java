package org.jahdoo.common.items.runes.rune_data;


import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public enum RuneCategories {

    ELEMENTAL("elemental", 5, 1, -1),
    PERK("perk", 10, 3, ColourHelpers.getPerkGreen()),
    AETHER("aether", 15, 0, ColourHelpers.getAetherBlue()),
    RESILIENCE("protector", 20, 2, ColourHelpers.getNegativeRed()),
    COSMIC("cosmic", 25, 4, ColourHelpers.getCosmicPurple()),
    INFINITY("infinity", 30, 5, ColourHelpers.getChampionGold()),
    EMPTY("blank", 500, -1, ColourHelpers.getOffWhite());

    private final String name;
    private final int cost;
    private final int model;
    private final int colour;

    RuneCategories(String name, int cost, int model, int colour) {
        this.name = name;
        this.cost = cost;
        this.model = model;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public int getModel() {
        return model;
    }

    public int getColour() {
        return colour;
    }

    @Override
    public String toString() {
        return String.format(
            "RuneCategories{name='%s', cost=%d, model=%d, colour=0x%06X}",
            name, cost, model, colour
        );
    }

    public int getCost(int tier) {
        var max = Math.max(tier + 1, 1);
        return (cost * max);
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