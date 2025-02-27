package org.jahdoo.ascension.ability.abilities.permafrost;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.EntityPropertyRegister;
import org.jahdoo.common.registers.SoundRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.ability.AbilityBuilder;

public class PermafrostAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = Helpers.res("permafrost");
    public static final String size = "Effect Radius";
    public static final String trapDura = "Trap Duration";

    @Override
    public void invokeAbility(Player player) {
        var aoeCloud = new AoeCloud(player.level(), player, 0f, EntityPropertyRegister.ARCTIC_STORM.get().setAbilityId(), abilityId.getPath().intern());
        var position = player.position();
        aoeCloud.setPos(position.x, position.y, position.z);
        Helpers.getSoundWithPositionV(player.level(), aoeCloud.position(), SoundRegister.ICE_ATTACH.get(), 1.2f, 0.6f);
        Helpers.getSoundWithPositionV(player.level(), aoeCloud.position(), SoundRegister.MAGIC_EXPLOSION.get(), 0.4f, 0.8f);
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
            .setStaticMana(60)
            .setStaticCooldown(1200)
            .setEffectDuration(300, 100, 50)
            .setEffectStrength(10, 5,1)
            .setLifetime(200, 100, 20)
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
        return ElementRegistry.frost();
    }
}
