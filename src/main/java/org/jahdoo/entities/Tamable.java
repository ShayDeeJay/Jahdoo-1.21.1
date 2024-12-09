package org.jahdoo.entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.UUID;

public interface Tamable  {
    LivingEntity getOwner();
}
