package org.jahdoo.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public class ParticleRenderTypes {
    static final ParticleRenderType ABILITY_RENDERER = new ParticleRenderType() {

        @Override
        public BufferBuilder begin(Tesselator buffer, TextureManager textureManager) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();

            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            RenderSystem.enableCull();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.setShaderColor(1,1,1,1f);
            RenderSystem.enableDepthTest();
            return buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "jahdoo:ability_part_ren";
        }
    };
}
