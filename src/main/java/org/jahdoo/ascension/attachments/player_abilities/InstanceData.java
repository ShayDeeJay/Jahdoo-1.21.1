package org.jahdoo.ascension.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jahdoo.ascension.attachments.AbstractAttachment;

public class InstanceData implements AbstractAttachment {

    private int zombies;
    private int skeleton;
    private int eternalWizard;
    private double health;
    private double speed;
    private double armor;
    private double attackDamage;

    public static final InstanceData DEFAULT = new InstanceData(5);

    public InstanceData(){}

    public InstanceData(int zombie){
        this.zombies = zombie;
    }

    public int getEternalWizard(){
        return eternalWizard;
    }

    public int getSkeleton() {
        return skeleton;
    }

    public int getZombies() {
        return zombies;
    }

    public double getHealthMultiplier(){
        return health;
    }

    public double getSpeedMultiplier(){
        return speed;
    }

    public double getArmorMultiplier(){
        return armor;
    }

    public double getAttackDamageMultiplier(){
        return attackDamage;
    }

    public void incrementSkeleton(int mobs){
        this.skeleton += mobs;
    }

    public void incrementZombie(int mobs){
        this.zombies += mobs;
    }

    public void incrementEternalWizard(int mobs){
        this.eternalWizard += mobs;
    }

    public void incrementHealth(double health){
        this.health += health;
    }

    public void incrementSpeed(double speed){
        this.speed += speed;
    }

    public void incrementArmor(double armor){
        this.armor += armor;
    }

    public void incrementAttackDamage(double attackDamage){
        this.attackDamage += attackDamage;
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putDouble("health", health);
        nbt.putDouble("speed", speed);
        nbt.putDouble("armor", armor);
        nbt.putDouble("damage", attackDamage);
        nbt.putInt("zombies", zombies);
        nbt.putInt("skeleton", zombies);
        nbt.putInt("eternal_wizard", eternalWizard);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        health = nbt.getDouble("health");
        speed = nbt.getDouble("speed");
        armor = nbt.getDouble("armor");
        attackDamage = nbt.getDouble("damage");
        zombies = nbt.getInt("zombies");
        skeleton = nbt.getInt("skeleton");
        eternalWizard = nbt.getInt("eternal_wizard");
    }

    @Override
    public String toString() {
        return
        "Level Data: " + "\n" +
        "Zombie Count = " + zombies + "\n" +
        "Skeleton Count = " + skeleton + "\n" +
        "Eternal Wizard Count = " + eternalWizard + "\n" +
        "Mob Health = " + health + "\n" +
        "Mob Speed = " + speed + "\n" +
        "Mob Attack Damage = " + attackDamage + "\n" +
        "Mob Armor = " + armor;
    }
}
