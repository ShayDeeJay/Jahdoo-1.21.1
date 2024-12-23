package org.jahdoo.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.ability.all_abilities.abilities.Utility.BlockPlacerAbility;
import org.jahdoo.ability.all_abilities.abilities.Utility.WallPlacerAbility;
import org.jahdoo.attachments.CastingData;
import org.jahdoo.attachments.player_abilities.*;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.datagen.DamageTypesProvider;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;

import java.util.Objects;

import static net.minecraft.world.ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
import static org.jahdoo.ability.rarity.JahdooRarity.COMMON;
import static org.jahdoo.ability.rarity.JahdooRarity.RARE;
import static org.jahdoo.event.event_helpers.CopyPasteEvent.copyPasteBlockProperties;
import static org.jahdoo.items.augments.AugmentItemHelper.throwNewItem;
import static org.jahdoo.items.wand.WandItemHelper.storeBlockType;
import static org.jahdoo.registers.AttachmentRegister.SAVE_DATA;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.ModTags.Block.ALLOWED_BLOCK_INTERACTIONS;


@EventBusSubscriber(modid = JahdooMod.MOD_ID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent.Pre event){
        Player player = event.getEntity();
        copyPasteBlockProperties(player);

        if(player instanceof ServerPlayer serverPlayer){
            CastingData.cooldownTickEvent(serverPlayer);
            CastingData.manaTickEvent(serverPlayer);
        }

        MageFlight.mageFlightTickEvent(player);
        PlayerScale.staticTickEvent(player);
        Static.staticTickEvent(player);
        VitalRejuvenation.staticTickEvent(player);
        DimensionalRecall.staticTickEvent(player);
        NovaSmash.novaSmashTickEvent(player);
        BouncyFoot.staticTickEvent(player);
    }

    @SubscribeEvent
    public static void blockInteraction(UseItemOnBlockEvent event) {
        var item = event.getItemStack().getItem();
        var player = event.getPlayer();
        var getBlock = event.getLevel().getBlockState(event.getPos());
        if(player != null){
            if (item instanceof WandItem && !player.isShiftKeyDown() && !getBlock.is(ALLOWED_BLOCK_INTERACTIONS)) {
                event.cancelWithResult(SKIP_DEFAULT_BLOCK_INTERACTION);
            }
        }
    }

    @SubscribeEvent
    public static void leftClickBlockInteraction(PlayerInteractEvent.LeftClickBlock event) {
        var item = event.getItemStack();
        var pos = event.getPos();
        var blockState = event.getLevel().getBlockState(pos);

        if(event.getItemStack().getItem() instanceof WandItem){
            if(event.getEntity().isShiftKeyDown()){
                var name = DataComponentHelper.getAbilityTypeItemStack(item);
                var wallPlacer = WallPlacerAbility.abilityId.getPath().intern();
                var blockPlacer = BlockPlacerAbility.abilityId.getPath().intern();

                if(name.equals(wallPlacer) || name.equals(blockPlacer)){
                    storeBlockType(item, blockState, event.getEntity(), pos);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void attributeEvent(ItemAttributeModifierEvent event) {
        var item = event.getItemStack();
        var slotAttributes = item.get(DataComponentRegistry.WAND_DATA.get());
        if(item.getItem() instanceof WandItem && slotAttributes != null){
            for (ItemStack itemStack : slotAttributes.runeSlots()) {
                var mods = itemStack.getAttributeModifiers().modifiers();
                if (!mods.isEmpty()) {
                    var acMod = mods.getFirst();
                    event.addModifier(acMod.attribute(), acMod.modifier(), EquipmentSlotGroup.MAINHAND);
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingDeathEvent(LivingDeathEvent event){
        var entity = event.getEntity();
        var source = event.getSource();
        if(entity instanceof Player player) player.getData(SAVE_DATA).addAllItems(player);
        if(!entity.level().isClientSide){
            var killedByJahdoo = Objects.equals(source.getMsgId(), DamageTypesProvider.JAHDOO_DAMAGE);
            var canGetAugment = Random.nextInt(40) == 0;
            if(killedByJahdoo && canGetAugment) {
                var canGetCore = Random.nextInt(40) == 0;
                if(canGetCore) throwNewItem(entity, new ItemStack(ItemsRegister.AUGMENT_CORE.get()));
                throwNewItem(entity, JahdooRarity.getAbilityAugment(COMMON, RARE));
            }
        }
    }


    @SubscribeEvent
    public static void playerCloneEvent(PlayerEvent.PlayerRespawnEvent event){
        var player = event.getEntity();
        player.getData(SAVE_DATA).takeAllItems(player);
    }

}
