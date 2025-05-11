package org.jahdoo.common.entities;

import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.UUID;

public interface ITamableEntity {
    LivingEntity getOwner();
    void setOwner(LivingEntity livingEntity);
    default Optional<UUID> getOwnerUUIDOptional() {
        return Optional.empty();
    }
    default void setOwnerUUIDOptional(UUID uuid) {
    }
}
