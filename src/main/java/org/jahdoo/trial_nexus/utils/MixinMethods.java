package org.jahdoo.trial_nexus.utils;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.mod.EntityDataReg;

import static org.jahdoo.trial_nexus.ability.abilities_combat.permafrost.PermafrostAbility.abilityId;

public class MixinMethods {

    public static void onTargetHit(Vec3 pos, Level level){
        var aoeCloud = new AoeCloud(level, null, 0f, EntityDataReg.BARRAGE.get().setAbilityId(), WAND_ABILITY_HOLDER_BARRAGE, abilityId.getPath().intern());
        aoeCloud.setPos(pos.x, pos.y, pos.z);
        level.addFreshEntity(aoeCloud);
    }

    private static final AbilityHolder WAND_ABILITY_HOLDER_BARRAGE =
        new AbilityBuilder(null, abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(1200)
            .setEffectDurationWithValue(300, 100, 100)
            .setEffectStrengthWithValue(10, 5,5)
            .setModifierWithoutBounds(AbilityBuilder.LIFETIME, 100)
            .setModifierWithoutBounds(AbilityBuilder.AOE, 2)
            .buildAndReturn();

}
