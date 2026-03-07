package org.jahdoo.trial_nexus.magic.skills;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Icons;

public class DripWalkSkill extends AbstractSkill {

    public static final String DRIP_WALK = "drip_walk";

    @Override
    public String id() {
        return DRIP_WALK;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.DRIP_WALK;
    }

    @Override
    public int unlockCost() {
        return 20;
    }

    @Override
    public int levelRequirement() {
        return 55;
    }

    @Override
    public String description() {
        return "Your legs can now extend further, letting you scale higher obstacles and climb multiple blocks at once.";
    }

}