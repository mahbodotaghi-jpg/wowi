package com.nitro.gui;

import com.nitro.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Drag HUD elements around. Positions are saved to the config. */
public final class HudEditor extends Screen {
    public static boolean editing;
    private HudMod drag; private int ox, oy; private boolean prevDown = true;

    public HudEditor() { super(Text.literal("HUD Editor")); }

    @Override public boolean shouldPause() { return false; }

    @Override
    protected void init() { editing = true; }

    @Override
    public void render(DrawContext g, int mx, int my, float delta) {
        long win = this.client.getWindow().getHandle();
        boolean down = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean pressed = down && !prevDown; prevDown = down;

        g.fill(0, 0, width, height, 0x30000000);
        g.fill(width / 2, 0, width / 2 + 1, height, 0x18FFFFFF);
        g.fill(0, height / 2, width, height / 2 + 1, 0x18FFFFFF);

        HudMod hovered = null;
        for (Mod m : ModuleManager.ALL) {
            if (!(m instanceof HudMod h) || !h.enabled) continue;
            if (mx >= h.x && mx < h.x + h.w && my >= h.y && my < h.y + h.h) hovered = h;
        }
        if (pressed && hovered != null) { drag = hovered; ox = mx - hovered.x; oy = my - hovered.y; }
        if (!down) drag = null;
        if (drag != null) {
            int nx = mx - ox, ny = my - oy;
            if (Math.abs(nx) < 6) nx = 0;
            if (Math.abs(nx + drag.w - width) < 6) nx = width - drag.w;
            if (Math.abs(nx + drag.w / 2 - width / 2) < 6) nx = width / 2 - drag.w / 2;
            if (Math.abs(ny) < 6) ny = 0;
            if (Math.abs(ny + drag.h - height) < 6) ny = height - drag.h;
            if (Math.abs(ny + drag.h / 2 - height / 2) < 6) ny = height / 2 - drag.h / 2;
            drag.x = Math.max(0, Math.min(width - drag.w, nx));
            drag.y = Math.max(0, Math.min(height - drag.h, ny));
        }

        for (Mod m : ModuleManager.ALL) {
            if (!(m instanceof HudMod h) || !h.enabled) continue;
            boolean act = h == drag || h == hovered;
            Gfx.outline(g, h.x - 2, h.y - 2, h.x + h.w + 2, h.y + h.h + 2, act ? Theme.accent() : Theme.accentA(0x80));
            Gfx.text(g, h.name, h.x, h.y - 12 < 0 ? h.y + h.h + 4 : h.y - 12, act ? Theme.TEXT : Theme.DIM);
        }
        String t = Lang.t("Drag HUD elements. Esc to go back.");
        Gfx.text(g, t, (width - Gfx.tw(t)) / 2, height - 14, Theme.TEXT);
    }

    @Override
    public void close() {
        ModuleManager.save();
        this.client.setScreen(new ClickGui());
    }

    @Override
    public void removed() { editing = false; ModuleManager.save(); }
}
