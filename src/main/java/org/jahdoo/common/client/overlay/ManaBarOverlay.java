package org.jahdoo.common.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.attachments.CastingData;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.NotNull;

import static com.mojang.blaze3d.systems.RenderSystem.enableBlend;
import static com.mojang.blaze3d.systems.RenderSystem.setShaderColor;
import static java.lang.String.valueOf;
import static net.minecraft.network.chat.Component.literal;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Configuration.*;
import static org.jahdoo.ascension.utils.Helpers.getUsedItem;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Maths.ticksToTime;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.centeredStringNoShadow;
import static org.jahdoo.common.client.SharedUI.drawStringWithBackground;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;

public class ManaBarOverlay implements LayeredDraw.Layer {

    float fadeIn;
    float fadeInExperience;
    float storedExp;
    float fadeXpTimer;
    float fadeInFood;
    float fadeFoodTimer;
    AlignedGui alignedGui;

    private static void inventoryIndex(@NotNull GuiGraphics graphics, int x, int y, int index, int textColour) {
        graphics.pose().pushPose();
        graphics.pose().translate(8.2,4.2,5d);
        centeredStringNoShadow(graphics, Minecraft.getInstance().font,  literal(valueOf(index)), x, y, textColour, false);
        graphics.pose().popPose();
    }

    public void progressOverlays(AlignedGui alignedGui, int startY, int manaProgress){
        alignedGui.displayGuiLayer(-manaProgress + 3, 18, 0, startY, manaProgress, 8, MANA_LEVEL_BAR);
    }

    private void healthAndAbsorptionCount(GuiGraphics graphics, Minecraft mc){
        var height = graphics.guiHeight();
        var player = mc.player;
        if(player == null) return;

        var playerHealth = player.getHealth();
        var absorption = player.getAbsorptionAmount();
        var manaPoolCount = withStyleComponent(valueOf(Math.round(playerHealth)), MAGNET_STRENGTH_RED).copy();

        if(absorption > 0){
            manaPoolCount.append(withStyleComponent(" + ", SUB_HEADER_COLOUR));
            manaPoolCount.append(withStyleComponent("" + Math.round(absorption), ABSORPTION_TEXT_YELLOW));
        }

        var colourBack = -13816531;
        var pose = graphics.pose();

        pose.pushPose();
        pose.translate(57 + this.alignedGui.shiftGuiX + (-77), height - 16.2 - this.alignedGui.shiftGuiY, 10D);

        if(player.hasEffect(MobEffects.REGENERATION)){
            pose.pushPose();
            var z = 0.7f;
            pose.scale(z, z, z);
            pose.translate(-8.4, -5.5, 0);
            setShaderColor(1f, 1f, 1f, 0.6F);
            graphics.renderItem(new ItemStack(ItemReg.HEALTH_CONTAINER), 0, 0);
            setShaderColor(1f, 1f, 1f, 1f);
            pose.popPose();
        }

        pose.scale(0.5f,0.5f,0.5f);
        pose.translate(0, 0, 1000);
        centeredStringNoShadow(graphics, mc.font, manaPoolCount, 0, 0, colourBack, false);
        pose.popPose();
    }

    private void manaPoolCount(double data, GuiGraphics graphics, Minecraft mc, double x, double y, int colour){
        var height = graphics.guiHeight();
        var manaPoolCount = withStyleComponent(valueOf(Math.round(data)), colour);
        var colourBack = -13816531;
        var pose = graphics.pose();

        pose.pushPose();
        pose.translate(57 + this.alignedGui.shiftGuiX + x, height - 16.2 - this.alignedGui.shiftGuiY + y, 10D);
        pose.scale(0.5f,0.5f,0.5f);
        pose.translate(0, 0, 1000);
        centeredStringNoShadow(graphics, mc.font, manaPoolCount, 0, 0, colourBack, false);
        pose.popPose();
    }

    private void alignedGuiInstance(GuiGraphics graphics){
        var width = graphics.guiWidth();
        var height = graphics.guiHeight();

        if (alignedGui == null || alignedGui.getScreenWidth() != width || alignedGui.getScreenWidth() != height) {
            var alignedGui1 = new AlignedGui(graphics, height, width);

            if(CUSTOM_UI.get()) alignedGui1.offsetGui(width / 2 - 16, 10);
            this.alignedGui = alignedGui1;
        }
    }

    private void setFadeInFood(Player player){
        var food = player.getFoodData();
        var alwaysShow = CUSTOM_UI_ALWAYS_SHOW_HUNGER.get();

        if (food.needsFood() || alwaysShow) {
            if (this.fadeInFood < 1) this.fadeInFood += 0.05F;
        } else {
            if (this.fadeInFood > -14) this.fadeInFood -= 0.1F;
        }

        this.fadeFoodTimer = Math.max(this.fadeFoodTimer - 0.5F, 0);
    }

    private void setFadeInExperience(Player player){
        var xp = player.totalExperience;
        var alwaysShow = CUSTOM_UI_ALWAYS_SHOW_XP.get();

        if(this.storedExp != xp) this.fadeXpTimer = 200;

        if (this.fadeXpTimer > 0 || alwaysShow) {
            if (this.fadeInExperience < 1) this.fadeInExperience += 0.8F;
        } else {
            if (this.fadeInExperience > -6) this.fadeInExperience -= 0.5F;
        }

        this.fadeXpTimer = Math.max(this.fadeXpTimer - 0.5F, 0);
        this.storedExp = xp;

    }

    private void setFadeGui(Player player){
        var fadeAmount = 0.07f;
        var wandItem = getUsedItem(player).getItem();
        var alwaysShow = CUSTOM_UI_SHOW_MANA.get();

        if (wandItem instanceof WandItem || alwaysShow) {
            if (this.fadeIn < 1) this.fadeIn += fadeAmount;
        } else {
            if (this.fadeIn > 0) this.fadeIn -= fadeAmount;
        }
    }

    private void cooldownTimer(AbilityRegistrar ability, CastingData casterData, GuiGraphics graphics, Minecraft minecraft){
        if (ability == null) return;

        if (casterData.isAbilityOnCooldown(ability.setAbilityId())) {
            var cooldownStatus = casterData.getCooldown(ability.setAbilityId());

            graphics.pose().pushPose();
            var v = 0.5F;
            graphics.pose().scale(v, v, v);

            var getCorrectX = CUSTOM_UI.get() ? ((graphics.guiWidth() / 2) * 2) : 32;
            var getCorrectY = graphics.guiHeight() * 2 - (CUSTOM_UI.get() ? 40 : 20) ;

            graphics.pose().translate(getCorrectX, getCorrectY, 10D);
            centeredStringNoShadow(graphics, minecraft.font, literal(ticksToTime(valueOf(cooldownStatus))), 0, 0, -1, false);
            graphics.pose().popPose();
        }
    }

    private void cooldownOverlay(AbilityRegistrar ability, CastingData casterData){
        if(this.fadeIn < 0 && !CUSTOM_UI.get() || ability == null) return;
        alignedGui.displayGuiLayer(4, 26, 0, 0, 23, ability.getAbilityIconLocation());
        if (casterData.isAbilityOnCooldown(ability.setAbilityId())) {
            var cooldownCost = casterData.getStaticCooldown(ability.setAbilityId());
            var cooldownStatus = casterData.getCooldown(ability.setAbilityId());
            var cooldownOverlaySize = 19;
            if(cooldownCost > 0){
                var currentOverlayHeight = (cooldownStatus * cooldownOverlaySize) / cooldownCost;
                enableBlend();
                setShaderColor(1f, 1f, 1f, 0.9F);
                alignedGui.displayGuiLayer(6, 5 + currentOverlayHeight, 0, 97, cooldownOverlaySize, currentOverlayHeight);
                setShaderColor(1f, 1f, 1f, 1f);
            }
        }
    }

    private static void renderSlot(GuiGraphics graphics, ItemStack next, int x, int y, ResourceLocation lit, int index, int textColour, float alpha) {
        var count = next.getCount();
        graphics.renderFakeItem(next, x, y);
        int size = 24;
        inventoryIndex(graphics, x, y, index,textColour);
        enableBlend();
        setShaderColor(1f, 1f, 1f, alpha);
        graphics.blit(lit, x-4,y-4,0,0, size, size, size, size);
        setShaderColor(1f, 1f, 1f, 1f);
        if(count > 1){
            graphics.pose().pushPose();
            graphics.pose().translate(0,0,170);
            graphics.drawCenteredString(Minecraft.getInstance().font, valueOf(count), x + 15, y + 10, -6710887);
            graphics.pose().popPose();
        }
    }

    private  void inventory(GuiGraphics graphics, LocalPlayer player) {
        var selectedIndex = player.getInventory().selected;
        var current = player.getInventory().getItem(selectedIndex);
        var prevIndex = selectedIndex - 1 < 0 ? 8 : selectedIndex - 1;
        var previous = player.getInventory().getItem(prevIndex);
        var nextIndex = selectedIndex + 1 > 8 ? 0 : selectedIndex + 1;
        var next = player.getInventory().getItem(nextIndex);
        var y = graphics.guiHeight() - 68 + (int) (-this.fadeInExperience);
        var x = graphics.guiWidth() / 2 - 9;
        var unSelected = GUI_ITEM_SLOT;
        var alpha = 0.6f;
        var textColour = -7303024;

        renderSlot(graphics, next, x + 20, y, unSelected, nextIndex + 1, textColour, alpha);
        renderSlot(graphics, current, x , y, GUI_GENERAL_SLOT, selectedIndex + 1, -12698050, 1f);
        renderSlot(graphics, previous, x - 20, y, unSelected, prevIndex + 1, textColour, alpha);
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker tracker) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if(player == null || minecraft.options.hideGui) return;

        var manaBarWidth = 47;
        var abilityRegistrars = AbilityReg.REGISTRY.get(DataComponentHelper.getAbilityTypeWand(player));
        var casterData = player.getData(CASTER_DATA);
        var manaPool = casterData.getManaPool();
        var maxMana = casterData.getMaxMana(player);
        var manaProgress = maxMana != 0 && manaPool != 0 ? (int) (manaPool * manaBarWidth / maxMana) : 0;
        var healthProgress = (player.getHealth() * manaBarWidth / player.getMaxHealth());
        var absorptionProgress = (player.getAbsorptionAmount() * manaBarWidth / player.getMaxAbsorption());
        var foodProgress = (player.getFoodData().getFoodLevel() * 46 / 20);
        var pose = graphics.pose();

        this.alignedGuiInstance(graphics);
        this.setFadeGui(player);
        this.setFadeInExperience(player);
        this.setFadeInFood(player);

        pose.pushPose();
        enableBlend();

        var scale = CUSTOM_UI_SCALE.get().floatValue();
        var yOffset = CUSTOM_UI_HEIGHT.get().floatValue() + (CUSTOM_UI.get() ? 0 : 10) ;
        var center = this.alignedGui.screenWidth / 2;
        var centerX = center - (41);
        var centerY = this.alignedGui.screenHeight - 7;

        pose.translate(center, centerY + yOffset, 0);
        pose.scale(scale, scale, 1);
        pose.translate(-center, -centerY + yOffset, 0);

        if(CUSTOM_UI.get()){
            Minecraft.getInstance().gui.renderSelectedItemName(graphics, (int) (100 + this.fadeInExperience));
            inventory(graphics, player);
            foodBar(pose, foodProgress);
            xpBar(graphics, pose, centerX, centerY, minecraft, player, center);
            alignedGui.displayGuiLayer(-53, 29, 0, 0, 137, 29);

            this.healthAndAbsorptionCount(graphics, minecraft);
            this.progressOverlays(alignedGui, 19, (int) (healthProgress + 3));
            this.progressOverlays(alignedGui, 27, (int) (absorptionProgress + 3));
        }

        if(!CUSTOM_UI.get()){
            pose.translate(0, -fadeIn, 0);
            setShaderColor(1f, 1f, 1f, fadeIn);
            alignedGui.displayGuiLayer(1, 29, 0, 47, 82, 28);
        }

        alignedGui.displayGuiLayer(25, 18, 0, 43, manaProgress + 3, 8, MANA_LEVEL_BAR);
        this.manaPoolCount(casterData.getManaPool(), graphics, minecraft, -5 , 0, AETHER_BLUE);
        this.cooldownOverlay(abilityRegistrars, casterData);
        this.cooldownTimer(abilityRegistrars, casterData, graphics, minecraft);

        setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();
    }

    private void foodBar(PoseStack pose, int foodProgress) {
        var alwaysShow = CUSTOM_UI_ALWAYS_SHOW_HUNGER.get();

        pose.pushPose();
        pose.translate(0, fadeInFood - 2, 0);
        setShaderColor(1f, 1f, 1f, Math.max(0, fadeInFood));
        if (fadeInFood > 0 || alwaysShow) {
            alignedGui.displayGuiLayer(-10, 5, 0, 30, 51, 11);
            alignedGui.displayGuiLayer(-8, 0, 0, 42, foodProgress, 4);
        }
        setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();
    }

    private void xpBar(GuiGraphics graphics, PoseStack pose, int centerX, int centerY, Minecraft minecraft, LocalPlayer player, int center) {
        var alwaysShow = CUSTOM_UI_ALWAYS_SHOW_XP.get();
        var k = (int) (player.experienceProgress * 98.0F);

        pose.pushPose();
        pose.translate(0, -fadeInExperience + 6, 0);
        setShaderColor(1f, 1f, 1f, Math.max(0, fadeInExperience));
        var y = 32;
        alignedGui.displayGuiLayer(-38, y, 0, 77, 107, 12);
        alignedGui.displayGuiLayer(-36, y - 2, 0, 90, k, 6);

        if(fadeInExperience > 0 || alwaysShow) drawStringWithBackground(graphics, minecraft.font, literal(valueOf(player.experienceLevel)), center, centerY - 45, 0, FastColor.ARGB32.color(133, 189, 72), true);
        setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();
    }

    public static class AlignedGui {
        GuiGraphics guiGraphics;
        private int shiftGuiX;
        private int shiftGuiY;
        private final int screenWidth;
        private final int screenHeight;
        private int scale;

        public AlignedGui(GuiGraphics guiGraphics, int screenHeight, int screenWidth){
            this.guiGraphics = guiGraphics;
            this.screenHeight = screenHeight;
            this.screenWidth = screenWidth;
        }

        public void displayGuiLayer(int xA, int yA, int offsetU, int offsetY, int barSizeXb, int barSizeYb){
            int positionX = xA + shiftGuiX;
            int positionY = screenHeight - yA - shiftGuiY;
            guiGraphics.blit(MANA_CONTAINER, positionX, positionY, offsetU, offsetY , barSizeXb, barSizeYb);
        }

        public void displayGuiLayer(int xA, int yA, int offsetX, int offsetY, int iconSize, ResourceLocation resourceLocation){
            int positionX = xA + shiftGuiX;
            int positionY = screenHeight - yA - shiftGuiY;
            guiGraphics.blit(resourceLocation, positionX, positionY, offsetX, offsetY, iconSize, iconSize, iconSize, iconSize);
        }
        public void displayGuiLayer(int xA, int yA, int offsetX, int offsetY, int iconSizeX, int iconSizeY, ResourceLocation resourceLocation){
            int positionX = xA + shiftGuiX;
            int positionY = screenHeight - yA - shiftGuiY;
            guiGraphics.blit(resourceLocation, positionX, positionY, offsetX, offsetY, iconSizeX, iconSizeY);
        }

        public void offsetGui(int shiftGuiX, int shiftGuiY){
            this.shiftGuiX = shiftGuiX;
            this.shiftGuiY = shiftGuiY;
        }

        private void setScale(int scale){
            this.scale = scale;
        }

        public int getScreenWidth() {
            return screenWidth;
        }
    }

}
