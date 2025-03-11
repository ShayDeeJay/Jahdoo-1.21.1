package org.jahdoo.ascension.boon;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.boon.BoonSelection.iconFromEffect;
import static org.jahdoo.ascension.utils.ColourStore.MAGNET_STRENGTH_RED;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.Maths.doubleFormattedDouble;

public class LevelBoonSelection {


    public static LevelBoon attributeBoon(int index, Component component, ResourceLocation location, double value){
        return new LevelBoon(component, -1, index , location, value);
    }

    public static void incrementHealth(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementHealth(percentageMultiplier);
    }

    public static void incrementSpeed(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementSpeed(percentageMultiplier);
    }

    public static void incrementAttackDamage(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementAttackDamage(percentageMultiplier);
    }

    public static void incrementArmor(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementArmor(percentageMultiplier);
    }

    public static void incrementZombies(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementZombie((int) percentageMultiplier);
    }

    public static void incrementSkeleton(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementSkeleton((int) percentageMultiplier);
    }

    public static void incrementEternalWizard(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementEternalWizard((int) percentageMultiplier);
    }

    public static void getRun(Level level, double value, int index){
        List<Runnable> exc = List.of(
            () -> incrementHealth(level, value),
            () -> incrementSpeed(level, value),
            () -> incrementZombies(level, value),
            () -> incrementAttackDamage(level, value),
            () -> incrementArmor(level, value),
            () -> incrementSkeleton(level, value),
            () -> incrementEternalWizard(level, value)
        );
        exc.get(index).run();
    }

    public static LevelBoon getRandomBoon(){
        var getRarity = JahdooRarity.getRarity().getAttributes();
        var mobs = getRarity.getRandomMaxAbsorption();
        var mobHealth = doubleFormattedDouble(getRarity.getRandomManaReduction());
        var mobSpeed = doubleFormattedDouble(getRarity.getRandomCooldown());
        var mobDamage = doubleFormattedDouble(getRarity.getRandomMaxHealth());
        var mobArmor = doubleFormattedDouble(getRarity.getRandomManaRegen());
        var boonCollection = new ArrayList<LevelBoon>();

        boonCollection.add(attributeBoon(0, withStyleComponent("+" + mobHealth + "% Mob Health", MAGNET_STRENGTH_RED), iconFromEffect(MobEffects.REGENERATION), mobHealth));
        boonCollection.add(attributeBoon(1, withStyleComponent("+" + mobSpeed + "% Mob Speed", MAGNET_STRENGTH_RED), iconFromEffect(MobEffects.MOVEMENT_SPEED), mobSpeed));
        boonCollection.add(attributeBoon(2, withStyleComponent("+" + mobs + " Zombie", MAGNET_STRENGTH_RED), Icons.ZOMBIE, mobs));
        boonCollection.add(attributeBoon(3, withStyleComponent("+" + mobDamage + "% Mob Damage", MAGNET_STRENGTH_RED), iconFromEffect(MobEffects.DAMAGE_BOOST), mobDamage));
        boonCollection.add(attributeBoon(4, withStyleComponent("+" + mobArmor + "% Mob Armor", MAGNET_STRENGTH_RED), iconFromEffect(MobEffects.DAMAGE_RESISTANCE), mobArmor));
        boonCollection.add( attributeBoon(5, withStyleComponent("+" + mobs + " Skeleton", MAGNET_STRENGTH_RED), Icons.SKELETON, mobs));

        if(Random.nextInt(5) == 0){
            boonCollection.add(attributeBoon(6, withStyleComponent("+" + mobs + " Eternal Wizard", MAGNET_STRENGTH_RED), Icons.ETERNAL_WIZARD, mobs));
        }

        return listRandom(boonCollection);
    }

}
