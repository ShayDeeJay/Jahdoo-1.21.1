package org.jahdoo.common.items.shields;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.burning_skull.BurningSkull;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.abilities_combat.BurningSkullsAbility;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.shaydee.shaydeeapi.Helpers;

import java.util.ArrayList;

import static org.jahdoo.trial_nexus.ability.Ability.calculateDirectionOffset;
import static org.jahdoo.trial_nexus.ability.Ability.fireProjectileDirection;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.SHOT_MULTIPLIER;
import static org.jahdoo.trial_nexus.ability.abilities_combat.BurningSkullsAbility.infernoSoundEffect;

public class UndeadProtectorShield extends JahdooShieldItem {

    public static final AbilityHolder skullProperty =
        new AbilityBuilder(BurningSkullsAbility.abilityId.getPath().intern())
            .setDamageWithValue(0, 0, 30)
            .setEffectDurationWithValue(0, 0, 1)
            .setEffectStrengthWithValue(0, 0, 1)
            .setEffectChanceWithValue(0, 0, 1)
            .shotMultiplierWithValue(0, 0, 3)
            .setLifetimeWithValue(0, 0, 40)
            .buildAndReturn();

    @Override
    public void doOnBlock(LivingEntity lEntity) {
        if(true) return;
        if(!(lEntity instanceof ServerPlayer player)) return;

        if(!player.level().isClientSide){
            var property = skullProperty;
            var projectileCount = CasterData.getSpecificValue(property, SHOT_MULTIPLIER);
            var adjustSpread = 1.8 - (projectileCount / 4);
            var getLocalEntities = new ArrayList<>(BurningSkull.getValidTargets(player, player, 20));
            var totalWidth = (projectileCount - 1) * adjustSpread;
            var startOffset = -totalWidth / 2.0;
            infernoSoundEffect(player);

            for (int i = 0; i < projectileCount; i++) {
                var isValid = !getLocalEntities.isEmpty();
                var randomTarget = isValid ? Helpers.listRandom(getLocalEntities) : null;
                var skull = new BurningSkull(player, property, 0);

                skull.setTarget(randomTarget);
                if (isValid) getLocalEntities.remove(randomTarget);

                var offset = projectileCount == 1 ? 0 : startOffset + i * (totalWidth / (projectileCount - 1));
                var directionOffset = calculateDirectionOffset(player, offset);
                var baseDirection = player.getLookAngle();
                var withoutY = new Vec3(baseDirection.x, 0,  baseDirection.z);

                var direction = withoutY
                    .add(directionOffset)
                    .reverse()
                    .normalize();

                fireProjectileDirection(skull, player, 0.3F, direction);
            }
        }
    }


}
