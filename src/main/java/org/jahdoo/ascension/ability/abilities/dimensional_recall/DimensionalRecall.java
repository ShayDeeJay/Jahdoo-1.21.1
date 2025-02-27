package org.jahdoo.ascension.ability.abilities.dimensional_recall;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.attachments.AbstractHoldUseAttachment;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.items.wand.CastHelper;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;

import java.util.List;

import static org.jahdoo.ascension.ability.AbilityBuilder.COOLDOWN;
import static org.jahdoo.ascension.ability.AbilityBuilder.MANA_COST;
import static org.jahdoo.ascension.ability.abilities.dimensional_recall.DimensionalRecallAbility.CASTING_TIME;
import static org.jahdoo.ascension.ability.abilities.dimensional_recall.DimensionalRecallAbility.abilityId;
import static org.jahdoo.common.items.wand.CastHelper.validManaAndCooldown;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.common.registers.AttachmentRegister.CASTER_DATA;
import static org.jahdoo.common.registers.AttachmentRegister.DIMENSIONAL_RECALL;

public class DimensionalRecall extends AbstractHoldUseAttachment {


    public static void staticTickEvent(Player player){
        player.getData(DIMENSIONAL_RECALL).onTickMethod(player);
    }

    @Override
    public void onTickMethod(Player player){
        super.onTickMethod(player);
        var getHolder = WandAbilityHolder.getHolderFromWand(player);
        var hasAbility = Helpers.getModifierValue(getHolder, abilityId.getPath().intern()) != null;
        var getCastTime = DataComponentHelper.getSpecificValue(abilityId.getPath().intern(), getHolder, CASTING_TIME);
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        var pos = serverPlayer.getRespawnPosition();

        if(startedUsing && hasAbility && validManaAndCooldown(player)){
            if (pos != null) {
                pullParticlesToCenter(player);
                var setVolume = Math.min(2, player.getTicksUsingItem() / 5);
                var setPitch = (float) player.getTicksUsingItem() / (int) getCastTime;

                if (player.getTicksUsingItem() % 3 == 0) {
                    var setAudio = SoundEvents.SOUL_ESCAPE.value();
                    Helpers.getSoundWithPositionV(player.level(), player.position(), setAudio, setVolume, setPitch);
                }

                if (player.getTicksUsingItem() % 40 == 0) {
                    var setAudio = SoundEvents.ILLUSIONER_CAST_SPELL;
                    Helpers.getSoundWithPositionV(player.level(), player.position(), setAudio, Math.max(setVolume, 0.2f), Math.max(setPitch, 0.6f));
                }

                this.onSuccessfulCast(serverPlayer, getHolder, (int) (getCastTime == 0 ? 200 : getCastTime));
            } else {
                sendNoHomeMessage(player);
            }
        }
    }

    private void onSuccessfulCast(ServerPlayer serverPlayer, WandAbilityHolder wandAbilityHolder, int ticksUsing){
        var pos = serverPlayer.getRespawnPosition();
        var dimension = serverPlayer.getRespawnDimension();
        var abilityName = abilityId.getPath().intern();
        var getCasterData = serverPlayer.getData(CASTER_DATA);
        var getTeleportSound = SoundEvents.CHORUS_FRUIT_TELEPORT;
        var getSuccessSound = SoundEvents.ILLUSIONER_CAST_SPELL;
        var getManaCost = DataComponentHelper.getSpecificValue(abilityName, wandAbilityHolder, MANA_COST);
        var getCooldownCost = DataComponentHelper.getSpecificValue(abilityName, wandAbilityHolder, COOLDOWN);
        var getLevelDimension = serverPlayer.getServer().getLevel(dimension);

        if(getLevelDimension != null){
            if (serverPlayer.getTicksUsingItem() >= ticksUsing && getCasterData.getManaPool() >= getManaCost) {
                this.startedUsing = false;
                serverPlayer.stopUsingItem();
                serverPlayer.teleportTo(getLevelDimension, pos.getX(), pos.getY(), pos.getZ(), serverPlayer.yya, serverPlayer.rotA);
                CastHelper.chargeMana(abilityName, getManaCost, serverPlayer);
                CastHelper.chargeCooldown(abilityName, getCooldownCost, serverPlayer);
                Helpers.getSoundWithPosition(serverPlayer.level(), serverPlayer.blockPosition(), getTeleportSound, 0.8f);
                Helpers.getSoundWithPosition(serverPlayer.level(), serverPlayer.blockPosition(), getSuccessSound, 1, 1.2f);
            }
        }
    }

    private void sendNoHomeMessage(Player player){
        player.displayClientMessage(Helpers.withStyleComponentTrans("ability.jahdoo.no_home", this.getElement().textColourA()), true);
    }

    public void pullParticlesToCenter(Player player){
        var casterData = player.getData(CASTER_DATA);
        var manaReduction = casterData.getMaxMana(player) / 60;
        var bakedParticleOption = bakedParticleOptions(this.getElement().id(), 6, 2f, false);
        var genericParticleOptions = genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, this.getElement(), 10, 1.4f);
        var particleOptionsList = List.of(bakedParticleOption, genericParticleOptions);
        var getRandomParticle = particleOptionsList.get(RandomSource.create().nextInt(0, 2));

        if(casterData.getManaPool() >= manaReduction){
            var numOfPoints = (double) player.getTicksUsingItem()/10;
            var pos = player.position()
                .add(0, player.getBbHeight() / 2, 0)
                .offsetRandom(RandomSource.create(), 1f);

            PositionFinders.getInnerRingOfRadiusRandom(pos, 2, Math.min(numOfPoints, 10),
                positions -> {
                    var directions = player.position()
                        .subtract(positions)
                        .normalize()
                        .add(0, player.getBbHeight() / 2, 0);

                    ParticleHandlers.sendParticles(player.level(), getRandomParticle, positions,
                        1,
                        directions.x,
                        Helpers.Random.nextDouble(-0.3, 0.3),
                        directions.z,
                        Math.min(0.1, (double) player.getTicksUsingItem() / 1000)
                    );
                }
            );
        }
    }

    public AbstractElement getElement(){
        return ElementRegistry.mystic();
    }

}
