package org.jahdoo.common.event.event_helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.jahdoo.common.client.RenderHelpers;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.trial_nexus.ability.abilities_combat.arcane_shift.ArcaneShiftAbility;
import org.jahdoo.trial_nexus.ability.abilities_combat.frostbolts.FrostboltsAbility;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.Configuration;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
import static org.jahdoo.common.client.RenderHelpers.drawHealthBar;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;
import static org.jahdoo.common.event.ClientEvents.getHealthHolderIcon;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.level_manager.LevelGenerator.LEVEL_PREFIX;

public class RenderEventHelper {

    public static void renderTeleportLocationOverlay(RenderLevelStageEvent event, Player player, ItemStack stack) {
        var ability = CasterData.selectedAbility(player);
        if(Objects.equals(ability, ArcaneShiftAbility.abilityId.getPath().intern())){
            var pickDistance = CasterData.getSpecificValue(player, CASTING_DISTANCE);
            var pick = player.pick(pickDistance, 1, false);
            if (CastHelper.validCasterType(stack.getItem())) {
                if (pick.getType() != HitResult.Type.MISS) {
                    if (pick instanceof BlockHitResult blockHitResult) {
                        renderSelectedBlock(event, new AABB(blockHitResult.getBlockPos()), new Color(193, 97, 228));
                    }
                }
            }
        }
    }

    public static void renderSelectedBlock(RenderLevelStageEvent event, AABB aabb, Color color) {
        final Minecraft mc = Minecraft.getInstance();
        var buffer = mc.renderBuffers().bufferSource();
        var view = mc.gameRenderer.getMainCamera().getPosition();
        var matrix = event.getPoseStack();
        matrix.pushPose();
        matrix.translate(-view.x(), -view.y(), -view.z());
        matrix.pushPose();
        RenderHelpers.renderLines(matrix, aabb, color, buffer);
        matrix.popPose();
        matrix.popPose();
    }

    public static LivingEntity getEntityInRange(Player player, double maxDistance, float maxAngle) {
        var playerPosition = player.position();
        var playerDirection = player.getLookAngle(); // Direction the player is looking

        var nearestEntity = player.level().getNearestEntity(
            Mob.class,
            TargetingConditions.DEFAULT,
            player,
            playerPosition.x, playerPosition.y, playerPosition.z,
            new AABB(BlockPos.containing(playerPosition)).inflate(maxDistance, 4, maxDistance)
        );

        if (nearestEntity == null) return null;
        var targetDirection = nearestEntity.position().subtract(playerPosition).normalize();
        var angleToTarget = Math.toDegrees(Math.acos(playerDirection.dot(targetDirection)));
        if (angleToTarget <= maxAngle) return nearestEntity; else return null;
    }

    public static void renderAbilityOverlay(RenderLevelStageEvent event, ItemStack stack, Player player) {
        var filtered = List.of(
              ArcaneShiftAbility.abilityId.getPath().intern(),
              FrostboltsAbility.abilityId.getPath().intern()
        );

        var typeId = CasterData.selectedAbility(player);
        var ability = AbilityReg.getFirstSpellByTypeId(typeId);
        var view = event.getCamera().getPosition();
        var pose = event.getPoseStack();
        var buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        if(ability.isEmpty() || filtered.contains(typeId) || player.getData(AttachmentReg.CASTER_DATA.get()).isAbilityOnCooldown(typeId)) return;

        var pickDistance = CasterData.getSpecificValue(player, CASTING_DISTANCE);
        var radius = CasterData.getSpecificValue(player, AOE) * 2;
        var scale = Math.sin((event.getRenderTick() + event.getPartialTick().getRealtimeDeltaTicks()) / 4.0F) * Math.max((radius/10), 0.1) + Math.max(radius, 1);
        var pick = player.pick(pickDistance, event.getPartialTick().getGameTimeDeltaTicks(), false);
        var item = stack.getItem();
        var isCaterItem = CastHelper.validCasterType(item);
        var hitSurface = pick.getType() != HitResult.Type.MISS;
        var elementByWandType = SharedUI.getElementWithType(ability.get(), stack);
        if (!isCaterItem || !hitSurface || elementByWandType == null) return;

        var colour = elementByWandType.textColourB();
        pose.pushPose();
        pose.translate(-view.x(), -view.y(), -view.z());
        pose.pushPose();
        pose.translate(pick.getLocation().x, pick.getLocation().y, pick.getLocation().z);
        pose.translate(0, 0.12f, 0);
        pose.rotateAround(Axis.YP.rotationDegrees(event.getRenderTick() + event.getPartialTick().getRealtimeDeltaTicks()), 0, 0, 0);
        drawTexture(pose.last(), buffer, FULL_BRIGHT, (float) scale, Helpers.res("textures/entity/shield.png"), FastColor.ARGB32.color(155, colour));
        drawTexture(pose.last(), buffer, FULL_BRIGHT, (float) scale, Helpers.res("textures/entity/target.png"), FastColor.ARGB32.color(155, colour));
        pose.popPose();
        pose.popPose();
    }

    public static void lockNearbyTarget(RenderLevelStageEvent event) {
        if(!Configuration.LOCK_ON_TARGET.get()) return;
        var player = (Player) event.getCamera().getEntity();
        var target = getEntityInRange(player, 15, 25);
        if (target == null || !player.hasLineOfSight(target)) return;
        if (!(CastHelper.validCasterType(Helpers.getUsedItem(player).getItem()))) return;

        target.addEffect(new MobEffectInstance(MobEffects.GLOWING.getDelegate(), 20, 1, false, false), player);

        var targetPos = target.position().add(0, target.getBbHeight() - 0.2, 0);

        double deltaX = targetPos.x - player.getX();
        double deltaY = targetPos.y - (player.getY() + player.getEyeHeight());
        double deltaZ = targetPos.z - player.getZ();

        float desiredYaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90);
        float desiredPitch = (float) -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)));

        float currentYaw = player.getYRot() % 360;
        if (currentYaw > 180) currentYaw -= 360;
        if (currentYaw < -180) currentYaw += 360;

        desiredYaw = desiredYaw % 360;
        if (desiredYaw > 180) desiredYaw -= 360;
        if (desiredYaw < -180) desiredYaw += 360;

        float yawDifference = desiredYaw - currentYaw;
        if (yawDifference > 180) yawDifference -= 360;
        if (yawDifference < -180) yawDifference += 360;

        float smoothFactor = 0.013f; // Adjust for smoother/faster transitions
        player.setYRot(currentYaw + yawDifference * smoothFactor);
        player.setXRot(player.getXRot() + (desiredPitch - player.getXRot()) * smoothFactor);
    }

    public static void renderHealthBar(RenderLivingEvent.Pre event, LivingEntity entity, Minecraft instance, PoseStack poseStack) {
        var getConfig = Configuration.SHOW_HOSTILE_ONLY.get();
        var getCheck = switch (getConfig){
            case "Hostile" -> entity instanceof Monster;
            case "Nexus Trial Only" -> entity.level().getDescription().getString().contains(LEVEL_PREFIX);
            case "All" -> true;
            default -> false;
        };

        if(getCheck){
            var d0 = instance.getEntityRenderDispatcher().distanceToSqr(entity);
            if (!(d0 > (double) 3096.0F)) {
                if (entity != instance.player) {
                    var z = Math.min(entity.getBbWidth() / 2F, 0.7F);
                    var getByAllied = getHealthHolderIcon(entity, instance.player);

                    poseStack.pushPose();
                    poseStack.translate(0, entity.getBbHeight() + 0.4, 0);
                    poseStack.mulPose(instance.getEntityRenderDispatcher().cameraOrientation());
                    poseStack.mulPose(Axis.XP.rotation(-1.5f));
                    poseStack.scale(z, z, z);
                    drawHealthBar(poseStack.last(), event.getMultiBufferSource(), entity.getHealth(), entity.getMaxHealth(), getByAllied);
                    poseStack.popPose();
                }
            }
        }
    }


    public static void renderChampionVisual(RenderLivingEvent.Pre livingEvent) {
        var entity = livingEvent.getEntity();
        if(!entity.hasEffect(EffectReg.CHAMPION_EFFECT)) return;

        var stack = livingEvent.getPoseStack();
        var itemStack = new ItemStack(ItemReg.CHAMPIONS_CROWN);
        var renderTypeBuffer = livingEvent.getMultiBufferSource();
        var instance = Minecraft.getInstance();
        var itemRenderer = instance.getItemRenderer();
        var rotate = entity.tickCount + livingEvent.getPartialTick();
        var animate = rotate / 5;
        var height = entity.getBbHeight() + 0.3;
        var width = entity.getBbWidth();
        var scale = Math.min(Math.max(width/2, 0.5F), animate);
        var bobOff = Math.sin(rotate / 10.0F) * 0.05F + height * 1.1;
        var level = entity.level();

        stack.pushPose();
        stack.translate(0, Math.min(bobOff, animate), 0.);
        stack.scale(scale, scale, scale);
        stack.mulPose(Axis.YP.rotationDegrees(rotate * 2));
        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            200,
            OverlayTexture.NO_OVERLAY,
            stack,
            renderTypeBuffer,
            level,
            1
        );
        stack.popPose();
    }

    public static void renderUtilityOverlay(RenderLevelStageEvent event, Player player, ItemStack stack) {
        var pick = player.pick(15, 1, false);
        if(CastHelper.validCasterType(stack.getItem())){
            if (pick.getType() != HitResult.Type.MISS) {
                if (pick instanceof BlockHitResult blockHitResult) {

                    var breakerSize = CasterData.getSpecificValue(player, SIZE);
                    var offSet = CasterData.getSpecificValue(player, OFFSET);
                    var size = (int) ((breakerSize / 2) - offSet);
                    var radius = (int) (breakerSize / 2);
                    var pos = blockHitResult.getBlockPos();
                    var pDirection = player.getDirection();
                    var lookAngleY = player.getLookAngle().y;
                    var isLookingUpOrDown = lookAngleY < -0.8 || lookAngleY > 0.8;
                    var axisZ = pDirection.getAxis() == Direction.Axis.Z;
                    var axisX = pDirection.getAxis() == Direction.Axis.X;

                    pos = pos.relative(lookAngleY < -0.8 ? pDirection : pDirection.getOpposite(),
                                !isLookingUpOrDown ? 0 : size)
                          .above(isLookingUpOrDown ? 0 : size);

                    var minPos = new BlockPos.MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
                    var maxPos = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

                    for (int x = -radius; x <= radius; x++) {
                        for (int y = -radius; y <= radius; y++) {
                            for (int z = -radius; z <= radius; z++) {
                                BlockPos offsetPos = pos.offset(
                                      x * (isLookingUpOrDown || axisZ ? 1 : 0),
                                      y * (isLookingUpOrDown ? 0 : 1),
                                      z * (isLookingUpOrDown || axisX ? 1 : 0)
                                );

                                minPos.set(Math.min(minPos.getX(), offsetPos.getX()),
                                      Math.min(minPos.getY(), offsetPos.getY()),
                                      Math.min(minPos.getZ(), offsetPos.getZ()));

                                maxPos.set(Math.max(maxPos.getX(), offsetPos.getX()),
                                      Math.max(maxPos.getY(), offsetPos.getY()),
                                      Math.max(maxPos.getZ(), offsetPos.getZ()));

                            }
                        }
                    }

                    if(breakerSize > 0){
                        AABB boundingBox = new AABB(
                              minPos.getX(),
                              minPos.getY(),
                              minPos.getZ(),
                              maxPos.getX() + 1,
                              maxPos.getY() + 1,
                              maxPos.getZ() + 1
                        );
                        renderSelectedBlock(event, boundingBox, new Color(113, 255, 173));
                    }
                }
            }
        }
    }
}
