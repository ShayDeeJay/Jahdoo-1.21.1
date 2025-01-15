package org.jahdoo.utils;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbilityBuilder;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.EntityPropertyRegister;

import static org.jahdoo.ability.abilities.ability_data.PermafrostAbility.abilityId;

public class MixinMethods {

    private static final WandAbilityHolder WAND_ABILITY_HOLDER_BARRAGE =
        new AbilityBuilder(null, abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(1200)
            .setEffectDurationWithValue(300, 100, 100)
            .setEffectStrengthWithValue(10, 5,5)
            .setModifierWithoutBounds(AbilityBuilder.LIFETIME, 100)
            .setModifierWithoutBounds(AbilityBuilder.AOE, 2)
            .buildAndReturn();


    public static void onTargetHit(Vec3 pos, Level level){
        var aoeCloud = new AoeCloud(level, null, 0f, EntityPropertyRegister.BARRAGE.get().setAbilityId(), WAND_ABILITY_HOLDER_BARRAGE, abilityId.getPath().intern());
        aoeCloud.setPos(pos.x, pos.y, pos.z);
        level.addFreshEntity(aoeCloud);
    }

}
