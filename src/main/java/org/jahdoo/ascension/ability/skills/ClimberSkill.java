package org.jahdoo.ascension.ability.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.EffectReg;

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
    public Holder<MobEffect> skillEffect() {
        return EffectReg.CLIMBER;
    }

    @Override
    public int unlockCost() {
        return 3;
    }

    @Override
    public int levelRequirement() {
        return 5;
    }

    @Override
    public String description() {
        return "Your legs can now extend further, letting you scale higher obstacles and climb multiple blocks at once.";
    }

}