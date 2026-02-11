package org.jahdoo.trial_nexus.ability.abilities_combat.dimensional_recall;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.attachments.AbstractHoldUseAttachment;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

import static org.jahdoo.common.items.caster_item.CastHelper.validManaAndCooldown;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.common.registers.AttachmentReg.DIMENSIONAL_RECALL;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.MANA_COST;
import static org.jahdoo.trial_nexus.ability.abilities_combat.dimensional_recall.DimensionalRecallAbility.CASTING_TIME;
import static org.jahdoo.trial_nexus.ability.abilities_combat.dimensional_recall.DimensionalRecallAbility.abilityId;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.getSoundWithPositionV;

public class DimensionalRecall extends AbstractHoldUseAttachment {

    public static void staticTickEvent(Player player){
        player.getData(DIMENSIONAL_RECALL).onTickMethod(player);
    }

    public static void sendNoHomeMessage(Player player, AbstractElement element){
        player.displayClientMessage(TextHelpers.withStyleComponentTrans("ability.jahdoo.no_home", element.textColourA()), true);
    }

    public AbstractElement getElement(){
        return ElementReg.mystic();
    }

    public void onSuccessfulCast(ServerPlayer serverPlayer, AbilityHolder abilityHolder, int ticksUsing){
        var pos = serverPlayer.getRespawnPosition();
        var dimension = serverPlayer.getRespawnDimension();
        var abilityName = abilityId.getPath().intern();
        var getCasterData = serverPlayer.getData(CASTER_DATA);
        var getTeleportSound = SoundEvents.CHORUS_FRUIT_TELEPORT;
        var getSuccessSound = SoundEvents.ILLUSIONER_CAST_SPELL;
        var getManaCost = CasterData.getSpecificValue(abilityHolder, MANA_COST);
        var getLevelDimension = serverPlayer.getServer().getLevel(dimension);

        if(getLevelDimension != null){
            if (serverPlayer.getTicksUsingItem() >= ticksUsing && getCasterData.getManaPool() >= getManaCost) {
                this.startedUsing = false;
                serverPlayer.stopUsingItem();
                serverPlayer.teleportTo(getLevelDimension, pos.getX(), pos.getY(), pos.getZ(), serverPlayer.yya, serverPlayer.rotA);
                CastHelper.chargeMana(abilityName, serverPlayer);
                CastHelper.chargeCooldown(abilityName, serverPlayer);

                var level = serverPlayer.level();
                var position = serverPlayer.position();
                SoundHelpers.getSoundWithPosition(level, position, getTeleportSound, SoundSource.PLAYERS, 1, 0.8f);
                SoundHelpers.getSoundWithPosition(level, position, getSuccessSound, SoundSource.PLAYERS, 1, 1.2f);
            }
        }
    }

    @Override
    public void onTickMethod(Player player){
        super.onTickMethod(player);
        var getHolder = CasterData.entityHolderWithSelected(player);
        var getCastTime = CasterData.getSpecificValue(getHolder, CASTING_TIME);
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        var pos = serverPlayer.getRespawnPosition();

        if(startedUsing && validManaAndCooldown(player)){
            if (pos != null) {
                pullParticlesToCenter(player, this.getElement());
                var setVolume = Math.min(2, player.getTicksUsingItem() / 5);
                var setPitch = (float) player.getTicksUsingItem() / (int) getCastTime;

                if (player.getTicksUsingItem() % 3 == 0) {
                    var setAudio = SoundEvents.SOUL_ESCAPE.value();
                    getSoundWithPositionV(player.level(), player.position(), setAudio, setVolume, setPitch);
                }

                if (player.getTicksUsingItem() % 40 == 0) {
                    var setAudio = SoundEvents.ILLUSIONER_CAST_SPELL;
                    getSoundWithPositionV(player.level(), player.position(), setAudio, Math.max(setVolume, 0.2f), Math.max(setPitch, 0.6f));
                }

                this.onSuccessfulCast(serverPlayer, getHolder, (int) (getCastTime == 0 ? 200 : getCastTime));
            } else {
                sendNoHomeMessage(player, this.getElement());
            }
        }
    }

    public static void pullParticlesToCenter(Player player, AbstractElement element){
        var casterData = player.getData(CASTER_DATA);
        var manaReduction = casterData.getMaxMana(player) / 60;
        var bakedParticleOption = bakedParticle(element.id(), 6, 2f, false);
        var genericParticleOptions = ParticleHandlers.genericParticle(ParticleStore.SOFT_PARTICLE, element, 10, 1.4f);
        var particleOptionsList = List.of(bakedParticleOption, genericParticleOptions);
        var getRandomParticle = particleOptionsList.get(RandomSource.create().nextInt(0, 2));

        if(casterData.getManaPool() >= manaReduction){
            var numOfPoints = (double) player.getTicksUsingItem()/10;
            var pos = player.position()
                .add(0, player.getBbHeight() / 2, 0)
                .offsetRandom(RandomSource.create(), 1f);

            PositionFinders.innerRadiusRandom(pos, 2, Math.min(numOfPoints, 10),
                positions -> {
                    var directions = player.position()
                        .subtract(positions)
                        .normalize()
                        .add(0, player.getBbHeight() / 2, 0);

                    ParticleHandlers.sendParticles(player.level(), getRandomParticle, positions,
                        1,
                        directions.x,
                        Random.nextDouble(-0.3, 0.3),
                        directions.z,
                        Math.min(0.1, (double) player.getTicksUsingItem() / 1000)
                    );
                }
            );
        }
    }

}
