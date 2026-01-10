package org.jahdoo.common.block.creator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jahdoo.common.registers.ItemReg;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class CreatorRenderer implements BlockEntityRenderer<org.jahdoo.common.block.creator.CreatorEntity> {

    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public CreatorRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(
        CreatorEntity creatorEntity,
        float pPartialTick,
        PoseStack pPoseStack,
        MultiBufferSource pBuffer,
        int pPackedLight,
        int pPackedOverlay
    ){
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var atomicInteger = new AtomicInteger();
        top(pPoseStack, creatorEntity, itemRenderer, pBuffer, pPartialTick, pPackedLight);
        focusedItem(pPoseStack, creatorEntity, itemRenderer, pBuffer, pPartialTick, pPackedLight);

        for(int i = 0; i < creatorEntity.inputItemHandler.getSlots(); i++){
            ItemStack itemStack = creatorEntity.inputItemHandler.getStackInSlot(i);
            if(!itemStack.isEmpty()) atomicInteger.set(atomicInteger.get() + 1);
        }

        for(int i = 0; i < creatorEntity.inputItemHandler.getSlots(); i++){
            ItemStack itemStack = creatorEntity.inputItemHandler.getStackInSlot(i);
            rotateAllItems(pPoseStack, creatorEntity, () -> rotateItem(pPoseStack, creatorEntity, itemRenderer, itemStack, pBuffer, pPartialTick), i, atomicInteger.get(), pPartialTick);
        }
    }

    private void rotateAllItems(PoseStack pPoseStack, CreatorEntity pBlockEntity, Runnable stuff, int index, int totalItems, float partialTicks) {
        pPoseStack.pushPose();

        if (pBlockEntity.canCraft()) {
            var angleOffset = 360.0f / totalItems;
            var itemAngle = (angleOffset * index);
            pPoseStack.translate(0.5, -0.1, 0.5);

            var frameTime = Minecraft.getInstance().level.getGameTime();
            pPoseStack.mulPose(Axis.YP.rotationDegrees(itemAngle + ((-frameTime) - partialTicks)));
        } else {
            float[][] positions = {
                {0.5f, 0.04f},
                {0.14f, 0.40f},
                {0.86f, 0.40f},
                {0.5f, 0.76f},
                {0.23f, 0.13f},
                {0.77f, 0.13f},
                {0.23f, 0.67f},
                {0.77f, 0.68f}
            };

            if (index < positions.length) {
                var dir = getPlayerRelativeDirection(pBlockEntity).getOpposite();
                var rotated = rotateAroundCenter(positions[index][0], positions[index][1], dir);
                pPoseStack.translate(rotated[0], index < 4 ? -0.11 : -0.17, rotated[1]);
            }
        }

        stuff.run();
        pPoseStack.popPose();
    }

    private Direction getPlayerRelativeDirection(CreatorEntity blockEntity) {

        var player = Minecraft.getInstance().player;
        var blockPos = blockEntity.getBlockPos();

        var dx = player.getX() - (blockPos.getX() + 0.5);
        var dz = player.getZ() - (blockPos.getZ() + 0.5);

        if (Math.abs(dx) > Math.abs(dz)) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    private float[] rotateAroundCenter(float x, float z, Direction direction) {
        var dx = x - 0.5f;
        var dz = z - 0.5f;

        return switch (direction) {
            case SOUTH -> new float[]{0.5f - dx, 0.3f - dz}; // 180°
            case WEST -> new float[]{0.6f + dz, 0.4f - dx}; // 90° CCW
            case EAST -> new float[]{0.4f - dz, 0.4f + dx}; // 90° CW
            default -> new float[]{x, z}; // No rotation
        };
    }

    private void top(PoseStack stack, CreatorEntity creator, ItemRenderer iRenderer, MultiBufferSource pBuffer, float partialTicks, int packedLight){
        stack.pushPose();
        var progress = creator.getProgress();
        var level = Minecraft.getInstance().level;
        if(level == null) return;

        var itemStack = new ItemStack(ItemReg.CREATOR_TOP.get());
        var scaleItem = 1.2f;

        var scale = Math.sin(((level.getGameTime() + partialTicks) / 14.0F)) * 0.03F + 1;
        stack.translate(0.5, scale, 0.5);
        stack.scale(scaleItem, scaleItem, scaleItem);

        if(progress > 0) stack.mulPose(Axis.YP.rotationDegrees(creator.getProgress() + partialTicks));

        iRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            stack,
            pBuffer,
            creator.getLevel(),
            1
        );

        stack.popPose();
    }

    private void focusedItem(PoseStack pPoseStack, CreatorEntity pBlockEntity, ItemRenderer itemRenderer, MultiBufferSource pBuffer, float partialTicks, float packedLight){
        pPoseStack.pushPose();
        var level = Minecraft.getInstance().level;
        if(level == null) return;

        var getCurrentTime = level.getGameTime() + partialTicks;
        var outputSlot = pBlockEntity.getResult();
        var itemStack = outputSlot == null ? pBlockEntity.outputItemHandler.getStackInSlot(0) : outputSlot;
        var scaleItem = 0.3F;
        var maxLightLevel = getLightLevel(Objects.requireNonNull(pBlockEntity.getLevel()), pBlockEntity.getBlockPos());

        var scale = Math.sin(((level.getGameTime() + partialTicks) / 14.0F)) * 0.02F;
        pPoseStack.translate(0.5f, 1.32f + scale, 0.5f);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-getCurrentTime));

        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            outputSlot != null ? (int) packedLight : 255,
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );

        pPoseStack.popPose();
    }

    private void rotateItem(
        PoseStack pPoseStack,
        CreatorEntity pBlockEntity,
        ItemRenderer itemRenderer,
        ItemStack itemStack,
        MultiBufferSource pBuffer,
        float partialTicks
    ){
        pPoseStack.pushPose();

        var level = Minecraft.getInstance().level;
        if(level == null) return;

        var getCurrentTime = level.getGameTime() + partialTicks;
        var scaleItem = 0.15f;

        var inc = pBlockEntity.animIncrement;
        pPoseStack.translate(0, 1.25f, (Math.min(0.9, inc))-0.4);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);

        if(!pBlockEntity.canCraft()) pPoseStack.mulPose(Axis.YP.rotationDegrees(getCurrentTime));
        var scale = Math.sin(((level.getGameTime() + partialTicks) / 14.0F)) * 0.15F;
        pPoseStack.translate(0, scale - 0.4, 0);

        itemRenderer.renderStatic(
            itemStack, ItemDisplayContext.FIXED,
            getLightLevel(level, pBlockEntity.getBlockPos()),
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );

        pPoseStack.popPose();
    }


    private int getLightLevel(Level level, BlockPos blockPos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, blockPos);
        int sLight = level.getBrightness(LightLayer.SKY, blockPos);
        return LightTexture.pack(bLight, sLight);
    }
}