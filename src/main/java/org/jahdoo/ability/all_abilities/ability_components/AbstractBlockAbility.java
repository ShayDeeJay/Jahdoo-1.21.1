package org.jahdoo.ability.all_abilities.ability_components;

import net.minecraft.core.Vec3i;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.DataComponentRegistry;

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

        GenericProjectile genericProjectile = new GenericProjectile(
            augment.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()),
            entity.getBlockPos().getCenter().subtract(0, isUp || isDown ? 0 : 0.1,0),
            entity.getLevel(),
            projectileKey(),
            setAbilityId()
        );
        genericProjectile.setMaxDistance(10);
        fireUtilityProjectile(genericProjectile, entity.getBlockPos(), direction);
    }
}
