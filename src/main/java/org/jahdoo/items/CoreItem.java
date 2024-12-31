package org.jahdoo.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ItemEntityBehaviour;
import org.jahdoo.utils.LevelGenerator;
import org.jahdoo.utils.ModHelpers;

public class CoreItem extends Item  {
    public CoreItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(level instanceof ServerLevel serverLevel){
            LevelGenerator.createNewWorld(player, serverLevel);
        }

//        System.out.println(level);
        System.out.println(player.level().getBiome(BlockPos.containing(player.position())));
        return InteractionResultHolder.fail(player.getMainHandItem());
    }
}
