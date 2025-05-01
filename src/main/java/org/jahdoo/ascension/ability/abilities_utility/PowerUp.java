package org.jahdoo.ascension.ability.abilities_utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ascension.ability.AbstractUtilityProjectile;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.abilities_utility.block_bomb.BlockBombAbility;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;

public class PowerUp extends AbstractUtilityProjectile {

    private static final ResourceLocation abilityId = Helpers.res("power_up_projectile_property");

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);

    }

    @Override
    public String abilityId() {
        return BlockBombAbility.abilityId.getPath().intern();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new PowerUp();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);

    }


    @Override
    public void onTickMethod() {
        super.onTickMethod();

    }


}
