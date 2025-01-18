package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jahdoo.block.shopping_table.ShoppingTableBlock;
import org.jahdoo.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.joml.Matrix4f;

import static org.jahdoo.block.shopping_table.ShoppingTableBlock.TEXTURE;

public class ShoppingTableRenderer implements BlockEntityRenderer<ShoppingTableEntity>{
    private final BlockEntityRenderDispatcher entityRenderDispatcher;


    public ShoppingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(ShoppingTableEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();
        var blockRenderer = mc.getBlockRenderer();
        var direction = DisplayDirection.fromMCDirection(pBlockEntity.getBlockState().getValue(ShoppingTableBlock.FACING));
        var state = Blocks.TINTED_GLASS.defaultBlockState();


        if(pBlockEntity.getBlockState().getValue(TEXTURE) == 2/*Set bool as render case, made for better items*/){
            pPoseStack.pushPose();
            var scale = 0.98F;
            pPoseStack.translate(0.01, 0.814, 0.01);
            pPoseStack.scale(scale, scale, scale);

            blockRenderer.renderSingleBlock(state, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, ModelData.EMPTY, RenderType.translucentMovingBlock());
            pPoseStack.popPose();
        }

        renderPrice(pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer, direction);
        renderSaleItem(pBlockEntity, pPoseStack, pBuffer, pPackedLight, itemRenderer, direction);

    }

    private void renderPrice(ShoppingTableEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ItemRenderer itemRenderer, DisplayDirection direction) {
        var itemStack1 = new ItemStack(ItemsRegister.GOLD_COIN.get());
        var number = 0.5f;

        pPoseStack.pushPose();
        renderPrice(pBlockEntity, Component.literal("24"), pPoseStack, pBuffer, -1,direction);
        pPoseStack.translate(direction.x, number, direction.z);
        var x = 0.2f;
        pPoseStack.scale(x, x, x);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(direction.direction));
        itemRenderer.renderStatic(itemStack1, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
        pPoseStack.popPose();

    }

    private void renderSaleItem(ShoppingTableEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ItemRenderer itemRenderer, DisplayDirection direction) {
        var itemStack1 = pBlockEntity.inputItemHandler.getStackInSlot(0);
        if(!itemStack1.isEmpty()){
            var height = 1.3f;
            var scale = itemStack1.getItem() instanceof WandItem ? 0.75F : 0.5f;

            renderName(itemStack1.getHoverName(), pPoseStack, pBuffer, pPackedLight, direction);
            pPoseStack.pushPose();
            pPoseStack.translate(0.5f, height, 0.5f);
            pPoseStack.scale(scale, scale, scale);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(direction.direction).invert());
            itemRenderer.renderStatic(itemStack1, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }
    }

    protected void renderPrice(BlockEntity bEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int textColour, DisplayDirection direction) {
        var z = 0.01f;

        pPoseStack.pushPose();
        pPoseStack.translate(direction.x, 0.78, direction.z);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(direction.direction));
        pPoseStack.scale(z, z, z);
        var matrix4f = pPoseStack.last().pose();
        var font = Minecraft.getInstance().font;
        var f1 = (float) -font.width(pDisplayName) / 2;
        font.drawInBatch(pDisplayName, f1, 0, textColour, true, matrix4f, pBuffer, Font.DisplayMode.SEE_THROUGH , 0, 255);
        pPoseStack.popPose();
    }

    protected void renderName(Component displayName, PoseStack pPoseStack, MultiBufferSource bufferSource, int packedLight, DisplayDirection direction) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 2.1, 0.5);

        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(direction.direction));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180));
        var x = 0.015F;
        pPoseStack.scale(x, -x, x);
        Matrix4f matrix4f = pPoseStack.last().pose();
        var font = Minecraft.getInstance().font;
        var f1 = (float)(-font.width(displayName) / 2);
        font.drawInBatch(displayName, f1, 0, ColourStore.OFF_WHITE, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL , 0, 255);
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