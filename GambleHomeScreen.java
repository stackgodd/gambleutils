package com.gambleutils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.math.BigDecimal;
import java.util.Optional;

public final class GambleHomeScreen extends Screen {
    private EditBox name, qty, worth, duration;
    private String notice = "";
    private final AuctionModel a = GambleUtilsClient.auction;
    private final GambleConfig c = GambleUtilsClient.config;
    public GambleHomeScreen() { super(Component.literal("GambleUtils")); }
    @Override protected void init() {
        int x = width / 2 - 145, y = height / 2 - 120;
        name = field(x+12,y+57, 188, a.itemName);
        qty = field(x+205,y+57, 72, "" + a.amount);
        worth = field(x+12,y+99, 128, a.manualUnitValue);
        duration = field(x+145,y+99, 132, "" + a.durationSeconds);
        addRenderableWidget(Button.builder(Component.literal("Use held item"), b -> {
            if (minecraft.player == null || minecraft.player.getMainHandItem().isEmpty()) { notice = "Hold an item first."; return; }
            var stack = minecraft.player.getMainHandItem();
            name.setValue(stack.getHoverName().getString());
            qty.setValue("" + stack.getCount());
            // Tooltip text varies by server; read only explicit sell-value labels.
            a.detectedUnitValue = null;
            for (String line : TooltipReader.read(stack, minecraft)) {
                Optional<BigDecimal> found = WorthParser.parsePerItem(line);
                if (found.isPresent()) { a.detectedUnitValue = found.get(); break; }
            }
            notice = a.detectedUnitValue == null ? "No sell value found. Enter unit worth." : "Sell value detected from item tooltip.";
        }).bounds(x+12,y+132, 128,20).build());
        addRenderableWidget(Button.builder(Component.literal("Start auction"), b -> {
            if (!sync()) return;
            a.start(); a.save(); minecraft.gui.setScreen(null);
        }).bounds(x+145,y+132, 132,20).build());
        addRenderableWidget(Button.builder(Component.literal("Stop"), b -> { a.stop(); notice = "Auction stopped."; }).bounds(x+12,y+161, 82,20).build());
        addRenderableWidget(Button.builder(Component.literal("Theme editor"), b -> { sync(); minecraft.gui.setScreen(new GambleThemeScreen()); }).bounds(x+99,y+161, 120,20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> { if (sync()) {a.save(); minecraft.gui.setScreen(null);} }).bounds(x+224,y+161, 53,20).build());
    }
    private EditBox field(int x,int y,int w,String value) {
        EditBox field = new EditBox(font, x, y, w, 20, Component.empty());
        field.setMaxLength(80);
        field.setValue(value);
        addRenderableWidget(field);
        return field;
    }
    private boolean sync() {
        try {
            int n = Integer.parseInt(qty.getValue());
            int seconds = Integer.parseInt(duration.getValue());
            if (n < 1 || n > 2304 || seconds < 1 || seconds > 3600) throw new NumberFormatException();
            if (!worth.getValue().isBlank() && WorthParser.parseNumber(worth.getValue()).isEmpty()) { notice = "Use values like 500, 1.2k, 3m."; return false; }
            a.itemName = name.getValue().trim(); a.amount = n;
            a.manualUnitValue = worth.getValue().trim(); a.durationSeconds = seconds;
            a.save(); return true;
        } catch (NumberFormatException ex) { notice = "Quantity: 1-2304; seconds: 1-3600"; return false; }
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g,mx,my,delta);
        int x=width/2-145,y=height/2-120;
        g.fill(x,y,x+290,y+233,c.panel);
        g.fill(x,y,x+290,y+4,c.accent);
        g.text(font,"GAMBLEUTILS  /  AUCTION",x+12,y+15,c.text,true);
        g.text(font,"Select an item and set its total worth",x+12,y+32,c.text,false);
        g.text(font,"Item name",x+12,y+45,c.text,false);
        g.text(font,"Amount",x+205,y+45,c.text,false);
        g.text(font,"Unit sell worth (optional)",x+12,y+87,c.text,false);
        g.text(font,"Timer (seconds)",x+145,y+87,c.text,false);
        Optional<BigDecimal> calc = valuePreview();
        g.text(font,"Estimated total: " + calc.map(GambleUtilsClient::money).orElse("Not available"),x+12,y+193,c.text,true);
        String origin = WorthParser.parseNumber(worth == null ? "" : worth.getValue()).isPresent() ? "manual" : a.detectedUnitValue != null ? "tooltip" : "none";
        g.text(font,"Source: " + origin + "  |  No external API required",x+12,y+208,c.accent,false);
        if (!notice.isEmpty()) g.text(font,notice,x+12,y+220,c.text,false);
    }
    private Optional<BigDecimal> valuePreview() {
        try {
            int n = Integer.parseInt(qty.getValue());
            Optional<BigDecimal> manual = WorthParser.parseNumber(worth.getValue());
            BigDecimal unit = manual.orElse(a.detectedUnitValue);
            return unit == null ? Optional.empty() : Optional.of(unit.multiply(BigDecimal.valueOf(n)));
        } catch (Exception e) { return Optional.empty(); }
    }
}
