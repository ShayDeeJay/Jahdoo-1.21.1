package org.jahdoo.trial_nexus.ability.skills;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;

public class ClimberSkill extends AbstractSkill {

    public static final String CLIMBER = "climber";

    @Override
    public String id() {
        return CLIMBER;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.CLIMBER;
    }

    @Override
    public int unlockCost() {
        return 3;
    }

    @Override
    public int levelRequirement() {
        return 15;
    }

    @Override
    public String description() {
        return "Your legs can now extend further, letting you scale higher obstacles and climb multiple blocks at once.";
    }

}