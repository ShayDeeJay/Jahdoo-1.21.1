package org.jahdoo.common.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.util.TriConsumer;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.items.augments.AugmentItemHelper;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;
import static org.jahdoo.ascension.ability.AbilityBuilder.SET_ELEMENT_TYPE;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.client.Icons.*;

public class SharedUI {

    public static final int BOX_COLOUR = -804253680;
    public static final int BORDER_COLOUR =  -12434878;

    public static List<ResourceLocation> getOverlays(){
        return List.of(Icons.AUGMENT_CORE, Icons.ADVANCED_AUGMENT_CORE, Icons.AUGMENT_HYPER_CORE);
    }

    public static List<Component> getComponents(ItemStack item, Level level){
        var components = new ArrayList<Component>();
        AugmentItemHelper.getHoverText(item, components, true, level);
        return components;
    }

    public static int getFadedColourBackground(float alpha){
        var minecraft = Minecraft.getInstance();
        return minecraft.options.getBackgroundColor(alpha);
    }

    public static void centeredStringNoShadow(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int color, boolean shadow) {
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        guiGraphics.drawString(font, formattedcharsequence, x - font.width(formattedcharsequence) / 2, y, color, shadow);
    }

    public static void boxMaker(GuiGraphics guiGraphics, int startX, int startY, int widthOffset, int heightOffset) {
        int widthTo = startX + widthOffset * 2;
        int heightTo = startY + heightOffset * 2;

        guiGraphics.fill(startX, startY, widthTo, heightTo, BOX_COLOUR);
        guiGraphics.renderOutline(startX, startY, widthTo - startX, heightTo - startY, BORDER_COLOUR);
    }

    public static void boxMaker(GuiGraphics guiGraphics, int startX, int startY, int widthOffset, int heightOffset, int colourBorder, int fillColour) {
        int widthTo = startX + widthOffset * 2;
        int heightTo = startY + heightOffset * 2;

        guiGraphics.fill(startX, startY, widthTo, heightTo, fillColour);
        guiGraphics.renderOutline(startX, startY, widthTo - startX, heightTo - startY, colourBorder);
    }

    public static void boxMaker(GuiGraphics guiGraphics, int startX, int startY, int widthOffset, int heightOffset, int colourBorder, int start, int end) {
        int widthTo = startX + widthOffset * 2;
        int heightTo = startY + heightOffset * 2;

        guiGraphics.fillGradient(startX, startY, widthTo, heightTo, start, end);
        guiGraphics.renderOutline(startX, startY, widthTo - startX, heightTo - startY, colourBorder);
    }

    public static void header(@NotNull GuiGraphics guiGraphics, int width, int height, ItemStack itemStack, Font font, Level level) {
        var yOff = 102;
        int xOff = width/2 - 55;
        guiGraphics.drawString(font, getComponents(itemStack, level).getFirst(), xOff, (height/2 - (yOff - 10)), 0, true);
        guiGraphics.drawString(font, AugmentItemHelper.getHoverName(itemStack), xOff, (height/2 - yOff), 0, true);
        abilityIcon(guiGraphics, itemStack, width - 155, height - 180, 109, 40, 12);
    }

    public static void renderEntityInInventoryFollowsMouse(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int scale, float yOffset, float mouseX, float mouseY, LivingEntity entity, float rotationSmoothing) {
        var f = (float)(x1 + x2) / 2.0F;
        var f1 = (float)(y1 + y2) / 2.0F;
        var f2 = (float)Math.atan((f - mouseX) / rotationSmoothing);
        var f3 = (float)Math.atan((f1 - mouseY) / rotationSmoothing);
        renderEntityInInventoryFollowsAngle(guiGraphics, x1, y1, x2, y2, scale, yOffset, f2, f3, entity);
    }

    public static List<Item> getCore(){
        return List.of(
            ItemReg.AUGMENT_CORE.get(),
            ItemReg.ADVANCED_AUGMENT_CORE.get(),
            ItemReg.AUGMENT_HYPER_CORE.get()
        );
    }

    public static void renderInventoryBackground(GuiGraphics guiGraphics, Screen screen, int IMAGE_SIZE, int yOffset, boolean show){
        if(show){
            var x = (screen.width - IMAGE_SIZE) / 2;
            var y = (screen.height - IMAGE_SIZE) / 2;
            guiGraphics.blit(
                WAND_GUI,
                x, y + yOffset - 44,
                0, 0, IMAGE_SIZE, IMAGE_SIZE
            );
        }
    }

    public static void renderFoodLevel(GuiGraphics graphics, Minecraft mc) {
        var player = mc.player;
        if (player != null) {
            mc.getProfiler().push("food");
            int i1 = graphics.guiWidth() / 2 + 91;
            int j1 = graphics.guiHeight() - 10;
            renderFood(graphics, player, j1, i1);
            mc.getProfiler().pop();
        }
    }

    private static final ResourceLocation FOOD_EMPTY_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_empty_hunger");
    private static final ResourceLocation FOOD_HALF_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_half_hunger");
    private static final ResourceLocation FOOD_FULL_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_full_hunger");
    private static final ResourceLocation FOOD_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_empty");
    private static final ResourceLocation FOOD_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_half");
    private static final ResourceLocation FOOD_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_full");
    private static void renderFood(GuiGraphics guiGraphics, Player player, int y, int x) {
        FoodData fooddata = player.getFoodData();
        int i = fooddata.getFoodLevel();
        RenderSystem.enableBlend();

        for(int j = 0; j < 10; ++j) {
            int k = y;
            ResourceLocation resourcelocation;
            ResourceLocation resourcelocation1;
            ResourceLocation resourcelocation2;
            if (player.hasEffect(MobEffects.HUNGER)) {
                resourcelocation = FOOD_EMPTY_HUNGER_SPRITE;
                resourcelocation1 = FOOD_HALF_HUNGER_SPRITE;
                resourcelocation2 = FOOD_FULL_HUNGER_SPRITE;
            } else {
                resourcelocation = FOOD_EMPTY_SPRITE;
                resourcelocation1 = FOOD_HALF_SPRITE;
                resourcelocation2 = FOOD_FULL_SPRITE;
            }

            if (player.getFoodData().getSaturationLevel() <= 0.0F && player.tickCount % (i * 3 + 1) == 0) {
                k = y + (Random.nextInt(3) - 1);
            }

            int l = x - j * 8 - 9;
            guiGraphics.blitSprite(resourcelocation, l, k, 9, 9);
            if (j * 2 + 1 < i) {
                guiGraphics.blitSprite(resourcelocation2, l, k, 9, 9);
            }

            if (j * 2 + 1 == i) {
                guiGraphics.blitSprite(resourcelocation1, l, k, 9, 9);
            }
        }

        RenderSystem.disableBlend();
    }

    public static void renderXPBar(GuiGraphics guiGraphics, int x, int l, Minecraft minecraft) {
        var expBackground = ResourceLocation.withDefaultNamespace("hud/experience_bar_background");
        var expProgress = ResourceLocation.withDefaultNamespace("hud/experience_bar_progress");
        if(minecraft == null || minecraft.player == null) return;
        minecraft.getProfiler().push("expBar");
        int i = minecraft.player.getXpNeededForNextLevel();
        if (i > 0) {
            int k = (int)(minecraft.player.experienceProgress * 183.0F);
            guiGraphics.blitSprite(expBackground, x, l, 182, 5);
            if (k > 0) guiGraphics.blitSprite(expProgress, 182, 5, 0, 0, x, l, k, 5);
        }
        minecraft.getProfiler().pop();
    }

    public static void renderMiniXPBar(GuiGraphics guiGraphics, int x, int l, Minecraft minecraft) {
        var expBackground = Helpers.res("textures/gui/xp_bar_container.png");
        var expProgress = Helpers.res("textures/gui/xp_bar.png");

        if(minecraft == null || minecraft.player == null) return;
        minecraft.getProfiler().push("expBar");
        var i = minecraft.player.getXpNeededForNextLevel();
        if (i > 0) {
            int k = (int)(minecraft.player.experienceProgress * 83.0F);
            var x1 = 82;
            guiGraphics.blit(expBackground, x, l, 0,0, x1, 5, x1, 5);
            if (k > 0) guiGraphics.blit(expProgress, x, l, 0, 0,  k, 5, 82, 5);
        }
        minecraft.getProfiler().pop();
    }

    public static void getAbilityNameWithColour(
        AbilityRegistrar abilityRegistrar,
        GuiGraphics guiGraphics,
        int posX,
        int posY,
        boolean isCenteredString
    ){
        var player = Minecraft.getInstance().player;
        if(player != null){
            Font font = Minecraft.getInstance().font;
            var element = getElementColour(abilityRegistrar, Helpers.getUsedItem(player));
            if (isCenteredString) {
                guiGraphics.drawCenteredString(font, abilityRegistrar.getAbilityName(), posX, posY, element);
                return;
            }
            guiGraphics.drawString(font, abilityRegistrar.getAbilityName(), posX, posY, element, false);
        }
    }

    public static void augmentCoreSlots(@NotNull GuiGraphics guiGraphics, int adjustX, int adjustY, int borderColour, int width, int height, int fade) {
        var spacer = new AtomicInteger();
        var posX = width / 2;
        var posY = height / 2;
        var startX = posX + adjustX - 159;
        var startY = posY - 62 + adjustY;
        boxMaker(guiGraphics, startX, startY, 17, 46, borderColour, fade);
        for(ResourceLocation location : getOverlays()){
            var offsetX = 158;
            var offsetY = 60;
            var x = posX - offsetX + adjustX;
            var y = posY + spacer.get() - offsetY + adjustY;
            guiGraphics.blit(GUI_ITEM_SLOT, x, y, 0,0,32,32,32,32);
            guiGraphics.blit(location, x, y, 0,0,32,32,32,32);
            spacer.set(spacer.get() + 28);
        }
    }

    public static void setCustomBackground(int height, int width, GuiGraphics guiGraphics){
        var widthOffset = 100;
        var heightOffset = 115;
        int i = width / 2;
        int i1 = height / 2;
        var widthFrom = i - widthOffset;
        var heightFrom = i1 - heightOffset;
        var widthTo = i + widthOffset;
        var heightTo = i1 + heightOffset;

        guiGraphics.fill(widthFrom, heightFrom, widthTo, heightTo, BOX_COLOUR);
        guiGraphics.hLine(i -100, i + 99, i1 - 70, BORDER_COLOUR);
        guiGraphics.renderOutline(widthFrom, heightFrom, widthTo - widthFrom, heightTo - heightFrom, BORDER_COLOUR);
        guiGraphics.enableScissor(0, heightFrom + 50, width, heightTo - 5);
    }

    public static void bezelMaker(GuiGraphics guiGraphics, int posX, int posY, int offsetX, int offsetY, int size, AbstractElement element) {
        guiGraphics.blit(BEZEL_1, posX + offsetX, posY, 0, 0, size, size, size, size);
        guiGraphics.blit(BEZEL_2, posX, posY, 0, 0, size, size, size, size);
        guiGraphics.blit(BEZEL_3, posX + 1 , posY + offsetY, 0, 0, size, size, size, size);
        guiGraphics.blit(BEZEL_4, posX + offsetX, posY + offsetY, 0, 0, size, size, size, size);
        if(element != null){
            int[] manaOverlay = {
                125,
                100,
                125,
                50,
                25,
                0
            };
            var index = element.id();
            var blitOffsetX = manaOverlay[index];
            var blitOffsetY = blitOffsetX + 17;
            var offsetXOL = 13;

            guiGraphics.blit(TYPE_OVERLAY, posX + 13, posY + offsetXOL, blitOffsetX, blitOffsetX, 8, 8, 25, 150);
            guiGraphics.blit(TYPE_OVERLAY, posX + offsetX + 11, posY + offsetXOL, blitOffsetY, blitOffsetX, 8, 8, 25, 150);
            guiGraphics.blit(TYPE_OVERLAY, posX + 13, posY + offsetY + 11, blitOffsetX, blitOffsetY, 8, 8, 25, 150);
            guiGraphics.blit(TYPE_OVERLAY, posX + offsetX + 11, posY + offsetY + 11, blitOffsetY, blitOffsetY, 8, 8, 25, 150);
        }
    }

    public static int getElementColour(
        AbilityRegistrar abilityRegistrars,
        ItemStack itemStack
    ){
        var element = new AtomicInteger();
        if (abilityRegistrars.isMultiType()) {
            var wandAbilityHolder = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
            var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityRegistrars.setAbilityId());
            var abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            ElementReg.fromId((int) abilityModifiers.actualValue()).ifPresent(
                getElement -> element.set(getElement.textColourA())
            );
        } else {
            element.set(abilityRegistrars.getElemenType().textColourB());
        }

        return element.get();
    }

    public static int getElementIdAugment(
        ItemStack itemStack
    ){
        var key = DataComponentHelper.getKeyFromAugment(itemStack);
        var abilityRegistrars = AbilityReg.getFirstSpellByTypeId(key).orElseThrow();
        var element = new AtomicInteger();
        if (abilityRegistrars.isMultiType()) {
            var wandAbilityHolder = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
            var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityRegistrars.setAbilityId());
            var abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            ElementReg.fromId((int) abilityModifiers.actualValue()).ifPresent(
                getElement -> element.set(getElement.textColourA())
            );
        } else {
            element.set(abilityRegistrars.getElemenType().id());
        }

        return element.get();
    }

    public static void setSlotTexture(GuiGraphics guiGraphics, int slotX, int slotY, int imageSize, String index){
        guiGraphics.pose().pushPose();
        double x = slotX + 16.2;
        double y = slotY + 10.4;
        guiGraphics.pose().translate(x, y, 2);
        guiGraphics.pose().scale(0.7f,0.7f, 0.7f);
        guiGraphics.pose().translate(0.2, 0.2, 0.2);

        centeredStringNoShadow(guiGraphics, Minecraft.getInstance().font, Component.literal(index), 0, 0, -10329502, false);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 2);
        guiGraphics.blit(GUI_AUGMENT_SLOT, slotX, slotY, 0, 0, imageSize, imageSize, imageSize , imageSize);
        guiGraphics.pose().popPose();
    }

    public static void setSlotTexture(GuiGraphics guiGraphics, int slotX, int slotY, int imageSize, String index, ItemStack itemStack){
        guiGraphics.pose().pushPose();
        double x = slotX + 16.2;
        double y = slotY + 10.4;
        guiGraphics.pose().translate(x, y, 2);
        guiGraphics.pose().scale(0.7f,0.7f, 0.7f);
        guiGraphics.pose().translate(0.2, 0.2, 0.2);

        centeredStringNoShadow(guiGraphics, Minecraft.getInstance().font, Component.literal(index), 0, 0, -10329502, false);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 2);
        guiGraphics.blit(GUI_AUGMENT_SLOT, slotX, slotY, 0, 0, imageSize, imageSize, imageSize , imageSize);
        abilityIcon(guiGraphics, itemStack,slotX * 2 + 33, slotY * 2 + 213, 0, 26, 10);
        guiGraphics.pose().popPose();
    }

    public static void drawStringWithBackground(GuiGraphics guiGraphics, Font pFont, Component pText, int pX, int pY, int backgroundColour, int textColour, boolean isCentered) {
        guiGraphics.pose().pushPose();
        FormattedCharSequence formattedcharsequence = pText.getVisualOrderText();
        String s = pText.getString();
        int i1 = isCentered ? pX - pFont.width(formattedcharsequence) / 2 : pX;
        guiGraphics.drawString(pFont, s, i1 + 1, pY, backgroundColour, false);
        guiGraphics.drawString(pFont, s, i1 - 1, pY, backgroundColour, false);
        guiGraphics.drawString(pFont, s, i1, pY + 1, backgroundColour, false);
        guiGraphics.drawString(pFont, s, i1, pY - 1, backgroundColour, false);
        guiGraphics.drawString(pFont, s, i1, pY, textColour, false);
        guiGraphics.pose().popPose();
    }

    public static AbstractElement getElementWithType(
        AbilityRegistrar abilityRegistrars,
        ItemStack itemStack
    ){
        AtomicReference<AbstractElement> element = new AtomicReference<>(ElementReg.mystic());

        if (abilityRegistrars.isMultiType()) {

            var wandAbilityHolder = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
            var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityRegistrars.setAbilityId());
            var abilityModifiers = abilityHolder.abilityProperties().get(SET_ELEMENT_TYPE);
            ElementReg.fromId((int) abilityModifiers.actualValue()).ifPresent(element::set);
        } else {
            element.set(abilityRegistrars.getElemenType());
        }

        return element.get();
    }

    public static void abilityIcon(GuiGraphics guiGraphics, ItemStack cachedItem, int width, int height, int offset, int localImageSize, int shrinkBy){
        var verticalOffset = 38 + offset;
        var imageWithShrink = localImageSize - shrinkBy;
        var posX = (width - localImageSize) / 2 ;
        var posY = (height - localImageSize) / 2 - 150 + verticalOffset;
        var posX1 = (width - imageWithShrink) / 2 ;
        var posY1 = (height - imageWithShrink) / 2 - 150 + verticalOffset;

        guiGraphics.blit(GUI_GENERAL_SLOT, posX, posY, 0, 0, localImageSize, localImageSize, localImageSize, localImageSize);

        if(cachedItem == null) return;
        var abilityRegistrars = AbilityReg.getSpellsByTypeId(DataComponentHelper.getAbilityTypeItemStack(cachedItem));

        if(!abilityRegistrars.isEmpty()){
            if (!abilityRegistrars.getFirst().getAbilityIconLocation().getPath().isEmpty()) {
                if(abilityRegistrars.getFirst().getAbilityIconLocation() != null){
                    guiGraphics.blit(
                        abilityRegistrars.getFirst().getAbilityIconLocation(),
                        posX1, posY1, 0, 0, imageWithShrink, imageWithShrink, imageWithShrink, imageWithShrink
                    );
                }
            }
        }
    }

    public static void renderItem(@NotNull GuiGraphics guiGraphics, double width, double height, Player player, ItemStack itemStack, float partialTicks, float scale) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(width, height, 400);
        guiGraphics.pose().scale(scale, scale, -scale);

//        guiGraphics.pose().rotateAround(Axis.YP.rotationDegrees(-5), 0,0,0); // Horizontal rotation
        guiGraphics.pose().rotateAround(Axis.ZP.rotationDegrees(45), 0,0,0); // Horizontal rotation

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            255,
            OverlayTexture.NO_OVERLAY,
            guiGraphics.pose(),
            buffer,
            Minecraft.getInstance().level,
            1
        );

        guiGraphics.pose().popPose();
        Lighting.setupForFlatItems();
    }

    public static void renderItem(@NotNull GuiGraphics guiGraphics, double width, double height, ItemStack itemStack, float scale, float mouseX, float mouseY, float rotationSpeed) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(width/2, height/2, 5);
        guiGraphics.pose().scale(scale, scale, -scale);

        var degrees = ((float) guiGraphics.guiWidth() / 2 - mouseX) / rotationSpeed;
        var maxRotation = 6;
        degrees = Math.max(-maxRotation, Math.min(maxRotation, degrees));
        var degrees1 = ((float) guiGraphics.guiHeight() / 2 - mouseY) / rotationSpeed;
        degrees1 = Math.max(-maxRotation, Math.min(maxRotation, degrees1));

        guiGraphics.pose().rotateAround(Axis.YP.rotationDegrees(degrees + 180), 0,0,0); // Horizontal rotation
        guiGraphics.pose().rotateAround(Axis.XP.rotationDegrees(degrees1 + 180), 0,0,0); // Horizontal rotation

        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            15728880,
            OverlayTexture.NO_OVERLAY,
            guiGraphics.pose(),
            buffer,
            Minecraft.getInstance().level,
            1
        );

        guiGraphics.pose().popPose();
        Lighting.setupLevel();
        Lighting.setupForEntityInInventory();
    }

    public static void handleSlotsInGridLayout(
        TriConsumer<Integer, Integer, Integer> slotAction,
        int totalSlots,
        int sharedScreenWidth,
        int shareScreenHeight,
        int xSpacing,
        int ySpacing
    ) {
        int centerX = sharedScreenWidth / 2;
        int centerY = shareScreenHeight / 2;

        if (totalSlots <= 5) {
            int startX = centerX - (totalSlots - 1) * xSpacing / 2; // Center the row

            for (int i = 0; i < totalSlots; i++) {
                int slotX = startX + i * xSpacing;
                int slotY = centerY;
                slotAction.accept(slotX + 80, slotY + 50, i);
            }
        } else {
            int slotsInTopRow = (totalSlots + 1) / 2;
            int slotsInBottomRow = totalSlots / 2;

            int startXTopRow = centerX - (slotsInTopRow - 1) * xSpacing / 2;
            int startXBottomRow = centerX - (slotsInBottomRow - 1) * xSpacing / 2;
            int startYTopRow = centerY - ySpacing / 2;
            int startYBottomRow = centerY + ySpacing / 2;

            for (int i = 0; i < totalSlots; i++) {
                int slotX, slotY;

                if (i < slotsInTopRow) {
                    slotX = startXTopRow + i * xSpacing;
                    slotY = startYTopRow;
                } else {
                    int col = i - slotsInTopRow;
                    slotX = startXBottomRow + col * xSpacing;
                    slotY = startYBottomRow;
                }

                slotAction.accept(slotX + 80, slotY + 50, i);
            }
        }
    }

    public static void renderEntityInInventoryFollowsAngle(GuiGraphics guiGraphics, int i, int i1, int i2, int i3, int i4, float v, float angleXComponent, float angleYComponent, LivingEntity livingEntity) {
        var f = (float)(i + i2) / 2.0F;
        var f1 = (float)(i1 + i3) / 2.0F;
        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quaternionfA = (new Quaternionf()).rotateX(angleYComponent * 20.0F * ((float)Math.PI / 180F));
        quaternionf.mul(quaternionfA);
        var f4 = livingEntity.yBodyRot;
        var f5 = livingEntity.getYRot();
        var f6 = livingEntity.getXRot();
        var f7 = livingEntity.yHeadRotO;
        var f8 = livingEntity.yHeadRot;
        livingEntity.yBodyRot = 180.0F + angleXComponent * 20.0F;
        livingEntity.setYRot(180.0F + angleXComponent * 40.0F);
        livingEntity.setXRot(-angleYComponent * 20.0F);
        livingEntity.yHeadRot = livingEntity.getYRot();
        livingEntity.yHeadRotO = livingEntity.getYRot();
        var f9 = livingEntity.getScale();
        Vector3f vector3f = new Vector3f(0.0F, livingEntity.getBbHeight() / 2.0F + v * f9, 0.0F);
        var f10 = (float)i4 / f9;
        renderEntityInInventory(guiGraphics, f, f1, f10, vector3f, quaternionf, quaternionfA, livingEntity);
        livingEntity.yBodyRot = f4;
        livingEntity.setYRot(f5);
        livingEntity.setXRot(f6);
        livingEntity.yHeadRotO = f7;
        livingEntity.yHeadRot = f8;
    }

}
