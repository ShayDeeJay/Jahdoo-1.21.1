package org.jahdoo.trial_nexus.magic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;

import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleHandlers.playParticles3;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;

public abstract class AbstractUtilityProjectile extends DefaultEntityBehaviour {

    @Override
    public AbstractElement getElementType() {
        return ElementReg.utility();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        discardParticleEffect(5);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return null;
    }

    public Level getLevel(){
        return this.generic.level();
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.generic.getAbilityHolder();
    }

    protected void discardParticleEffect(int lifetime) {
        var particle = genericParticle(ParticleStore.GENERIC_PARTICLE, ElementReg.utility(), lifetime, 1.5f, 0.1f);
        ParticleHandlers.particleBurst(this.generic.level(), this.generic.position().add(0,0.1,0), 1, particle,0,0,0,0.06f);
    }

    @Override
    public void onTickMethod() {
        if(this.generic != null){
            animateParticles(this.generic, getElementType());
        }
    }

    protected void utilityParticleBurst(Level level, Vec3 pPos, int lifeTime, float size, int count, float speed) {
        int col1 = this.getElementType().partColourA();
        int col2 = this.getElementType().partColourFade();
        var genericParticle = genericParticle(SOFT_PARTICLE, lifeTime, size, col1, col2, false);
        ParticleHandlers.particleBurst(level, pPos, count, genericParticle, speed);
    }

    public static void animateParticles(Projectile projectile, AbstractElement element) {
        if(projectile.tickCount > 1){
            playParticles3(
                genericParticle(SOFT_PARTICLE, element, 2, 0.9f, false),
                projectile, 8, 0.01
            );
        }
    }

    @Override
    public void discardCondition() {
        if(this.generic != null){
            if (this.generic.tickCount > 300) {
                this.generic.discard();
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

}
