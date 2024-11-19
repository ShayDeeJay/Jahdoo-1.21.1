package org.jahdoo.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jahdoo.JahdooMod;
import org.jahdoo.client.KeyBinding;
import org.jahdoo.client.RenderHelpers;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityWheelMenu;
import org.jahdoo.event.event_helpers.WandAbilitySelector;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.utils.ModHelpers;

import java.awt.*;
import java.util.List;

import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.*;
import static org.jahdoo.all_magic.AbilityBuilder.OFFSET;
import static org.jahdoo.all_magic.AbilityBuilder.SIZE;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class ModEvents {

    @EventBusSubscriber(modid = JahdooMod.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Player player = Minecraft.getInstance().player;

            if(player != null && player.getMainHandItem().getItem() instanceof WandItem){
                if (KeyBinding.QUICK_SELECT.isDown()) Minecraft.getInstance().setScreen(new AbilityWheelMenu());
            }

            if(KeyBinding.WAND_SLOT_1A.consumeClick()) WandAbilitySelector.selectWandSlot(1);
            if(KeyBinding.WAND_SLOT_2A.consumeClick()) WandAbilitySelector.selectWandSlot(2);
            if(KeyBinding.WAND_SLOT_3A.consumeClick()) WandAbilitySelector.selectWandSlot(3);
            if(KeyBinding.WAND_SLOT_4A.consumeClick()) WandAbilitySelector.selectWandSlot(4);
            if(KeyBinding.WAND_SLOT_5A.consumeClick()) WandAbilitySelector.selectWandSlot(5);
            if(KeyBinding.WAND_SLOT_6A.consumeClick()) WandAbilitySelector.selectWandSlot(6);
            if(KeyBinding.WAND_SLOT_7A.consumeClick()) WandAbilitySelector.selectWandSlot(7);
            if(KeyBinding.WAND_SLOT_8A.consumeClick()) WandAbilitySelector.selectWandSlot(8);
            if(KeyBinding.WAND_SLOT_9A.consumeClick()) WandAbilitySelector.selectWandSlot(9);
            if(KeyBinding.WAND_SLOT_10A.consumeClick()) WandAbilitySelector.selectWandSlot(10);

        }



        @SubscribeEvent
        public static void PlayerRenderer(RenderLevelStageEvent event) {
            var player = (Player) event.getCamera().getEntity();
            var stack = player.getMainHandItem();
            var pick = player.pick(15, 1, false);
            if(stack.getItem() instanceof WandItem){
                if (pick.getType() != HitResult.Type.MISS) {
                    if (pick instanceof BlockHitResult blockHitResult) {
                        var getSelectedAbility = stack.get(WAND_DATA);
                        if(getSelectedAbility == null) return;;
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

                                    // Update the min and max positions
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
                            AABB boundingBox = new AABB(minPos.getX(), minPos.getY(), minPos.getZ(),
                                maxPos.getX() + 1, maxPos.getY() + 1, maxPos.getZ() + 1);

                            renderSelectedBlock(event, boundingBox);
                        }

                    }
                }
            }
        }

        public static void renderSelectedBlock(RenderLevelStageEvent event, AABB aabb) {
            final Minecraft mc = Minecraft.getInstance();
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            Vec3 view = mc.gameRenderer.getMainCamera().getPosition();
            PoseStack matrix = event.getPoseStack();
            matrix.pushPose();
            matrix.translate(-view.x(), -view.y(), -view.z());
            matrix.pushPose();
            RenderHelpers.renderLines(matrix, aabb, new Color(113, 255, 173), buffer);
            matrix.popPose();
            matrix.popPose();
        }


        @SubscribeEvent
        public static void overlayEvent(RenderGuiLayerEvent.Pre event) {
            var player = Minecraft.getInstance().player;
            if (Minecraft.getInstance().screen instanceof AbilityWheelMenu && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
                event.setCanceled(true);
            }

            if(player.getMainHandItem().getItem() instanceof WandItem){
                List<ResourceLocation> exceptions = List.of(
                    EXPERIENCE_LEVEL,
                    EXPERIENCE_BAR,
                    HOTBAR,
                    PLAYER_HEALTH,
                    FOOD_LEVEL
//                    CROSSHAIR
                );

//                if(exceptions.contains(event.getName())){
//                    event.setCanceled(true);
//                }
            }
        }

    }

}


