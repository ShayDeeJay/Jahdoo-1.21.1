package org.jahdoo.common.event.event_helpers;

import com.mojang.math.Axis;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.apache.logging.log4j.Level;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.abilities.block_placer.BlockPlacerAbility;
import org.jahdoo.ascension.ability.abilities.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.ascension.ability.abilities.wall_placer.WallPlacerAbility;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.utils.ModTags;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.ElementReg;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import static net.minecraft.world.ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
import static net.minecraft.world.entity.EquipmentSlotGroup.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.Helpers.getSoundWithPosition;
import static org.jahdoo.ascension.utils.ModTags.Block.ALLOWED_BLOCK_INTERACTIONS;
import static org.jahdoo.common.items.wand.WandItemHelper.storeBlockType;
import static org.jahdoo.common.particle.ParticleHandlers.getAllParticleTypes;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.registers.AttachmentReg.SAVE_DATA;
import static org.jahdoo.common.registers.ComponentReg.INTERACTION_HAND;
import static org.jahdoo.common.registers.ComponentReg.RUNE_HOLDER;

public class EventHelpers {

    public static void saveDestinyBondItems(LivingEntity entity) {
        if(entity instanceof Player player) player.getData(SAVE_DATA).addAllItems(player);
    }

    public static void disallowEffectsInCustomDim(MobEffectEvent.Applicable event) {
        if(!(event.getEntity() instanceof Player player)) return;

        if(event.getEntity().level() instanceof CustomLevel && !player.isCreative()){
            if(!(event.getEffectInstance() instanceof JahdooMobEffect) && event.getEffectInstance().getEffect().value().isBeneficial()){
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }

    public static void removeWandInteractionWithBlocks(UseItemOnBlockEvent event, Player player, Item item, BlockState getBlock) {
        if(player != null){
            var isAllowed = !getBlock.is(ALLOWED_BLOCK_INTERACTIONS);
            var isShift = !player.isShiftKeyDown();
            var isWand = item instanceof WandItem;

            if (isWand && isShift && isAllowed) {
                event.cancelWithResult(SKIP_DEFAULT_BLOCK_INTERACTION);
            }
        }
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

    public static void greaterVitalityEffect(LivingDamageEvent.Pre event, LivingEntity entity) {
        if(entity.hasEffect(EffectReg.GREATER_VITALITY_EFFECT)){
            var getAttacker = event.getSource().getEntity();
            if(Random.nextInt(4) == 0){
                if(getAttacker instanceof Player player){
                    VitalRejuvenation.successfulCastAnimation(player);
                    player.heal(2);
                }
            }
        }
    }

    public static void onDeathGreaterFrostEffect(LivingEntity entity) {
        if(entity.hasEffect(EffectReg.GREATER_FROST_EFFECT)){
            getSoundWithPosition(entity.level(), entity.blockPosition(), SoundEvents.GLASS_BREAK, 1, 1);
            getAllParticleTypes(ElementReg.frost(), 20, 1);
            sendParticles(
                  entity.level(),
                  getAllParticleTypes(ElementReg.frost(), 12, 1.5f),
                  entity.position().add(0, entity.getBbHeight()/2, 0), 30,
                  0, 1, 0, 0.2
            );
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

    public static void greaterFrostEffectDamageAmplifier(LivingDamageEvent.Pre event, LivingEntity entity) {
        if(entity.hasEffect(EffectReg.GREATER_FROST_EFFECT)){
            var origin = event.getOriginalDamage();
            var modifiedDamage = origin * 2;
            if(!entity.isAlive()){
                getSoundWithPosition(entity.level(), entity.blockPosition(), SoundEvents.GLASS_BREAK, 1, 1);
                getAllParticleTypes(ElementReg.frost(), 20, 1);
                sendParticles(
                    entity.level(),
                    getAllParticleTypes(ElementReg.frost(), 12, 1.5f),
                    entity.position().add(0, entity.getBbHeight()/2, 0), 30,
                    0, 1, 0, 0.2
                );
            }
            event.setNewDamage(modifiedDamage);
        }
    }

    public static void mysticEffectClient(RenderLivingEvent.Pre event) {
        var entity = event.getEntity();
        var effect = EffectReg.MYSTIC_EFFECT;
        var putEffect = entity.getEffect(effect);
        if(entity.hasEffect(effect)){
            var height = entity.getBbHeight() / 2;
            var tick = entity.tickCount;
            var anim = (tick + event.getPartialTick());
            var pos = event.getPoseStack();
            pos.rotateAround(Axis.XN.rotationDegrees(anim), 0, height, 0);
            pos.rotateAround(Axis.YN.rotationDegrees(anim), 0, height, 0);
            pos.rotateAround(Axis.ZN.rotationDegrees(anim), 0, height, 0);
            if(putEffect.getDuration() == 0) entity.removeEffect(effect);
        }
    }

    public static void useRuneAttributesCurios(CurioAttributeModifierEvent event) {
        var item = event.getItemStack();
        var slotAttributes = item.get(RUNE_HOLDER.get());
        if(slotAttributes != null) {
            for (var itemStack : slotAttributes.runeSlots()) {
                var mods = itemStack.getAttributeModifiers().modifiers();
                if (mods.isEmpty()) return;
                var acMod = mods.getFirst();
                try {
                    event.addModifier(acMod.attribute(), acMod.modifier());
                } catch (Exception e){
                    JahdooMod.LOGGER.log(Level.ALL, e);
                }
            }

        }

        for (var modifier : item.getAttributeModifiers().modifiers()) {
            event.addModifier(modifier.attribute(), modifier.modifier());
        }
    }

    public static void useRuneAttributes(ItemAttributeModifierEvent event) {
        var item = event.getItemStack();
        var slotAttributes = item.get(RUNE_HOLDER.get());
        var handComponent = item.get(INTERACTION_HAND);
        var hand = handComponent == null ? 2 : handComponent;
        if(slotAttributes == null || item.getItem() instanceof ICurioItem) return;

        if(item.is(ModTags.Items.WAND_TAGS) && hand == 2) return;

        for (ItemStack itemStack : slotAttributes.runeSlots()) {
            var mods = itemStack.getAttributeModifiers().modifiers();
            if (mods.isEmpty()) return;
            var acMod = mods.getFirst();
            /* Would be nice if you could actually control the hand allowed. One way would be to add a component that changes
             *  when swapped. Or just get slot context from inventory tick? */
            var slot = item.getItem() instanceof ArmorItem ? ARMOR : hand == 0 ? MAINHAND : OFFHAND;

            event.addModifier(acMod.attribute(), acMod.modifier(), slot);
        }

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

    public static Entity getEntityPlayerIsLookingAt(Player player, double maxDistance) {
        var eyePosition = player.getEyePosition(1.0F);
        var lookVector = player.getViewVector(1.0F).scale(maxDistance);
        var endPoint = eyePosition.add(lookVector);
        var searchBox = player.getBoundingBox().expandTowards(lookVector).inflate(1.0D);
        var entities = player.level().getEntities(player, searchBox, entity -> entity.isPickable());
        Entity closestEntity = null;
        var closestDistance = maxDistance;

        for (var entity : entities) {
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
}
