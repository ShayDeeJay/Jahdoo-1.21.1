package org.jahdoo.trial_nexus.magic.skills;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Icons;

public class LiquistepSkill extends AbstractSkill {

    public static final String LIQUISTEP = "liquistep";

    @Override
    public String id() {
        return LIQUISTEP;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.LIQUISTEP;
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