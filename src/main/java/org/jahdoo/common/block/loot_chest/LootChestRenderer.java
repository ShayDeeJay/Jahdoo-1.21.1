package org.jahdoo.common.block.loot_chest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.shaydee.shaydeeapi.block.SyncedBlockEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.WEST;

public class LootChestRenderer extends GeoBlockRenderer<LootChestEntity>{
    private final EntityRenderDispatcher entityRenderDispatcher;

    public LootChestRenderer(BlockEntityRendererProvider.Context context) {
        super(new LootChestModel());
        this.entityRenderDispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(LootChestEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        if(animatable.isCoinChest() && !animatable.showHover){
            roomData(animatable, poseStack, bufferSource, entityRenderDispatcher, partialTick);
        }
    }

    @Override
    public void actuallyRender(PoseStack poseStack, LootChestEntity chestEntity, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        var blockState = chestEntity.getBlockState();
        var direction =  blockState.getValue(LootChestBlock.FACING);
        var jahdooRarity = KeyItem.getJahdooRarity(new CustomModelData(chestEntity.getRarity));
        var displayName = JahdooHelpers.withStyleComponent(jahdooRarity.getSerializedName(), jahdooRarity.getColour());

        if(chestEntity.canRender()) {
            renderName(chestEntity, displayName, poseStack, bufferSource, direction, partialTick);
            renderNameReverse(chestEntity, displayName, poseStack, bufferSource, direction, partialTick);
        }

        super.actuallyRender(poseStack, chestEntity, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    public static void roomData(SyncedBlockEntity entity, PoseStack pPoseStack, MultiBufferSource bufferSource, EntityRenderDispatcher dispatcher, float partialTicks) {
        var offWhite = ColourStore.EXPERIENCE_GREEN;
        var displayName = JahdooHelpers.withStyleComponent("" + (entity.getSaveInt() + 1), offWhite);
        pPoseStack.pushPose();

        var scale = Math.sin(((entity.getPrivateTicks() + partialTicks) / 10.0F)) * 0.2F + 5;
        pPoseStack.translate(0.5, scale, 0.5);
        var scale1 = (float) scale * 2;
        pPoseStack.scale(scale1, scale1, scale1);
        pPoseStack.mulPose(dispatcher.camera.rotation());

        var x = 0.020F;
        pPoseStack.scale(x, -x, x);
        Matrix4f matrix4f = pPoseStack.last().pose();

        var font = Minecraft.getInstance().font;
        var f1 = (float)(-font.width(displayName) / 2);

        var text = JahdooHelpers.withStyleComponent("Room", ColourStore.SUB_HEADER_COLOUR);
        var f2 = (float)(-font.width(text) / 2);

        font.drawInBatch(text, f2, -10, offWhite, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        font.drawInBatch(displayName, f1, 0, offWhite, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        pPoseStack.popPose();

    }

    protected void renderName(LootChestEntity entity, Component displayName, PoseStack pPoseStack, MultiBufferSource bufferSource, Direction direction, float partialTicks) {
        pPoseStack.pushPose();
        var uniqueOffset = (entity.getRarity ) * 0.5 ; // Or another unique value per entity
        var scale = Math.sin(((entity.getPrivateTicks() + partialTicks) / 10.0F) + uniqueOffset) * 0.08F + 1.4;

        pPoseStack.translate(0, scale, 0);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(direction == EAST || direction == WEST ? direction.getOpposite().toYRot() : direction.toYRot()));

        var x = 0.020F;
        pPoseStack.scale(x, -x, x);
        Matrix4f matrix4f = pPoseStack.last().pose();
        var font = Minecraft.getInstance().font;
        var f1 = (float)(-font.width(displayName) / 2);
        font.drawInBatch(displayName, f1, 0, ColourStore.OFF_WHITE, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        pPoseStack.popPose();

    }

    protected void renderNameReverse(LootChestEntity entity, Component displayName, PoseStack pPoseStack, MultiBufferSource bufferSource, Direction direction, float partialTicks) {
        pPoseStack.pushPose();
        var uniqueOffset = (entity.getRarity ) * 0.5 ; // Or another unique value per entity
        var scale = Math.sin(((entity.getPrivateTicks() + partialTicks) / 10.0F) + uniqueOffset) * 0.08F + 1.4;

        pPoseStack.translate(0, scale, 0);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(direction == EAST || direction == WEST ? direction.toYRot() : direction.getOpposite().toYRot()));
        var x = 0.020F;
        pPoseStack.scale(x, -x, x);
        Matrix4f matrix4f = pPoseStack.last().pose();
        var font = Minecraft.getInstance().font;
        var f1 = (float)(-font.width(displayName) / 2);
        font.drawInBatch(displayName, f1, 0, ColourStore.OFF_WHITE, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        pPoseStack.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(LootChestEntity animatable) {
        return super.getTextureLocation(animatable);
    }
}

