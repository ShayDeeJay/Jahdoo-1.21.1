package org.jahdoo.client.gui.block.rune_table;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.block.rune_table.RuneTableEntity;
import org.jahdoo.client.SharedUI;
import org.jahdoo.client.gui.block.RuneSlot;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.items.runes.rune_data.RuneHolder;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.items.wand.WandItemHelper;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;
import static org.jahdoo.client.IconLocations.GUI_BUTTON;
import static org.jahdoo.client.IconLocations.GUI_GENERAL_SLOT;
import static org.jahdoo.client.SharedUI.*;
import static org.jahdoo.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.utils.ModHelpers.filterList;
import static org.jahdoo.utils.ModHelpers.withStyleComponent;

public class RuneTableScreen extends AbstractContainerScreen<RuneTableMenu> {
    public static WidgetSprites WIDGET = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);
    private final RuneTableMenu runeTableMenu;
    int scaleItem = 60;
    AbstractElement element;
    int borderColour;
    boolean isHovering;

    public RuneTableScreen(RuneTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        var element = ElementRegistry.getElementFromWand(pMenu.getRuneTableEntity().inputItemHandler.getStackInSlot(0).getItem());
        this.runeTableMenu = pMenu;
        this.element = element.orElse(ElementRegistry.MYSTIC.get());
        this.borderColour = element.map(AbstractElement::textColourPrimary).orElseGet(() -> FastColor.ARGB32.color(56, 157, 59));
    }

    @Override
    protected void containerTick() {
        if(this.hoveredSlot != null) rebuildWidgets();
    }

    public RuneTableEntity entity(){
        return this.runeTableMenu.getRuneTableEntity();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    private void wandProperties(@NotNull GuiGraphics guiGraphics) {
        var shiftY = 0;
        var shiftX = -5;
        var spacer = new AtomicInteger();

        remainingPotential(guiGraphics, shiftX, spacer, shiftY);
        var getRunes = RuneHolder.getRuneholder(getItem());
        handleSlotsInGridLayout(
            (slotX, slotY, index) -> {
                for (ItemStack ignored : getRunes.runeSlots()) {
                    var size = 32;
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0,0,100);
                    var x = slotX + runeTableMenu.posX - 96;
                    var y = slotY - runeTableMenu.posY - 9;
                    guiGraphics.blit(GUI_GENERAL_SLOT, x, y, 0,0, size, size, size, size);
                    spacer.set(spacer.get() + runeTableMenu.runeYSpacer);
                    guiGraphics.pose().popPose();
                }
            },
            getRunes.runeSlots().size(),
            this.width,
            this.height,
            runeTableMenu.offSetX,
            runeTableMenu.offSetY
        );

        var startX11 = this.width/2 + shiftX - 38;
        var startY11 = this.height/2 + shiftY - 89;
        var heightOffset = 86 - 40;
        boxMaker(guiGraphics, startX11, startY11, Math.max(10, 74), heightOffset, borderColour, groupFade());

        if(getRunes.runeSlots().isEmpty()){
            guiGraphics.drawCenteredString(this.font, "No Slots Available", startX11 + 74, startY11 + 40, SUB_HEADER_COLOUR);
        }

        var startX1 = this.width / 2 - 38 + shiftX;
        var startY1 = this.height / 2 - 111 + shiftY;
        boxMaker(guiGraphics, startX1, startY1, 39, 10, borderColour, groupFade());

        var header = Component.literal("Rune Manager");
        var x = this.width / 2 - 34 + shiftX;
        var y1 = this.height / 2 - 105 + shiftY;
        guiGraphics.drawString(this.font, header, x, y1, SUB_HEADER_COLOUR);
    }

    private void renderItem(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int startX, int startY) {
        // Define constants for positioning and sizing
        final int ITEM_OFFSET_X = 40;
        final int ITEM_OFFSET_Y = -17;
        final int SHIFT_X = 75;
        final int WAND_ITEM_OFFSET = getItem().getItem() instanceof WandItem ? 80 : 90;
        final int SCALED_ITEM = scaleItem - WAND_ITEM_OFFSET;
        final int OFFSET_Y = 164 - 80;

        int minX = startX + ITEM_OFFSET_X + 70 - SHIFT_X;
        int minY = startY + ITEM_OFFSET_Y - 94;
        int maxX = startX + ITEM_OFFSET_X + 55;
        int width = this.width - SHIFT_X * 2;
        int height = this.height - (144 - SCALED_ITEM);
        int posX = startX + 23;
        int posY = startY + ITEM_OFFSET_Y - 106;

        guiGraphics.pose().pushPose();
        bezelMaker(guiGraphics, posX, posY, 52, OFFSET_Y, 32, null);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -10);
        guiGraphics.enableScissor(minX, minY, maxX, minY + 172);

        var stand = EntityType.ARMOR_STAND.create(getMinecraft().level);
        if (getItem().getItem() instanceof ArmorItem armorItem && stand != null) {
            stand.setItemSlot(armorItem.getEquipmentSlot(), getItem());
            stand.setInvisible(true);

            int yOffset = switch (armorItem.getEquipmentSlot()) {
                case HEAD -> 70;
                case CHEST -> 8;
                case LEGS -> -30;
                case FEET -> -60;
                default -> 20;
            };

            renderEntityInInventoryFollowsMouse(guiGraphics, this.leftPos - 48, this.topPos, this.leftPos + 75, this.height / 2 + yOffset, 40, 0.0625F, mouseX, mouseY, stand);
        } else {
            SharedUI.renderItem(guiGraphics, width, height, getItem(), SCALED_ITEM, mouseX, mouseY, 16);
        }

        if (this.element != null) {
            int colorFade = FastColor.ARGB32.color(100, borderColour);
            int heightOffset = 86 - 40;
            int color = FastColor.ARGB32.color(50, borderColour);
            int colorA = FastColor.ARGB32.color(20, borderColour);

            boxMaker(guiGraphics, minX, minY, 30, heightOffset, getFadedColourBackground(0.4f));
            SharedUI.boxMaker(guiGraphics, minX, minY, 30, heightOffset, color, colorA, colorFade);
        }

        guiGraphics.disableScissor();
        guiGraphics.pose().popPose();
    }

    public ItemStack getItem(){
        return entity().inputItemHandler.getStackInSlot(0);
    }

    private void remainingPotential(@NotNull GuiGraphics guiGraphics, int shiftX, AtomicInteger spacer, int shiftY) {
        var slot = ModHelpers.withStyleComponent(String.valueOf(getPotential()), SUB_HEADER_COLOUR);
        var component = withStyleComponent("Potential: ", HEADER_COLOUR).copy().append(slot);
        var sharedX = this.width / 2 - 30 + shiftX;
        var posY = this.height / 2 - 85 + spacer.get() + shiftY;
        guiGraphics.drawString(this.font, component, sharedX -1, posY + 2, 0);
    }

    private static int groupFade() {
        return getFadedColourBackground(0.7f);
    }

    private void hoverCarried(GuiGraphics guiGraphics, int x, int y){
        var carried = this.hoveredSlot == null || hoveredSlot.getItem().isEmpty() ? runeTableMenu.getCarried() : hoveredSlot.getItem();
        if(carried.getItem() instanceof RuneItem){
            var getTooltip = this.getTooltipFromContainerItem(carried);
            if (!carried.isEmpty()) {
                guiGraphics.renderTooltip(font, getTooltip, Optional.empty(), x, y);
            }
        }
    }

    private void overlayInventory(@NotNull GuiGraphics guiGraphics, int startX, int startY) {
        guiGraphics.pose().popPose();
        var i = 40;
        var i1 = -17;
        guiGraphics.pose().translate(0,0,20);
        var startX1 = startX + i - 5;
        boxMaker(guiGraphics, startX1, startY + i1, 105, 55, borderColour, groupFade());
        renderInventoryBackground(guiGraphics, this, 256, 24, true);
        guiGraphics.pose().pushPose();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.renderBlurredBackground(pPartialTick);
        var adjustX = 18;
        var adjustY = -27;
        var i = this.width / 2;
        var i1 = this.height / 2;
        var startX = i - 140;
        var startY = i1 + 22;

        scaleItem();
        augmentCoreSlots(guiGraphics, adjustX, adjustY, borderColour, this.width, this.height, groupFade());
        renderItem(guiGraphics, mouseX, mouseY, startX, startY);
        wandProperties(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        var hSlot = this.hoveredSlot;
        if(hSlot != null && !(hSlot.getItem().getItem() instanceof RuneItem)){
            this.renderTooltip(guiGraphics, mouseX, mouseY);
        }
        overlayInventory(guiGraphics, startX, startY);

        hoverCarried(guiGraphics, mouseX, mouseY);
        this.isHovering = false;
    }

    public static void renderEntityInInventoryFollowsMouse(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int scale, float yOffset, float mouseX, float mouseY, LivingEntity entity) {
        var f = (float)(x1 + x2) / 2.0F;
        var f1 = (float)(y1 + y2) / 2.0F;
        var f2 = (float)Math.atan((f - mouseX) / 320.0F);
        var f3 = (float)Math.atan((f1 - mouseY) / 320.0F);
        renderEntityInInventoryFollowsAngle(guiGraphics, x1, y1, x2, y2, scale, yOffset, f2, f3, entity);
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

    private int getPotential() {
        return RuneHolder.potential(getItem());
    }

    private int getExperienceCost() {
        var potential = getPotential();
        return Math.max(10, 100 - potential);
    }

    private void scaleItem() {
        this.scaleItem = Math.min(140, scaleItem + 8);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}
}
