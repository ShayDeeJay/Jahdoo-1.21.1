package org.jahdoo.common.entities.void_spider;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.entities.inferno_creeper.GlowLayer;

public class VoidSpiderEyesLayer <T extends Entity, M extends SpiderModel<T>> extends GlowLayer<T, M> {
    private static final RenderType SPIDER_EYES = RenderType.eyes(JahdooHelpers.res("textures/entity/void_spider_eyes.png"));

    public VoidSpiderEyesLayer(RenderLayerParent<T, M> p_117507_) {
        super(p_117507_);
    }

    public RenderType renderType() {
        return SPIDER_EYES;
    }
}
