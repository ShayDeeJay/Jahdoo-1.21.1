package org.jahdoo.ascension.boon;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.List;
import java.util.function.Consumer;

import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;

public class LevelBoonSelection {


    public static LevelBoon attributeBoon(int index, Component component){
        return new LevelBoon(component, -1, index , BoonSelection.iconFromEffect(MobEffects.HEAL));
    }

    public static void incrementHealth(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementHealth(percentageMultiplier);
    }

    public static void incrementSpeed(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementSpeed(percentageMultiplier);
    }

    public static void incrementMobs(Level level, double percentageMultiplier){
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        data.incrementMobs((int) percentageMultiplier);
    }

    public static void getRun(Level level, double value, int index){
        List<Runnable> exc = List.of(
            () -> incrementHealth(level, value),
            () -> incrementSpeed(level, value),
            () -> incrementMobs(level, value)
        );
        exc.get(index).run();
    }

    public static LevelBoon getRandomBoon(double value){
        var roundValue = Maths.doubleFormattedDouble(value);
        var levelBoons = List.of(
            attributeBoon(0, withStyleComponent("+" + roundValue + "% Mob Health", MAGNET_STRENGTH_RED)),
            attributeBoon(1, withStyleComponent("+" + roundValue + "% Mob Speed", MAGNET_STRENGTH_RED)),
            attributeBoon(2, withStyleComponent("+" + (int) value + " Mobs", MAGNET_STRENGTH_RED))
        );

        return listRandom(levelBoons);
    }

}
