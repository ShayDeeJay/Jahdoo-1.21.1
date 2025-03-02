package org.jahdoo.ascension.boon;

import net.minecraft.network.chat.Component;

public record Boon(String label, int colour, Runnable execute){}
