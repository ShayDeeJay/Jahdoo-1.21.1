package org.jahdoo.ascension.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jahdoo.ascension.attachments.AbstractAttachment;

public class InstanceData implements AbstractAttachment {

    private int mobs;
    private double health;
    private double speed;

    public InstanceData(){}

    public InstanceData(int mobs){
        this.mobs = mobs;
    }

    public static final InstanceData DEFAULT = new InstanceData(5);

    public void incrementMobs(int mobs){
        this.mobs += mobs;
    }

    public int getMobs() {
        return mobs;
    }

    public void incrementHealth(double health){
        this.health += health;
    }

    public void incrementSpeed(double speed){
        this.speed += speed;
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putDouble("health", health);
        nbt.putDouble("speed", speed);
        nbt.putInt("mobs", mobs);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        health = nbt.getDouble("health");
        speed = nbt.getDouble("speed");
        mobs = nbt.getInt("mobs");
    }

    @Override
    public String toString() {
        return
            "Level Data: " + "\n" +
            "Mob Count: " + mobs + "\n" +
            "Mob Health: " + health + "\n" +
            "Mob Speed: " + speed;
    }
}
