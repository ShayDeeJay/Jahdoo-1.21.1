package org.jahdoo.common.entities;

import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class EntityHelpers {
    public static boolean canTarget(LivingEntity target, LivingEntity owner){
        var isPet = target instanceof ITamableEntity i && Objects.equals(i.getOwner(), owner);

        return target != owner && !isPet;
    }
}
