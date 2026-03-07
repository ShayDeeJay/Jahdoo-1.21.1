package org.jahdoo.trial_nexus.magic;

import net.minecraft.core.Vec3i;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

public abstract class AbstractBlockAbility extends Ability {

    public boolean isInputUser() {
        return false;
    }

    public boolean isOutputUser() {
        return false;
    }

    public abstract String projectileKey();

    public void invokeAbilityBlock(Vec3i direction, AbstractBEInventory entity, AbilityHolder holder) {
        var isUp = direction.equals(entity.getBlockPos().above());
        var isDown = direction.equals(entity.getBlockPos().below());

        var genericProjectile = new GenericProjectile(
            holder,
            entity.getBlockPos().getCenter().subtract(0, isUp || isDown ? 0 : 0.1,0),
            entity.getLevel(),
            projectileKey(),
            setAbilityId()
        );

        genericProjectile.setMaxDistance(10);
        fireUtilityProjectile(genericProjectile, entity.getBlockPos(), direction);
    }

}
