package org.jahdoo.ability.all_abilities.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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

public class PermafrostAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("permafrost");
    public static final String size = "Effect Radius";
    public static final String trapDura = "Trap Duration";

    @Override
    public void invokeAbility(Player player) {
        Vec3 location = player.pick(40, 0,false).getLocation();
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 0f, EntityPropertyRegister.ARCTIC_STORM.get().setAbilityId(), abilityId.getPath().intern());
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().playSound(null, BlockPos.containing(location), SoundRegister.ICE_ATTACH.get(), SoundSource.NEUTRAL, 1.2f, 0.6f);
        player.level().playSound(null, BlockPos.containing(location), SoundRegister.MAGIC_EXPLOSION.get(), SoundSource.NEUTRAL, 0.8f, 0.8f);
        player.level().addFreshEntity(aoeCloud);
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
            .setMana(50, 20,  5)
            .setCooldown(1800, 1200, 100)
//            .setDamage(10, 1, 1)
            .setEffectDuration(600, 200, 50)
            .setEffectStrength(10, 4,1)
            .setCastingDistance(30, 10, 5)
            .setLifetime(400, 100, 50)
            .setAoe(4, 1, 1)
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
        return ElementRegistry.FROST.get();
    }
}
