package org.jahdoo.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ParticleRenderTypes {

    static ParticleRenderType ABILITY_RENDERER = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator buffer, @NotNull TextureManager textureManager) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            RenderSystem.enableCull();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableDepthTest();
            return buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "ABILITY_PART_RENDER";
        }
    };
}
