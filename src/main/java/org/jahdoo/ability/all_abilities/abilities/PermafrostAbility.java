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
        var aoeCloud = new AoeCloud(player.level(), player, 0f, EntityPropertyRegister.ARCTIC_STORM.get().setAbilityId(), abilityId.getPath().intern());
        var position = player.position();
        aoeCloud.setPos(position.x, position.y, position.z);
        ModHelpers.getSoundWithPositionV(player.level(), aoeCloud.position(), SoundRegister.ICE_ATTACH.get(), 1.2f, 0.6f);
        ModHelpers.getSoundWithPositionV(player.level(), aoeCloud.position(), SoundRegister.MAGIC_EXPLOSION.get(), 0.4f, 0.8f);
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
            .setEffectDuration(300, 100, 50)
            .setEffectStrength(10, 5,1)
            .setLifetime(300, 100, 50)
            .setAoe(4, 2, 0.5)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return AREA_CAST;
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
