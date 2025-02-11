package org.jahdoo.event.event_helpers;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jahdoo.ability.abilities.ability_data.ArcaneShiftAbility;
import org.jahdoo.ability.abilities.ability_data.FrostboltsAbility;
import org.jahdoo.client.RenderHelpers;
import org.jahdoo.client.SharedUI;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.utils.Configuration;
import org.jahdoo.utils.ModHelpers;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.client.RenderHelpers.drawTexture;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class RenderEventHelper {

    public static void renderUtilityOverlay(RenderLevelStageEvent event, Player player, ItemStack stack) {
        var pick = player.pick(15, 1, false);
        if(stack.getItem() instanceof WandItem){
            if (pick.getType() != HitResult.Type.MISS) {
                if (pick instanceof BlockHitResult blockHitResult) {
                    var getSelectedAbility = stack.get(WAND_DATA);
                    if(getSelectedAbility == null) return;
                    var breakerSize = ModHelpers.getTag(player, SIZE, getSelectedAbility.selectedAbility());
                    var offSet = ModHelpers.getTag(player, OFFSET, getSelectedAbility.selectedAbility());
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

    public static void renderTeleportLocationOverlay(RenderLevelStageEvent event, Player player, ItemStack stack) {
        var getSelectedAbility = stack.get(WAND_DATA);
        if(getSelectedAbility == null) return;
        if(Objects.equals(getSelectedAbility.selectedAbility(), ArcaneShiftAbility.abilityId.getPath().intern())){
            var pickDistance = ModHelpers.getTag(player, CASTING_DISTANCE, getSelectedAbility.selectedAbility());
            var pick = player.pick(pickDistance, 1, false);
            if (stack.getItem() instanceof WandItem) {
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
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        var view = mc.gameRenderer.getMainCamera().getPosition();
        var matrix = event.getPoseStack();
        matrix.pushPose();
        matrix.translate(-view.x(), -view.y(), -view.z());
        matrix.pushPose();
        RenderHelpers.renderLines(matrix, aabb, color, buffer);
        matrix.popPose();
        matrix.popPose();
    }

    public static void renderAbilityOverlay(RenderLevelStageEvent event, ItemStack stack, Player player) {
        var getSelectedAbility = stack.get(WAND_DATA);
        if(getSelectedAbility == null) return;
        var filtered = List.of(
            ArcaneShiftAbility.abilityId.getPath().intern(),
            FrostboltsAbility.abilityId.getPath().intern()
        );

        var typeId = getSelectedAbility.selectedAbility();
        var ability = AbilityRegister.getFirstSpellByTypeId(typeId);
        var view = event.getCamera().getPosition();
        var pose = event.getPoseStack();
        var buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        if(ability.isEmpty() || filtered.contains(typeId) || player.getData(AttachmentRegister.CASTER_DATA.get()).isAbilityOnCooldown(typeId)) return;

        var pickDistance = ModHelpers.getTag(player, CASTING_DISTANCE, typeId);
        var radius = ModHelpers.getTag(player, AOE, typeId) * 3;
        var scale = Math.sin((event.getRenderTick() + event.getPartialTick().getRealtimeDeltaTicks()) / 4.0F) * Math.max((radius/10), 0.1) + Math.max(radius, 1);
        var pick = player.pick(pickDistance, event.getPartialTick().getGameTimeDeltaTicks(), false);
        var item = stack.getItem();
        var isWand = item instanceof WandItem;
        var hitSurface = pick.getType() != HitResult.Type.MISS;
        var elementByWandType = SharedUI.getElementWithType(ability.get(), stack);
        if (!isWand || !hitSurface || elementByWandType == null) return;

        var colour = elementByWandType.textColourSecondary();
        pose.pushPose();
        pose.translate(-view.x(), -view.y(), -view.z());
        pose.pushPose();
        pose.translate(pick.getLocation().x, pick.getLocation().y, pick.getLocation().z);
        pose.translate(0, 0.12f, 0);
        pose.rotateAround(Axis.YP.rotationDegrees(event.getRenderTick() + event.getPartialTick().getRealtimeDeltaTicks()), 0, 0, 0);
        drawTexture(pose.last(), buffer, FULL_BRIGHT, (float) scale, ModHelpers.res("textures/entity/shield.png"), FastColor.ARGB32.color(155, colour));
        drawTexture(pose.last(), buffer, FULL_BRIGHT, (float) scale, ModHelpers.res("textures/entity/target.png"), FastColor.ARGB32.color(155, colour));
        pose.popPose();
        pose.popPose();
    }


    public static void lockNearbyTarget(RenderLevelStageEvent event) {
        if(!Configuration.LOCK_ON_TARGET.get()) return;
        var player = (Player) event.getCamera().getEntity();
        var target = getEntityInRange(player, 15, 25);
        if (target == null || !player.hasLineOfSight(target)) return;
        if (!(ModHelpers.getUsedItem(player).getItem() instanceof WandItem)) return;

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


    public static LivingEntity getEntityInRange(Player player, double maxDistance, float maxAngle) {
        var playerPosition = player.position();
        var playerDirection = player.getLookAngle(); // Direction the player is looking

        LivingEntity nearestEntity = player.level().getNearestEntity(
            Mob.class,
            TargetingConditions.DEFAULT,
            player,
            playerPosition.x, playerPosition.y, playerPosition.z,
            new AABB(BlockPos.containing(playerPosition)).inflate(maxDistance, 4, maxDistance)
        );

        if (nearestEntity == null) return null;
        var targetDirection = nearestEntity.position().subtract(playerPosition).normalize();

        double angleToTarget = Math.toDegrees(Math.acos(playerDirection.dot(targetDirection)));

        if (angleToTarget <= maxAngle) return nearestEntity; else return null;
    }
}
