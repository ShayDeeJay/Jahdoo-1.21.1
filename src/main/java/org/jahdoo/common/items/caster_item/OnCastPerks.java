package org.jahdoo.common.items.caster_item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.trial_nexus.utils.Helpers;

import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class OnCastPerks {

    public static void onCastPerkApply(Player player){
        healOnCast(player);
        addAbsorptionOnCast(player);
    }

    public static void healOnCast(Player player){
        var castHeal = AttributeReg.CAST_HEAL;
        if(player.getAttribute(castHeal) == null) return;
        var healChance = Helpers.getAttributeValue(player, castHeal);
        if(healChance > 0 && Random.nextFloat(100) < healChance) return;
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.heal(Random.nextInt(1, 3));
        }
    }

    public static void addAbsorptionOnCast(Player player){
        var absorptionHearts = AttributeReg.ABSORPTION_HEARTS;
        if(player.getAttribute(absorptionHearts) == null) return;
        var absorption = Helpers.getAttributeValue(player, absorptionHearts);
        if(absorption > 0 && Random.nextFloat(100) < absorption) return;
        if (player instanceof ServerPlayer serverPlayer) {
            Helpers.addTransientAttribute(player,  4, "absorption", Attributes.MAX_ABSORPTION);
            serverPlayer.setAbsorptionAmount(4);
        }
    }

}
