package org.jahdoo.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public interface ITamableEntity {

    UUID uuid = null;

    LivingEntity getOwner();

    void setOwner(LivingEntity livingEntity);

    default Optional<UUID> getOwnerUUIDOptional() {
        return Optional.empty();
    }

    default void setOwnerUUIDOptional(UUID uuid) {}

    default void saveTag(LivingEntity owner, CompoundTag compoundTag){
        if(owner != null) compoundTag.putUUID("saveOwner", owner.getUUID());
    }

    default UUID loadTag(CompoundTag compoundTag){
        if(compoundTag.hasUUID("saveOwner")) return compoundTag.getUUID("saveOwner");
        return null;
    }

    default LivingEntity reassignOwner(Level level, LivingEntity owner, UUID ownerUUID) {
        if(!(level instanceof ServerLevel serverLevel)) return null;
        if(owner == null && ownerUUID != null)  return serverLevel.getPlayerByUUID(ownerUUID);
        return null;
    }


}
