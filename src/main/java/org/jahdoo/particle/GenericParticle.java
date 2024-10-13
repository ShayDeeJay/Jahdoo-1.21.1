package org.jahdoo.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.utils.GeneralHelpers;

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
    }

    @Override
    public void move(double pX, double pY, double pZ) {
        super.move(pX, pY, pZ);
    }

    @Override
    public int getLightColor(float pPartialTick) {
        return 255;
    }

    @Override
    public ParticleRenderType getRenderType() {

        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
                    this.quadSize *= 0.9f;
                }

            };
            if(pType.setStaticSize()){
                genericParticle.quadSize = pType.size();
            } else {
                genericParticle.quadSize *= pType.size();
            }
            genericParticle.lifetime = pType.lifetime() + GeneralHelpers.Random.nextInt(pType.lifetime());
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
                int counter;
                @Override
                public void tick() {
                    super.tick();
                    counter++;
                    if(!pType.setStaticSize()){
                        this.quadSize *= 0.9f;
                    }
                    this.speedUpWhenYMotionIsBlocked = true;
                }

                @Override
                public AABB getBoundingBox() {
                    return super.getBoundingBox();
                }
            };

            genericParticle.setAlpha(1f);
            if(pType.setStaticSize()){
                genericParticle.quadSize = pType.size();
            } else {
                genericParticle.quadSize *= pType.size();
            }
            genericParticle.setColor(pType.colour());
            genericParticle.setFadeColor(pType.fade());
            genericParticle.lifetime = pType.lifetime() + GeneralHelpers.Random.nextInt(pType.lifetime());
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

                    // Randomize the quad size to simulate spark flickering
                    this.quadSize *= 0.9f;

                    // Make abrupt, large position changes to simulate snapping movement
                    double randX = (random.nextDouble() - 0.5) * 0.5f; // Larger random value between -1.0 and 1.0
                    double randY = (random.nextDouble() - 0.5) * 0.5f;
                    double randZ = (random.nextDouble() - 0.5) * 0.5f;

                    // Apply abrupt position change or keep as is based on pType
                    this.setPos(
                      this.x + randX,
                        this.y + randY,
                        this.z + randZ
                    );

                    // Occasionally change velocity to add more erratic behavior
                    this.xd += (random.nextDouble() - 0.5) * 0.6; // Larger random velocity change
                    this.yd += (random.nextDouble() - 0.5) * 0.6;
                    this.zd += (random.nextDouble() - 0.5) * 0.6;

                    // Optional: you can still keep speed decay to simulate sparks losing energy
                    this.xd *= pType.speed();
                    this.yd *= pType.speed();
                    this.zd *= pType.speed();

                    this.speedUpWhenYMotionIsBlocked = true;
                }
            };

            genericParticle.setAlpha(1f);
            if (pType.setStaticSize()) {
                genericParticle.quadSize = pType.size();
            } else {
                genericParticle.quadSize *= pType.size();
            }
            genericParticle.setColor(pType.colour());
            genericParticle.setFadeColor(pType.fade());
            genericParticle.lifetime = pType.lifetime() + GeneralHelpers.Random.nextInt(pType.lifetime());

            return genericParticle;
        }
    }

}
