package org.jahdoo.common.entities.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jahdoo.common.entities.inferno_creeper.InfernoCreeper;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SwellGoal extends Goal {
    private final InfernoCreeper creeper;
    @Nullable
    private LivingEntity target;

    public SwellGoal(InfernoCreeper creeper) {
        this.creeper = creeper;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        LivingEntity livingentity = this.creeper.getTarget();
        return this.creeper.getSwellDir() > 0 || livingentity != null && this.creeper.distanceToSqr(livingentity) < (double)9.0F;
    }

    public void start() {
        this.creeper.getNavigation().stop();
        this.target = this.creeper.getTarget();
    }

    public void stop() {
        this.target = null;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        if (this.target == null) {
            this.creeper.setSwellDir(-1);
        } else if (this.creeper.distanceToSqr(this.target) > (double)49.0F) {
            this.creeper.setSwellDir(-1);
        } else if (!this.creeper.getSensing().hasLineOfSight(this.target)) {
            this.creeper.setSwellDir(-1);
        } else {
            this.creeper.setSwellDir(1);
        }

    }
}