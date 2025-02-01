package org.jahdoo.event;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import org.jahdoo.JahdooMod;
import org.jahdoo.attachments.CastingData;
import org.jahdoo.attachments.player_abilities.*;
import org.jahdoo.challenge.LevelGenerator;
import org.jahdoo.entities.living.CustomSkeleton;
import org.jahdoo.entities.living.CustomZombie;
import org.jahdoo.entities.living.EternalWizard;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.List;
import java.util.Objects;

import static org.jahdoo.challenge.LevelGenerator.DimHandler.TRADING_POST;
import static org.jahdoo.challenge.LevelGenerator.DimHandler.TRIAL;
import static org.jahdoo.event.event_helpers.CopyPasteEvent.copyPasteBlockProperties;
import static org.jahdoo.event.event_helpers.EventHelpers.*;
import static org.jahdoo.registers.AttachmentRegister.SAVE_DATA;
import static org.jahdoo.utils.ModHelpers.Random;


@EventBusSubscriber(modid = JahdooMod.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent.Pre event){
        var player = event.getEntity();

        if(player instanceof ServerPlayer serverPlayer){
            CastingData.cooldownTickEvent(serverPlayer);
            CastingData.manaTickEvent(serverPlayer);
        }

        copyPasteBlockProperties(player);
        MageFlight.mageFlightTickEvent(player);
        PlayerScale.staticTickEvent(player);
        Static.staticTickEvent(player);
        VitalRejuvenation.staticTickEvent(player);
        DimensionalRecall.staticTickEvent(player);
        NovaSmash.novaSmashTickEvent(player);
        TripleJump.tripleJumpTickEvent(player);
        BouncyFoot.staticTickEvent(player);
    }

    @SubscribeEvent
    public static void blockInteraction(UseItemOnBlockEvent event) {
        var item = event.getItemStack().getItem();
        var player = event.getPlayer();
        var pos = event.getPos();
        var getBlock = event.getLevel().getBlockState(pos);

//        if(item instanceof WandItem){
//            var block = BlocksRegister.TRAIL_PORTAL.get();
//            var setBlockState = block.defaultBlockState().setValue(DIMENSION_KEY, KEY_TRADING_POST);
//            event.getLevel().setBlockAndUpdate(pos.above(), setBlockState);
//        }

        removeWandInteractionWithBlocks(event, player, item, getBlock);
    }

    @SubscribeEvent
    public static void dimChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        var player = event.getEntity();
        setGameModeOnDimChange(event, player);
    }

    @SubscribeEvent
    public static void effectEvent(MobEffectEvent.Applicable event) {
        disallowEffectsInCustomDim(event);
    }

    @SubscribeEvent
    public static void itemClickEvent(PlayerInteractEvent.RightClickItem event) {
        var mainHand = event.getItemStack();
        var player = event.getEntity();

//        System.out.println(event.getLevel());
//        System.out.println(mainHand.get(DataComponentRegistry.RUNE_HOLDER));

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
    public static void onEntityDamageEvent(LivingDamageEvent.Pre event){
        var entity = event.getEntity();
        greaterFrostEffectDamageAmplifier(event, entity);
        greaterVitalityEffect(event, entity);
    }

    @SubscribeEvent
    public static void levelTickEvent(LevelTickEvent.Pre tickEvent){
        if(!(tickEvent.getLevel() instanceof CustomLevel level)) return;
        for (var entity : level.getEntities().getAll()) {
            if(entity instanceof Mob mob && mob.getTarget() == null){
                var players = level.players();
                if(!players.isEmpty()){
                    mob.setTarget(ModHelpers.getRandomListElement(players));
                }
            }
        }
    }

    @SubscribeEvent
    public static void totem(LivingUseTotemEvent event){
        var entity = event.getEntity();

        if(entity.level() instanceof CustomLevel) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void levelTickEvent(EntityJoinLevelEvent event){
        var level = event.getLevel();
        var entity = event.getEntity();

        if(level instanceof CustomLevel customLevel && entity instanceof Player player){
            var getData = ChallengeLevelData.getProperties(customLevel);
            if(Objects.equals(getData.dimType, TRIAL)){
                LevelGenerator.playerSetup(player, getData.round());
            }

            if(Objects.equals(getData.dimType, TRADING_POST)){
                ChallengeLevelData.setDimension(customLevel, TRIAL);
            }
        }
    }

    @SubscribeEvent
    public static void leftClickBlockInteraction(PlayerInteractEvent.LeftClickBlock event) {
        var item = event.getItemStack();
        var pos = event.getPos();
        var blockState = event.getLevel().getBlockState(pos);
        saveBlockType(event, item, blockState, pos);
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
    public static void livingDeathEvent(LivingDeathEvent event){
        var entity = event.getEntity();
        var source = event.getSource();
        var bonus = entity.tickCount / 10;

        if(entity.level() instanceof CustomLevel){
            var max = Math.max(1, bonus);
            if(entity instanceof CustomZombie){
                if(Random.nextInt(0, Math.min(10, max)) == 0){
                    var stack = new ItemStack(ItemsRegister.BRONZE_COIN).copyWithCount(Math.max(1, 10 - bonus));
                    BehaviorUtils.throwItem(entity, stack, entity.position());
                }
            }

            if(entity instanceof CustomSkeleton){
                if(Random.nextInt(0,Math.min(5, max)) == 0){
                    var stack = new ItemStack(ItemsRegister.BRONZE_COIN).copyWithCount(Math.max(1, 20 - bonus));
                    BehaviorUtils.throwItem(entity, stack, entity.position());
                }
            }

            if(entity instanceof EternalWizard wizard){
                if(wizard.getOwner() == null){
                    if (Random.nextInt(0,Math.min(10 , max)) == 0) {
                        var stack = new ItemStack(ItemsRegister.SILVER_COIN).copyWithCount(Math.max(1, 10 - bonus));
                        BehaviorUtils.throwItem(entity, stack, entity.position());
                    }
                }
            }
        }

        onDeathGreaterFrostEffect(entity);
        resetGameModeOnDeath(entity);
        saveDestinyBondItems(entity);
        entityDeathLoot(entity, source);
    }

    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.PlayerRespawnEvent event){
        var player = event.getEntity();
        player.getData(SAVE_DATA).takeAllItems(player);
    }

}
