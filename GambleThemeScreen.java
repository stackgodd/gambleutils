package com.gambleutils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.awt.Color;

/** Simple HSB colour controls and four built-in themes. */
public final class GambleThemeScreen extends Screen {
    private final GambleConfig c = GambleUtilsClient.config;
    private String target = "Accent";
    private int hue = 230, saturation = 65, brightness = 95;
    private EditBox hex;
    private String status = "";
    public GambleThemeScreen() { super(Component.literal("GambleUtils / Themes")); }
    @Override protected void init() {
        int x = width/2-145, y=height/2-119;
        String[] themes = {"Midnight","Light","Emerald","Rose"};
        for(int i=0;i<4;i++) {
            String t=themes[i];
            addRenderableWidget(Button.builder(Component.literal(t), b->{c.applyTheme(t);select(target);c.save();}).bounds(x+10+(i%2)*137,y+35+(i/2)*25,132,20).build());
        }
        String[] targets={"Accent","Panel","Text","Bar"};
        for(int i=0;i<4;i++) {
            String t=targets[i];
            addRenderableWidget(Button.builder(Component.literal(t), b->select(t)).bounds(x+10+i*68,y+93,64,20).build());
        }
        addRenderableWidget(Button.builder(Component.literal("Hue -"),b->adjust(-10,0,0)).bounds(x+10,y+126,56,20).build());
        addRenderableWidget(Button.builder(Component.literal("Hue +"),b->adjust(10,0,0)).bounds(x+72,y+126,56,20).build());
        addRenderableWidget(Button.builder(Component.literal("Sat -"),b->adjust(0,-10,0)).bounds(x+139,y+126,56,20).build());
        addRenderableWidget(Button.builder(Component.literal("Sat +"),b->adjust(0,10,0)).bounds(x+201,y+126,56,20).build());
        addRenderableWidget(Button.builder(Component.literal("Dim"),b->adjust(0,0,-10)).bounds(x+10,y+154,56,20).build());
        addRenderableWidget(Button.builder(Component.literal("Bright"),b->adjust(0,0,10)).bounds(x+72,y+154,56,20).build());
        hex = new EditBox(font,x+139,y+154,128,20,Component.literal("Hex ARGB"));
        hex.setMaxLength(8); addRenderableWidget(hex);select("Accent");
        addRenderableWidget(Button.builder(Component.literal("Apply HEX"),b->{
            try { String v=hex.getValue().replace("#","");
                if(v.length()!=6 && v.length()!=8) throw new IllegalArgumentException();
                int color = (int)Long.parseLong(v,16); if(v.length()==6) color|=0xFF000000;
                setTarget(color); select(target); c.save(); status="Saved HEX colour.";
            } catch(Exception e){status="Use RRGGBB or AARRGGBB.";}
        }).bounds(x+10,y+185,120,20).build());
        addRenderableWidget(Button.builder(Component.literal("Show bar: " + (c.showBar?"ON":"OFF")),b->{c.showBar=!c.showBar;c.save();b.setMessage(Component.literal("Show bar: "+(c.showBar?"ON":"OFF")));}).bounds(x+139,y+185,128,20).build());
        addRenderableWidget(Button.builder(Component.literal("Back to auction"),b->{c.save();minecraft.gui.setScreen(new GambleHomeScreen());}).bounds(x+10,y+214,257,20).build());
    }
    private void select(String t) {
        target=t;int color=getTarget(); float[] hsb=Color.RGBtoHSB((color>>16)&255,(color>>8)&255,color&255,null);
        hue=(int)(hsb[0]*360);saturation=(int)(hsb[1]*100);brightness=(int)(hsb[2]*100);
        if(hex!=null)hex.setValue(String.format("%06X",color&0xFFFFFF));
    }
    private int getTarget(){return switch(target){case "Panel"->c.panel;case "Text"->c.text;case "Bar"->c.bar;default->c.accent;};}
    private void setTarget(int argb){switch(target){case "Panel"->c.panel=argb;case "Text"->c.text=argb;case "Bar"->c.bar=argb;default->c.accent=argb;}}
    private void adjust(int h,int s,int b){hue=Math.floorMod(hue+h,360);saturation=Math.max(0,Math.min(100,saturation+s));brightness=Math.max(0,Math.min(100,brightness+b));setTarget(0xFF000000|Color.HSBtoRGB(hue/360f,saturation/100f,brightness/100f));c.theme="Custom";c.save();select(target);}
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float dt){
        super.extractRenderState(g,mx,my,dt);
        int x=width/2-145,y=height/2-119;
        g.fill(x,y,x+290,y+249,c.panel);g.fill(x,y,x+290,y+4,c.accent);
        g.text(font,"GAMBLEUTILS / APPEARANCE",x+10,y+13,c.text,true);
        g.text(font,"Themes",x+10,y+25,c.text,false);
        g.text(font,"Edit: "+target+"  |  H:"+hue+" S:"+saturation+" B:"+brightness,x+10,y+116,c.text,false);
        g.fill(x+10,y+240,x+267,y+244,c.bar);
        g.text(font,status,x+10,y+235,c.text,false);
    }
}
