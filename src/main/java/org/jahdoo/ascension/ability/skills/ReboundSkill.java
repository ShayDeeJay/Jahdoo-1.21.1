package org.jahdoo.ascension.ability.skills;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.EffectReg;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.ability.skills.ClimberSkill.CLIMBER;

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
    public List<Component> tooltip() {
        var components = new ArrayList<Component>();

//        components.add(Helpers.withStyleComponent(Helpers.stringIdToName(id()), ColourStore.HEADER_COLOUR));
//        components.add(Helpers.withStyleComponent(description(), ColourStore.AETHER_BLUE));

        return components;
    }

    @Override
    public int unlockCost() {
        return 8;
    }

    @Override
    public String dependency() {
        return CLIMBER;
    }

    @Override
    public int levelRequirement() {
        return 5;
    }

    @Override
    public String description() {
        return "Removes all fall damage by imbuing your body with a spell that alters your form, giving it a rubber-like resilience that absorbs impact.";
    }

}