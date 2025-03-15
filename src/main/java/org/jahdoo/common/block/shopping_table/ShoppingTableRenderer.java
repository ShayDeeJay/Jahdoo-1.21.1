package org.jahdoo.common.block.shopping_table;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.wand.WandItem;
import org.joml.Matrix4f;

import static net.minecraft.client.gui.Font.DisplayMode.NORMAL;
import static net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
import static net.minecraft.world.item.ItemDisplayContext.FIXED;
import static org.jahdoo.ascension.attachments.PlayerWallet.*;
import static org.jahdoo.ascension.utils.ColourStore.OFF_WHITE;
import static org.jahdoo.common.block.shopping_table.DisplayDirection.*;
import static org.jahdoo.common.block.shopping_table.ShoppingTableBlock.TEXTURE;

public class ShoppingTableRenderer implements BlockEntityRenderer<ShoppingTableEntity>{

    private final BlockEntityRenderDispatcher entityRenderDispatcher;

    public ShoppingTableRenderer(Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(ShoppingTableEntity entity, float partial, PoseStack poseStack, MultiBufferSource source, int packedLight, int packedOverlay) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();
        var direction = DisplayDirection.fromMCDirection(entity.getBlockState().getValue(ShoppingTableBlock.FACING));

        if(entity.getBlockState().getValue(TEXTURE) == 2/*Set bool as render case, made for better items*/){
            var scale = 1.99F;
            var stack = new ItemStack(Blocks.TINTED_GLASS.asItem());

            poseStack.pushPose();
            poseStack.translate(0.5, 1.31, 0.5);
            poseStack.scale(scale, scale, scale);
            itemRenderer.renderStatic(stack, FIXED, packedLight, NO_OVERLAY, poseStack, source, entity.getLevel(), 1);
            poseStack.popPose();
        }

        renderPrice(entity, poseStack, source, packedLight, itemRenderer, direction);
        renderSaleItem(entity, poseStack, source, packedLight, itemRenderer, direction);

    }

    private void renderPrice(ShoppingTableEntity entity, PoseStack poseStack, MultiBufferSource source, int packedLight, ItemRenderer renderer, DisplayDirection direction) {
        var number = 0.5f;

        if(!entity.getItem().getStackInSlot(0).isEmpty()){
            poseStack.pushPose();
            var coin = CurrencyConverter.getCoin(entity.itemCosts);
            renderCostText(Helpers.withStyleComponent(String.valueOf(coin.getSecond()), coin.getFirst().getTextColour()), poseStack, source, -1, direction);
            poseStack.translate(direction.x(), number, direction.z());

            var x = 0.6f;
            poseStack.scale(x, x, x);
            poseStack.mulPose(Axis.YP.rotationDegrees(direction.direction()));
            renderer.renderStatic(entity.getCurrencyType(), FIXED, packedLight, NO_OVERLAY, poseStack, source, entity.getLevel(), 1);
            poseStack.popPose();
        }

    }

    private void renderSaleItem(ShoppingTableEntity entity, PoseStack poseStack, MultiBufferSource source, int packedLight, ItemRenderer renderer, DisplayDirection direction) {
        var itemStack1 = entity.inputItemHandler.getStackInSlot(0);
        if(!itemStack1.isEmpty()){
            var height = 1.3f;
            var scale = itemStack1.getItem() instanceof WandItem ? 0.75F : 0.5f;

            renderName(entity, itemStack1.getHoverName(), poseStack, source, direction);
            poseStack.pushPose();
            poseStack.translate(0.5f, height, 0.5f);
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotationDegrees(direction.direction()).invert());
            renderer.renderStatic(itemStack1, FIXED, packedLight, NO_OVERLAY, poseStack, source, entity.getLevel(), 1);
            poseStack.popPose();
        }
    }

    protected void renderCostText(Component name, PoseStack poseStack, MultiBufferSource source, int textColour, DisplayDirection direction) {
        var z = 0.01f;
        poseStack.pushPose();
        var adjustEastWest = direction == EAST ? 0.01 : direction == WEST ? -0.01 : direction == SOUTH ? 0.01 : direction == NORTH ? -0.01 : 0;

        poseStack.translate(direction.x() + adjustEastWest, 0.78, direction.z() + adjustEastWest);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.direction()));
        poseStack.scale(z, z, z);
        var matrix4f = poseStack.last().pose();
        var font = Minecraft.getInstance().font;
        var f1 = (float) -font.width(name) / 2;
        font.drawInBatch(name, f1, 0, textColour, false, matrix4f, source, NORMAL , 0, 255);
        poseStack.popPose();
    }

    protected void renderName(BlockEntity entity, Component displayName, PoseStack poseStack, MultiBufferSource source, DisplayDirection direction) {
        var isRandomTable = entity.getBlockState().getValue(TEXTURE) == 3;
        poseStack.pushPose();
        poseStack.translate(0.5, entity.getBlockState().getValue(TEXTURE) == 2 ? 2.1 : 1.9, 0.5);

        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.direction()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        var x = isRandomTable ? 0.025F : 0.015F;
        poseStack.scale(x, -x, x);
        Matrix4f matrix4f = poseStack.last().pose();

        var font = Minecraft.getInstance().font;
        var f1 = (float)(-font.width(displayName) / 2);

        font.drawInBatch(isRandomTable ? Component.literal("?") : displayName, isRandomTable ? -3 : f1, 0, OFF_WHITE, false, matrix4f, source, NORMAL , 0, 255);
        poseStack.popPose();
    }

}