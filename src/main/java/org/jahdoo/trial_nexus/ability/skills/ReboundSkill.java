package org.jahdoo.trial_nexus.ability.skills;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;

public class ReboundSkill extends AbstractSkill {

    public static final String REBOUND = "rebound";

    @Override
    public String id() {
        return REBOUND;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.REBOUND;
    }

    @Override
    public int unlockCost() {
        return 5;
    }

    @Override
    public int levelRequirement() {
        return 25;
    }

    @Override
    public String description() {
        return "Removes all fall damage by imbuing your body with a spell that alters your form, giving it a rubber-like resilience that absorbs impact.";
    }

}