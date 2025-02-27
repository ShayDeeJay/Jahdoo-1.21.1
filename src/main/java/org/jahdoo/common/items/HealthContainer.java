package org.jahdoo.common.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import org.jahdoo.ascension.ability.abilities.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.common.registers.DataComponentRegistry;
import org.jahdoo.ascension.utils.ItemEntityBehaviour;
import org.jahdoo.common.particle.ParticleHandlers;

import static org.jahdoo.common.particle.ParticleHandlers.bakedParticleOptions;

public class HealthContainer extends Item implements ItemEntityBehaviour {

    public HealthContainer() {
        super(new Properties());
    }

    @Override
    public boolean onItemInteraction(ItemEntity itemEntity, LivingEntity livingEntity) {
        if(livingEntity.level() instanceof ServerLevel serverLevel){
            var itemStack = itemEntity.getItem();
            var getDamage = itemStack.get(DataComponentRegistry.HEART_CONTAINER.get());
            livingEntity.heal((getDamage != null ? getDamage : 0.1f) * itemStack.getCount());
            itemStack.shrink(itemStack.getCount());
            ParticleHandlers.spawnElectrifiedParticles(
                serverLevel, livingEntity.position(),
                bakedParticleOptions(7, 5,  2, false),
                20, livingEntity, 0.1
            );
            VitalRejuvenation.successfulCastAnimation(livingEntity);
        }
        return true;
    }

}
