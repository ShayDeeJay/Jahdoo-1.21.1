package org.jahdoo.common.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import org.jahdoo.trial_nexus.ability.abilities_combat.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.trial_nexus.utils.IItemEntityBehaviour;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;

public class ManaContainer extends Item implements IItemEntityBehaviour {

    public ManaContainer() {
        super(new Properties());
    }

    @Override
    public boolean onItemInteraction(ItemEntity itemEntity, LivingEntity livingEntity) {
        if(livingEntity.level() instanceof ServerLevel serverLevel){
            var itemStack = itemEntity.getItem();
            var getDamage = itemStack.get(ComponentReg.HEART_CONTAINER.get());
            livingEntity.heal((getDamage != null ? getDamage : 0.1f) * itemStack.getCount());
            itemStack.shrink(itemStack.getCount());
            ParticleHandlers.spawnElectrifiedParticles(
                serverLevel, livingEntity.position(),
                bakedParticle(ElementReg.vitality().id(), 5,  2, false),
                20, livingEntity, 0.1
            );
            VitalRejuvenation.successfulCastAnimation(livingEntity);
        }
        return true;
    }

}
