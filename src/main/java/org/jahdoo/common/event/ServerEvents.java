package org.jahdoo.common.event;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.commands.JahdooCommands;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.trial_nexus.ability.abilities_combat.dimensional_recall.DimensionalRecall;
import org.jahdoo.trial_nexus.ability.abilities_combat.nova_smash.NovaSmash;
import org.jahdoo.trial_nexus.ability.abilities_combat.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.player_abilities.MageFlight;
import org.jahdoo.trial_nexus.attachments.player_abilities.Rebound;
import org.jahdoo.trial_nexus.attachments.player_abilities.TripleJump;
import org.jahdoo.trial_nexus.utils.Helpers;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import static org.jahdoo.common.event.TriggerEvents.triggerKillEvent;
import static org.jahdoo.common.event.TriggerEvents.triggerUseEvent;
import static org.jahdoo.common.event.event_helpers.EventHelpers.*;
import static org.jahdoo.common.registers.AttachmentReg.SAVE_ITEM_DATA;
import static org.jahdoo.trial_nexus.utils.Helpers.syncClientData;


@EventBusSubscriber(modid = JahdooMod.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void effectEvent(MobEffectEvent.Applicable event) {
        disallowEffectsInCustomDim(event);
    }

    @SubscribeEvent
    public static void attributeEvent(ItemAttributeModifierEvent event) {
       event.removeIf(s -> removeArmorAttributes(s, event));
       useRuneAttributes(event);
    }

    @SubscribeEvent
    public static void attributeEvent(CurioAttributeModifierEvent event) {
        useRuneAttributesCurios(event);
    }

    @SubscribeEvent
    public static void totem(LivingUseTotemEvent event){
        var entity = event.getEntity();
        if(entity.level() instanceof CustomLevel) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.PlayerRespawnEvent event){
        var player = event.getEntity();
        player.getData(SAVE_ITEM_DATA).takeAllItems(player);
        syncClientData(event.getEntity());
    }

    @SubscribeEvent
    public static void shieldEvent(LivingShieldBlockEvent event){
        var entity = event.getEntity();
        shieldBlock(event, entity);
    }

    @SubscribeEvent
    public static void leftClickBlockInteraction(PlayerInteractEvent.LeftClickBlock event) {
        var item = event.getItemStack();
        var pos = event.getPos();
        var level = event.getLevel();
        var blockState = level.getBlockState(pos);

        setChaosCubeAbility(event, level, pos, item);
        saveBlockType(event, item, blockState, pos);
    }

    @SubscribeEvent
    public static void rightClick(PlayerInteractEvent.RightClickItem rightClickItem) {
        var player = rightClickItem.getEntity();

        triggerUseEvent(player, player.level());
        removeShieldUse(rightClickItem);
    }

    @SubscribeEvent
    public static void onEntityDamageEvent(LivingDamageEvent.Pre event){
        var entity = event.getEntity();
        championLootCalculator(event, entity);
        resilienceDamageRecalculate(event, entity);
        greaterFrostEffectDamageAmplifier(event, entity);
        greaterVitalityEffect(event, entity);
    }

    @SubscribeEvent
    public static void armorEvent(ArmorHurtEvent event) {
        for (var slot : event.getArmorMap().entrySet()) {
            var stack = slot.getValue().armorItemStack;
            if(stack.getItem() instanceof JahdooItem){
                var i = Helpers.durabilityDamageCount(stack);
                if (i == 0) event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        JahdooCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();

        syncPlayerAttributes(player);
        onFirstTimeJoined(player);
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent.Pre event){
        var player = event.getEntity();
        var level = player.level();

        questTracker(level, player);

        if(player instanceof ServerPlayer serverPlayer){
            restrictElytra(serverPlayer, level);
            CasterData.cooldownTickEvent(serverPlayer);
            CasterData.manaTickEvent(serverPlayer);
            CasterData.checkMultiKillStatic(serverPlayer, 3);
        }

        copyPasteBlockProperties(player);
        MageFlight.mageFlightTickEvent(player);
        VitalRejuvenation.staticTickEvent(player);
        DimensionalRecall.staticTickEvent(player);
        NovaSmash.novaSmashTickEvent(player);
        TripleJump.tripleJumpTickEvent(player);
        Rebound.staticTickEvent(player);
    }

    @SubscribeEvent
    public static void levelTickEvent(LevelTickEvent.Pre tickEvent){
        instanceEndingWarning(tickEvent);
        discardLevelOnEnd(tickEvent);
    }

    @SubscribeEvent
    public static void onBlockBreak(PlayerEvent.BreakSpeed event) {
        TrailNexusDimensionEvents.useItemBlockEvent(event);
    }

    @SubscribeEvent
    public static void blockInteraction(UseItemOnBlockEvent event) {
        var item = event.getItemStack().getItem();
        var player = event.getPlayer();
        var pos = event.getPos();
        var level = event.getLevel();
        var getBlock = level.getBlockState(pos);

        TrailNexusDimensionEvents.useItemBlockEvent(event);
        perkTableInteraction(getBlock, level, pos, player, event);
        removeWandInteractionWithBlocks(event, player, item, getBlock);
    }

    @SubscribeEvent
    public static void joinEvent(EntityJoinLevelEvent event){
        syncClientData(event.getEntity());
        removeNonAllowedEffects(event);
        TriggerEvents.onPlayerJoined(event.getEntity());
    }

    @SubscribeEvent
    public static void leaveEvent(EntityLeaveLevelEvent event){
        removeInstanceBuffs(event);
    }

    @SubscribeEvent
    public static void hitEvent(ProjectileImpactEvent event) {
        dontDamageAlliedMobs(event);
    }

    @SubscribeEvent
    public static void livingDropsEvent(LivingDropsEvent event){
        var entity = event.getEntity();

        if(entity.level() instanceof CustomLevel) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void livingDeathEvent(LivingDeathEvent event){
        var entity = event.getEntity();
        var bonus = entity.tickCount / 10;
        var killer = event.getSource().getEntity();

        CasterData.incrementMultiKillStatic(killer);
        triggerKillEvent(killer, entity.level());
        coinDropCalc(entity, bonus);
        onDeathGreaterFrostEffect(entity);
        resetGameModeOnDeath(entity);
        saveDestinyBondItems(entity);

    }

}
