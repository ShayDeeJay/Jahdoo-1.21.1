package org.jahdoo.trial_nexus.magic.abilities_combat.storm_rush;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.server2client.MoveClientEntityS2CP;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.magic.AbstractAbility;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static org.jahdoo.common.registers.SoundReg.DASH_EFFECT_INSTANT;

public class StormRush extends AbstractAbility {

    private final Player player;
    private final AbilityHolder abilityHolder;

    public StormRush(Player player){
        this.player = player;
        this.abilityHolder = CasterData.entityHolderWithSelected(player);
    }

    AbstractElement getType(){
        return ElementReg.frost();
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return abilityHolder;
    }

    @Override
    public String abilityId() {
        return StormRushAbility.abilityId.getPath().intern();
    }

    public void launchPlayerDirection() {
        var level = player.level();
        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getAbilities().mayfly = true;
            var launchDistances = getTag(StormRushAbility.LAUNCH_DISTANCE);
            var lookVector = serverPlayer.getLookAngle().scale(launchDistances);
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntityS2CP(lookVector.x, lookVector.y, lookVector.z, serverPlayer.getId()));
            SoundHelpers.getSoundWithPosition(level, serverPlayer.blockPosition(), DASH_EFFECT_INSTANT.get(), SoundSource.NEUTRAL, 2f);
        }
    }

}
