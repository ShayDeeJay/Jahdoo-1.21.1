package org.jahdoo.block.challange_altar;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

import static org.jahdoo.utils.ModHelpers.Random;

public class RoundGenerator {
//    private final List<LivingEntity> registerMobs = new ArrayList<>();
//    private final List<LivingEntity> getActiveMobs = new ArrayList<>();
    private final int round;

    public RoundGenerator(int round){
        this.round = round;
    }


    public int getMaxAllowedMobsOnMap() {
        return Math.min(2, Math.min(getRound() / 2, 30));
    }

    public int getMaxAllowedMobsPerTrial() {
        return round * 3 + Random.nextInt(Math.max(round/5, 3));
    }

    public int getRound() {
        return round;
    }

}
