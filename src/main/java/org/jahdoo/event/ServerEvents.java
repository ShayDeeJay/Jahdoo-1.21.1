package org.jahdoo.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.item.ItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.attachments.CastingData;
import org.jahdoo.attachments.player_abilities.*;
import org.jahdoo.components.rune_data.RuneHolder;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.registers.DataComponentRegistry;

import java.util.ArrayList;

import static org.jahdoo.event.event_helpers.CopyPasteEvent.copyPasteBlockProperties;
import static org.jahdoo.event.event_helpers.EventHelpers.*;
import static org.jahdoo.registers.AttachmentRegister.SAVE_DATA;


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
        var getBlock = event.getLevel().getBlockState(event.getPos());

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

        if(mainHand.has(DataComponentRegistry.RUNE_HOLDER)){
            var rune = player.getOffhandItem();
            var list = new ArrayList<ItemStack>();
            System.out.println("iom,ere");
            if(rune.getItem() instanceof RuneItem){
                list.add(rune);
                System.out.println("im here");
                RuneHolder.updateRuneSlots(mainHand, list);
                event.setCanceled(true);
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
        var item = event.getItemStack();
        useRuneAttributes(event, item);
    }

    @SubscribeEvent
    public static void livingDeathEvent(LivingDeathEvent event){
        var entity = event.getEntity();
        var source = event.getSource();

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
