package org.jahdoo.trial_nexus.ability.abilities_combat.dimensional_recall;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jahdoo.common.block.ticket_bureau.TicketBureauBlock;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.attachments.AbstractHoldUseAttachment;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.Collection;
import java.util.Collections;
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
        var abilityName = abilityId.getPath().intern();
        var getCasterData = serverPlayer.getData(CASTER_DATA);
        var getManaCost = CasterData.getSpecificValue(abilityHolder, MANA_COST);
        var itemStack = serverPlayer.getItemInHand(serverPlayer.getUsedItemHand());

        if (serverPlayer.getTicksUsingItem() >= ticksUsing && getCasterData.getManaPool() >= getManaCost) {
            this.startedUsing = false;
            serverPlayer.stopUsingItem();

            if (!serverPlayer.level().isClientSide) {
                final InteractionHand hand = serverPlayer.getUsedItemHand();

                final Collection<Waystone> waystones = PlayerWaystoneManager.getTargetsForItem(serverPlayer, itemStack);
                PlayerWaystoneManager.ensureSortingIndex(serverPlayer, waystones);
                Balm.getNetworking().openMenu(serverPlayer,
                    new BalmMenuProvider<ModMenus.ItemInitiatedWaystoneMenuData>() {
                        public Component getDisplayName() {
                            return Component.translatable("container.waystones.waystone_selection");
                        }

                        public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                            return (new WaystoneSelectionMenu(ModMenus.warpStoneSelection.get(), null, windowId, waystones, Collections.emptySet()))
                                .withWarpItem(itemStack)
                                .setPostTeleportHandler(
                                    (context) -> {
                                        itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                                        CastHelper.chargeMana(abilityName, serverPlayer);
                                        CastHelper.chargeCooldown(abilityName, serverPlayer);
                                        SoundHelpers.getSoundWithPosition(serverPlayer.level(), serverPlayer.position(), SoundReg.TELEPORT.get(), SoundSource.PLAYERS, 1.4F, 0.8f);

                                    }
                                );
                        }

                        public ModMenus.ItemInitiatedWaystoneMenuData getScreenOpeningData(ServerPlayer serverPlayer) {
                            return new ModMenus.ItemInitiatedWaystoneMenuData(waystones, itemStack);
                        }

                        public StreamCodec<RegistryFriendlyByteBuf, ModMenus.ItemInitiatedWaystoneMenuData> getScreenStreamCodec() {
                            return ModMenus.ItemInitiatedWaystoneMenuData.STREAM_CODEC;
                        }
                    }
                );
            }

            var level = serverPlayer.level();
            var position = serverPlayer.position();
            SoundHelpers.getSoundWithPosition(level, position, SoundReg.REJECT.get(), SoundSource.PLAYERS, 1.4F, 0.8f);
        }
    }

    @Override
    public void onTickMethod(Player player){
        super.onTickMethod(player);
        var getHolder = CasterData.entityHolderWithSelected(player);
        var getCastTime = CasterData.getSpecificValue(getHolder, CASTING_TIME);

        if(startedUsing && validManaAndCooldown(player)){
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            var pos = serverPlayer.getRespawnPosition();
            if (pos != null) {
                pullParticlesToCenter(player, this.getElement());
                var setVolume = Math.min(2, player.getTicksUsingItem() / 5);
                var setPitch = (float) player.getTicksUsingItem() / (int) getCastTime;

                if (player.getTicksUsingItem() % 3 == 0) {
                    var setAudio = SoundEvents.SOUL_ESCAPE.value();
                    getSoundWithPositionV(player.level(), player.position(), setAudio, setVolume, setPitch);
                }

                if (player.getTicksUsingItem() % 40 == 0) {
                    var setAudio = SoundReg.LEVITATE.value();
                    TicketBureauBlock.acceptParticle(player.level(), player.position().add(0, 3, 0), ParticleStore.MAGIC_MOVE_PARTICLE, ColourHelpers.getCosmicPurple());
                    getSoundWithPositionV(player.level(), player.position(), setAudio, Math.max(setVolume, 1.5f), Math.max(setPitch, 1f));
                }

                this.onSuccessfulCast(serverPlayer, getHolder, (int) (getCastTime == 0 ? 200 : getCastTime));
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
