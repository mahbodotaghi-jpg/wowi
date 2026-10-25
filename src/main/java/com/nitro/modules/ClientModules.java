package com.nitro.modules;

import com.nitro.core.*;

/** Client → Interface: language, accent colour, panel opacity. */
final class InterfaceMod extends Mod {
    InterfaceMod() {
        super("Interface", "Menu look, accent colour and language", Category.CLIENT);
        add(Theme.ACCENT); add(Theme.OPACITY); add(Lang.MODE);
        enabled = true;
    }
    @Override public boolean persistEnabled() { return false; }
}
