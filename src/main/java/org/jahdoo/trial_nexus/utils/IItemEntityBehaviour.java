package org.jahdoo.trial_nexus.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

public interface IItemEntityBehaviour {
    boolean onItemInteraction(ItemEntity itemEntity, LivingEntity livingEntity);
}
