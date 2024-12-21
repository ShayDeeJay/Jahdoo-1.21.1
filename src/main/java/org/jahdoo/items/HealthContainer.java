package org.jahdoo.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.ItemEntityBehaviour;
import org.jahdoo.particle.ParticleHandlers;

public class HealthContainer extends Item implements ItemEntityBehaviour {
    public HealthContainer(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean onItemInteraction(ItemEntity itemEntity, LivingEntity livingEntity) {
        if(livingEntity.level() instanceof ServerLevel serverLevel){
            ModHelpers.getSoundWithPosition(serverLevel, itemEntity.blockPosition(), SoundEvents.ILLUSIONER_PREPARE_MIRROR,1,1f);
            ItemStack itemStack = itemEntity.getItem();
            float getDamage = itemStack.get(DataComponentRegistry.HEART_CONTAINER.get()).floatValue();
            livingEntity.heal(Math.max(getDamage, 0.1f) * itemStack.getCount());
            itemStack.shrink(itemStack.getCount());
            ParticleHandlers.spawnElectrifiedParticles(
                serverLevel,
                livingEntity.position(),
                new BakedParticleOptions(7, 5,  2, false),
                20, livingEntity, 0.1
            );
        }
        return true;
    }

}
