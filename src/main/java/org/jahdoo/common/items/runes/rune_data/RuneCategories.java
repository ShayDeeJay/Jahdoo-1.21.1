package org.jahdoo.common.items.runes.rune_data;


import org.shaydee.shaydeeapi.helpers.ColourHelpers;

public enum RuneCategories {

    ELEMENTAL("elemental", 5, 1, -1, false),
    PERK("perk", 10, 3, ColourHelpers.getPerkGreen(), false),
    AETHER("aether", 15, 0, ColourHelpers.getAetherBlue(),  false),
    RESILIENCE("protector", 20, 2, ColourHelpers.getNegativeRed(), false),
    COSMIC("cosmic", 25, 4, ColourHelpers.getCosmicPurple(),  false),
    INFINITY("infinity", 30, 5, ColourHelpers.getChampionGold(), false),
    EFFECT("effect", 500, -1, -1, true),
    SKILL("skill", 500, -1, ColourHelpers.getUniqueA(), true),
    EMPTY("blank", 500, -1, ColourHelpers.getOffWhite(), true);

    private final String name;
    private final int cost;
    private final int model;
    private final int colour;
    private final boolean isDummy;

    RuneCategories(String name, int cost, int model, int colour, boolean isDummy) {
        this.name = name;
        this.cost = cost;
        this.model = model;
        this.colour = colour;
        this.isDummy = isDummy;
    }

    public boolean getIsDummy() {
        return isDummy;
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