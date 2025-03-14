package org.jahdoo.common.event;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.abilities.dimensional_recall.DimensionalRecall;
import org.jahdoo.ascension.ability.abilities.nova_smash.NovaSmash;
import org.jahdoo.ascension.ability.abilities.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.ascension.attachments.CastingData;
import org.jahdoo.ascension.attachments.player_abilities.BouncyFoot;
import org.jahdoo.ascension.attachments.player_abilities.MageFlight;
import org.jahdoo.ascension.attachments.player_abilities.TripleJump;
import org.jahdoo.common.networking.server2client.MoveClientEntityS2CP;
import org.jahdoo.common.networking.server2client.WalletSyncS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import static org.jahdoo.common.event.event_helpers.CopyPasteEvent.copyPasteBlockProperties;
import static org.jahdoo.common.event.event_helpers.EventHelpers.*;
import static org.jahdoo.common.registers.AttachmentReg.SAVE_DATA;


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
    public static void levelTickEvent(EntityJoinLevelEvent event){
        var entity = event.getEntity();

        if(entity instanceof ServerPlayer player){
            var wallet = player.getData(AttachmentReg.PLAYER_WALLET).getWallet();
            PacketDistributor.sendToPlayer(player, new WalletSyncS2CP(wallet));

//            if(!customLevel.getDescriptionKey().contains(TRADING_POST)){
//                LevelGenerator.playerSetup(player, getData.round());
//            }
//
//            if(Objects.equals(getData.dimType, TRADING_POST)){
//                ChallengeLevelData.setDimension(customLevel, TRIAL);
//            }
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
    public static void livingDeathEvent(LivingDeathEvent event){
        var entity = event.getEntity();
        var bonus = entity.tickCount / 10;

        coinDropCalc(entity, bonus);
        onDeathGreaterFrostEffect(entity);
        resetGameModeOnDeath(entity);
        saveDestinyBondItems(entity);
    }


}
