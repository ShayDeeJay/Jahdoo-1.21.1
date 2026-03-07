package org.jahdoo.trial_nexus.magic.skills;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Icons;

public class BlinkSkill extends AbstractSkill {

    public static final String BLINK = "blink";

    @Override
    public String id() {
        return BLINK;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.BLINK;
    }

    @Override
    public int unlockCost() {
        return 10;
    }

    @Override
    public int levelRequirement() {
        return 30;
    }

    @Override
    public String description() {
        return "Tap into divine energy to defy gravity itself. You can now levitate freely, gliding through the air at the cost of mana.";
    }

}