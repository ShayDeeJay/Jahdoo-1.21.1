package org.jahdoo.ascension.ability;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.ComponentReg;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;

import static org.jahdoo.ascension.utils.Maths.doubleFormattedDouble;

public class AbilityBuilder {

    //Mandatory mods
    public static final String MANA_COST = "Mana Cost";
    public static final String COOLDOWN = "Cooldown Duration";

    //Optional mods
    public static final String DAMAGE = "Damage";
    public static final String EFFECT_CHANCE = "Effect Apply Chance";
    public static final String EFFECT_DURATION = "Effect Duration";
    public static final String EFFECT_STRENGTH = "Effect Strength";
    public static final String RANGE = "Range";
    public static final String CASTING_DISTANCE = "Cast Distance";
    public static final String SET_ELEMENT_TYPE = "Element Type";
    public static final String LIFETIME = "Duration";
    public static final String AOE = "Area of Effect";
    public static final String SIZE = "Block Size";
    public static final String OFFSET = "Offset";
    public static final String GRAVITATIONAL_PULL = "Gravitational Pull";
    public static final String SHOT_MULTIPLIER = "Shot Multiplier";
    public static final String VELOCITY = "Projectile Velocity";
    public static final String NUMBER_OF_RICOCHET = "Ricochets";
    private ItemStack item = null;
    private final String abilityId;
    private final AbilityData abilityData = new AbilityData(new LinkedHashMap<>());

    /**
     * If you null ItemStack then you should only buildAndReturn()
     * **/
    public AbilityBuilder(@Nullable ItemStack itemStack, String abilityId) {
        this.item = itemStack;
        this.abilityId = abilityId;
    }

    public AbilityBuilder(String abilityId) {
        this.abilityId = abilityId;
    }

    public AbilityBuilder setDamage(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(DAMAGE, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder setDamageWithValue(double high, double low, double actualValue){
        this.setModifier(DAMAGE, high, low, true, actualValue);
        return this;
    }

    public AbilityBuilder setGravitationalPullWithValue(double high, double low, double actualValue){
        this.setModifier(GRAVITATIONAL_PULL, high, low, true, actualValue);
        return this;
    }

    public AbilityBuilder setGravitationalPull(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(GRAVITATIONAL_PULL, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder setStaticMana(double value){
        this.setModifierWithoutBounds(MANA_COST, value);
        return this;
    }

    public AbilityBuilder setMana(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(MANA_COST, high, low, false, step, baseCost);
        return this;
    }

    public AbilityBuilder setBlockSize(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(SIZE, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder setManaWithValue(double high, double low, double value){
        this.setModifier(MANA_COST, high, low, false, value);
        return this;
    }

    public AbilityBuilder setStaticCooldown(double value){
        this.setModifierWithoutBounds(COOLDOWN, value);
        return this;
    }

    public AbilityBuilder setCooldown(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(COOLDOWN, high, low, false, step, baseCost);
        return this;
    }

    public AbilityBuilder setCooldownWithValue(double high, double low, double value){
        this.setModifier(COOLDOWN, high, low, false, value);
        return this;
    }

    public AbilityBuilder setEffectStrength(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(EFFECT_STRENGTH, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder setEffectStrengthWithValue(double high, double low, double value){
        this.setModifier(EFFECT_STRENGTH, high, low, false, value);
        return this;
    }

    public AbilityBuilder setEffectDuration(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(EFFECT_DURATION, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder setEffectDurationWithValue(double high, double low, double value){
        this.setModifier(EFFECT_DURATION, high, low, false, value);
        return this;
    }

    public AbilityBuilder setEffectChance(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(EFFECT_CHANCE, high, low, false, step, baseCost);
        return this;
    }

    public AbilityBuilder shotMultiplier(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(SHOT_MULTIPLIER, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder shotVelocity(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(VELOCITY, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder ricochets(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(NUMBER_OF_RICOCHET, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder setEffectChanceWithValue(double high, double low, double value){
        this.setModifier(EFFECT_CHANCE, high, low, false, value);
        return this;
    }

    public AbilityBuilder setCastingDistance(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(CASTING_DISTANCE, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder setRange(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(RANGE, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder setAoe(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(AOE, high, low, true, step, baseCost);
        return this;
    }

    public AbilityBuilder setLifetime(double high, double low, double step, double baseCost){
        this.setAbilityTagModifiersRandom(LIFETIME, high, low, true, step, baseCost);
        return this;
    }

    public AbilityHolder buildAndReturn(){
        return new AbilityHolder(abilityId, abilityData);
    }

    public void build() {
        if(this.item != null){
            this.item.set(ComponentReg.ABILITY_HOLDER.get(), new AbilityHolder(abilityId, abilityData));
        }
    }

    public AbilityBuilder setElement(double actualValue) {
        var abilityModifiers = new AbilityData.AbilityModifiers(actualValue, 0, 0, 0, actualValue, 0, false);
        this.abilityData.abilityProperties().put(SET_ELEMENT_TYPE, abilityModifiers);
        return this;
    }

    public AbilityBuilder setModifier(String name, double high, double low, boolean isHigherBetter, double actualValue) {
        var abilityModifiers = new AbilityData.AbilityModifiers(actualValue, high, low, 0, actualValue, 0, isHigherBetter);
        this.abilityData.abilityProperties().put(name, abilityModifiers);
        return this;
    }

    public AbilityBuilder setModifierWithStep(String name, double high, double low, boolean isHigherBetter, double actualValue, double step, double baseCost) {
        var abilityModifiers = new AbilityData.AbilityModifiers(actualValue, high, low, step, actualValue, baseCost, isHigherBetter);
        this.abilityData.abilityProperties().put(name, abilityModifiers);
        return this;
    }

    public AbilityBuilder setModifierWithStepSet(String name, double high, double low, boolean isHigherBetter, double actualValue, double setValue, double step, double baseCost) {
        var abilityModifiers = new AbilityData.AbilityModifiers(actualValue, high, low, step, setValue,  baseCost, isHigherBetter);
        this.abilityData.abilityProperties().put(name, abilityModifiers);
        return this;
    }
    public AbilityBuilder setModifierWithoutBounds(String name, double actualValue) {
        var abilityModifiers = new AbilityData.AbilityModifiers(actualValue, -1, -1, 0, actualValue, 0, true);
        this.abilityData.abilityProperties().put(name, abilityModifiers);
        return this;
    }

    public AbilityBuilder setAbilityTagModifiersRandom(String name, double high, double low, boolean isHigherBetter, double step, double baseCost) {
        var getValue = isHigherBetter ? low : high;
        var chosenR = doubleFormattedDouble(getValue);
        var highR = doubleFormattedDouble(high);
        var lowR = doubleFormattedDouble(low);
        var stepR = doubleFormattedDouble(step);
        var abilityModifiers = new AbilityData.AbilityModifiers(chosenR, highR, lowR, stepR, chosenR, baseCost, isHigherBetter);
        this.abilityData.abilityProperties().put(name, abilityModifiers);
        return this;
    }


}
