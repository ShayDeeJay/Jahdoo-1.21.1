package org.jahdoo.ascension.ability.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.EffectReg;

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
    public Holder<MobEffect> skillEffect() {
        return EffectReg.REBOUND;
    }

    @Override
    public int unlockCost() {
        return 8;
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