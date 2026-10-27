package com.gambleutils;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import java.math.BigDecimal;

public final class GambleUtilsClient implements ClientModInitializer {
    public static GambleConfig config;
    public static AuctionModel auction;
    @Override public void onInitializeClient() {
        config = GambleConfig.load();
        auction = new AuctionModel(config);
        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("gambleutils", "general"));
        KeyMapping open = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.gambleutils.open", InputConstants.Type.KEYSYM, InputConstants.KEY_G, category));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (open.consumeClick()) {
                if (client.player != null) client.gui.setScreen(new GambleHomeScreen());
            }
            if (auction.active && auction.secondsLeft() == 0) auction.stop();
        });
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("gambleutils", "auction_hud"), (g, tick) -> {
            Minecraft mc = Minecraft.getInstance();
            if (!auction.active || mc.player == null) return;
            int left = 10, top = 12;
            g.fill(left, top, left + 215, top + 53, config.panel);
            g.fill(left, top, left + 3, top + 53, config.accent);
            g.text(mc.font, "GambleUtils / Auction", left + 10, top + 6, config.accent, true);
            String item = auction.itemName.isBlank() ? "Unnamed item" : auction.itemName;
            g.text(mc.font, item + " x" + auction.amount, left + 10, top + 21, config.text, true);
            g.text(mc.font, "Remaining: " + auction.secondsLeft() + "s", left + 10, top + 34, config.text, false);
            if (config.showBar) {
                int barWidth = (int) (195 * (auction.secondsLeft() / (double) Math.max(1, auction.durationSeconds)));
                g.fill(left + 10, top + 47, left + 10 + barWidth, top + 50, config.bar);
            }
        });
    }
    public static String money(BigDecimal amount) {
        return "$" + amount.stripTrailingZeros().toPlainString();
    }
}
