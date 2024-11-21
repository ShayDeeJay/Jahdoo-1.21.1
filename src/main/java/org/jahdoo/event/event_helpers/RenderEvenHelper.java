package org.jahdoo.event.event_helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jahdoo.ability.all_abilities.abilities.ArcaneShiftAbility;
import org.jahdoo.client.RenderHelpers;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.utils.ModHelpers;

import java.awt.*;
import java.util.Objects;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class RenderEvenHelper {
    public static void renderUtilityOverlay(RenderLevelStageEvent event, Player player, ItemStack stack) {
        var pick = player.pick(15, 1, false);
        if(stack.getItem() instanceof WandItem){
            if (pick.getType() != HitResult.Type.MISS) {
                if (pick instanceof BlockHitResult blockHitResult) {
                    var getSelectedAbility = stack.get(WAND_DATA);
                    if(getSelectedAbility == null) return;
                    double breakerSize = ModHelpers.getTag(player, SIZE, getSelectedAbility.selectedAbility());
                    double offSet = ModHelpers.getTag(player, OFFSET, getSelectedAbility.selectedAbility());
                    int size = (int) ((breakerSize / 2)-offSet);
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

                    BlockPos.MutableBlockPos minPos = new BlockPos.MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
                    BlockPos.MutableBlockPos maxPos = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

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
        Vec3 view = mc.gameRenderer.getMainCamera().getPosition();
        PoseStack matrix = event.getPoseStack();
        matrix.pushPose();
        matrix.translate(-view.x(), -view.y(), -view.z());
        matrix.pushPose();
        RenderHelpers.renderLines(matrix, aabb, color, buffer);
        matrix.popPose();
        matrix.popPose();
    }
}
