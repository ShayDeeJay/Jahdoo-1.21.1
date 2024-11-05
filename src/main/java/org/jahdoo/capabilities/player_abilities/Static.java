package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jahdoo.capabilities.AbstractAttachment;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.all_abilities.abilities.StaticAbility;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.items.wand.CastHelper;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.*;
import org.jahdoo.all_magic.effects.CustomMobEffect;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.utils.ModHelpers;

import java.util.Map;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.spawnElectrifiedParticles;
import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;
import static org.jahdoo.registers.AttachmentRegister.STATIC;
import static org.jahdoo.all_magic.AbilityBuilder.*;

public class Static implements AbstractAttachment {

    private boolean isActive;
    double damageA;
    double manaPerHitA;
    double rangeA;
    double effectDurationA;
    double effectStrengthA;
    double effectChanceA;
    double manaCost;
    double cooldownCost;

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putBoolean("lastJumped", this.isActive);
        nbt.putDouble(DAMAGE, this.damageA);
        nbt.putDouble(StaticAbility.mana_per_damage, this.manaPerHitA);
        nbt.putDouble(RANGE, this.rangeA);
        nbt.putDouble(EFFECT_DURATION, this.effectDurationA);
        nbt.putDouble(EFFECT_STRENGTH, this.effectStrengthA);
        nbt.putDouble(EFFECT_CHANCE, this.effectChanceA);
        nbt.putDouble(MANA_COST, this.manaCost);
        nbt.putDouble(COOLDOWN, this.cooldownCost);

    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.isActive = nbt.getBoolean("lastJumped");
        this.damageA = nbt.getDouble(DAMAGE);
        this.manaPerHitA = nbt.getDouble(StaticAbility.mana_per_damage);
        this.rangeA = nbt.getDouble(RANGE);
        this.effectDurationA = nbt.getDouble(EFFECT_DURATION);
        this.effectStrengthA = nbt.getDouble(EFFECT_STRENGTH);
        this.effectChanceA = nbt.getDouble(EFFECT_CHANCE);
        this.manaCost = nbt.getDouble(MANA_COST);
        this.cooldownCost = nbt.getDouble(COOLDOWN);
    }

    public void activate(Player player){
        Map<String, AbilityHolder.AbilityModifiers> wandAbilityHolder = DataComponentHelper.getSpecificValue(player);
        var damage = wandAbilityHolder.get(DAMAGE).actualValue();
        this.damageA = ModHelpers.attributeModifierCalculator(
            player,
            (float) damage,
            this.getType(),
            AttributesRegister.MAGIC_DAMAGE_MULTIPLIER,
            true
        );
        this.manaPerHitA = wandAbilityHolder.get(StaticAbility.mana_per_damage).actualValue();
        this.rangeA = wandAbilityHolder.get(RANGE).actualValue();
        this.effectDurationA = wandAbilityHolder.get(EFFECT_DURATION).actualValue();
        this.effectStrengthA = wandAbilityHolder.get(EFFECT_STRENGTH).actualValue();
        this.effectChanceA = wandAbilityHolder.get(EFFECT_CHANCE).actualValue();
        this.manaCost = wandAbilityHolder.get(MANA_COST).actualValue();
        this.cooldownCost = wandAbilityHolder.get(COOLDOWN).actualValue();
        CastHelper.chargeMana(StaticAbility.abilityId.getPath().intern(), manaCost, player);
        this.isActive = true;
    }

    public void deactivate(Player player){
        CastHelper.chargeCooldown(StaticAbility.abilityId.getPath().intern(), (int) this.cooldownCost, player);
        this.isActive = false;
    }

    public boolean getIsActive(){
        return this.isActive;
    }


    public static void staticTickEvent(Player player){
        player.getData(STATIC).onTickMethod(player);
    }

    public void onTickMethod(Player player) {
        if(player == null) return;
        var manaSystem = player.getData(CASTER_DATA);
        if(!this.getIsActive()) return;
        if(!(player.getMainHandItem().getItem() instanceof WandItem)) return;

        String staticId = StaticAbility.abilityId.getPath().intern();
        if (player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()).abilityProperties().containsKey(staticId)) {
            if(manaSystem.getManaPool() >= manaPerHitA){
                if (!(player.level() instanceof ServerLevel serverLevel)) return;
                int getRandomChance = ModHelpers.Random.nextInt(0, effectChanceA == 0 ? 20 : Math.max((int) effectChanceA, 10));
                if(getRandomChance == 0) setEffectParticle(player, serverLevel);
                this.damageAttackingEntity(player, serverLevel, getRandomChance);
            } else {
                this.deactivate(player);
            }
        }
    }

    public static void setEffectParticle(
        LivingEntity targetEntity,
        Level level
    ){
        if(targetEntity.isAlive()){
            ModHelpers.getSoundWithPosition(targetEntity.level(), targetEntity.blockPosition(), SoundRegister.BOLT.get(), 0.05f, 1.5f);

            GenericParticleOptions particleOptions = genericParticleOptions(
                ParticleStore.ELECTRIC_PARTICLE_SELECTION,
                ElementRegistry.LIGHTNING.get(),
                ModHelpers.Random.nextInt(5, 8),
                1.5f,
                0.8
            );

            int particleCount = targetEntity instanceof Player ? 5 : 30;
            spawnElectrifiedParticles(level, targetEntity.position(), particleOptions, particleCount, targetEntity, 0);
        }
    }

    private void damageAttackingEntity(Player player, ServerLevel serverLevel, int getRandomChance){
        var manaSystem = player.getData(CASTER_DATA);
        player.level().getNearbyEntities(
            LivingEntity.class, TargetingConditions.DEFAULT, player,
            player.getBoundingBox().inflate(rangeA)
        ).forEach(
            entities -> {
                if (entities == player || entities instanceof EternalWizard) return;
                if (player.getLastHurtByMob() == entities && player.hurtTime > 8) {

                    ModHelpers.getSoundWithPosition(
                        entities.level(),
                        entities.blockPosition(),
                        SoundRegister.BOLT.get(),
                        1f
                    );

                    setEffectParticle(entities, serverLevel);
                    manaSystem.subtractMana(manaPerHitA, player);

                    entities.hurt(
                        player.damageSources().magic(),
                        (float) this.damageA
                    );

                    if (getRandomChance == 0) {
                        entities.addEffect(
                            new CustomMobEffect(
                                EffectsRegister.LIGHTNING_EFFECT.getDelegate(),
                                (int) effectDurationA,
                                (int) effectStrengthA
                            )
                        );
                    }
                    player.setLastHurtByMob(null);
                }
            }
        );
    }

    AbstractElement getType(){
        return ElementRegistry.LIGHTNING.get();
    }
}
