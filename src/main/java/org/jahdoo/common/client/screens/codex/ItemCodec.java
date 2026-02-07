package org.jahdoo.common.client.screens.codex;

import net.minecraft.world.item.ItemStack;

public record ItemCodec(
    ItemStack stack,
    String description,
    String foundIn,
    String additionalInformation
) {}
