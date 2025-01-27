package org.jahdoo.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.SoundRegister;

import static org.jahdoo.utils.ModHelpers.Random;


public class ExperienceOrb extends Item {
    public ExperienceOrb() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var low = 220;
        var medium = 440;
        var high = 880;
        player.giveExperiencePoints(high);
        player.getItemInHand(usedHand).shrink(1);

        for(int i = 0; i < 50; i++){
            var particle = ParticleHandlers.getNonBakedParticles(8453920, 8453920, 17, 1F);
            var x = player.getRandomX(0.5);
            var y = player.getRandomY();
            var z = player.getRandomZ(0.5);
            level.addParticle(particle, x, y, z, Random.nextDouble(0.1, 0.3) - 0.2, Random.nextDouble(0.1, 0.3), Random.nextDouble(0.1, 0.3)- 0.2);
        }

        player.playSound(SoundEvents.PLAYER_LEVELUP, 1, 1.5f);
        player.playSound(SoundRegister.ORB_CREATE.get(), 1, 2f);
        return super.use(level, player, usedHand);
    }
}
