package org.jahdoo.ascension.ability.skills;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;

import java.util.List;

public class TripleJumpSkill extends AbstractSkill {

    @Override
    String name() {
        return "Triple Jump";
    }

    @Override
    ResourceLocation icon() {
        return Icons.TRIPLE_JUMP;
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
        return 10;
    }

    @Override
    String dependency() {
        return "Rebound";
    }

    @Override
    int levelRequirement() {
        return 30;
    }

}