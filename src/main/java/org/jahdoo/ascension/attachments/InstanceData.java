package org.jahdoo.ascension.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class InstanceData implements IAttachment {

    private int horde;
    private int skeleton;
    private int eternalWizard;
    private int voidSpider;
    private int clearedRooms;
    private int ticks;
    private int maxTime;
    private int bronzeCoin;
    private int silverCoin;
    private int goldCoin;
    private double health;
    private double speed;
    private double armor;
    private double attackDamage;

    public InstanceData() {}

    public InstanceData(
        int ticks,
        int horde,
        int skeleton,
        int eternalWizard,
        int voidSpider,
        int clearedRooms,
        int maxTime,
        int bronzeCoin,
        int silverCoin,
        int goldCoin,
        double health,
        double speed,
        double armor,
        double attackDamage
    ) {
        this.ticks = ticks;
        this.horde = horde;
        this.skeleton = skeleton;
        this.eternalWizard = eternalWizard;
        this.voidSpider = voidSpider;
        this.clearedRooms = clearedRooms;
        this.maxTime = maxTime;
        this.health = health;
        this.speed = speed;
        this.armor = armor;
        this.attackDamage = attackDamage;
        this.bronzeCoin = bronzeCoin;
        this.silverCoin = silverCoin;
        this.goldCoin = goldCoin;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getEternalWizard() {
        return eternalWizard;
    }

    public int getSkeleton() {
        return skeleton;
    }

    public int getHorde() {
        return horde;
    }

    public int getVoidSpiders() {
        return voidSpider;
    }

    public double getHealthMultiplier() {
        return health;
    }

    public double getSpeedMultiplier() {
        return speed;
    }

    public double getArmorMultiplier() {
        return armor;
    }

    public double getAttackDamageMultiplier() {
        return attackDamage;
    }

    public int getClearedRooms() {
        return this.clearedRooms;
    }

    public int getVoidSpider() {
        return voidSpider;
    }

    public double getHealth() {
        return health;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public double getArmor() {
        return armor;
    }

    public int getTicks() {
        return ticks;
    }

    public int getBronzeCoin() {
        return bronzeCoin;
    }

    public int getSilverCoin() {
        return silverCoin;
    }

    public int getGoldCoin() {
        return goldCoin;
    }

    public void incrementSkeleton(double mobs) {
        this.skeleton += (int) mobs;
    }

    public void incrementHorde(double mobs) {
        this.horde += (int) mobs;
    }

    public void incrementEternalWizard(double mobs) {
        this.eternalWizard += (int) mobs;
    }

    public void incrementVoidSpider(double mobs) {
        this.voidSpider += (int) mobs;
    }

    public void incrementHealth(double health) {
        this.health += health;
    }

    public void incrementSpeed(double speed) {
        this.speed += speed;
    }

    public void incrementArmor(double armor) {
        this.armor += armor;
    }

    public void incrementAttackDamage(double attackDamage) {
        this.attackDamage += attackDamage;
    }

    public void incrementClearedRooms() {
        this.clearedRooms++;
    }

    public void incrementTicks() {
        this.ticks++;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime += maxTime;
    }

    public void setBronzeCoin(int bronzeCoin) {
        this.bronzeCoin += bronzeCoin;
    }

    public void setSilverCoin(int silverCoin) {
        this.silverCoin += silverCoin;
    }

    public void setGoldTime(int goldCoin) {
        this.goldCoin += goldCoin;
    }

    public static InstanceData setBaseData() {
        var data = new InstanceData();
        data.incrementHorde(5);

        //Current default is 15 Minutes
        data.setMaxTime(18000);
        return data;
    }

    public static final Codec<InstanceData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("ticks").forGetter(InstanceData::getTicks),
            Codec.INT.fieldOf("horde").forGetter(InstanceData::getHorde),
            Codec.INT.fieldOf("skeleton").forGetter(InstanceData::getSkeleton),
            Codec.INT.fieldOf("eternal_wizard").forGetter(InstanceData::getEternalWizard),
            Codec.INT.fieldOf("void_spider").forGetter(InstanceData::getVoidSpider),
            Codec.INT.fieldOf("cleared_rooms").forGetter(InstanceData::getClearedRooms),
            Codec.INT.fieldOf("max_time").forGetter(InstanceData::getMaxTime),
            Codec.INT.fieldOf("bronze_coin").forGetter(InstanceData::getBronzeCoin),
            Codec.INT.fieldOf("silver_coin").forGetter(InstanceData::getSilverCoin),
            Codec.INT.fieldOf("gold_coin").forGetter(InstanceData::getGoldCoin),
            Codec.DOUBLE.fieldOf("health").forGetter(InstanceData::getHealth),
            Codec.DOUBLE.fieldOf("speed").forGetter(InstanceData::getSpeed),
            Codec.DOUBLE.fieldOf("armor").forGetter(InstanceData::getArmor),
            Codec.DOUBLE.fieldOf("attack_damage").forGetter(InstanceData::getAttackDamage)
        ).apply(instance, InstanceData::new)
    );

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putDouble("health", health);
        nbt.putDouble("speed", speed);
        nbt.putDouble("armor", armor);
        nbt.putDouble("damage", attackDamage);
        nbt.putInt("horde", horde);
        nbt.putInt("skeleton", skeleton);
        nbt.putInt("eternal_wizard", eternalWizard);
        nbt.putInt("void_spider", voidSpider);
        nbt.putInt("cleared_rooms", clearedRooms);
        nbt.putInt("tick", ticks);
        nbt.putInt("max_time", maxTime);
        nbt.putInt("bronze_coin", bronzeCoin);
        nbt.putInt("silver_coin", silverCoin);
        nbt.putInt("gold_coin", goldCoin);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        speed = nbt.getDouble("speed");
        armor = nbt.getDouble("armor");
        health = nbt.getDouble("health");
        attackDamage = nbt.getDouble("damage");
        horde = nbt.getInt("horde");
        skeleton = nbt.getInt("skeleton");
        voidSpider = nbt.getInt("void_spider");
        eternalWizard = nbt.getInt("eternal_wizard");
        clearedRooms = nbt.getInt("cleared_rooms");
        ticks = nbt.getInt("tick");
        maxTime = nbt.getInt("max_time");
        bronzeCoin = nbt.getInt("bronze_coin");
        silverCoin = nbt.getInt("silver_coin");
        goldCoin = nbt.getInt("gold_coin");
    }

    @Override
    public String toString() {
        return
        "Level Data: " + "\n" +
        "Ticks = " + ticks + "\n" +
        "Max Time = " + maxTime + "\n" +
        "Bronze Coins = " + bronzeCoin + "\n" +
        "Silver Coins = " + silverCoin + "\n" +
        "Gold Coins = " + goldCoin + "\n" +
        "Completed Rooms = " + clearedRooms + "\n" +
        "Horde = " + horde + "\n" +
        "Skeletons = " + skeleton + "\n" +
        "Eternal Wizard = " + eternalWizard + "\n" +
        "Void Spider = " + voidSpider + "\n" +
        "Mob Health = " + health + "\n" +
        "Mob Speed = " + speed + "\n" +
        "Mob Attack Damage = " + attackDamage + "\n" +
        "Mob Armor = " + armor;
    }
}

