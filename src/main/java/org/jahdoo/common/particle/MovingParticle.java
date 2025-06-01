package org.jahdoo.common.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;

import java.util.List;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
import static org.jahdoo.trial_nexus.utils.Configuration.BRIGHT_PARTICLE;

public class MovingParticle extends TextureSheetParticle {
    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final boolean isGlowing;
    private final Particle.LifetimeAlpha lifetimeAlpha;

    protected MovingParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        this(level, x, y, z, xSpeed, ySpeed, zSpeed, false, LifetimeAlpha.ALWAYS_OPAQUE);
    }

    MovingParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, boolean isGlowing, Particle.LifetimeAlpha lifetimeAlpha) {
        super(level, x, y, z);
        this.isGlowing = isGlowing;
        this.lifetimeAlpha = lifetimeAlpha;
        this.setAlpha(lifetimeAlpha.startAlpha());
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.xStart = x;
        this.yStart = y;
        this.zStart = z;
        this.xo = x + xSpeed;
        this.yo = y + ySpeed;
        this.zo = z + zSpeed;
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
//        this.rCol = f;
//        this.gCol = f;
//        this.bCol = f;
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * (double)10.0F) + 30;
    }

    public static List<Float> intToRGB(int color) {
        int red   = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue  = color & 0xFF;

        // Convert to float range 0.0 - 1.0
        float fRed = red / 255.0f;
        float fGreen = green / 255.0f;
        float fBlue = blue / 255.0f;

        return List.of(fRed, fBlue, fGreen);
    }

    public ParticleRenderType getRenderType() {
        return BRIGHT_PARTICLE.get() ? ParticleRenderTypes.ABILITY_RENDERER : ParticleRenderTypes.ABILITY_RENDERER_ALT;
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    public int getLightColor(float partialTick) {
        return FULL_BRIGHT;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float f = (float)this.age / (float)this.lifetime;
            f = 1.0F - f;
            float f1 = 1.0F - f;
            f1 *= f1;
            f1 *= f1;
            this.x = this.xStart + this.xd * (double)f;
            this.y = this.yStart + this.yd * (double)f - (double)(f1 * 1.2F);
            this.z = this.zStart + this.zd * (double)f;
        }
    }

    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        this.setAlpha(this.lifetimeAlpha.currentAlphaForAge(this.age, this.lifetime, partialTicks));
        super.render(buffer, renderInfo, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    public static class EnchantProvider implements ParticleProvider<GenericParticleOptions> {
        private final SpriteSet sprite;

        public EnchantProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(
            GenericParticleOptions type,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
        ) {
            var movingParticle = new MovingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed){
                int tick;
                @Override
                public void tick() {
                    super.tick();
                    tick++;
                    if(!type.setStaticSize()) this.quadSize *= 0.9f;
                    this.speedUpWhenYMotionIsBlocked = true;
                }

            };
            int[] utility = {39, 236, 144};

            var colourByType = type.colour() == 0 ? utility : utility;

            if(type.setStaticSize()){
                movingParticle.quadSize = type.size();
            } else {
                movingParticle.quadSize *= type.size();
            }
            movingParticle.pickSprite(this.sprite);
            movingParticle.setColor(colourByType[0]/ 255.0f, colourByType[1]/ 255.0f, colourByType[2]/ 255.0f);
            movingParticle.setLifetime(type.lifetime());
            return movingParticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class NautilusProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public NautilusProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            var movingParticle = new MovingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            movingParticle.pickSprite(this.sprite);
            return movingParticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class VaultConnectionProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public VaultConnectionProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            var movingParticle = new MovingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, true, new Particle.LifetimeAlpha(0.0F, 0.6F, 0.25F, 1.0F));
            movingParticle.scale(1.5F);
            movingParticle.pickSprite(this.sprite);
            return movingParticle;
        }
    }
}