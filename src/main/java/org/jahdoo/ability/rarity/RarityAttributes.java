package org.jahdoo.ability.rarity;
import com.mojang.datafixers.util.Pair;
import static org.jahdoo.utils.ModHelpers.Random;

public class RarityAttributes {
    private final Pair<Double, Double> manaPool;
    private final Pair<Double, Double> manaRegen;
    private final Pair<Double, Double> cooldown;
    private final Pair<Double, Double> manaReduction;
    private final Pair<Double, Double> damage;
    private final Pair<Integer, Integer> refinementPotential;
    private final Pair<Double, Double> healChance;
    private final Pair<Integer, Integer> maxHealth;
    private final Pair<Integer, Integer> maxAbsorption;

    public RarityAttributes(
        Pair<Double, Double> manaPool,
        Pair<Double, Double> manaRegen,
        Pair<Double, Double> cooldown,
        Pair<Double, Double> manaReduction,
        Pair<Double, Double> damage,
        Pair<Integer, Integer> refinementPotential,
        Pair<Double, Double> healChance,
        Pair<Integer, Integer> maxHealth,
        Pair<Integer, Integer> maxAbsorption
    ) {
        this.manaPool = manaPool;
        this.manaRegen = manaRegen;
        this.cooldown = cooldown;
        this.manaReduction = manaReduction;
        this.damage = damage;
        this.refinementPotential = refinementPotential;
        this.healChance = healChance;
        this.maxHealth = maxHealth;
        this.maxAbsorption = maxAbsorption;
    }

    public double getRandomManaPool() {
        return getRandomDouble(manaPool);
    }

    public double getRandomManaRegen() {
        return getRandomDouble(manaRegen);
    }

    public double getRandomCooldown() {
        return getRandomDouble(cooldown);
    }

    public double getRandomManaReduction() {
        return getRandomDouble(manaReduction);
    }

    public double getRandomDamage() {
        return getRandomDouble(damage);
    }

    public double getRandomHealChance() {
        return getRandomDouble(healChance);
    }

    public int getRandomRefinementPotential() {
        return getRandomInteger(refinementPotential);
    }

    public int getRandomMaxHealth() {
        return getRandomInteger(maxHealth);
    }

    public int getRandomMaxAbsorption() {
        return getRandomInteger(maxAbsorption);
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

    public Pair<Double, Double> getHealChanceRange() {
        return healChance;
    }

    public Pair<Integer, Integer> getRefinementPotentialRange() {
        return refinementPotential;
    }

    public Pair<Integer, Integer> getMaxHealthRange() {
        return maxHealth;
    }

    public Pair<Integer, Integer> getMaxAbsorptionRange() {
        return maxAbsorption;
    }

    private double getRandomDouble(Pair<Double, Double> range) {
        return Random.nextDouble(range.getFirst(), range.getSecond());
    }

    private int getRandomInteger(Pair<Integer, Integer> range) {
        return Random.nextInt(range.getFirst(), range.getSecond());
    }

    public static final RarityAttributes COMMON_ATTRIBUTES =
        new RarityAttributes(
            Pair.of(10.0, 20.0),   // Mana Pool
            Pair.of(2.5, 5.0),    // Mana Regen
            Pair.of(0.5, 2.0),     // Cooldown
            Pair.of(1.0, 3.0),     // Mana Reduction
            Pair.of(3.0, 6.0),    // Damage
            Pair.of(50, 100),      // Refinement Potential
            Pair.of(1.0, 1.5),     // Heal Chance
            Pair.of(2, 4),         // Max Health
            Pair.of(1, 2)          // Max Absorption
        );

    public static final RarityAttributes RARE_ATTRIBUTES =
        new RarityAttributes(
            Pair.of(20.0, 30.0),   // Mana Pool
            Pair.of(5.0, 7.5),   // Mana Regen
            Pair.of(2.0, 3.5),     // Cooldown
            Pair.of(3.0, 5.0),     // Mana Reduction
            Pair.of(6.0, 9.0),   // Damage
            Pair.of(100, 150),     // Refinement Potential
            Pair.of(2.0, 2.5),     // Heal Chance
            Pair.of(5, 7),         // Max Health
            Pair.of(3, 4)          // Max Absorption
        );

    public static final RarityAttributes EPIC_ATTRIBUTES =
        new RarityAttributes(
            Pair.of(30.0, 40.0),   // Mana Pool
            Pair.of(7.5, 10.0),   // Mana Regen
            Pair.of(3.5, 5.0),     // Cooldown
            Pair.of(5.0, 7.0),    // Mana Reduction
            Pair.of(9.0, 12.0),   // Damage
            Pair.of(150, 200),     // Refinement Potential
            Pair.of(3.0, 3.5),     // Heal Chance
            Pair.of(8, 10),        // Max Health
            Pair.of(5, 6)          // Max Absorption
        );

    public static final RarityAttributes LEGENDARY_ATTRIBUTES =
        new RarityAttributes(
            Pair.of(40.0, 50.0),  // Mana Pool
            Pair.of(10.0, 12.5),   // Mana Regen
            Pair.of(5.0, 6.5),     // Cooldown
            Pair.of(7.0, 9.0),   // Mana Reduction
            Pair.of(12.0, 15.0),   // Damage
            Pair.of(200, 250),     // Refinement Potential
            Pair.of(4.0, 4.5),     // Heal Chance
            Pair.of(11, 13),       // Max Health
            Pair.of(7, 8)          // Max Absorption
        );

    public static final RarityAttributes ETERNAL_ATTRIBUTES =
        new RarityAttributes(
            Pair.of(50.0, 60.0), // Mana Pool
            Pair.of(12.5, 15.0),   // Mana Regen
            Pair.of(6.5, 8.0),    // Cooldown
            Pair.of(9.0, 12.0),   // Mana Reduction
            Pair.of(15.0, 18.0),   // Damage
            Pair.of(250, 300),     // Refinement Potential
            Pair.of(5.0, 5.5),     // Heal Chance
            Pair.of(14, 16),       // Max Health
            Pair.of(9, 10)         // Max Absorption
        );
}