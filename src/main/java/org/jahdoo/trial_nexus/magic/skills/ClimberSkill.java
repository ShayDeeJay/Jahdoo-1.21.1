package org.jahdoo.trial_nexus.magic.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.trial_nexus.utils.Icons;

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
        return 2;
    }

    @Override
    public int levelRequirement() {
        return 15;
    }

    @Override
    public String description() {
        return "Your legs can now extend further, letting you scale higher obstacles and climb multiple blocks at once.";
    }

    @Override
    public Holder<Attribute> attributeModifier() {
        return AttributeReg.CLIMBER;
    }
}