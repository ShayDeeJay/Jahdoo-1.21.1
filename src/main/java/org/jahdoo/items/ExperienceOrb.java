package org.jahdoo.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.utils.ColourStore.*;
import static org.jahdoo.utils.ModHelpers.Random;


public class ExperienceOrb extends Item {
    public ExperienceOrb() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var getExp = data == null ? "Greater" : data.value() == 1 ? "Lesser" : "Better";
        return ModHelpers.withStyleComponent(getExp + " XP Relic", EXPERIENCE_GREEN) ;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);
        var data = item.get(DataComponents.CUSTOM_MODEL_DATA);
        var low = 220;
        var medium = 440;
        var high = 880;
        var getExp = data == null ? high : data.value() == 1 ? low : medium;

        player.giveExperiencePoints(getExp);

        item.shrink(1);

        for(int i = 0; i < (getExp/8); i++){
            var particle = ParticleHandlers.getNonBakedParticles(EXPERIENCE_GREEN, EXPERIENCE_GREEN, 17, 1F);
            var x = player.getRandomX(0.5);
            var y = player.getRandomY();
            var z = player.getRandomZ(0.5);
            double xSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
            double ySpeed = Random.nextDouble(0.1, 0.3);
            double zSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
            level.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
        }

        player.playSound(SoundEvents.PLAYER_LEVELUP, 1, 0.5f);
        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1.5f);
        return super.use(level, player, usedHand);
    }
}
