package org.jahdoo.ascension.ability;

import net.minecraft.core.Vec3i;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.ComponentReg;

public abstract class AbstractBlockAbility extends AbilityRegistrar {

    public boolean isInputUser() {
        return false;
    }

    public boolean isOutputUser() {
        return false;
    }

    public abstract String projectileKey();

    public void invokeAbilityBlock(Vec3i direction, AbstractBEInventory entity) {
        var augment = entity.inputItemHandler.getStackInSlot(0);
        var isUp = direction.equals(entity.getBlockPos().above());
        var isDown = direction.equals(entity.getBlockPos().below());

        var genericProjectile = new GenericProjectile(
            augment.get(ComponentReg.WAND_ABILITY_HOLDER.get()),
            entity.getBlockPos().getCenter().subtract(0, isUp || isDown ? 0 : 0.1,0),
            entity.getLevel(),
            projectileKey(),
            setAbilityId()
        );

        genericProjectile.setMaxDistance(10);
        fireUtilityProjectile(genericProjectile, entity.getBlockPos(), direction);
    }

}
