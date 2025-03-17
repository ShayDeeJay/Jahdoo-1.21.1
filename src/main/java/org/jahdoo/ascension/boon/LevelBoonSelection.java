package org.jahdoo.ascension.boon;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.effect.MobEffects.*;
import static org.jahdoo.ascension.boon.BoonSelection.iconFromEffect;
import static org.jahdoo.ascension.rarity.JahdooRarity.*;
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
        data.incrementHorde(percentageMultiplier);
    }

    public static void incrementSkeleton(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementSkeleton(percentageMultiplier);
    }

    public static void incrementEternalWizard(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementEternalWizard(percentageMultiplier);
    }

    public static void incrementVoidSpider(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementVoidSpider(percentageMultiplier);
    }

    public static void getRun(Level level, double value, int index){
        List<Runnable> exc = List.of(
            () -> incrementHealth(level, value),
            () -> incrementSpeed(level, value),
            () -> incrementAttackDamage(level, value),
            () -> incrementArmor(level, value),
            () -> incrementZombies(level, value),
            () -> incrementSkeleton(level, value),
            () -> incrementVoidSpider(level, value),
            () -> incrementEternalWizard(level, value)
        );
        exc.get(index).run();
    }

    public static LevelBoon getRandomBoon(){
        var getRarity = getRarity().getAttributes();
        var boonCollection = new ArrayList<LevelBoon>();

        boonCollection.add(attributeTemplate(0, getRarity.getRandomManaReduction(), "Mob Health", REGENERATION));
        boonCollection.add(attributeTemplate(1, getRarity.getRandomCooldown(), "Mob Speed", MOVEMENT_SPEED));
        boonCollection.add(attributeTemplate(2, getRarity.getRandomMaxHealth(), "Mob Damage", DAMAGE_BOOST));
        boonCollection.add(attributeTemplate(3, getRarity.getRandomManaRegen(), "Mob Armor", DAMAGE_RESISTANCE));

        boonCollection.add(mobTemplate(4, "Horde", Icons.HORDE));

        if(Random.nextInt(2) == 0){
            boonCollection.add(mobTemplate(5, "Skeleton", Icons.SKELETON));
        }

        if(Random.nextInt(5) == 0){
            boonCollection.add(mobTemplate(6, "Void Spider", Icons.VOID_SPIDER));
        }

        if(Random.nextInt(7) == 0){
            boonCollection.add(mobTemplate(7, "Eternal Wizard", Icons.ETERNAL_WIZARD));
        }

        return listRandom(boonCollection);
    }

    public static LevelBoon mobTemplate(int id, String name, ResourceLocation icon){
        var mobs =  getRarity().getAttributes().getRandomMaxAbsorption();
        return attributeBoon(id, withStyleComponent("+" + mobs + " " + name, MAGNET_STRENGTH_RED), icon, mobs);
    }

    public static LevelBoon attributeTemplate(int id, double value, String name, Holder<MobEffect> effect){
        var formatted = doubleFormattedDouble(value);
        return attributeBoon(id, withStyleComponent("+" + formatted + "% " + name, MAGNET_STRENGTH_RED), iconFromEffect(effect), formatted);
    }

}
