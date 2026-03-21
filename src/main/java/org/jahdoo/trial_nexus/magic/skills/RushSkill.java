package org.jahdoo.trial_nexus.magic.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.trial_nexus.utils.Icons;

public class RushSkill extends AbstractSkill {

    public static final Integer MANA_COST = 30;
    public static final String RUSH = "rush";

    @Override
    public String id() {
        return RUSH;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.RUSH;
    }

    @Override
    public int unlockCost() {
        return 25;
    }

    @Override
    public int levelRequirement() {
        return 30;
    }

    @Override
    public String description() {
        return "Tap into divine energy to defy gravity itself. You can now levitate freely, gliding through the air at the cost of mana.";
    }

    @Override
    public Holder<Attribute> attributeModifier() {
        return AttributeReg.RUSH;
    }
}