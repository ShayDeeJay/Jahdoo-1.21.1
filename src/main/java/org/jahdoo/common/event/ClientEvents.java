package org.jahdoo.common.event;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.client.OverlayBlockTooltip;
import org.jahdoo.common.client.screens.AbilityUnlockScreen;
import org.jahdoo.common.client.screens.RunScreen;
import org.jahdoo.common.client.screens.StatScreen;
import org.jahdoo.common.client.tooltip_renderer.RuneTooltipRenderer;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import static net.neoforged.neoforge.client.event.RenderLivingEvent.Pre;
import static org.jahdoo.common.client.KeyBinding.*;
import static org.jahdoo.common.event.event_helpers.EventHelpers.*;
import static org.jahdoo.common.event.event_helpers.KeyBindHelper.quickSelectBehaviour;
import static org.jahdoo.common.event.event_helpers.KeyBindHelper.toggleLockAbility;
import static org.jahdoo.common.event.event_helpers.OverlayEvent.crosshairManager;
import static org.jahdoo.common.event.event_helpers.OverlayEvent.simpleGui;
import static org.jahdoo.common.event.event_helpers.RenderEventHelper.*;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.getAllSlots;
import static org.jahdoo.trial_nexus.ability.AbilityComponentHelper.getAugmentModificationScreenWand;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void entityRenderer(Pre event) {
        var poseStack = event.getPoseStack();
        var entity = event.getEntity();
        var instance = Minecraft.getInstance();

//        instance.getSoundManager().


        if(!entity.isAlive()) return;
        renderHealthBar(event, entity, instance, poseStack);
        mysticEffectClient(event);
        renderChampionVisual(event);
    }

    public static ResourceLocation getHealthHolderIcon(Entity entity, Player player) {
        if (entity instanceof ITamableEntity tamable) {
            Optional<UUID> ownerUUID = tamable.getOwnerUUIDOptional();
            if (ownerUUID.isPresent() && ownerUUID.get().equals(player.getUUID())) {
                return Icons.HEALTH_HOLDER_ALLIED;
            }
        }
        return Icons.HEALTH_HOLDER;
    }

    @SubscribeEvent
    public static void levelEvent(LevelTickEvent.Pre event) {
        var level = event.getLevel();
        var sManager = Minecraft.getInstance().getSoundManager();
//        if(sManager.getSoundEvent(SoundReg.RE_ROLL.get().getLocation()).)
//        sManager.stop(SoundReg.RE_ROLL.get().getLocation(), SoundSource.MUSIC);

        var sound = new SimpleSoundInstance(
            SoundReg.SPELL_SOUND.get().getLocation(),
            SoundSource.MUSIC,
            1.0F, 1.0F,
            SoundInstance.createUnseededRandom(),
            true,
            0,
            SoundInstance.Attenuation.NONE,
            0.0F, 0.0F, 0.0F,
            true
        );

//        var active = sManager.isActive(sound);

//        sManager.st();

//        sManager.stop();
//        sManager.getSoundEvent(SoundReg.RE_ROLL.get().getLocation()).
//        if(level.getGameTime()%20 == 0){
//            if(!active){
//                System.out.println(sound.isLooping());
//                sManager.play(
//                sound
//            );
//            }
//
//        }


    }


    @SubscribeEvent
    public static void overlayEventPre(RenderGuiLayerEvent.Pre event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;

        crosshairManager(event);
        simpleGui(event, player);
    }

    @SubscribeEvent
    public static void overlayEventPost(RenderGuiLayerEvent.Post event) {
        OverlayBlockTooltip.overlayEvent(event);
    }

    @SubscribeEvent
    public static void tooltipEvent(RenderTooltipEvent.GatherComponents e){
        var current = e.getTooltipElements();
        var itemStack = e.getItemStack();
        var item = itemStack.getItem();
        var instance = Minecraft.getInstance();
//        var soundManager = instance.getSoundManager();;
//        Minecraft.getInstance().getSoundManager().play(
//            SimpleSoundInstance.forMusic(
//                SoundReg.RE_ROLL.get()
//            )
//        );

//        if(item.asItem() instanceof JahdooItem && Helpers.isKeyDown(KEY_LSHIFT)) Minecraft.getInstance().setScreen(new QuestLogScreen());

        if(item instanceof JahdooItem){
            var iterator = current.iterator();
            while (iterator.hasNext()) {
                var tooltipElement = iterator.next();
                var left = tooltipElement.left();
                if (left.isPresent()) {
                    var formattedText = left.get();
                    var string = formattedText.toString();
                    var neoforge = string.contains("neoforge");
                    var whenOn = string.contains("item.modifiers");
                    var enchantment = string.contains("enchantment");
                    var mcComponent = string.contains("literal{ }[style={color=dark_green}");
                    var modNamePre = string.contains("creative_tab.jahdoo_tab");
                    var curio = string.contains("curios.modifiers");
                    var empty = formattedText.getString().isEmpty();
                    if (empty || neoforge || whenOn || enchantment || mcComponent || modNamePre || curio) {
                        iterator.remove();
                    }
                }
            }
        }
        var mc = Minecraft.getInstance();
        var level = mc.level;

        if(level != null){
            var iterator = current.listIterator();
            while (iterator.hasNext()) {
                var tooltipElement = iterator.next();
                var left = tooltipElement.left();
                if (left.isEmpty()) continue;

                var formattedText = left.get();
                var rawString = formattedText.getString();
                var components = itemStack.getComponents();
                var storedEnchants = components.get(DataComponents.STORED_ENCHANTMENTS);
                var appliedEnchants = components.get(DataComponents.ENCHANTMENTS);

                if(storedEnchants != null){
                     isFoundOverEnchanted(storedEnchants, formattedText, iterator, rawString, level);
                } else if (appliedEnchants != null) {
                    isFoundOverEnchanted(appliedEnchants, formattedText, iterator, rawString, level);
                }
            }
        }

        var allSlots = getAllSlots(itemStack);
        if(allSlots.isEmpty()) return;
        var runeSockets = new RuneTooltipRenderer.RuneComponent(itemStack, allSlots);
        current.add(current.size(), Either.right(runeSockets));
    }

    @SubscribeEvent
    public static void playerRenderer(RenderLevelStageEvent event) {
        if (event.getStage() != Stage.AFTER_BLOCK_ENTITIES) return;

        var player = (Player) event.getCamera().getEntity();
        var stack = Helpers.getUsedItem(player);

        renderUtilityOverlay(event, player, stack);
        renderTeleportLocationOverlay(event, player, stack);
        renderAbilityOverlay(event,stack, player);
        lockNearbyTarget(event);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        if(player == null) return;

        quickSelectBehaviour(player, instance);
        toggleLockAbility(player);

        checkKey(WAND_SLOT_1A, () -> selectAbilitySlot(1));
        checkKey(WAND_SLOT_2A, () -> selectAbilitySlot(2));
        checkKey(WAND_SLOT_3A, () -> selectAbilitySlot(3));
        checkKey(WAND_SLOT_4A, () -> selectAbilitySlot(4));
        checkKey(WAND_SLOT_5A, () -> selectAbilitySlot(5));
        checkKey(WAND_SLOT_6A, () -> selectAbilitySlot(6));
        checkKey(WAND_SLOT_7A, () -> selectAbilitySlot(7));
        checkKey(WAND_SLOT_8A, () -> selectAbilitySlot(8));
        checkKey(WAND_SLOT_9A, () -> selectAbilitySlot(9));
        checkKey(WAND_SLOT_10A, () -> selectAbilitySlot(10));

        checkKey(STAT_SCREEN, () -> instance.setScreen(new StatScreen()));
        checkKey(ABILITY_SCREEN, () -> instance.setScreen(new AbilityUnlockScreen()));
        checkKey(ABILITY_MODIFICATION_SCREEN, () -> instance.setScreen(getAugmentModificationScreenWand(player, null)));
        checkKey(RUN_SCREEN, () -> instance.setScreen(new RunScreen()));
    }

    private static final Map<KeyMapping, Boolean> keyWasDown = new HashMap<>();

    private static void checkKey(KeyMapping key, Runnable action) {
        var isDown = key.isDown();
        var wasDown = keyWasDown.getOrDefault(key, false);

        if (isDown && !wasDown) action.run();
        keyWasDown.put(key, isDown);
    }
}


