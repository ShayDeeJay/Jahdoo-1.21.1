package org.jahdoo.event.event_helpers;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.jahdoo.ability.abilities.ability_data.Utility.BlockPlacerAbility;
import org.jahdoo.ability.abilities.ability_data.Utility.WallPlacerAbility;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.datagen.DamageTypesProvider;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;

import java.util.Objects;

import static net.minecraft.world.ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
import static org.jahdoo.ability.rarity.JahdooRarity.COMMON;
import static org.jahdoo.ability.rarity.JahdooRarity.RARE;
import static org.jahdoo.items.augments.AugmentItemHelper.throwNewItem;
import static org.jahdoo.items.wand.WandItemHelper.storeBlockType;
import static org.jahdoo.registers.AttachmentRegister.SAVE_DATA;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.ModTags.Block.ALLOWED_BLOCK_INTERACTIONS;

public class EventHelpers {

    public static Entity getEntityPlayerIsLookingAt(Player player, double maxDistance) {
        var eyePosition = player.getEyePosition(1.0F);
        var lookVector = player.getViewVector(1.0F).scale(maxDistance);
        var endPoint = eyePosition.add(lookVector);
        var searchBox = player.getBoundingBox().expandTowards(lookVector).inflate(1.0D);
        var entities = player.level().getEntities(player, searchBox, entity -> entity.isPickable());
        Entity closestEntity = null;
        var closestDistance = maxDistance;

        for (Entity entity : entities) {
            var entityBox = entity.getBoundingBox().inflate(0.3D);
            var hit = entityBox.clip(eyePosition, endPoint);

            if (hit.isPresent()) {
                var distance = eyePosition.distanceTo(hit.get());

                if (distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity;
    }

    public static void setGameModeOnDimChange(PlayerEvent.PlayerChangedDimensionEvent event, Player player) {
        var toMCDim = event.getTo().location().toString().contains("minecraft:");
        var fromCustomDim = event.getFrom().location().toString().contains("jahdoo:");
        var fromMCDim = event.getFrom().location().toString().contains("minecraft:");
        var toCustomDim = event.getTo().location().toString().contains("jahdoo:");

        // Set adventure mode on dim join
        if(fromMCDim && toCustomDim){
            if(player instanceof ServerPlayer serverPlayer){
                if(serverPlayer.gameMode.isSurvival()){
                    serverPlayer.setGameMode(GameType.ADVENTURE);
                }
            }
        }

        // Reset game mode when leaving custom dim
        if(toMCDim && fromCustomDim){
            if(player instanceof ServerPlayer serverPlayer){
                if(serverPlayer.gameMode.isSurvival()){
                    serverPlayer.setGameMode(GameType.SURVIVAL);
                }
            }
        }
    }

    public static void saveBlockType(PlayerInteractEvent.LeftClickBlock event, ItemStack item, BlockState blockState, BlockPos pos) {
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

    public static void useRuneAttributes(ItemAttributeModifierEvent event, ItemStack item) {
        var slotAttributes = item.get(DataComponentRegistry.RUNE_HOLDER.get());
        if(slotAttributes != null){
            for (ItemStack itemStack : slotAttributes.runeSlots()) {
                var mods = itemStack.getAttributeModifiers().modifiers();
                if (!mods.isEmpty()) {
                    var acMod = mods.getFirst();
                    event.addModifier(acMod.attribute(), acMod.modifier(), EquipmentSlotGroup.MAINHAND);
                }
            }
        }
    }

    public static void entityDeathLoot(LivingEntity entity, DamageSource source) {
        if(!entity.level().isClientSide){
            if(entity.shouldDropExperience()){
                var killedByJahdoo = Objects.equals(source.getMsgId(), DamageTypesProvider.JAHDOO_DAMAGE);
                var canGetAugment = Random.nextInt(40) == 0;
                if (killedByJahdoo && canGetAugment) {
                    var canGetCore = Random.nextInt(40) == 0;
                    if (canGetCore) throwNewItem(entity, new ItemStack(ItemsRegister.AUGMENT_CORE.get()));
                    throwNewItem(entity, JahdooRarity.getAbilityAugment(COMMON, RARE));
                }
            }
        }
    }

    public static void saveDestinyBondItems(LivingEntity entity) {
        if(entity instanceof Player player) player.getData(SAVE_DATA).addAllItems(player);
    }

    public static void resetGameModeOnDeath(LivingEntity entity) {
        //Reset game mode if died in custom dim
        if(entity.level() instanceof CustomLevel){
            if(entity instanceof ServerPlayer serverPlayer){
                if(serverPlayer.gameMode.getGameModeForPlayer() == GameType.ADVENTURE){
                    serverPlayer.setGameMode(GameType.SURVIVAL);
                }
            }
        }
    }

    public static void disallowEffectsInCustomDim(MobEffectEvent.Applicable event) {
        if(event.getEntity().level() instanceof CustomLevel){
            if( !(event.getEffectInstance() instanceof CustomMobEffect) && event.getEffectInstance().getEffect().value().isBeneficial()){
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);

            }
        }
    }

    public static void removeWandInteractionWithBlocks(UseItemOnBlockEvent event, Player player, Item item, BlockState getBlock) {
        if(player != null){
            if (item instanceof WandItem && !player.isShiftKeyDown() && !getBlock.is(ALLOWED_BLOCK_INTERACTIONS)) {
                event.cancelWithResult(SKIP_DEFAULT_BLOCK_INTERACTION);
            }
        }
    }
}
