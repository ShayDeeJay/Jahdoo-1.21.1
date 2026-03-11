package org.jahdoo.common.event;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.registers.mod.EntityEffectReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.effects.AbstractEntityEffect;
import org.jahdoo.trial_nexus.attachments.effects.FrostEffect;
import org.jahdoo.trial_nexus.attachments.player_abilities.Blink;
import org.jahdoo.trial_nexus.attachments.player_abilities.MageFlight;
import org.jahdoo.trial_nexus.attachments.player_abilities.PhantomJump;
import org.jahdoo.trial_nexus.attachments.player_abilities.Rebound;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.magic.abilities_combat.dimensional_recall.DimensionalRecall;
import org.jahdoo.trial_nexus.magic.abilities_combat.nova_smash.NovaSmash;
import org.jahdoo.trial_nexus.magic.abilities_combat.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.trial_nexus.mobs.mob_setup.MiniBossMobs;
import org.jahdoo.trial_nexus.utils.JahdooCommands;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import static org.jahdoo.common.event.TriggerEvents.triggerKillEvent;
import static org.jahdoo.common.event.TriggerEvents.triggerUseEvent;
import static org.jahdoo.common.event.event_helpers.EventHelpers.*;
import static org.jahdoo.common.registers.AttachmentReg.SAVE_ITEM_DATA;
import static org.jahdoo.trial_nexus.mobs.mob_setup.MiniBossMobs.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.durabilityDamageCount;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.syncClientData;


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
        if(LevelGenerator.isNexus(entity.level())) event.setCanceled(true);
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

        blockNoAIDamage(event, entity);
        shieldBlock(event, entity);
    }

    @SubscribeEvent
    public static void leftClickBlockInteraction(PlayerInteractEvent.LeftClickBlock event) {
        var item = event.getItemStack();
        var pos = event.getPos();
        var level = event.getLevel();
        var blockState = level.getBlockState(pos);

//        setAbilityToItem(event, level, pos, item);
//        setChaosCubeAbility(event, level, pos, item);
        saveBlockType(event, item, blockState, pos);
    }

    @SubscribeEvent
    public static void blockInteraction(UseItemOnBlockEvent event) {
        var item = event.getItemStack().getItem();
        var player = event.getPlayer();
        var pos = event.getPos();
        var level = event.getLevel();
        var getBlock = level.getBlockState(pos);

        TrailNexusDimensionEvents.mineBlockEvent(event);
        perkTableInteraction(getBlock, level, pos, player, event);
        removeWandInteractionWithBlocks(event, player, item, getBlock);
    }

    @SubscribeEvent
    public static void mobGriefEvent(EntityMobGriefingEvent event) {
        if(event.getEntity() instanceof IABoss_monster) event.setCanGrief(false);
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
                var i = durabilityDamageCount(stack);
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

        if(player instanceof ServerPlayer serverPlayer){
            restrictElytra(serverPlayer, level);
            CasterData.cooldownTickEvent(serverPlayer);
            CasterData.manaTickEvent(serverPlayer);
            CasterData.checkMultiKillStatic(serverPlayer, 3);
            questTracker(level, player);
            Blink.blinkTickEvent(serverPlayer);
        }

        MageFlight.mageFlightTickEvent(player);
        copyPasteBlockProperties(player);
        VitalRejuvenation.staticTickEvent(player);
        DimensionalRecall.staticTickEvent(player);
        NovaSmash.novaSmashTickEvent(player);
        PhantomJump.jumpTickEvent(player);
        Rebound.staticTickEvent(player);
    }

    @SubscribeEvent
    public static void levelTickEvent(LevelTickEvent.Pre tickEvent){
        instanceEndingWarning(tickEvent);
        discardLevelOnEnd(tickEvent);
    }

    @SubscribeEvent
    public static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        TrailNexusDimensionEvents.breakBlockEvent(event);
    }

    @SubscribeEvent
    public static void onMineEvent(PlayerEvent.BreakSpeed event) {
        TrailNexusDimensionEvents.mineBlockEvent(event);
    }

    @SubscribeEvent
    public static void joinEvent(EntityJoinLevelEvent event){
        syncClientData(event.getEntity());
        removeNonAllowedEffects(event);
        TriggerEvents.onPlayerJoined(event.getEntity());
    }

    @SubscribeEvent
    public static void teleportEvent(EntityTeleportEvent event){
        //Method to stop player teleporting out of dim unless using the portal
//        if(event.)
    }

    @SubscribeEvent
    public static void leaveEvent(EntityLeaveLevelEvent event){
        removeInstanceBuffs(event);
    }

    @SubscribeEvent
    public static void hitEvent(ProjectileImpactEvent event) {
        avoidDamageAlliedMobs(event);
    }

    @SubscribeEvent
    public static void livingDropsEvent(LivingDropsEvent event){
        var entity = event.getEntity();
        if(LevelGenerator.isNexus(entity.level())) event.setCanceled(true);
    }

    @SubscribeEvent
    private static void entityInteractEvent(PlayerInteractEvent.EntityInteractSpecific event){
        var entity = event.getTarget();
        var player = event.getEntity();
        if(!player.level().isClientSide){
            AbstractEntityEffect.setTypeEffect(FrostEffect::new, player, (LivingEntity) entity, true, 300, 1);

            if(player.level() instanceof ServerLevel serverLevel){
                var level = serverLevel.getServer();
//                level.tickRateManager().setFrozen(false);
//                ((LivingEntity) entity).hurtDuration = 0;
            }
//            System.out.println("323");
        }

        rightClickInteract(event);
    }

    @SubscribeEvent
    private static void entityTickEvent(EntityTickEvent.Pre event){
        var entity = event.getEntity();
        if(entity instanceof LivingEntity livingEntity && livingEntity.level() instanceof ServerLevel){
            for (var effect : EntityEffectReg.getAll()) {
                AbstractEntityEffect.serverOnTick(livingEntity, effect.getAttachment());
            }
        }

        tickDeathLootsplotion(event);
    }

    @SubscribeEvent
    public static void livingDeathEvent(LivingDeathEvent event){
        var entity = event.getEntity();
        var bonus = entity.tickCount / 10;
        var killer = event.getSource().getEntity();

        if(LevelGenerator.isNexus(entity.level())){
            MiniBossMobs.onDeath(entity);
            CasterData.incrementMultiKillStatic(killer);
        }

        triggerKillEvent(killer, entity.level());
        coinDropCalc(entity, bonus);
        onDeathGreaterFrostEffect(entity);
        resetGameModeOnDeath(entity);
        saveDestinyBondItems(entity);
    }

}
