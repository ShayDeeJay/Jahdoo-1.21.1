package org.jahdoo.common.items.runes.rune_data;

public enum RuneCategories {
    ELEMENTAL("elemental", 5, -1),
    PERK("perk", 10, 7),
    AETHER("aether", 15, 0),
    RESILIENCE("resilience", 20, 6),
    COSMIC("cosmic", 25, 9),
    INFINITY("infinity", 30, 8),
    EMPTY("blank", 500, -1);

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

    public int getModel() {
        return model;
    }

    @Override
    public String toString() {
        return String.format("RuneCategories{name='%s', cost=%d, model=%d}", name, cost, model);
    }

    public int getCost(int tier) {
        var max = Math.max(tier + 1, 1);
        return (cost * max);
    }

    public static int getCostByName(String name) {
        for (RuneCategories category : RuneCategories.values()) {
            if (category.name.equalsIgnoreCase(name)) return category.cost;
        }
        return -1;
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