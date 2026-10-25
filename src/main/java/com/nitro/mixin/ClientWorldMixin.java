package com.nitro.mixin;

import com.nitro.core.State;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Recolours the sky for the Skies module. Handler takes no target arguments and require=0 so a
 * changed signature can never crash the game - worst case the sky just stays vanilla.
 */
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true, require = 0)
    private void nitro$sky(CallbackInfoReturnable<Object> cir) {
        int custom = State.skyRgb;
        if (custom < 0) return;
        Object v = cir.getReturnValue();
        if (v instanceof Integer i) {
            cir.setReturnValue((i & 0xFF000000) | blend(i & 0xFFFFFF, custom));
        } else if (v instanceof Vec3d d) {
            int rgb = blend(((int) (d.x * 255) << 16) | ((int) (d.y * 255) << 8) | (int) (d.z * 255), custom);
            cir.setReturnValue(new Vec3d(((rgb >> 16) & 255) / 255.0, ((rgb >> 8) & 255) / 255.0, (rgb & 255) / 255.0));
        }
    }

    /** Keep vanilla's day/night brightness, swap in the palette colour. */
    private static int blend(int vanilla, int custom) {
        int vr = (vanilla >> 16) & 255, vg = (vanilla >> 8) & 255, vb = vanilla & 255;
        double lum = (0.3 * vr + 0.59 * vg + 0.11 * vb) / 255.0;
        State.skyLum = lum;
        double f = Math.max(0.04, Math.min(1.0, lum * 1.7));
        double s = State.skyStrength;
        int r = (int) (vr * (1 - s) + ((custom >> 16) & 255) * f * s);
        int g = (int) (vg * (1 - s) + ((custom >> 8) & 255) * f * s);
        int b = (int) (vb * (1 - s) + (custom & 255) * f * s);
        return (Math.min(255, r) << 16) | (Math.min(255, g) << 8) | Math.min(255, b);
    }
}
