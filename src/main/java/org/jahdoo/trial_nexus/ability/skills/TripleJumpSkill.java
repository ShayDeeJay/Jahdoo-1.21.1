package org.jahdoo.trial_nexus.ability.skills;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;

public class TripleJumpSkill extends AbstractSkill {
    public static final String TRIPLE_JUMP = "triple_jump";

    @Override
    public String id() {
        return TRIPLE_JUMP;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.TRIPLE_JUMP;
    }

    @Override
    public int unlockCost() {
        return 14;
    }

    @Override
    public int levelRequirement() {
        return 45;
    }

    @Override
    public String description() {
        return "By harnessing ethereal power, you can summon brief platforms beneath your feet, allowing you to jump up to three times in midair.";
    }

}