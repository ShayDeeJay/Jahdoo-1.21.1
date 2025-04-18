package org.jahdoo.ascension.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

import static org.jahdoo.ascension.utils.Helpers.*;

public class InstanceData implements IAttachment {

    // Constants for all keys used in the values map
    public static final String KEY_TICKS = "ticks";
    public static final String KEY_HORDE = "horde";
    public static final String KEY_SKELETON = "skeleton";
    public static final String KEY_ETERNAL_WIZARD = "eternal_wizard";
    public static final String KEY_VOID_SPIDER = "void_spider";
    public static final String KEY_INFERNO_CREEPER = "inferno_creeper";
    public static final String KEY_CLEARED_ROOMS = "cleared_rooms";
    public static final String KEY_MAX_TIME = "max_time";
    public static final String KEY_BRONZE_COIN = "bronze_coin";
    public static final String KEY_SILVER_COIN = "silver_coin";
    public static final String KEY_GOLD_COIN = "gold_coin";
    public static final String KEY_PLATINUM_COIN = "platinum_coin";
    public static final String KEY_HEALTH = "health";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_ARMOR = "armor";
    public static final String KEY_ATTACK_DAMAGE = "attack_damage";
    public static final String KEY_EXPERIENCE = "expereience";

    private final Map<String, Double> values = new HashMap<>();
    private String difficulty;

    public InstanceData() {}

    public InstanceData(String difficulty, Map<String, Double> values) {
        this.difficulty = difficulty;
        this.values.putAll(values);
    }

    // Core map access

    public Map<String, Double> getInstance(){
        return this.values;
    }

    private double get(String key) {
        return values.getOrDefault(key, 0.0);
    }

    private void set(String key, double value) {
        values.put(key, value);
    }

    private void increment(String key, double amount) {
        set(key, get(key) + amount);
    }

    // Original getters
    public String getDifficulty() {
        return difficulty == null ? "" : difficulty;
    }

    public int getTicks() {
        return (int) get(KEY_TICKS);
    }

    public int getHorde() {
        return (int) get(KEY_HORDE);
    }

    public int getSkeleton() {
        return (int) get(KEY_SKELETON);
    }

    public int getEternalWizard() {
        return (int) get(KEY_ETERNAL_WIZARD);
    }

    public int getVoidSpider() {
        return (int) get(KEY_VOID_SPIDER);
    }

    public int getInfernoCreeper() {
        return (int) get(KEY_INFERNO_CREEPER);
    }

    public int getClearedRooms() {
        return (int) get(KEY_CLEARED_ROOMS);
    }

    public int getMaxTime() {
        return (int) get(KEY_MAX_TIME);
    }

    public int getBronzeCoin() {
        return (int) get(KEY_BRONZE_COIN);
    }

    public int getSilverCoin() {
        return (int) get(KEY_SILVER_COIN);
    }

    public int getGoldCoin() {
        return (int) get(KEY_GOLD_COIN);
    }

    public int getExperience() {
        return (int) get(KEY_EXPERIENCE);
    }

    public double getHealth() {
        return get(KEY_HEALTH);
    }

    public double getSpeed() {
        return get(KEY_SPEED);
    }

    public double getArmor() {
        return get(KEY_ARMOR);
    }

    public double getAttackDamage() {
        return get(KEY_ATTACK_DAMAGE);
    }

    // Original increment/setters
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void incrementSkeleton(double mobs) {
        increment(KEY_SKELETON, mobs);
    }

    public void incrementHorde(double mobs) {
        increment(KEY_HORDE, mobs);
    }

    public void incrementEternalWizard(double mobs) {
        increment(KEY_ETERNAL_WIZARD, mobs);
    }

    public void incrementVoidSpider(double mobs) {
        increment(KEY_VOID_SPIDER, mobs);
    }

    public void incrementInfernoCreeper(double mobs) {
        increment(KEY_INFERNO_CREEPER, mobs);
    }

    public void incrementHealth(double amount) {
        increment(KEY_HEALTH, amount);
    }

    public void incrementSpeed(double amount) {
        increment(KEY_SPEED, amount);
    }

    public void incrementArmor(double amount) {
        increment(KEY_ARMOR, amount);
    }

    public void incrementAttackDamage(double amount) {
        increment(KEY_ATTACK_DAMAGE, amount);
    }

    public void incrementClearedRooms() {
        increment(KEY_CLEARED_ROOMS, 1);
    }

    public void incrementTicks() {
        increment(KEY_TICKS, 1);
    }

    public void setMaxTime(int maxTime) {
        increment(KEY_MAX_TIME, maxTime);
    }

    public void setBronzeCoin(int bronzeCoin) {
        increment(KEY_BRONZE_COIN, bronzeCoin);
    }

    public void setExperience(int experience) {
        increment(KEY_EXPERIENCE, experience);
    }

    public void setSilverCoin(int silverCoin) {
        increment(KEY_SILVER_COIN, silverCoin);
    }

    public void setGoldCoin(int goldCoin) {
        increment(KEY_GOLD_COIN, goldCoin);
    }

    public void setPlatinumCoin(int goldCoin) {
        increment(KEY_PLATINUM_COIN, goldCoin);
    }

    public static InstanceData setEasyData() {
        InstanceData data = new InstanceData();
        data.setDifficulty(EASY);
        data.incrementHorde(5);
        data.setMaxTime(24000);
        return data;
    }

    public static InstanceData setMediumData() {
        InstanceData data = new InstanceData();
        data.setDifficulty(MEDIUM);
        data.incrementHorde(10);
        data.incrementHealth(10);
        data.incrementAttackDamage(15);
        data.setMaxTime(18000);
        return data;
    }

    public static InstanceData setHardData() {
        InstanceData data = new InstanceData();
        data.setDifficulty(HARD);
        data.incrementHorde(10);
        data.incrementSkeleton(10);
        data.incrementHealth(100);
        data.incrementAttackDamage(100);
        data.incrementSpeed(15);
        data.setMaxTime(12000);
        return data;
    }

    public static InstanceData copyInstance(InstanceData original) {
        return new InstanceData(original.difficulty, original.values);
    }

    public static final Codec<InstanceData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("Difficulty").forGetter(InstanceData::getDifficulty),
            Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("Data").forGetter(run -> run.values)
        ).apply(instance, InstanceData::new)
    );

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putString("difficulty", difficulty);
        var valuesTag = new CompoundTag();
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            valuesTag.putDouble(entry.getKey(), entry.getValue());
        }

        nbt.put("Values", valuesTag);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        difficulty = nbt.getString("difficulty");
        values.clear();
        if (nbt.contains("Values")) {
            CompoundTag valuesTag = nbt.getCompound("Values");
            for (String key : valuesTag.getAllKeys()) {
                values.put(key, valuesTag.getDouble(key));
            }
        }
    }

    @Override
    public String toString() {
        return
            "Level Data: \n" +
                "Difficulty = " + difficulty + "\n" +
                "Ticks = " + getTicks() + "\n" +
                "Max Time = " + getMaxTime() + "\n" +
                "Bronze Coins = " + getBronzeCoin() + "\n" +
                "Silver Coins = " + getSilverCoin() + "\n" +
                "Gold Coins = " + getGoldCoin() + "\n" +
                "Completed Rooms = " + getClearedRooms() + "\n" +
                "Horde = " + getHorde() + "\n" +
                "Skeletons = " + getSkeleton() + "\n" +
                "Eternal Wizard = " + getEternalWizard() + "\n" +
                "Void Spider = " + getVoidSpider() + "\n" +
                "Inferno Creeper = " + getInfernoCreeper() + "\n" +
                "Mob Health = " + getHealth() + "\n" +
                "Mob Speed = " + getSpeed() + "\n" +
                "Mob Attack Damage = " + getAttackDamage() + "\n" +
                "Mob Armor = " + getArmor();
    }
}


