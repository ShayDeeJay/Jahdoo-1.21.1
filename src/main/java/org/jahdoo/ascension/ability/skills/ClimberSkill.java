package org.jahdoo.ascension.ability.skills;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.EffectReg;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.ability.Ability.NON;

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
        //TODO
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
        return 3;
    }

    @Override
    public String dependency() {
        return NON;
    }

    @Override
    public int levelRequirement() {
        return 0;
    }

    @Override
    public String description() {
        return "Your legs can now extend further, letting you scale higher obstacles and climb multiple blocks at once.";
    }

}