package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.jahdoo.common.registers.mod.QuestReg;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.magic.skills.AbstractSkill;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;

import java.util.Objects;

import static com.mojang.blaze3d.systems.RenderSystem.*;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.trial_nexus.utils.Icons.GUI_BUTTON_SKILL;

public class SkillsOverlay extends AbstractTimedOverlay {

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        var spread = 5;
        var scale = 24;
        var spacer = 0;

        var player = ClientHelpers.getMinecraft().player;
        if(player == null) return;

        var casterData = player.getData(CASTER_DATA);
        var skills = casterData.getActiveSkills();

        spacer = spaceForQuest(player);

        enableBlend();
        setShaderColor(1f, 1f, 1f, 1F);

        for (var activeSkill : skills) {
            var getSkill = SkillReg.getAllSkills().stream().filter(abstractSkill -> Objects.equals(abstractSkill.id(), activeSkill)).findFirst();
            if(getSkill.isEmpty()) return;

            var iconSize = scale - 6;
            var totalSkills = skills.size();

            var xA = (graphics.guiWidth() / 2) - (totalSkills * (scale / 2)) + spread - 3;
            var yA = 10 - spacer;

            var deScale = 6;
            var fitIcon = iconSize - deScale;
            var centerIcon = deScale / 2;

            iconWithImage(graphics, xA, yA, iconSize, getSkill.get(), centerIcon, fitIcon);
            spread += scale;
        }

        setShaderColor(1f, 1f, 1f, 1F);
        disableBlend();
        super.render(graphics, deltaTracker);
    }

    private static int spaceForQuest(LocalPlayer player) {
        if(LevelGenerator.isNexus(player.level())){
            var runData = RunData.getRunData(player);
            var getQuest = QuestReg.getQuestByName(runData.getCurrentQuestId());
            if(getQuest.isPresent()) return 24;
        }

        return 0;
    }

    private static void iconWithImage(GuiGraphics graphics, int xA, int yA, int iconSize, AbstractSkill get, int centerIcon, int fitIcon) {
        graphics.blit(GUI_BUTTON_SKILL, xA, yA, 0, 0, iconSize, iconSize, iconSize, iconSize);
        graphics.blit(get.icon(), xA + centerIcon, yA + centerIcon, 0, 0, fitIcon, fitIcon, fitIcon, fitIcon);
    }

}
