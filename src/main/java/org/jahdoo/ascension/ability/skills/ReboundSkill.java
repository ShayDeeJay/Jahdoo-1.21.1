package org.jahdoo.ascension.ability.skills;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;

import java.util.List;

public class ReboundSkill extends AbstractSkill {

    @Override
    String name() {
        return "Rebound";
    }

    @Override
    ResourceLocation icon() {
        return Icons.REBOUND;
    }

    @Override
    void doOnCall() {
        //TODO
    }

    @Override
    List<Component> tooltip() {
        return List.of();
    }

    @Override
    int unlockCost() {
        return 8;
    }

    @Override
    String dependency() {
        return "Climber";
    }

    @Override
    int levelRequirement() {
        return 5;
    }

}