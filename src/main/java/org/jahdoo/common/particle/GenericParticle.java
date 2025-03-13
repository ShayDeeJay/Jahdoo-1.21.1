package org.jahdoo.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jahdoo.common.particle.particle_options.BakedParticleOptions;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.ascension.utils.Helpers.*;

public class GenericParticle extends SimpleAnimatedParticle {

    public GenericParticle(
        ClientLevel leve,
        double x,
        double y,
        double z,
        double xSpeed,
        double ySpeed,
        double zSpeed,
        SpriteSet sprite
    ) {
        super(leve, x, y, z, sprite, 0.0125F);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize *= 0.55F;
        this.lifetime = 10 + this.random.nextInt(10);
        this.pickSprite(sprite);
        this.hasPhysics = false;
    }

    @Override
    public int getLightColor(float pPartialTick) {
        return 255;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderTypes.ABILITY_RENDERER;

    }

    @OnlyIn(Dist.CLIENT)
    public static class BakedProvider implements ParticleProvider<BakedParticleOptions> {
        private final SpriteSet sprites;

        public BakedProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(
            BakedParticleOptions type,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
        ) {
            var genericParticle = new GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites){
                @Override
                public void tick() {
                    super.tick();
                    if(!type.setStaticSize()) this.quadSize *= 0.9f;
                    this.speedUpWhenYMotionIsBlocked = true;
                }
            };

            if(type.setStaticSize()){
                genericParticle.quadSize = type.size();
            } else {
                genericParticle.quadSize *= type.size();
            }

            genericParticle.lifetime = type.lifetime() + Random.nextInt(type.lifetime());
            return genericParticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class GenericProvider implements ParticleProvider<GenericParticleOptions> {
        private final SpriteSet sprites;

        public GenericProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
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
            var genericParticle = new GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,this.sprites){
                int tick;

                @Override
                public void tick() {
                    super.tick();
                    tick++;
                    if(!type.setStaticSize()) this.quadSize *= 0.9f;
                    this.speedUpWhenYMotionIsBlocked = true;
                    if(tick % 4 == 0) setSprite(sprites.get(random.fork()));
                }

                @Override
                public void setPos(double x, double y, double z) {
                    super.setPos(x, y, z);
                }
            };


            if(type.setStaticSize()){
                genericParticle.quadSize = type.size();
            } else {
                genericParticle.quadSize *= type.size();
            }
            genericParticle.setColor(type.colour());
            genericParticle.setFadeColor(type.fade());
            genericParticle.lifetime = type.lifetime() + Random.nextInt(type.lifetime());
            return genericParticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class PlusParticle implements ParticleProvider<GenericParticleOptions> {
        private final SpriteSet sprites;

        public PlusParticle(SpriteSet pSprites) {
            this.sprites = pSprites;
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
            var genericParticle = new GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,this.sprites){
                int tick;

                @Override
                public void tick() {
                    super.tick();
                    tick++;
                    if(!type.setStaticSize()) this.quadSize *= 0.96f;
                    this.speedUpWhenYMotionIsBlocked = true;
                }

                @Override
                public void setPos(double x, double y, double z) {
                    super.setPos(x, y, z);
                }
            };

            genericParticle.gravity = 0;
            genericParticle.setColor(type.colour());
            genericParticle.setFadeColor(type.fade());
            genericParticle.lifetime = type.lifetime() + Random.nextInt(type.lifetime());
            return genericParticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ElectricalParticle implements ParticleProvider<GenericParticleOptions> {
        private final SpriteSet sprites;

        public ElectricalParticle(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(
            GenericParticleOptions type,
            ClientLevel level,
            double x, double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
        ) {
            var genericParticle = new GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,this.sprites){
                @Override
                public void tick() {
                    super.tick();
                    this.quadSize *= 0.9f;

                    var randX = (random.nextDouble() - 0.5) * 0.5f; // Larger random value between -1.0 and 1.0
                    var randY = (random.nextDouble() - 0.5) * 0.5f;
                    var randZ = (random.nextDouble() - 0.5) * 0.5f;
                    this.setPos(this.x + randX, this.y + randY, this.z + randZ);

                    this.xd += (random.nextDouble() - 0.5) * 0.6; // Larger random velocity change
                    this.yd += (random.nextDouble() - 0.5) * 0.6;
                    this.zd += (random.nextDouble() - 0.5) * 0.6;

                    this.xd *= type.speed();
                    this.yd *= type.speed();
                    this.zd *= type.speed();

                    this.speedUpWhenYMotionIsBlocked = true;
                }
            };

            if (type.setStaticSize()) genericParticle.quadSize = type.size(); else  genericParticle.quadSize *= type.size();
            genericParticle.setColor(type.colour());
            genericParticle.setFadeColor(type.fade());
            genericParticle.lifetime = type.lifetime() + Random.nextInt(type.lifetime());
            return genericParticle;
        }
    }


}
