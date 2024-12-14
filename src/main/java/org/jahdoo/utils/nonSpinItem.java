package org.jahdoo.utils;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

public class nonSpinItem extends ItemEntity {
    public final float bobOffs;

    public nonSpinItem(EntityType<? extends ItemEntity> entityType, Level level) {
        super(entityType, level);
        this.bobOffs = 0;
        this.setYRot(0);
    }
}
