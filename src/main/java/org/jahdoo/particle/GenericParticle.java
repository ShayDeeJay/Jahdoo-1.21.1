package org.jahdoo.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

public class GenericParticle extends SimpleAnimatedParticle {

    public GenericParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pSprites, 0.0125F);
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize *= 0.55F;
        this.lifetime = 10 + this.random.nextInt(10);
        this.pickSprite(pSprites);
        this.hasPhysics = false;
        this.alpha = 0.5f;
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

        public Particle createParticle(BakedParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            GenericParticle genericParticle = new GenericParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed,this.sprites){
                @Override
                public void tick() {
                    super.tick();
                    if(!pType.setStaticSize()) this.quadSize *= 0.9f;
                    this.speedUpWhenYMotionIsBlocked = true;
                }
            };

            if(pType.setStaticSize()){
                genericParticle.quadSize = pType.size();
            } else {
                genericParticle.quadSize *= pType.size();
            }

            genericParticle.lifetime = pType.lifetime() + ModHelpers.Random.nextInt(pType.lifetime());
            return genericParticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class GenericProvider implements ParticleProvider<GenericParticleOptions> {
        private final SpriteSet sprites;

        public GenericProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(GenericParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            GenericParticle genericParticle = new GenericParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed,this.sprites){
                int tick;
                @Override
                public void tick() {
                    super.tick();
                    tick++;
                    if(!pType.setStaticSize()) this.quadSize *= 0.9f;
                    this.speedUpWhenYMotionIsBlocked = true;
                    if(tick % 4 == 0) setSprite(sprites.get(random.fork()));
                }
            };

            if(pType.setStaticSize()){
                genericParticle.quadSize = pType.size();
            } else {
                genericParticle.quadSize *= pType.size();
            }
            genericParticle.setColor(pType.colour());
            genericParticle.setFadeColor(pType.fade());
            genericParticle.lifetime = pType.lifetime() + ModHelpers.Random.nextInt(pType.lifetime());
            return genericParticle;
        }
    }


    @OnlyIn(Dist.CLIENT)
    public static class ElectricalParticle implements ParticleProvider<GenericParticleOptions> {
        private final SpriteSet sprites;

        public ElectricalParticle(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(GenericParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            GenericParticle genericParticle = new GenericParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed,this.sprites){

                @Override
                public void tick() {
                    super.tick();
                    this.quadSize *= 0.9f;

                    double randX = (random.nextDouble() - 0.5) * 0.5f; // Larger random value between -1.0 and 1.0
                    double randY = (random.nextDouble() - 0.5) * 0.5f;
                    double randZ = (random.nextDouble() - 0.5) * 0.5f;

                    this.setPos(
                      this.x + randX,
                        this.y + randY,
                        this.z + randZ
                    );

                    this.xd += (random.nextDouble() - 0.5) * 0.6; // Larger random velocity change
                    this.yd += (random.nextDouble() - 0.5) * 0.6;
                    this.zd += (random.nextDouble() - 0.5) * 0.6;

                    this.xd *= pType.speed();
                    this.yd *= pType.speed();
                    this.zd *= pType.speed();

                    this.speedUpWhenYMotionIsBlocked = true;
                }
            };

            if (pType.setStaticSize()) genericParticle.quadSize = pType.size(); else  genericParticle.quadSize *= pType.size();
            genericParticle.setColor(pType.colour());
            genericParticle.setFadeColor(pType.fade());
            genericParticle.lifetime = pType.lifetime() + ModHelpers.Random.nextInt(pType.lifetime());

            return genericParticle;
        }
    }


}
