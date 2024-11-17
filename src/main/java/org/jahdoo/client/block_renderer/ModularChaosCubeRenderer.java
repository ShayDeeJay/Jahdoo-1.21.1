package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.client.block_models.ModularChaosCubeModel;
import org.jahdoo.entities.ElementProjectile;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.Color;

import static org.jahdoo.block.infuser.InfuserBlock.FACING;

public class ModularChaosCubeRenderer extends GeoBlockRenderer<ModularChaosCubeEntity>{

    public ModularChaosCubeRenderer(BlockEntityRendererProvider.Context context) {
        super(new ModularChaosCubeModel());
    }

}

