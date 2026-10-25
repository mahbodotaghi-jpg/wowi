package com.nitro.core;

import net.minecraft.client.gui.DrawContext;

/** A module that draws a movable HUD element. */
public abstract class HudMod extends Mod {
    public int x, y, w = 60, h = 14;

    protected HudMod(String name, String desc, Category cat, int x, int y) {
        super(name, desc, cat); this.x = x; this.y = y;
    }

    @Override public boolean isHud() { return true; }

    /** Draw at (x,y) and set w/h. */
    public abstract void draw(DrawContext c, float td);

    @Override
    public void onHud(DrawContext c, float td) {
        draw(c, td);
        int sw = c.getScaledWindowWidth(), sh = c.getScaledWindowHeight();
        x = Math.max(0, Math.min(x, Math.max(0, sw - w)));
        y = Math.max(0, Math.min(y, Math.max(0, sh - h)));
    }

    protected void chip(DrawContext c, String s) {
        w = Gfx.tw(s) + 12; h = 16;
        Gfx.glass(c, x, y, w, h);
        Gfx.text(c, s, x + 6, y + 4, Theme.TEXT);
    }
}
