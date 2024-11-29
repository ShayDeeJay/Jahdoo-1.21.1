package org.jahdoo.ability.all_abilities.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

public class ArmageddonAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("armageddon");
    public static final String SPAWNING_SPEED = "Spawning Speed";

    @Override
    public void invokeAbility(Player player) {
        Vec3 location = player.pick(40, 0,false).getLocation();
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 3f, EntityPropertyRegister.ARMAGEDDON.get().setAbilityId(), abilityId.getPath().intern());
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().playSound(null, BlockPos.containing(location), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1.4f, 0.3f);
        player.level().playSound(null, BlockPos.containing(location), SoundRegister.ORB_FIRE.get(), SoundSource.NEUTRAL, 0.8f, 0.6f);
        player.level().addFreshEntity(aoeCloud);
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.ETERNAL;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(180, 80, 10)
            .setCooldown(6000, 2400, 600)
            .setDamage(40, 20, 5)
            .setCastingDistance(30, 10, 5)
            .setLifetime(400, 200, 40)
            .setAoe(6,1,1)
            .setAbilityTagModifiersRandom(SPAWNING_SPEED, 30,5, false, 5)
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
        return ElementRegistry.INFERNO.get();
    }
}
