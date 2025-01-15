package org.jahdoo.items.wand;

import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.utils.ModHelpers.Random;

public class OnCastPerks {

    public static void onCastPerkApply(Player player){
        healOnCast(player);
        addAbsorptionOnCast(player);
    }

    public static void healOnCast(Player player){
        var castHeal = AttributesRegister.CAST_HEAL;
        if(player.getAttribute(castHeal) == null) return;
        var healChance = ModHelpers.getAttributeValue(player, castHeal);
        if(healChance > 0 && Random.nextFloat(100) < healChance) return;
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.heal(Random.nextInt(1, 3));
        }
    }

    public static void addAbsorptionOnCast(Player player){
        var absorptionHearts = AttributesRegister.ABSORPTION_HEARTS;
        if(player.getAttribute(absorptionHearts) == null) return;
        var absorption = ModHelpers.getAttributeValue(player, absorptionHearts);
        if(absorption > 0 && Random.nextFloat(100) < absorption) return;
        if (player instanceof ServerPlayer serverPlayer) {
            ModHelpers.addTransientAttribute(player,  4, "absorption", Attributes.MAX_ABSORPTION);
            serverPlayer.setAbsorptionAmount(4);
        }
    }

}
