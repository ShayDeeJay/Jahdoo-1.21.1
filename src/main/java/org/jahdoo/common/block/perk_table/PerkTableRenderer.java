package org.jahdoo.common.block.perk_table;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

import static net.minecraft.client.Minecraft.getInstance;
import static org.jahdoo.common.block.loot_chest.LootChestRenderer.roomData;
import static org.jahdoo.common.block.perk_table.PerkTable.HALF;
import static org.jahdoo.common.block.perk_table.PerkTable.TEXTURE;
import static org.jahdoo.common.registers.ItemReg.*;

public class PerkTableRenderer implements BlockEntityRenderer<PerkTableEntity>{
    private final EntityRenderDispatcher entityRenderDispatcher;

    public PerkTableRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getEntityRenderer();
    }

    public static final List<Item> getByIndex = List.of(
        HEALTH_CONTAINER.get(),MANA_CONTAINER.get(),QUEST_CONTAINER.get(),BOON_CONTAINER.get(),Blocks.BLACK_BED.asItem(),Items.DIAMOND_SWORD.asItem()
    );

    @Override
    public void render(PerkTableEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int packedLight, int packed) {
        if(entity.getBlockState().getValue(HALF).equals(DoubleBlockHalf.UPPER)) return;
        var mc = getInstance();
        var itemRenderer = mc.getItemRenderer();
        int state = entity.getBlockState().getValue(TEXTURE);
        var render = new ItemStack(getByIndex.get(state));
        var rotate = entity.getPrivateTicks() + partialTick;
        var animate = rotate / 12;
        var scale = Math.min(1.2F, animate);
        var bobOff = Math.sin(rotate / 10.0F) * 0.08F + 2F - 0.51;
        var packedLightCoords = entity.interacted(mc.player) ? 50 : 180;

        stack.pushPose();
        stack.translate(0.5f, Math.min(bobOff, animate), 0.5F);
        stack.scale(scale, scale, scale);
        stack.mulPose(Axis.YP.rotationDegrees(rotate * 2));
        itemRenderer.renderStatic(
            render,
            ItemDisplayContext.FIXED,
            packedLightCoords,
            OverlayTexture.NO_OVERLAY,
            stack,
            source,
            entity.getLevel(),
            1
        );
        stack.popPose();

        if(state == 2){
            var offWhite = ColourHelpers.getExperienceGreen();
            var displayName = TextHelpers.withStyleComponent("Quest", FastColor.ARGB32.color(232, 169, 0));
            var font = Minecraft.getInstance().font;
            var f1 = (float)(-font.width(displayName) / 2);
            var x = 0.020F;
            var scaled = Math.sin(((entity.getPrivateTicks() + partialTick) / 10.0F)) * 0.2F + 5;
            var scale1 = (float) scaled/2;

            stack.pushPose();
            stack.translate(0.5, scaled/1.8, 0.5);
            stack.scale(scale1, scale1, scale1);
            stack.mulPose(entityRenderDispatcher.camera.rotation());
            stack.scale(x, -x, x);
            font.drawInBatch(displayName, f1, 0, offWhite, true, stack.last().pose(), source, Font.DisplayMode.NORMAL , 0, packedLightCoords);
            stack.popPose();
        }

        if(state == 3){
            roomData(entity, stack, source, entityRenderDispatcher, partialTick);
        }
    }

}