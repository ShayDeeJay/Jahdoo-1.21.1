package org.jahdoo.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbilityBuilder;
import org.jahdoo.ability.abilities.ability_data.StormRushAbility;
import org.jahdoo.ability.effects.JahdooMobEffect;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.EntityPropertyRegister;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.ability.abilities.ability_data.PermafrostAbility.abilityId;
import static org.jahdoo.components.DataComponentHelper.getSpecificValue;
import static org.jahdoo.registers.EffectsRegister.LIGHTNING_EFFECT;
import static org.jahdoo.utils.ModHelpers.Random;

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

    public static void spinAttackMixin(Player player, Entity target) {
        if(player.isAutoSpinAttack()){
            var wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
            if(wandAbilityHolder != null){
                var ability = StormRushAbility.abilityId.getPath().intern();
                if(wandAbilityHolder.abilityProperties().containsKey(ability)){
                    var chance = getSpecificValue(ability, wandAbilityHolder, EFFECT_CHANCE);
                    var duration = getSpecificValue(ability, wandAbilityHolder, EFFECT_DURATION);
                    var strength = getSpecificValue(ability, wandAbilityHolder, EFFECT_STRENGTH);
                    if (target instanceof LivingEntity livingEntity) {
                        if (Random.nextInt(0, (int) chance) == 0) {
                            livingEntity.addEffect(new JahdooMobEffect(LIGHTNING_EFFECT, (int) duration, (int) strength));
                        }
                    }
                }
            }
        }
    }

}
