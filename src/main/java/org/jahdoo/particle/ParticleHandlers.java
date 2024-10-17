package org.jahdoo.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;

import java.util.List;

import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;

public class ParticleHandlers {

    public static void spawnPoof(ServerLevel world, Vec3 pos, int particleCount, ParticleOptions particleOptions) {
        for (int i = 0; i < 10; i++) {
            double d0 = pos.x ;
            double d1 = pos.y;
            double d2 = pos.z;
            (world).sendParticles(particleOptions, d0, d1, d2, particleCount, (world.random.nextFloat() * 1 - 0.5) / 3, (world.random.nextFloat() * 1 - 0.5) / 3, (world.random.nextFloat() * 1 - 0.5) / 3, 0.1f);
        }
    }
    public static void spawnPoofWithSpeed(ServerLevel world, Vec3 pos, int particleCount, ParticleOptions particleOptions, double speed) {
        for (int i = 0; i < 1 ; i++) {
            double d0 = pos.x ;
            double d1 = pos.y;
            double d2 = pos.z;
            (world).sendParticles(particleOptions, d0, d1, d2, particleCount, (world.random.nextFloat() * 1 - 0.5) / 6, (world.random.nextFloat() * 1 - 0.5) / 6, (world.random.nextFloat() * 1 - 0.5) / 6, speed);
        }
    }

    public static void spawnPoof(ServerLevel world, Vec3 pos, int particleCount, ParticleOptions particleOptions, double x, double y, double z, float speed) {
        for (int i = 0; i < 10; i++) {
            double d0 = pos.x ;
            double d1 = pos.y;
            double d2 = pos.z;
            (world).sendParticles(particleOptions, d0, d1, d2, particleCount, x, y, z, speed);
        }
    }

    public static void spawnPoof(ServerLevel world, Vec3 pos, int particleCount, ParticleOptions particleOptions, double x, double y, double z, float speed, int totalPoofs) {
        for (int i = 0; i < totalPoofs; i++) {
            double d0 = pos.x ;
            double d1 = pos.y;
            double d2 = pos.z;
            (world).sendParticles(particleOptions, d0, d1, d2, particleCount, x , y, z, speed);
        }
    }

    public void pullParticlesToCenter(Player player){
        var casterData = player.getData(CASTER_DATA);
        var manaReduction = casterData.getMaxMana(player) / 60;
        BakedParticleOptions bakedParticleOptions = new BakedParticleOptions(
            ElementRegistry.VITALITY.get().getTypeId(),
            6, 2f, false
        );
        GenericParticleOptions genericParticleOptions = genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, ElementRegistry.VITALITY.get(), 6, 2f);

        List<ParticleOptions> particleOptionsList = List.of(
            bakedParticleOptions,
            genericParticleOptions
        );

        if(casterData.getManaPool() >= manaReduction){
            GeneralHelpers.getInnerRingOfRadiusRandom(
                player.position()
                    .add(0, player.getBbHeight() / 2, 0)
                    .offsetRandom(RandomSource.create(), 1.5f), 3, (double) player.getTicksUsingItem()/10,
                positions -> {
                    if (player.level() instanceof ServerLevel serverLevel) {
                        Vec3 directions = player.position().subtract(positions).normalize().add(0, player.getBbHeight() / 2, 0);
                        GeneralHelpers.generalHelpers.sendParticles(
                            serverLevel,
                            particleOptionsList.get(RandomSource.create().nextInt(0, 2)),
                            positions,
                            0,
                            directions.x,
                            GeneralHelpers.Random.nextDouble(-0.3, 0.3),
                            directions.z,
                            (double) player.getTicksUsingItem()/500
                        );
                    }
                }
            );
        }
    }

    public static void invisibleLight(Level world, Vec3 loc, ParticleOptions particleOptions, double bound1, double bound2) {
        for (int i = 0; i < 3; i++) {
            double d0 = loc.x;
            double d1 = loc.y;
            double d2 = loc.z;

            if (world.isClientSide) {
                world.addParticle(particleOptions, d0 , d1 , d2, 0, GeneralHelpers.Random.nextDouble(bound1, bound2),0);
            }
        }
    }

    public static void playParticles(
        ParticleOptions particleOptions,
        Projectile projectile,
        double getX,
        double getY,
        double getZ,
        int multiplier
    ) {
        double deltaX = getX - projectile.xOld;
        double deltaY = getY - projectile.yOld;
        double deltaZ = getZ - projectile.zOld;
        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * multiplier);
        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;

            if (projectile.level() instanceof ServerLevel serverLevel){
                Vec3 position = new Vec3((float) (projectile.xo + deltaX * coeff), (float) (projectile.yo + deltaY * coeff) + 0.1, (float) (projectile.zo + deltaZ * coeff));
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel, particleOptions, position, 1,
                    0.0125f * (GeneralHelpers.Random.nextFloat() - 0.5f),
                    0.0125f * (GeneralHelpers.Random.nextFloat() - 0.5f),
                    0.0125f * (GeneralHelpers.Random.nextFloat() - 0.5f),
                    0
                );
            }
        }
    }

    public static void playParticles2(
        ParticleOptions particleOptions,
        Projectile projectile,
        double getX,
        double getY,
        double getZ,
        int multiplier,
        double speed
    ) {
        double deltaX = getX - projectile.xOld;
        double deltaY = getY - projectile.yOld;
        double deltaZ = getZ - projectile.zOld;
        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * multiplier);
        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;

            if (projectile.level() instanceof ServerLevel serverLevel){
                Vec3 position = new Vec3((float) (projectile.xo + deltaX * coeff), (float) (projectile.yo + deltaY * coeff) + 0.1, (float) (projectile.zo + deltaZ * coeff));
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel, particleOptions, position, 1,
                    0.0125f * (GeneralHelpers.Random.nextFloat() - 0.5f),
                    0.0125f * (GeneralHelpers.Random.nextFloat() - 0.5f),
                    0.0125f * (GeneralHelpers.Random.nextFloat() - 0.5f),
                    speed
                );
            }
        }
    }

    public static void  playParticles3(
        ParticleOptions particleOptions,
        Projectile projectile,
        int multiplier,
        double speed
    ) {
        double deltaX = projectile.getX() - projectile.xOld;
        double deltaY = projectile.getY() - projectile.yOld;
        double deltaZ = projectile.getZ() - projectile.zOld;
        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * multiplier);
        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;

            if (projectile.level() instanceof ServerLevel serverLevel){
                Vec3 position = new Vec3((float) (projectile.xo + deltaX * coeff), (float) (projectile.yo + deltaY * coeff) + 0.1, (float) (projectile.zo + deltaZ * coeff));
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel, particleOptions, position, 1,
                    0.0125f * (GeneralHelpers.Random.nextFloat() - 0.5f),
                    0.0125f * (GeneralHelpers.Random.nextFloat() - 0.5f),
                    0.0125f * (GeneralHelpers.Random.nextFloat() - 0.5f),
                    speed
                );
            }
        }
    }

    public static void GenericProjectile(
        Projectile projectile,
        ParticleOptions particleMain,
        ParticleOptions particleTrail,
        double speed
    ){
        if(projectile.tickCount > 2){
            Vec3 velocity = projectile.getDeltaMovement();
            double directionX = projectile.getX() - projectile.xOld;
            double directionY = projectile.getY() - projectile.yOld;
            double directionZ = projectile.getZ() - projectile.zOld;
            double magnitude = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
            double normalizedX = directionX / magnitude;
            double normalizedY = directionY / magnitude;
            double normalizedZ = directionZ / magnitude;
            double offsetDistance = 0.8; // Adjust this value as needed
            double offsetX = projectile.getX() - normalizedX * offsetDistance;
            double offsetY = projectile.getY() - normalizedY * offsetDistance;
            double offsetZ = projectile.getZ() - normalizedZ * offsetDistance;

            playParticles(particleMain, projectile, projectile.getX(), projectile.getY(), projectile.getZ(), (int) velocity.length() + 2);
            playParticles2(particleTrail, projectile, offsetX, offsetY, offsetZ, (int) velocity.length() + 20, speed);
        }
    }

    public static void EntityProjectileParticles(
        Projectile projectile,
        int tickCount,
        float spread,
        AbstractElement element
    ){
        if(!(projectile.level() instanceof ServerLevel serverLevel)) return;
        if (tickCount > 1.5) {
            Vec3 velocity = projectile.getDeltaMovement();
            double offsetX = velocity.x * -2;  // Reverse the x direction to be behind the entity
            double offsetY = velocity.y * -2;
            double offsetZ = velocity.z * -2;  // Reverse the z direction to be behind the entity
            double particleX = projectile.getX() + offsetX;
            double particleY = projectile.getY() + projectile.getBbHeight() / 2 + offsetY;  // Adjust Y position as needed
            double particleZ = projectile.getZ() + offsetZ;
            double heightOffset = 0.05;

            for (int i = 0; i < 6; i++){
                Vec3 position = new Vec3(
                    particleX + GeneralHelpers.Random.nextFloat(-spread, spread),
                    particleY + GeneralHelpers.Random.nextFloat(-spread, spread),
                    particleZ + GeneralHelpers.Random.nextFloat(-spread, spread)
                );
                GenericParticleOptions genericSlow = genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, element, 4, 1.5f);
                BakedParticleOptions bakedSlow = new BakedParticleOptions(element.getTypeId(), 2,2.5f,false);
                GeneralHelpers.generalHelpers.sendParticles(serverLevel, bakedSlow, position.subtract(0,heightOffset,0), 0, 0, 0, 0,0);
                GeneralHelpers.generalHelpers.sendParticles(serverLevel, genericSlow, position.subtract(0,heightOffset,0), 0, 0, 0, 0,0);
            }
        }
    }

    public static void EntityProjectileParticlesClient(
        Projectile projectile,
        int tickCount,
        float spread,
        AbstractElement element
    ){
        if (tickCount > 1.5) {
            Vec3 velocity = projectile.getDeltaMovement();
            double offsetX = velocity.x * -2;  // Reverse the x direction to be behind the entity
            double offsetY = velocity.y * -2;
            double offsetZ = velocity.z * -2;  // Reverse the z direction to be behind the entity
            double particleX = projectile.getX() + offsetX;
            double particleY = projectile.getY() + projectile.getBbHeight() / 2 + offsetY;  // Adjust Y position as needed
            double particleZ = projectile.getZ() + offsetZ;
            double heightOffset = 0.05;

            for (int i = 0; i < 6; i++){
                Vec3 position = new Vec3(
                    particleX + GeneralHelpers.Random.nextFloat(-spread, spread),
                    particleY + GeneralHelpers.Random.nextFloat(-spread, spread),
                    particleZ + GeneralHelpers.Random.nextFloat(-spread, spread)
                );
                GenericParticleOptions genericSlow = genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, element, 4, 1.5f);
                BakedParticleOptions bakedSlow = new BakedParticleOptions(element.getTypeId(), 2,2.5f,false);

                projectile.level().addParticle(bakedSlow, position.x, position.y, position.z, 0,0,0);
                projectile.level().addParticle(genericSlow, position.x, position.y, position.z, 0,0,0);
            }
        }
    }


    public static GenericParticleOptions genericParticleOptions(int particleType, AbstractElement element, int lifetime, float size, double speed){
        return new GenericParticleOptions(particleType, element.particleColourPrimary(), element.particleColourFaded(), lifetime, size, false, speed);
    }

    public static GenericParticleOptions genericParticleOptions(AbstractElement element, int lifetime, float size){
        return new GenericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, element.particleColourPrimary(), element.particleColourFaded(), lifetime,size,false,1);
    }

    public static GenericParticleOptions genericParticleOptions(int type, AbstractElement element, int lifetime, float size){
        return new GenericParticleOptions(type, element.particleColourPrimary(), element.particleColourFaded(), lifetime,size,false, 1);
    }

    public static BakedParticleOptions bakedParticleOptions(int type, int lifetime, float size, boolean setStaticSize){
        return new BakedParticleOptions(type, lifetime, size,setStaticSize);
    }

    public static GenericParticleOptions genericParticleOptions(AbstractElement element, int lifetime, float size, boolean staticSize){
        return new GenericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, element.particleColourPrimary(), element.particleColourFaded(), lifetime,size,staticSize, 1);
    }

    public static GenericParticleOptions genericParticleOptions(int lifetime, float size, int colourPrimary, int colourSecondary){
        return new GenericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, colourPrimary, colourSecondary, lifetime,size,false, 1);
    }

    public static GenericParticleOptions genericParticleOptions(int particleType, AbstractElement element, int lifetime, float size, boolean staticSize){
        return new GenericParticleOptions(particleType, element.particleColourPrimary(), element.particleColourFaded(), lifetime,size,staticSize, 1);
    }

    public static GenericParticleOptions genericParticleOptions(int particleType, int lifetime, float size, int colourPrimary, int colourSecondary){
        return new GenericParticleOptions(particleType, colourPrimary, colourSecondary, lifetime,size,false, 1);
    }

    public static GenericParticleOptions genericParticleOptions(int particleType, int lifetime, float size, int colourPrimary, int colourSecondary, boolean setStaticSize){
        return new GenericParticleOptions(particleType, colourPrimary, colourSecondary, lifetime,size,setStaticSize, 1);
    }

    public static GenericParticleOptions genericParticleOptions(int particleType, AbstractElement element, boolean setStaticSize, int lifetime, float size){
        return new GenericParticleOptions(particleType, element.particleColourPrimary(), element.particleColourFaded(), lifetime,size, setStaticSize, 1);
    }

    public static void spawnElectrifiedParticles(ServerLevel level, Vec3 position, ParticleOptions particleType, int count, LivingEntity livingEntity, double speed) {

        for (int i = 0; i < count; i++) {
            double offsetX = (GeneralHelpers.Random.nextDouble() - 0.5) * livingEntity.getBbWidth();
            double offsetY = GeneralHelpers.Random.nextDouble() * livingEntity.getBbHeight();
            double offsetZ = (GeneralHelpers.Random.nextDouble() - 0.5) * livingEntity.getBbWidth();

            // Give the particles an electrified jittery motion
            double speedX = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.1;
            double speedY = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.1;
            double speedZ = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.1;

            GeneralHelpers.generalHelpers.sendParticles(
                level, particleType, position.add(offsetX, offsetY, offsetZ), 1,speedX, speedY, speedZ,speed
            );

        }
    }

    public static void spawnElectrifiedParticles(ServerLevel level, Vec3 position, ParticleOptions particleType, int count, LivingEntity livingEntity, double speed, double ySpeed) {

        for (int i = 0; i < count; i++) {
            double offsetX = (GeneralHelpers.Random.nextDouble() - 0.5) * livingEntity.getBbWidth();
            double offsetY = GeneralHelpers.Random.nextDouble() * livingEntity.getBbHeight();
            double offsetZ = (GeneralHelpers.Random.nextDouble() - 0.5) * livingEntity.getBbWidth();

            // Give the particles an electrified jittery motion
            double speedX = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.1;
            double speedY = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.1;
            double speedZ = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.1;
            GeneralHelpers.generalHelpers.sendParticles(
                level, particleType, position.add(offsetX, offsetY, offsetZ), 1,speedX, speedY + ySpeed, speedZ,speed
            );
        }
    }

}
