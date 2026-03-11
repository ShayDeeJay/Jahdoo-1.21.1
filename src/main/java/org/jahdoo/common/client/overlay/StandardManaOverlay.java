package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.networking.client2server.SelectAbilityC2SP;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.magic.Ability;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;

import static com.mojang.blaze3d.systems.RenderSystem.enableBlend;
import static com.mojang.blaze3d.systems.RenderSystem.setShaderColor;
import static java.lang.String.valueOf;
import static net.minecraft.network.chat.Component.literal;
import static org.jahdoo.common.client.SharedUI.centeredStringNoShadow;
import static org.jahdoo.common.client.overlay.CustomHudOverlay.sharedHUDNumber;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.trial_nexus.attachments.CasterData.selectedAbility;
import static org.jahdoo.trial_nexus.utils.Configuration.CUSTOM_UI;
import static org.jahdoo.trial_nexus.utils.Icons.MANA_CONTAINER;
import static org.jahdoo.trial_nexus.utils.Icons.MANA_LEVEL_BAR;

public class StandardManaOverlay extends AbstractTimedOverlay {


    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if(CUSTOM_UI.get()) return;

        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if(player == null || minecraft.options.hideGui) return;

        super.render(graphics, deltaTracker);

        var pose = graphics.pose();
        var height = graphics.guiHeight();

        pose.pushPose();
        pose.translate(2, height - 42, 0.0D);

        var casterData = player.getData(CASTER_DATA);
        var manaPool = casterData.getManaPool();
        var maxMana = casterData.getMaxMana(player);
        var manaBarWidth = 47;
        var manaProgress = maxMana != 0 && manaPool != 0 ? (int) (manaPool * manaBarWidth / maxMana) : 0;

        var typeId = selectedAbility(player);
        if(typeId == null) PacketDistributor.sendToServer(new SelectAbilityC2SP(""));

        graphics.blit(MANA_CONTAINER, 0, 10, 0, 47 , 82, 29);
        graphics.blit(MANA_LEVEL_BAR, 24, 21, 0, 43, manaProgress + 3, 8);
        manaPoolCount(casterData.getManaPool(), graphics, minecraft, 50 , 23, ColourHelpers.getAetherBlue());

        var abilityRegistrars = AbilityReg.getFirstSpellByTypeId(typeId);
        abilityRegistrars.ifPresent(
            location -> {
                this.cooldownOverlay(graphics, location, casterData);
                this.cooldownTimer(location, casterData, graphics, minecraft);
            }
        );

        pose.popPose();
    }

    private void cooldownOverlay(GuiGraphics graphics, Ability ability, CasterData casterData){
        if(ability == null) return;
        graphics.blit(ability.getAbilityIconLocation(), 3, 13, 0, 0, 23, 23, 23, 23);
        if (casterData.isAbilityOnCooldown(ability.setAbilityId())) {
            var cooldownCost = casterData.getStaticCooldown(ability.setAbilityId());
            var cooldownStatus = casterData.getCooldown(ability.setAbilityId());
            var cooldownOverlaySize = 19;
            if(cooldownCost > 0){
                var currentOverlayHeight = (cooldownStatus * cooldownOverlaySize) / cooldownCost;
                enableBlend();
                setShaderColor(1f, 1f, 1f, 0.9F);
                graphics.blit(MANA_CONTAINER, 5, 34 - currentOverlayHeight, 0, 97, cooldownOverlaySize, currentOverlayHeight);
                setShaderColor(1f, 1f, 1f, 1f);
            }
        }
    }

    private void cooldownTimer(Ability ability, CasterData casterData, GuiGraphics graphics, Minecraft minecraft){
        if (ability == null) return;

        if (casterData.isAbilityOnCooldown(ability.setAbilityId())) {
            var cooldownStatus = casterData.getCooldown(ability.setAbilityId());

            graphics.pose().pushPose();
            var v = 0.5F;
            graphics.pose().scale(v, v, v);
            centeredStringNoShadow(graphics, minecraft.font, literal(MathHelpers.ticksToTime(valueOf(cooldownStatus))), 30, 58, -1, false);
            graphics.pose().popPose();
        }
    }


    private void manaPoolCount(double data, GuiGraphics graphics, Minecraft mc, double x, double y, int colour){
        var pose = graphics.pose();

        pose.pushPose();
        pose.translate(x, y, 10D);
        pose.translate(0, -0.9, 10);

        sharedHUDNumber(graphics, (float) data);
        pose.popPose();
    }
}
