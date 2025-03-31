package org.jahdoo.common.items;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.ElementReg;

import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.ability.abilities_combat.dimensional_recall.DimensionalRecall.pullParticlesToCenter;
import static org.jahdoo.ascension.ability.abilities_combat.dimensional_recall.DimensionalRecall.sendNoHomeMessage;
import static org.jahdoo.ascension.ability.abilities_combat.dimensional_recall.DimensionalRecallAbility.abilityId;
import static org.jahdoo.ascension.utils.Helpers.getSoundWithPosition;
import static org.jahdoo.ascension.utils.Helpers.getSoundWithPositionV;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;

public class RecallToken extends Item {

    public RecallToken() { super(new Properties()); }

    @Override
    public Component getName(ItemStack stack) {
        return Helpers.withStyleComponent("Recall Token", color(161, 104, 251));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Helpers.withStyleComponent("Teleports you back home", color(161, 104, 251)));
        tooltipComponents.add(Helpers.withStyleComponent("Consumes when used in the INSERT NAME", ColourStore.OFF_WHITE));
    }

    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int remainingUseDuration) {
        var getCastTime = 150;

        if (!(player instanceof ServerPlayer serverPlayer)) return;
        var pos = serverPlayer.getRespawnPosition();

        if(player.isUsingItem()){
            var mystic = ElementReg.mystic();
            if (pos != null) {
                pullParticlesToCenter((Player) player, mystic);
                var setVolume = Math.min(2, player.getTicksUsingItem() / 5);
                var setPitch = (float) player.getTicksUsingItem() / getCastTime;

                if (player.getTicksUsingItem() % 3 == 0) {
                    var setAudio = SoundEvents.SOUL_ESCAPE.value();
                    getSoundWithPositionV(player.level(), player.position(), setAudio, setVolume, setPitch);
                }

                if (player.getTicksUsingItem() % 40 == 0) {
                    var setAudio = SoundEvents.ILLUSIONER_CAST_SPELL;
                    getSoundWithPositionV(player.level(), player.position(), setAudio, Math.max(setVolume, 0.2f), Math.max(setPitch, 0.6f));
                }

                this.onSuccessfulCast(serverPlayer, getCastTime);
            } else {
                sendNoHomeMessage((Player) player, mystic);
            }
        }

        super.onUseTick(level, player, stack, remainingUseDuration);
    }

    public void onSuccessfulCast(ServerPlayer serverPlayer,int ticksUsing){
        var pos = serverPlayer.getRespawnPosition();
        var dimension = serverPlayer.getRespawnDimension();
        var abilityName = abilityId.getPath().intern();
        var getCasterData = serverPlayer.getData(CASTER_DATA);
        var getTeleportSound = SoundEvents.CHORUS_FRUIT_TELEPORT;
        var getSuccessSound = SoundEvents.ILLUSIONER_CAST_SPELL;
        var getManaCost = 150;
        var getLevelDimension = serverPlayer.getServer().getLevel(dimension);

        if(getLevelDimension != null){
            if (serverPlayer.getTicksUsingItem() >= ticksUsing/* && getCasterData.getManaPool() >= getManaCost*/) {
                if(serverPlayer.level() instanceof CustomLevel) {
                    serverPlayer.getItemInHand(serverPlayer.getUsedItemHand()).shrink(1);
                };
                serverPlayer.stopUsingItem();
                serverPlayer.teleportTo(getLevelDimension, pos.getX(), pos.getY(), pos.getZ(), serverPlayer.yya, serverPlayer.rotA);
//                CastHelper.chargeMana(abilityName, getManaCost, serverPlayer);
                getSoundWithPosition(serverPlayer.level(), serverPlayer.blockPosition(), getTeleportSound, 0.8f);
                getSoundWithPosition(serverPlayer.level(), serverPlayer.blockPosition(), getSuccessSound, 1, 1.2f);
            }
        }
    }
}
