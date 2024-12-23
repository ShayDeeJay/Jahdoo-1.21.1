package org.jahdoo.ability.rarity;

import com.mojang.datafixers.util.Pair;

import static org.jahdoo.utils.ModHelpers.Random;

public class RarityAttributes {
    private final Pair<Double, Double> manaPool;
    private final Pair<Double, Double> manaRegen;
    private final Pair<Double, Double> cooldown;
    private final Pair<Double, Double> manaReduction;
    private final Pair<Double, Double> damage;

    public RarityAttributes(
        Pair<Double, Double> manaPool,
        Pair<Double, Double> manaRegen,
        Pair<Double, Double> cooldown,
        Pair<Double, Double> manaReduction,
        Pair<Double, Double> damage
    ) {
        this.manaPool = manaPool;
        this.manaRegen = manaRegen;
        this.cooldown = cooldown;
        this.manaReduction = manaReduction;
        this.damage = damage;
    }

    public double getRandomManaPool() {
        return getRandomValue(manaPool);
    }

    public double getRandomManaRegen() {
        return getRandomValue(manaRegen);
    }

    public double getRandomCooldown() {
        return getRandomValue(cooldown);
    }

    public double getRandomManaReduction() {
        return getRandomValue(manaReduction);
    }

    public double getRandomDamage() {
        return getRandomValue(damage);
    }

    private double getRandomValue(Pair<Double, Double> range) {
        return Random.nextDouble(range.getFirst(), range.getSecond());
    }

    public Pair<Double, Double> getManaPoolRange() {
        return manaPool;
    }

    public Pair<Double, Double> getManaRegenRange() {
        return manaRegen;
    }

    public Pair<Double, Double> getCooldownRange() {
        return cooldown;
    }

    public Pair<Double, Double> getManaReductionRange() {
        return manaReduction;
    }

    public Pair<Double, Double> getDamageRange() {
        return damage;
    }

    public static final RarityAttributes COMMON_ATTRIBUTES = new RarityAttributes(
        Pair.of(20.0, 40.0), Pair.of(5.0, 10.0), Pair.of(0.5, 2.5), Pair.of(0.5, 4.5), Pair.of(5.0, 10.0)
    );

    public static final RarityAttributes RARE_ATTRIBUTES = new RarityAttributes(
        Pair.of(40.0, 60.0), Pair.of(10.0, 15.0), Pair.of(2.5, 4.5), Pair.of(4.5, 8.5), Pair.of(10.0, 15.0)
    );

    public static final RarityAttributes EPIC_ATTRIBUTES = new RarityAttributes(
        Pair.of(60.0, 80.0), Pair.of(15.0, 20.0), Pair.of(4.5, 6.5), Pair.of(8.5, 12.5), Pair.of(15.0, 20.0)
    );

    public static final RarityAttributes LEGENDARY_ATTRIBUTES = new RarityAttributes(
        Pair.of(80.0, 100.0), Pair.of(20.0, 25.0), Pair.of(6.5, 8.5), Pair.of(12.5, 16.5), Pair.of(20.0, 25.0)
    );

    public static final RarityAttributes ETERNAL_ATTRIBUTES = new RarityAttributes(
        Pair.of(100.0, 120.0), Pair.of(25.0, 40.0), Pair.of(8.5, 11.5), Pair.of(16.5, 22.5), Pair.of(25.0, 45.0)
    );
}