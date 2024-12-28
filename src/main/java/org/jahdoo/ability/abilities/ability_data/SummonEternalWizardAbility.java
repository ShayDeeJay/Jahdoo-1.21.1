package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

public class SummonEternalWizardAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("eternal_wizard");

    @Override
    public void invokeAbility(Player player) {
        Vec3 location = player.pick(40, 0,false).getLocation();
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 0f, EntityPropertyRegister.SUMMON_ETERNAL_WIZARD.get().setAbilityId(), abilityId.getPath().intern());
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().addFreshEntity(aoeCloud);
        player.level().playSound(null, BlockPos.containing(location), SoundEvents.WITHER_AMBIENT, SoundSource.BLOCKS, 2f, 0.8f);
        player.level().playSound(null, BlockPos.containing(location), SoundRegister.EXPLOSION.get(), SoundSource.BLOCKS, 2f, 1.2f);

    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(100)
            .setStaticCooldown(6000)
//            .setMana(150, 50,  10)
//            .setCooldown(6000, 3600, 400)
            .setDamage(40, 15, 5)
            .setEffectStrength(10, 0, 1)
            .setEffectDuration(600, 200, 50)
            .setEffectChance(60, 20, 10)
            .setCastingDistance(30, 10, 5)
            .setLifetime(12000, 2400, 1200)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return DISTANCE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.VITALITY.get();
    }

}
