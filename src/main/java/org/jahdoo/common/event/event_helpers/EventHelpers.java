package org.jahdoo.common.event.event_helpers;

import com.mojang.math.Axis;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.abilities_combat.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.ascension.ability.abilities_utility.block_placer.BlockPlacerAbility;
import org.jahdoo.ascension.ability.abilities_utility.wall_placer.WallPlacerAbility;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.ascension.attachments.RunData;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.ascension.level_manager.LevelGenerator;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.ascension.utils.ModTags;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.block.perk_table.PerkTableEntity;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.components.LootCrateData;
import org.jahdoo.common.entities.CustomSkeleton;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.entities.SharedEntityBehaviours;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.inferno_creeper.InfernoCreeper;
import org.jahdoo.common.entities.void_spider.VoidSpider;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.caster_item.CasterItem;
import org.jahdoo.common.networking.client2server.ChaosCubeC2SP;
import org.jahdoo.common.networking.client2server.SelectAbilityC2SP;
import org.jahdoo.common.networking.client2server.UseAbilityC2SP;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.networking.server2client.InstanceSyncS2CP;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.*;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jahdoo.common.registers.mod.RuneReg;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;

import static com.mojang.blaze3d.platform.InputConstants.*;
import static java.util.Objects.requireNonNull;
import static net.minecraft.client.Minecraft.getInstance;
import static net.minecraft.sounds.SoundSource.PLAYERS;
import static net.minecraft.world.ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
import static net.minecraft.world.entity.EquipmentSlotGroup.*;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.VAULT;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN;
import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.ascension.attachments.ChaosCubeData.getRelativePosition;
import static org.jahdoo.ascension.attachments.ChaosCubeData.updateAll;
import static org.jahdoo.ascension.attachments.RunData.*;
import static org.jahdoo.ascension.loot.LootHelpers.itemBehaviour;
import static org.jahdoo.ascension.loot.RewardLootTables.getCompletionLoot;
import static org.jahdoo.ascension.mobs.MobItemHandler.getEnchantedArmor;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.ModTags.Block.ALLOWED_BLOCK_INTERACTIONS;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.storeBlockType;
import static org.jahdoo.common.particle.ParticleHandlers.getAllParticleTypes;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.registers.AttachmentReg.*;
import static org.jahdoo.common.registers.ComponentReg.INTERACTION_HAND;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_GEAR_DATA;

public class EventHelpers {

    public static void saveDestinyBondItems(LivingEntity entity) {
        if(entity instanceof Player player) {
            player.getData(SAVE_ITEM_DATA).addAllItems(player);
        }
    }

    public static int getColour(ItemStack stack){
        var colour = stack.get(ComponentReg.RUNE_DATA.get());
        if(colour != null && !colour.name().contains("blank")) return RuneReg.getRuneFromId(colour.name()).runeColour();
        return -1;
    }

    public static void perkTableInteraction(
        BlockState getBlock,
        Level level,
        BlockPos pos,
        Player player,
        UseItemOnBlockEvent event
    ){
        if(getBlock.is(Blocks.BARRIER)){
            if(level.getBlockEntity(pos.below(1)) instanceof PerkTableEntity entity){
                entity.setUsed(entity.getBlockState(), player);
                event.cancelWithResult(ItemInteractionResult.SUCCESS);
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

    public static void removeNonAllowedEffects(EntityJoinLevelEvent event) {
        var entity = event.getEntity();
        if(entity instanceof ServerPlayer player){
            if(event.getLevel() instanceof CustomLevel){
                for (var activeEffect : player.getActiveEffects()) {
                    if(!(activeEffect instanceof JahdooMobEffect)){
                        player.removeEffect(activeEffect.getEffect());
                    }
                }
            }
        }
    }

    public static void removeWandInteractionWithBlocks(UseItemOnBlockEvent event, Player player, Item item, BlockState getBlock) {
        if(player != null){
            var isAllowed = !getBlock.is(ALLOWED_BLOCK_INTERACTIONS);
            var isShift = !player.isShiftKeyDown();
            var isWand = item instanceof CasterItem;

            if (isWand && isShift && isAllowed) {
                event.cancelWithResult(SKIP_DEFAULT_BLOCK_INTERACTION);
            }
        }
    }

    public static void restrictElytra(ServerPlayer serverPlayer, Level level) {
        if(level instanceof CustomLevel){
            var itemStack = serverPlayer.getItemBySlot(EquipmentSlot.CHEST);
            if (itemStack.getItem() instanceof ElytraItem) {
                if(serverPlayer.isFallFlying()){
                    serverPlayer.stopFallFlying();
                }
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
                RunData.endRun(serverPlayer, true);
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

    public static void onFirstTimeJoined(Player player) {
        var playerData = player.getPersistentData();
        var data = playerData.getCompound(Player.PERSISTED_NBT_TAG);

        if (!data.getBoolean("first_join")) {
            if(player instanceof ServerPlayer serverPlayer){
                var castingData = player.getData(CASTER_DATA.get());
                serverPlayer.setData(RUN_DATA, EMPTY);
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
        if(event.getItemStack().getItem() instanceof CasterItem){
            if(event.getEntity().isShiftKeyDown()){
                var name = CasterData.selectedAbility(event.getEntity());
                var wallPlacer = WallPlacerAbility.abilityId.getPath().intern();
                var blockPlacer = BlockPlacerAbility.abilityId.getPath().intern();

                if(name.equals(wallPlacer) || name.equals(blockPlacer)){
                    storeBlockType(item, blockState, event.getEntity(), pos);
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void removeShieldUse(PlayerInteractEvent.RightClickItem rightClickItem) {
        if(rightClickItem.getItemStack().getItem() instanceof ShieldItem && rightClickItem.getLevel() instanceof CustomLevel){
            rightClickItem.setCanceled(true);
            rightClickItem.getEntity().displayClientMessage(Helpers.withStyleComponent("This item doesn't work here", ColourStore.OFF_WHITE), true);
        }
    }

    public static void shieldBlock(LivingShieldBlockEvent event, LivingEntity entity) {
        var curioSlotsItems = CuriosApi.getCuriosInventory(entity);
        if(curioSlotsItems.isEmpty()) return;

        var withSlots = curioSlotsItems.get().getEquippedCurios();
        var shieldSlots = withSlots.getStackInSlot(2);
        if (shieldSlots.isEmpty()) return;

        var shieldDurability = durabilityDamageCount(shieldSlots);
        var blockPercentage = shieldSlots.get(ComponentReg.SHIELD_BLOCK_CHANCE);
        if(blockPercentage == null) return;

        var blockChance = Maths.percentageChance(blockPercentage);
        if (blockChance && shieldDurability > 0) {
            event.setBlocked(true);
            var damage = (int) (event.getBlockedDamage());

            hurtAndKeepItem(shieldSlots, damage, event.getEntity().level(), entity);
            getSoundWithPositionV(entity.level(), entity.position(), SoundReg.BLOCK.get(), 1, 1);
        }
    }

    public static void setChaosCubeAbility(PlayerInteractEvent.LeftClickBlock event, Level level, BlockPos pos, ItemStack item) {
        if(level.getBlockEntity(pos) instanceof ChaosCubeEntity entity && item.getItem() instanceof CasterItem){
            var player = event.getEntity();
            var casterData = player.getData(CASTER_DATA.get());
            var ability = AbilityReg.getFirstSpellByTypeId(casterData.getSelectedAbility());

            if(ability.isPresent()) {
                var element = ElementReg.utility();
                if (ability.get().getElemenType() == element) {
                    event.setCanceled(true);
                    var holder = CasterData.entityHolderWithSelected(player);
                    if (holder != AbilityHolder.DEFAULT) {
                        entity.setHolder(holder);

                        for (int i = 0; i < 10; i++) {
                            var part = ParticleHandlers.getAllParticleTypes(element, 20, 2);
                            ParticleHandlers.particleBurst(level, pos.getCenter(), 1, part);
                        }

                        getSoundWithPosition(level, pos, SoundReg.SUSPEND.get(), 1, 0.5F);
                    } else {
                        var message = "You don't have this ability";
                        var messageComponent = withStyleComponent(message, element.textColourA());
                        player.sendSystemMessage(messageComponent);
                    }
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

    public static void championLootCalculator(LivingDamageEvent.Pre event, LivingEntity entity) {
        if(entity.level() instanceof CustomLevel customLevel){
            var isChampion = entity.hasEffect(EffectReg.CHAMPION_EFFECT);
            if (isChampion && event.getSource().getEntity() != null) {
                var position = entity.position();
                var instanceData = customLevel.getData(INSTANCE_DATA.get());
                var difficulty = InstanceDifficulty.getFromName(instanceData.getDifficulty());
                var chestRarity = difficulty.getId();
                var type = difficulty.getSerializedName();
                var rewards = getCompletionLoot(customLevel, position, type, chestRarity);
                itemBehaviour(position, customLevel, Helpers.getRgb(), true, 10, chestRarity, Helpers.listRandom(rewards));
            }
        }
    }

    public static void resilienceDamageRecalculate(LivingDamageEvent.Pre event, LivingEntity entity) {
        var attribute = entity.getAttribute(AttributeReg.RESILIENCE);
        if(attribute != null){
            var resilience = attribute.getValue();
            var damageReduction = Maths.getPercentage(resilience, event.getNewDamage());
            var damageWithResilience = event.getNewDamage() - damageReduction;
            event.setNewDamage((float) damageWithResilience);
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
        var slotAttributes = item.get(JAHDOO_GEAR_DATA.get());
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

        if(Helpers.durabilityDamageCount(item) > 0){
            for (var modifier : item.getAttributeModifiers().modifiers()) {
                event.addModifier(modifier.attribute(), modifier.modifier());
            }
        }
    }

    public static boolean removeArmorAttributes(ItemAttributeModifiers.Entry entry, ItemAttributeModifierEvent event){
        var item = event.getItemStack();
        var durability = Helpers.durabilityDamageCount(item);
        if(item.has(DataComponents.DAMAGE) && item.getItem() instanceof JahdooItem){
            return durability == 0;
        }
        return false;
    }

    public static void useRuneAttributes(ItemAttributeModifierEvent event) {
        var item = event.getItemStack();
        var slotAttributes = item.get(JAHDOO_GEAR_DATA.get());
        var handComponent = item.get(INTERACTION_HAND);
        var hand = handComponent == null ? 2 : handComponent;
        var item1 = item.getItem();
        if(slotAttributes == null || item1 instanceof ICurioItem) return;

        if(item.is(ModTags.Items.WAND_TAGS) && hand == 2) return;

        var durability = Helpers.durabilityDamageCount(item);
        if(item.has(DataComponents.DAMAGE) && item.getItem() instanceof JahdooItem && durability > 0){
            for (ItemStack itemStack : slotAttributes.runeSlots()) {
                var mods = itemStack.getAttributeModifiers().modifiers();
                if (mods.isEmpty()) return;

                var acMod = mods.getFirst();
                var slot = item1 instanceof ArmorItem ? ARMOR : hand == 0 || item1 instanceof SwordItem ? MAINHAND : OFFHAND;

                event.addModifier(acMod.attribute(), acMod.modifier(), slot);
            }
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
            var getKiller = entity.getKillCredit();
            entity.skipDropExperience();

            if(entity.getType().is(ModTags.Entities.HORDE_MOBS)){
                var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(1, 10 - bonus));
                onKillExpAndCoin(entity, level, stack, getKiller, 1, 0);
            }

            if(entity instanceof CustomSkeleton){
                var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(5, 20 - bonus));
                onKillExpAndCoin(entity, level, stack, getKiller, 3, 0);
            }

            if(entity instanceof VoidSpider spider && !spider.isBaby()){
                if(spider.getOwner() == null){
                    var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(1, 10 - bonus));
                    onKillExpAndCoin(entity, level, stack, getKiller, 5, 1);
                }
            }

            if(entity instanceof EternalWizard wizard){
                if(wizard.getOwner() == null){
                    var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(4, 10 - bonus));
                    onKillExpAndCoin(entity, level, stack, getKiller, 10, Random.nextInt(10) == 0 ? 2 : 1);
                }
            }

            if(entity instanceof InfernoCreeper){
                var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(4, 10 - bonus));
                onKillExpAndCoin(entity, level, stack, getKiller, 10, Random.nextInt(10) == 0 ? 2 : 1);
            }

            if(entity.getPersistentData().getBoolean("boss")){
                var stack = new ItemStack(ItemReg.COIN).copyWithCount(Math.max(4, 10 - bonus)).copyWithCount(10);
                onKillExpAndCoin(entity, level, stack, getKiller, 200, 2);
            }
        }
    }

    private static void onKillExpAndCoin(LivingEntity entity, CustomLevel level, ItemStack stack, LivingEntity getKiller, int exp, int modelData) {
        var isChampion = entity.hasEffect(EffectReg.CHAMPION_EFFECT);
        var championMultiplier = 5;
        var data = InstanceData.difficultyFromInstance(level.getData(INSTANCE_DATA.get()));

        var originalCount = stack.getCount() * (data.map(InstanceDifficulty::expMultiplier).orElse(1));
        var originalExp = exp * (data.map(InstanceDifficulty::expMultiplier).orElse(1));

        var withChampionCoins = originalCount * (isChampion ? championMultiplier : 1);
        var withChampionExp = originalExp * (isChampion ? championMultiplier : 1);

        if(modelData > 0) stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(modelData));
        throwItem(entity, stack.copyWithCount(withChampionCoins), entity.position());
        if(getKiller != null) {
            incrementKilledMobsExp(level, getKiller, withChampionExp);
            if(isChampion) incrementChampionsKilled(getKiller);
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

    //Remove custom levels when time runs out, level should already be discarded on end of run
    //this is more for safety incase something has been left behind
    public static void discardLevelOnEnd(LevelTickEvent.Pre tickEvent) {
        if(tickEvent.getLevel() instanceof ServerLevel serverLevel){
            if(serverLevel instanceof CustomLevel customLevel){
                var data = serverLevel.getData(INSTANCE_DATA.get());
                var players = customLevel.players();
                if(!data.getDifficulty().isEmpty() && players.isEmpty()){
                    var i = data.getMaxTime() - data.getTicks();
                    if (i <= 0) {
                        for (var player : players) player.kill();
                        LevelGenerator.removeLevel(customLevel);
                    }
                }
            }
        }
    }

    public static void selectAbilitySlot(int keyNum){
        var player = Minecraft.getInstance().player;
        if(player == null) return;

        var casterData = player.getData(CASTER_DATA.get());
        var getAbility = casterData.abilitySlots.get(keyNum - 1);
        var b = withStyleComponent(String.valueOf(keyNum), ColourStore.PERK_GREEN);
        var a = withStyleComponentTrans("abilitySelector.jahdoo.non_assigned", ColourStore.SUB_HEADER_COLOUR, b);

        if (!getAbility.isEmpty()) {
            casterData.setSelectedAbility(getAbility);
            PacketDistributor.sendToServer(new SelectAbilityC2SP(getAbility));
            PacketDistributor.sendToServer(new UseAbilityC2SP());
        } else player.displayClientMessage(a, true);
    }

    public static void copyPasteBlockProperties(Player player) {
        var pick = player.pick(5, 1, false);

        if(pick.getType() == HitResult.Type.MISS) return;
        if(!(pick instanceof BlockHitResult blockHitResult)) return;

        var be = player.level().getBlockEntity(blockHitResult.getBlockPos());

        if(!(be instanceof ChaosCubeEntity modEntity)) return;
        if(!(player instanceof LocalPlayer)) return;

        var window = getInstance().getWindow().getWindow();
        var keyDownCtrl = isKeyDown(window, KEY_LCONTROL);
        var keyDownC = isKeyDown(window, KEY_C);
        var keyDownV = isKeyDown(window, KEY_V);

        if(keyDownC && keyDownCtrl) {
            if(modEntity.hasData(MODULAR_CHAOS_CUBE)){
                var data = modEntity.getData(MODULAR_CHAOS_CUBE);
                player.setData(MODULAR_CHAOS_CUBE, data);
                player.displayClientMessage(Component.literal("Copied!"), true);
            } else {
                player.displayClientMessage(Component.literal("No data to copy!"), true);
            }
        };

        if(keyDownV && keyDownCtrl) {
            if(player.hasData(MODULAR_CHAOS_CUBE)){
                var chaosCubeProperties = player.getData(MODULAR_CHAOS_CUBE);
                var action = chaosCubeProperties.getDirection(chaosCubeProperties.action());
                var input = chaosCubeProperties.getDirection(chaosCubeProperties.input());
                var output = chaosCubeProperties.getDirection(chaosCubeProperties.output());

                var actionNew = getRelativePosition(action, modEntity.getBlockPos());
                var inputNew = getRelativePosition(input, modEntity.getBlockPos());
                var outputNew = getRelativePosition(output, modEntity.getBlockPos());
                var update = updateAll(actionNew, inputNew, outputNew, chaosCubeProperties.active(), chaosCubeProperties.speed(), modEntity.getBlockPos(), chaosCubeProperties.chained());

                PacketDistributor.sendToServer(new ChaosCubeC2SP(modEntity.getBlockPos(), update));
                modEntity.setData(MODULAR_CHAOS_CUBE, update);
                modEntity.setChanged();
                player.displayClientMessage(Component.literal("Pasted!"), true);
            } else {
                player.displayClientMessage(Component.literal("Nothing to paste!"), true);
            }
        };
    }

    public static void questTracker(Level level, Player player) {
        if(!(level instanceof CustomLevel customLevel)) return;
        if(!(player instanceof ServerPlayer serverPlayer)) return;

        var runData = serverPlayer.getData(RUN_DATA.get());
        var getQuestId = runData.getCurrentQuestId();
        if(getQuestId == null) return;

        var getQuest = QuestReg.getQuestByName(getQuestId);
        if (getQuest.isEmpty()) return;

        var quest = getQuest.get();
        var stat = runData.getStat(getQuestId);
        var i = quest.questQuantity(serverPlayer);

        if (stat >= i && !runData.isCompletedQuest()) {
            Helpers.sendClientSound(serverPlayer, SoundReg.QUEST_COMPLETE.get(), 1, 1);

            for(int j = 0; j < 100; j++){
                var color = Helpers.getRgb();
                var particle = ParticleHandlers.getNonBakedParticles(color, color, 27, Random.nextInt(1, 3));
                var x = player.getRandomX(0.5);
                var y = player.getRandomY();
                var z = player.getRandomZ(0.5);
                double xSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
                double ySpeed = Random.nextDouble(0.1, 0.3);
                double zSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
                ParticleHandlers.sendParticles(level, particle, new Vec3(x,y,z), 1, xSpeed, ySpeed, zSpeed, 0.1);
            }

            var item = BlockReg.LOOT_CRATE.get();
            var newBlock = new ItemStack(item);
            var playerLevel = CasterData.getLevel(serverPlayer);
            var instanceData = customLevel.getData(INSTANCE_DATA.get());
            var lootMultiplier = Math.max(1, instanceData.getQuestCrateMultiplier());
            var completionTime = instanceData.getMaxTime() - instanceData.getTicks();
            var difficulty = instanceData.getDifficulty();
            var value = new LootCrateData(playerLevel, lootMultiplier, completionTime, difficulty);

            newBlock.set(ComponentReg.LOOT_CRATE_DATA, value);
            Helpers.throwOrAddItem(serverPlayer, newBlock);
            Helpers.throwOrAddItem(serverPlayer, new ItemStack(ItemReg.EXIT_KEY));
            addExperienceToTotal(quest.questXp(serverPlayer), serverPlayer);
            runData.setCompletedQuest(true);
        }
    }

}
