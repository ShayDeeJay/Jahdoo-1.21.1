package org.jahdoo.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.IGuiGraphicsExtension;
import org.jahdoo.all_magic.AbilityBuilder;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.GeneralHelpers;

import java.util.List;
public class AugmentMenu extends Screen  {

    ItemStack itemStack;

    public AugmentMenu(ItemStack itemStack) {
        super(Component.literal("Augment Menu"));
        this.itemStack = itemStack;
    }

    private float localTick = 50;
    public static final int RADIAL_SIZE = 150;
    int buttonSize = RADIAL_SIZE / 7 + 3;

    public static List<String> getAllAbilities(ItemStack wand){
        var wandData = DataComponentRegistry.WAND_DATA.get();
        if(wand.has(wandData)){
            return wand.get(wandData).abilitySet();
        }
        return wand.get(DataComponentRegistry.WAND_DATA.get()).abilitySet();
    }

    @Override
    protected void init() {

        var texture = GeneralHelpers.modResourceLocation("textures/gui/wand_gui.png");
        WidgetSprites BUTTON = new WidgetSprites(texture, texture);
        this.addRenderableWidget(
            new ImageButton(
                50,50, BUTTON,
                (s) -> {
                    var holder = this.itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
                    var mainHolder = holder.abilityProperties().values().stream().findFirst();
                    mainHolder.get().abilityProperties().forEach(
                        (e, v) ->{
                            System.out.println(e);
                            System.out.println(v);
                        }
                    );
                },
                Component.literal("")
            )
        );

//        if(player == null) return;
//        var wand = player.getMainHandItem();
//        var abilityHolder = getAllAbilities(wand); // Define the number of positions around the circle
//        var totalSlots = wand.get(DataComponentRegistry.WAND_DATA.get()).abilitySlots();
//        int centerX = this.width / 2;
//        int centerY = this.height / 2;
//        int radius = (int) (8.4 * ((double) RADIAL_SIZE / 20) - 3.5); // Adjust the radius as needed
//        double angleOffset = -Math.PI / 2.0; // Start from the top
//
//        for (int i = 0; i < totalSlots; i++) {
//            double angle = angleOffset + 2 * Math.PI * i / totalSlots; // Calculate angle for each position
//            int buttonX = (int) (centerX + radius * Math.cos(angle)) - buttonSize / 2;
//            int buttonY = (int) (centerY + radius * Math.sin(angle)) - buttonSize / 2;
//
//            int finalI = i;
//            if (!abilityHolder.isEmpty() && !AbilityRegister.getSpellsByTypeId(abilityHolder.get(i)).isEmpty()) {
//                ResourceLocation iconResource = AbilityRegister.getSpellsByTypeId(abilityHolder.get(i)).getFirst().getAbilityIconLocation();
//                WidgetSprites BUTTON = new WidgetSprites(iconResource, iconResource);
//                this.addRenderableWidget(
//                    new AbilityIconButton(
//                        buttonX, buttonY,
//                        BUTTON,
//                        buttonSize,
//                        pButton -> {
//                            PacketDistributor.sendToServer(new StopUsingC2SPacket());
//                            PacketDistributor.sendToServer(new SelectedAbilityC2SPacket(abilityHolder.get(finalI)));
//                        },
//                        i
//                    )
//                );
//            } else {
//                this.addRenderableOnly(
//                    new Overlay() {
//                        @Override
//                        public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
//                            SharedUI.drawStringWithBackground(
//                                guiGraphics,
//                                Minecraft.getInstance().font,
//                                Component.literal(String.valueOf(finalI + 1)),
//                                buttonX + 13,
//                                buttonY + 9,
//                                1, -1, true
//                            );
//                        }
//                    }
//                );
//            }
//        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {}

}
