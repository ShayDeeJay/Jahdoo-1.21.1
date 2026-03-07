package org.jahdoo.trial_nexus.magic.skills;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Icons;

public class MageFlightSkill extends AbstractSkill {

    public static final String MAGE_FLIGHT = "mage_flight";

    @Override
    public String id() {
        return MAGE_FLIGHT;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.MAGE_FLIGHT;
    }

    @Override
    public int unlockCost() {
        return 40;
    }

    @Override
    public int levelRequirement() {
        return 50;
    }

    @Override
    public String description() {
        return "Tap into divine energy to defy gravity itself. You can now levitate freely, gliding through the air at the cost of mana.";
    }

}