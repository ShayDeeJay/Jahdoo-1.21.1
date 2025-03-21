package org.jahdoo.common.event;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.abilities.dimensional_recall.DimensionalRecall;
import org.jahdoo.ascension.ability.abilities.nova_smash.NovaSmash;
import org.jahdoo.ascension.ability.abilities.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.ascension.attachments.CastingData;
import org.jahdoo.ascension.attachments.player_abilities.BouncyFoot;
import org.jahdoo.ascension.attachments.player_abilities.MageFlight;
import org.jahdoo.ascension.attachments.player_abilities.TripleJump;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.networking.server2client.InstanceSyncS2CP;
import org.jahdoo.common.networking.server2client.WalletSyncS2CP;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import static net.minecraft.sounds.SoundSource.PLAYERS;
import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.common.event.event_helpers.CopyPasteEvent.copyPasteBlockProperties;
import static org.jahdoo.common.event.event_helpers.EventHelpers.*;
import static org.jahdoo.common.registers.AttachmentReg.*;


@EventBusSubscriber(modid = JahdooMod.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void effectEvent(MobEffectEvent.Applicable event) {
        disallowEffectsInCustomDim(event);
    }

    @SubscribeEvent
    public static void attributeEvent(ItemAttributeModifierEvent event) {
        useRuneAttributes(event);
    }

    @SubscribeEvent
    public static void attributeEvent(CurioAttributeModifierEvent event) {
        useRuneAttributesCurios(event);
    }

    @SubscribeEvent
    public static void dimChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        var player = event.getEntity();
        setGameModeOnDimChange(event, player);
    }

    @SubscribeEvent
    public static void totem(LivingUseTotemEvent event){
        var entity = event.getEntity();
        if(entity.level() instanceof CustomLevel) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.PlayerRespawnEvent event){
        var player = event.getEntity();
        player.getData(SAVE_DATA).takeAllItems(player);
    }

    @SubscribeEvent
    public static void onEntityDamageEvent(LivingDamageEvent.Pre event){
        var entity = event.getEntity();
        greaterFrostEffectDamageAmplifier(event, entity);
        greaterVitalityEffect(event, entity);
    }

    @SubscribeEvent
    public static void leftClickBlockInteraction(PlayerInteractEvent.LeftClickBlock event) {
        var item = event.getItemStack();
        var pos = event.getPos();
        var blockState = event.getLevel().getBlockState(pos);
        saveBlockType(event, item, blockState, pos);
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent.Pre event){
        var player = event.getEntity();

        if(player instanceof ServerPlayer serverPlayer){
            CastingData.cooldownTickEvent(serverPlayer);
            CastingData.manaTickEvent(serverPlayer);
        }

        copyPasteBlockProperties(player);
        MageFlight.mageFlightTickEvent(player);
        VitalRejuvenation.staticTickEvent(player);
        DimensionalRecall.staticTickEvent(player);
        NovaSmash.novaSmashTickEvent(player);
        TripleJump.tripleJumpTickEvent(player);
        BouncyFoot.staticTickEvent(player);
    }

    @SubscribeEvent
    public static void levelTickEvent(LevelTickEvent.Pre tickEvent){
        assignTarget(tickEvent);

        if(tickEvent.getLevel() instanceof CustomLevel cLevel){
            if(!cLevel.hasData(INSTANCE_DATA)) return;
            var data = cLevel.getData(INSTANCE_DATA);

            var difficulty = data.getDifficulty();

            if(!difficulty.isEmpty()){
                data.incrementTicks();
                for (var player : cLevel.players()) {
                    sendToPlayer(player, new InstanceSyncS2CP(data));
                    var remaining = data.getMaxTime() - data.getTicks();
                    var lessThan20Seconds = remaining <= 400;
                    var lessThan10Seconds = remaining <= 200;

                    if (lessThan20Seconds && (data.getTicks() % 20) == 0) {
                        var pitch = (float) Math.abs((remaining / 10) - 38) / 19;
                        player.playNotifySound(SoundEvents.NOTE_BLOCK_BASS.value(), PLAYERS, 1, pitch);
                        player.playNotifySound(SoundEvents.WARDEN_HEARTBEAT, PLAYERS, 1, 0.8F);
                    }

                    if (lessThan20Seconds && (data.getTicks() % (lessThan10Seconds ? 10 : 20)) == 0) {
                        player.playNotifySound(SoundEvents.WARDEN_HEARTBEAT, PLAYERS, 1, 0.8F);
                    }

                    if (remaining == 0) {
                        player.playNotifySound(SoundEvents.ALLAY_DEATH, PLAYERS, 1, 0.8F);
                        player.kill();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void blockInteraction(UseItemOnBlockEvent event) {
        var item = event.getItemStack().getItem();
        var player = event.getPlayer();
        var pos = event.getPos();
        var level = event.getLevel();
        var getBlock = level.getBlockState(pos);

        perkTableInteraction(getBlock, level, pos, player);
        removeWandInteractionWithBlocks(event, player, item, getBlock);
    }

    @SubscribeEvent
    public static void joinEvent(EntityJoinLevelEvent event){
        var entity = event.getEntity();

        if(entity instanceof ServerPlayer player){
            var wallet = player.getData(PLAYER_WALLET).getWallet();
            sendToPlayer(player, new WalletSyncS2CP(wallet));
            if(event.getLevel() instanceof CustomLevel){
                player.removeAllEffects();
            }
        }

    }

    @SubscribeEvent
    public static void itemClickEvent(PlayerInteractEvent.RightClickItem event) {
        var mainHand = event.getItemStack();
        var player = event.getEntity();

//        if(mainHand.has(DataComponentRegistry.RUNE_HOLDER)){
//            var rune = player.getOffhandItem();
//            var list = new ArrayList<ItemStack>();
//            if(rune.getItem() instanceof RuneItem){
//                list.add(rune);
//                RuneHolder.updateRuneSlots(mainHand, list);
//                event.setCanceled(true);
//            }
//        }

    }

    @SubscribeEvent
    public static void hitEvent(ProjectileImpactEvent event) {
        var projectile = event.getProjectile();
        var type = event.getRayTraceResult();
        if(projectile.level() instanceof CustomLevel){
            if (projectile instanceof Arrow) {
                if(type instanceof BlockHitResult) projectile.discard();
            }

            if (event.getRayTraceResult() instanceof EntityHitResult result) {
                var owner = projectile.getOwner();
                var entity = result.getEntity();

                var nonFriendlyProjectile = !(owner instanceof Player) && !(owner instanceof ITamableEntity t && t.getOwner() != null);

                if(nonFriendlyProjectile){
                    var isNotTarget = !(entity instanceof Player) && !(entity instanceof ITamableEntity t && t.getOwner() != null);
                    if(isNotTarget){
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingDropsEvent(LivingDropsEvent event){
        var entity = event.getEntity();

        if(entity.level() instanceof CustomLevel) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void livingDeathEvent(LivingDeathEvent event){
        var entity = event.getEntity();
        var bonus = entity.tickCount / 10;

        coinDropCalc(entity, bonus);
        onDeathGreaterFrostEffect(entity);
        resetGameModeOnDeath(entity);
        saveDestinyBondItems(entity);
    }

}
