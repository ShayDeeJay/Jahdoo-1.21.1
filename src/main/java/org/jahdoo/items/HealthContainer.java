package org.jahdoo.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.attachments.player_abilities.VitalRejuvenation;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.ItemEntityBehaviour;
import org.jahdoo.particle.ParticleHandlers;

import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;

public class HealthContainer extends Item implements ItemEntityBehaviour {
    public HealthContainer(Properties pProperties) {
        super(pProperties);
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
