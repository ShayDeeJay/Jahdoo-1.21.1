package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.particle.ParticleHandlers;

import static org.jahdoo.trial_nexus.utils.ColourStore.EXPERIENCE_GREEN;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;


public class ExperienceOrb extends Item {
    public ExperienceOrb() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var getExp = data == null ? "Greater" : data.value() == 1 ? "Lesser" : "Better";
        return Helpers.withStyleComponent(getExp + " XP Relic", EXPERIENCE_GREEN) ;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);
        var data = item.get(DataComponents.CUSTOM_MODEL_DATA);
        var low = 220;
        var medium = 440;
        var high = 880;
        var getExp = data == null ? high : data.value() == 1 ? low : medium;

        if(player instanceof ServerPlayer serverPlayer){
            var xpPoints = repairPlayerItems(serverPlayer, getExp);
            if(xpPoints > 0) player.giveExperiencePoints(xpPoints);
        }

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
