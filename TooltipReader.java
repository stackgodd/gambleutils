package com.gambleutils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

/** Safe fallback: item hover name can be read, but the 26.2 server tooltip interface needs in-game validation. */
final class TooltipReader {
    private TooltipReader() {}
    static List<String> read(ItemStack item, Minecraft mc) {
        // TODO: Wire the current 26.2 tooltip-component API after capturing a genuine DonutSMP tooltip.
        // Do not invent values: with no verified tooltip line this returns no detected price.
        return new ArrayList<>();
    }
}
