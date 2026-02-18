package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.ItemReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;


public class ExperienceOrb extends BaseJahdooItem {

    public ExperienceOrb() {
        super(new Properties().stacksTo(64));
    }

    public int getByType(ItemStack item){
        if(item.is(ItemReg.GLOWING_EXPERIENCE_ORB)) return 120;
        if(item.is(ItemReg.RADIANT_EXPERIENCE_ORB)) return 1200;
        return 10;
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ColourHelpers.getExperienceGreen()) ;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);
        var getExp = getByType(item);
        var colour = ColourHelpers.getExperienceGreen();

        if(player instanceof ServerPlayer serverPlayer){
            var xpPoints = repairPlayerItems(serverPlayer, getExp);
            if(xpPoints > 0) player.giveExperiencePoints(xpPoints);
        }

        item.shrink(1);

        for(int i = 0; i < (getExp/8); i++){
            var particle = ParticleHandlers.getNonBakedParticles(colour, colour, 17, 1F);
            var x = player.getRandomX(0.5);
            var y = player.getRandomY();
            var z = player.getRandomZ(0.5);
            var xSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
            var ySpeed = Random.nextDouble(0.1, 0.3);
            var zSpeed = Random.nextDouble(0.1, 0.3) - 0.2;

            level.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
        }

        player.playSound(SoundEvents.PLAYER_LEVELUP, 1, 0.5f);
        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1.5f);
        return super.use(level, player, usedHand);
    }

    private int repairPlayerItems(ServerPlayer player, int value) {
        var optional = EnchantmentHelper.getRandomItemWith(EnchantmentEffectComponents.REPAIR_WITH_XP, player, ItemStack::isDamaged);
        if (optional.isPresent()) {
            ItemStack itemstack = optional.get().itemStack();
            int i = EnchantmentHelper.modifyDurabilityToRepairFromXp(player.serverLevel(), itemstack, (int)((float)value * itemstack.getXpRepairRatio()));
            int j = Math.min(i, itemstack.getDamageValue());
            itemstack.setDamageValue(itemstack.getDamageValue() - j);
            if (j > 0) {
                int k = value - j * value / i;
                if (k > 0) {
                    return this.repairPlayerItems(player, k);
                }
            }
            return 0;
        } else {
            return value;
        }
    }
}
