package org.jahdoo.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

public interface ItemEntityBehaviour {
    boolean onItemInteraction(ItemEntity itemEntity, LivingEntity livingEntity);
}
