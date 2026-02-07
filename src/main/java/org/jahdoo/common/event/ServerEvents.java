package org.jahdoo.common.event;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
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
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.List;

import static com.github.L_Ender.cataclysm.init.ModEntities.*;
import static org.jahdoo.common.event.TriggerEvents.triggerKillEvent;
import static org.jahdoo.common.event.TriggerEvents.triggerUseEvent;
import static org.jahdoo.common.event.event_helpers.EventHelpers.*;
import static org.jahdoo.common.registers.AttachmentReg.SAVE_ITEM_DATA;
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

        TrailNexusDimensionEvents.useItemBlockEvent(event);
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

//        spawnAllTestEntities(player);

        triggerUseEvent(player, player.level());
        removeShieldUse(rightClickItem);
    }

    public static void spawnAllTestEntities(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        BlockPos origin = player.blockPosition().offset(0, 0, 6);

        int spacing = 8;      // blocks between entities
        int perRow = 4;       // grid width

        int index = 0;

        for (DeferredHolder<EntityType<?>, ? extends EntityType<? extends Entity>> supplier : SPAWN_TEST_ENTITIES) {
            EntityType<? extends Entity> type = supplier.get();

            Entity entity = type.create(serverLevel);
            if (entity == null) continue;

            int xOffset = (index % perRow) * spacing;
            int zOffset = (index / perRow) * spacing;

            entity.moveTo(
                origin.getX() + xOffset + 0.5,
                origin.getY(),
                origin.getZ() + zOffset + 0.5,
                player.getYRot(),
                0
            );

            if (entity instanceof Mob mob) {
                mob.setNoAi(true);
                mob.setPersistenceRequired();
            }

            serverLevel.addFreshEntity(entity);
            index++;
        }
    }

    public static final List<DeferredHolder<EntityType<?>, ? extends EntityType<? extends Entity>>> SPAWN_TEST_ENTITIES = List.of(

        // ===== BOSSES / LARGE MOBS =====
        ENDER_GOLEM,
        ENDER_GUARDIAN,
        NETHERITE_MONSTROSITY,
        IGNIS,
        THE_HARBINGER,
        THE_PROWLER,
        THE_LEVIATHAN,
        ANCIENT_REMNANT,
        ANCIENT_ANCIENT_REMNANT,
        MALEDICTUS,
        CLAWDIAN,
        SCYLLA,
        WADJET,
        KOBOLEDIATOR,
        APTRGANGR,

        // ===== MEDIUM MOBS =====
        CORAL_GOLEM,
        CORALSSUS,
        IGNITED_REVENANT,
        IGNITED_BERSERKER,
        AMETHYST_CRAB,
        HIPPOCAMTUS,
        CINDARIA,
        DRAUGR,
        ROYAL_DRAUGR,
        ELITE_DRAUGR,
        DEEPLING_BRUTE,
        DEEPLING_ANGLER,
        DEEPLING_PRIEST,
        DEEPLING_WARLOCK,

        // ===== SMALL / NORMAL MOBS =====
        DEEPLING,
        ENDERMAPTERA,
        LIONFISH,
        URCHINKIN,
        KOBOLETON,
        THE_WATCHER,
        SYMBIOCTO,
        DROWNED_HOST,
        MODERN_REMNANT,

        // ===== CREATURES / PASSIVES =====
        NETHERITE_MINISTROSITY,
        THE_BABY_LEVIATHAN,

        // ===== SOLID MISC ENTITIES (SAFE TO SPAWN) =====
        VOID_RUNE,
        ABYSS_MINE,
        CM_FALLING_BLOCK,
        VOID_VORTEX,
        DIMENSIONAL_RIFT,
        ABYSS_PORTAL,
        ABYSS_BLAST_PORTAL,
        ACCRETION,
        EYE_OF_DUNGEON,
        SANDSTORM,
        CURSED_SANDSTORM,
        ANCIENT_DESERT_STELE,
        WITHER_SMOKE_EFFECT,
        LIGHTNING_AREA_EFFECT,
        FLAME_STRIKE,
        EARTHQUAKE
    );

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

        if(entity.level() instanceof CustomLevel){
            CasterData.incrementMultiKillStatic(killer);
        }
        triggerKillEvent(killer, entity.level());
        coinDropCalc(entity, bonus);
        onDeathGreaterFrostEffect(entity);
        resetGameModeOnDeath(entity);
        saveDestinyBondItems(entity);

    }

}
