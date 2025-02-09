package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.block.shopping_table.ShoppingTableBlock;
import org.jahdoo.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.utils.ColourStore;
import org.joml.Matrix4f;

import static org.jahdoo.block.shopping_table.ShoppingTableBlock.TEXTURE;
import static org.jahdoo.client.block_renderer.ShoppingTableRenderer.DisplayDirection.*;

public class ShoppingTableRenderer implements BlockEntityRenderer<ShoppingTableEntity>{
    private final BlockEntityRenderDispatcher entityRenderDispatcher;


    public ShoppingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(ShoppingTableEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();
        var direction = DisplayDirection.fromMCDirection(pBlockEntity.getBlockState().getValue(ShoppingTableBlock.FACING));

        if(pBlockEntity.getBlockState().getValue(TEXTURE) == 2/*Set bool as render case, made for better items*/){
            pPoseStack.pushPose();
            var scale = 1.99F;
            pPoseStack.translate(0.5, 1.31, 0.5);
            pPoseStack.scale(scale, scale, scale);
            var stack = new ItemStack(Blocks.TINTED_GLASS.asItem());
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }

        renderPrice(pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer, direction);
        renderSaleItem(pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer, direction);

    }

    private void renderPrice(ShoppingTableEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ItemRenderer itemRenderer, DisplayDirection direction) {
        var itemStack1 = pBlockEntity.getCurrencyType() == null ? ItemStack.EMPTY : pBlockEntity.getCurrencyType();
        var number = 0.5f;

        if(!itemStack1.isEmpty()){
            pPoseStack.pushPose();
            renderCostText(pBlockEntity, Component.literal(String.valueOf(pBlockEntity.getCost())), pPoseStack, pBuffer, -1, direction);
            pPoseStack.translate(direction.x, number, direction.z);
            var x = 0.2f;
            pPoseStack.scale(x, x, x);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(direction.direction));
            itemRenderer.renderStatic(itemStack1, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }

    }

    private void renderSaleItem(ShoppingTableEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ItemRenderer itemRenderer, DisplayDirection direction) {
        var itemStack1 = pBlockEntity.inputItemHandler.getStackInSlot(0);
        if(!itemStack1.isEmpty()){
            var height = 1.3f;
            var scale = itemStack1.getItem() instanceof WandItem ? 0.75F : 0.5f;

            renderName(pBlockEntity, itemStack1.getHoverName(), pPoseStack, pBuffer, pPackedLight, direction);
            pPoseStack.pushPose();
            pPoseStack.translate(0.5f, height, 0.5f);
            pPoseStack.scale(scale, scale, scale);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(direction.direction).invert());
            itemRenderer.renderStatic(itemStack1, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }
    }

    protected void renderCostText(BlockEntity bEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int textColour, DisplayDirection direction) {
        var z = 0.01f;
        pPoseStack.pushPose();
        var adjustEastWest = direction == EAST ? 0.01 : direction == WEST ? -0.01 : direction == SOUTH ? 0.01 : direction == NORTH ? -0.01 : 0;

        pPoseStack.translate(direction.x + adjustEastWest, 0.78, direction.z + adjustEastWest);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(direction.direction));
        pPoseStack.scale(z, z, z);
        var matrix4f = pPoseStack.last().pose();
        var font = Minecraft.getInstance().font;
        var f1 = (float) -font.width(pDisplayName) / 2;
        font.drawInBatch(pDisplayName, f1, 0, textColour, false, matrix4f, pBuffer, Font.DisplayMode.NORMAL , 0, 255);
        pPoseStack.popPose();
    }

    protected void renderName(BlockEntity entity, Component displayName, PoseStack pPoseStack, MultiBufferSource bufferSource, int packedLight, DisplayDirection direction) {
        var isRandomTable = entity.getBlockState().getValue(TEXTURE) == 3;
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, entity.getBlockState().getValue(TEXTURE) == 2 ? 2.1 : 1.9, 0.5);

        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(direction.direction));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180));
        var x = isRandomTable ? 0.025F : 0.015F;
        pPoseStack.scale(x, -x, x);
        Matrix4f matrix4f = pPoseStack.last().pose();
        var font = Minecraft.getInstance().font;
        var f1 = (float)(-font.width(displayName) / 2);

        font.drawInBatch(isRandomTable ? Component.literal("?") : displayName, isRandomTable ? -3 : f1, 0, ColourStore.OFF_WHITE, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
        pPoseStack.popPose();
    }

    public record DisplayDirection(
        int direction,
        double x,
        double z
    ){
        public static final DisplayDirection NORTH = new DisplayDirection(0, 0.5, 0);
        public static final DisplayDirection SOUTH = new DisplayDirection(180, 0.5, 1);
        public static final DisplayDirection EAST = new DisplayDirection(90, 1, 0.5);
        public static final DisplayDirection WEST = new DisplayDirection(270, 0, 0.5);
        private static final DisplayDirection[] DIRECTIONS = {NORTH, EAST, SOUTH, WEST};

        public static void writeToNBT(DisplayDirection displayDirection, CompoundTag tag) {
            if(displayDirection != null){
                tag.putInt("Direction", displayDirection.direction());
                tag.putDouble("X", displayDirection.x());
                tag.putDouble("Z", displayDirection.z());
            }
        }

        public static DisplayDirection readFromNBT(CompoundTag tag) {
            int direction = tag.getInt("Direction");
            double x = tag.getDouble("X");
            double z = tag.getDouble("Z");
            return new DisplayDirection(direction, x, z);
        }

        public DisplayDirection rotate() {
            for (int i = 0; i < DIRECTIONS.length; i++) {
                if (this.equals(DIRECTIONS[i])) {
                    return DIRECTIONS[(i + 1) % DIRECTIONS.length];
                }
            }

            return NORTH;
        }

        public static DisplayDirection fromMCDirection(Direction mcDirection) {
            return switch (mcDirection) {
                case NORTH -> NORTH;
                case SOUTH -> SOUTH;
                case EAST -> EAST;
                case WEST -> WEST;
                default -> throw new IllegalArgumentException("Unsupported direction: " + mcDirection);
            };
        }
    }

}