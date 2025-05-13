package org.jahdoo.common.items.runes.rune_data;

import static org.jahdoo.trial_nexus.utils.ColourStore.*;

public enum RuneCategories {

    ELEMENTAL("elemental", 5, 1, -1),
    PERK("perk", 10, 3, PERK_GREEN),
    AETHER("aether", 15, 0, AETHER_BLUE),
    RESILIENCE("protector", 20, 2, NEGATIVE_RED),
    COSMIC("cosmic", 25, 4, COSMIC_PURPLE),
    INFINITY("infinity", 30, 5, CHAMPION_GOLD),
    EMPTY("blank", 500, -1, OFF_WHITE);

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