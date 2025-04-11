package org.jahdoo.common.event.event_helpers;

import com.mojang.math.Axis;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.abilities_combat.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.ascension.ability.abilities_utility.block_placer.BlockPlacerAbility;
import org.jahdoo.ascension.ability.abilities_utility.wall_placer.WallPlacerAbility;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.attachments.CastingData;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.ascension.trading_post.RewardLootTables;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.ModTags;
import org.jahdoo.common.block.perk_table.PerkTableEntity;
import org.jahdoo.common.entities.CustomSkeleton;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.entities.SharedEntityBehaviours;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.void_spider.VoidSpider;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.networking.server2client.InstanceSyncS2CP;
import org.jahdoo.common.networking.server2client.WalletSyncS2CP;
import org.jahdoo.common.registers.*;
import org.jahdoo.common.registers.mod.ElementReg;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;

import static java.util.Objects.requireNonNull;
import static net.minecraft.sounds.SoundSource.PLAYERS;
import static net.minecraft.world.ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
import static net.minecraft.world.entity.EquipmentSlotGroup.*;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.VAULT;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN;
import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.ascension.mobs.MobItemHandler.getEnchantedArmor;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.ModTags.Block.ALLOWED_BLOCK_INTERACTIONS;
import static org.jahdoo.common.block.loot_chest.LootChestBlock.lootsplosian;
import static org.jahdoo.common.items.wand.WandItemHelper.storeBlockType;
import static org.jahdoo.common.particle.ParticleHandlers.getAllParticleTypes;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.registers.AttachmentReg.*;
import static org.jahdoo.common.registers.ComponentReg.INTERACTION_HAND;
import static org.jahdoo.common.registers.ComponentReg.RUNE_HOLDER;

public class EventHelpers {

    public static void saveDestinyBondItems(LivingEntity entity) {
        if(entity instanceof Player player) {
            player.getData(SAVE_DATA).addAllItems(player);
        }
    }

    public static int getColour(ItemStack stack){
        var colour = stack.get(ComponentReg.RUNE_DATA.get());
        if(colour != null) return colour.colour();
        return -1;
    }

    public static void perkTableInteraction(
        BlockState getBlock,
        Level level,
        BlockPos pos,
        Player player
    ){
        if(getBlock.is(Blocks.BARRIER)){
            if(level.getBlockEntity(pos.below(1)) instanceof PerkTableEntity entity){
                entity.setUsed(entity.getBlockState(), player);
                level.destroyBlock(pos, false);
            }
        }
    }

    public static void disallowEffectsInCustomDim(MobEffectEvent.Applicable event) {
        if(!(event.getEntity() instanceof Player player)) return;

        if(event.getEntity().level() instanceof CustomLevel && !player.isCreative()){
            if(!(event.getEffectInstance() instanceof JahdooMobEffect) && event.getEffectInstance().getEffect().value().isBeneficial()){
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }

    public static void removeCurrentEffects(EntityJoinLevelEvent event) {
        var entity = event.getEntity();

        if(entity instanceof ServerPlayer player){
            var wallet = player.getData(PLAYER_WALLET).getWallet();
            sendToPlayer(player, new WalletSyncS2CP(wallet));
            if(event.getLevel() instanceof CustomLevel){
                player.removeAllEffects();
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

    /**
     * Need this to init player attributes for the attribute screen,
     * without it any attributes with 0 value wont show
     * */
    public static void syncPlayerAttributes(Player player) {
        for (var entry : AttributeReg.ATTRIBUTES.getEntries()) {
            player.getAttributes().getInstance(entry);
        }
    }

    public static void onFirstTimeJoined(CompoundTag data, Player player, CompoundTag playerData) {
        if (!data.getBoolean("first_join")) {
            if(player instanceof ServerPlayer serverPlayer){
                var castingData = player.getData(CASTER_DATA.get());
                castingData.playerInit();
                sendToPlayer(serverPlayer, new CastingDataSyncS2CP(castingData));
            }
            if(player.level() instanceof ServerLevel serverLevel){
                getStarterKit(player, serverLevel);
            }
            data.putBoolean("first_join", true);
            playerData.put(Player.PERSISTED_NBT_TAG, data);
        }
    }

    public static void saveBlockType(PlayerInteractEvent.LeftClickBlock event, ItemStack item, BlockState blockState, BlockPos pos) {
        if(event.getItemStack().getItem() instanceof WandItem){
            if(event.getEntity().isShiftKeyDown()){
                var name = CastingData.selectedAbility(event.getEntity());
                var wallPlacer = WallPlacerAbility.abilityId.getPath().intern();
                var blockPlacer = BlockPlacerAbility.abilityId.getPath().intern();

                if(name.equals(wallPlacer) || name.equals(blockPlacer)){
                    storeBlockType(item, blockState, event.getEntity(), pos);
                    event.setCanceled(true);
                }
            }
        }
    }


    public static void dontDamageAlliedMobs(ProjectileImpactEvent event) {
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

    public static void getStarterKit(Player player, ServerLevel serverLevel) {
        var freeItems = new ArrayList<ItemStack>();
        var element = ElementReg.random();

        for (int i = 0; i < 2; i++){
            freeItems.add(ItemStack.EMPTY);
        }

        var params = new LootParams.Builder(serverLevel).withParameter(ORIGIN, player.position()).create(VAULT);
        freeItems.addAll(
            getEnchantedArmor(
                serverLevel,
                element,
                Items.IRON_HELMET,
                Items.IRON_CHESTPLATE,
                Items.IRON_LEGGINGS,
                Items.IRON_BOOTS,
                Items.IRON_SWORD
            ).getRandomItems(params)
        );

        for (int i = 0; i < 5; i++) freeItems.add(ItemStack.EMPTY);

        freeItems.add(new ItemStack(ItemReg.CHALLENGER_TICKET));
        freeItems.add(new ItemStack(requireNonNull(element.getWand())));
        freeItems.add(new ItemStack(ItemReg.CHALLENGER_TICKET));

        for (int i = 0; i < 6; i++) freeItems.add(ItemStack.EMPTY);

        freeItems.add(ItemStack.EMPTY);

        var shulkerBox = new ItemStack(
            switch (element.id()){
                case 1 -> Items.LIGHT_BLUE_SHULKER_BOX;
                case 2 -> Items.ORANGE_SHULKER_BOX;
                case 3 -> Items.PURPLE_SHULKER_BOX;
                default -> Items.RED_SHULKER_BOX;
            }
        );

        shulkerBox.set(DataComponents.CUSTOM_NAME, withStyleComponent(element.name() + " Starter Box", element.textColourB()));
        shulkerBox.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(freeItems));
        ItemHandlerHelper.giveItemToPlayer(player, shulkerBox);
    }

    public static void removeInstanceBuffs(EntityLeaveLevelEvent event) {
        var entity = event.getEntity();
        if(event.getLevel() instanceof CustomLevel && entity instanceof Player player){
            for (var syncableAttribute : player.getAttributes().getSyncableAttributes()) {
                var modifiers = syncableAttribute.getModifiers();
                if(!modifiers.isEmpty()){
                    for (var attributeModifier : modifiers.stream().toList()) {
                        if(attributeModifier.id().getPath().intern().contains("boon")){
                            syncableAttribute.removeModifier(attributeModifier);
                        }
                    }
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
                    JahdooMod.LOGGER.log(org.apache.logging.log4j.Level.ALL, e);
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

    public static void coinDropCalc(LivingEntity entity, int bonus) {
        if(entity.level() instanceof CustomLevel level){
            entity.skipDropExperience();
            var max = Math.max(1, bonus);

            if(entity.getType().is(ModTags.Entities.HORDE_MOBS)){
                if(Random.nextInt(0, Math.min(2, max)) == 0){
                    var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(1, 10 - bonus));
                    throwItem(entity, stack, entity.position());
                }
            }

            if(entity.getPersistentData().getBoolean("boss")){
                var data = new InstanceData();
                data.setGoldTime(10);
                lootsplosian(entity.position(), level, 10, ColourStore.ABSORPTION_YELLOW, RewardLootTables.getCoinItems(data), false, 0);
            }

            if(entity instanceof CustomSkeleton){
                var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(1, 20 - bonus));
                throwItem(entity, stack, entity.position());
            }

            if(entity instanceof VoidSpider spider && !spider.isBaby()){
                if(spider.getOwner() == null){
                    var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(1, 10 - bonus));
                    stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
                    throwItem(entity, stack, entity.position());
                }
            }

            if(entity instanceof EternalWizard wizard){
                if(wizard.getOwner() == null){
                    var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(4, 10 - bonus));
                    var canDropGold = Random.nextInt(10) == 0;

                    stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(canDropGold ? 2 : 1));
                    throwItem(entity, stack, entity.position());
                }
            }
        }
    }

    public static void throwItem(LivingEntity livingEntity, ItemStack stack, Vec3 offset) {
        Vec3 vec3 = new Vec3(0.3F, 0.3F, 0.3F);
        throwItem(livingEntity, stack, offset, vec3, 0.3F);
    }

    public static void throwItem(LivingEntity entity, ItemStack stack, Vec3 offset, Vec3 speedMultiplier, float yOffset) {
        double d0 = entity.getEyeY() - (double)yOffset;
        var itementity = new ItemEntity(entity.level(), entity.getX(), d0, entity.getZ(), stack);
        itementity.setPickUpDelay(0);
        itementity.setThrower(entity);
        var vec3 = offset.subtract(entity.position());
        vec3 = vec3.normalize().multiply(speedMultiplier.x, speedMultiplier.y, speedMultiplier.z);
        itementity.setDeltaMovement(vec3);
        entity.level().addFreshEntity(itementity);
    }

    public static void assignTarget(LevelTickEvent.Pre tickEvent) {
        if(!(tickEvent.getLevel() instanceof CustomLevel level)) return;
        var asList = new ArrayList<Mob>();
        level.getEntities().getAll().forEach(e -> { if (e instanceof Mob mob) asList.add(mob); });

        for (var entity : asList) {
            if(entity.getTarget() == null){
                var getNearby = level.getNearbyEntities(
                    LivingEntity.class,
                    TargetingConditions.DEFAULT,
                    entity,
                    entity.getBoundingBox().inflate(500)
                );

                for (var livingEntity : getNearby) {

                    if(livingEntity instanceof ITamableEntity t && t.getOwner() != null){
                        if(Helpers.canPathfindToTarget(entity, livingEntity)){
                            entity.setTarget(livingEntity);
                        }
                    } else if (livingEntity instanceof Player) {
                        if(Helpers.canPathfindToTarget(entity, livingEntity)){
                            entity.setTarget(livingEntity);
                        }
                    } else if (entity instanceof ITamableEntity t && t.getOwner() != null) {
                        if(SharedEntityBehaviours.canTarget(livingEntity, t.getOwner())){
                            entity.setTarget(livingEntity);
                        }
                    }
                }
            }
        }
    }

    public static void instanceEndingWarning(LevelTickEvent.Pre tickEvent) {
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
}
