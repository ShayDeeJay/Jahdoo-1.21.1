package org.jahdoo.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.ElementRegistry;

import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;

public abstract class AbstractUtilityProjectile extends DefaultEntityBehaviour {

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.UTILITY.get();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        discardParticleEffect(5);
    }

    protected void discardParticleEffect(int lifetime) {
        var particle = genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, ElementRegistry.UTILITY.get(), lifetime, 1.5f, 0.1f);
        ParticleHandlers.particleBurst(this.genericProjectile.level(), this.genericProjectile.position().add(0,0.1,0), 1, particle,0,0,0,0.06f);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.genericProjectile.wandAbilityHolder();
    }

    @Override
    public void onTickMethod() {
        if(this.genericProjectile != null){

            ParticleHandlers.GenericProjectile(this.genericProjectile,
                bakedParticleOptions(getElementType().getTypeId(), 2, 0.15f, true),
                genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 5, 1.2f),
                0.015
            );
//            playParticles3(
//                genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 3, 0.08f, true),
//                genericProjectile, 10, 0.01
//            );
        }
    }

    public Level getLevel(){
        return this.genericProjectile.level();
    }

    protected void utilityParticleBurst(Level level, Vec3 pPos, int lifeTime, float size, int count, float speed) {
        int col1 = this.getElementType().particleColourPrimary();
        int col2 = this.getElementType().particleColourFaded();
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifeTime, size, col1, col2, false);
        ParticleHandlers.particleBurst(level, pPos, count, genericParticle, speed);
    }

    @Override
    public void discardCondition() {
        if(this.genericProjectile != null){
            if (this.genericProjectile.tickCount > 300) {
                this.genericProjectile.discard();
                this.discardParticleEffect(5);
            }
//            var maxDis = this.genericProjectile.maxDistance;
//            if(maxDis == 0) return;
//            if(this.genericProjectile.distanceToSqr(genericProjectile.blockEntityPos) > (maxDis * 6)){
//                this.genericProjectile.discard();
//                this.discardParticleEffect(5);
//            }
        }
    }



    @Override
    public ResourceLocation getAbilityResource() {
        return null;
    }

}
