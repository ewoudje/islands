package org.mashed.islands.level

import net.minecraft.client.renderer.DimensionSpecialEffects
import net.minecraft.world.phys.Vec3

object VoidSpecialEffects : DimensionSpecialEffects(0f, true, SkyType.NORMAL, false, false) {
    override fun getBrightnessDependentFogColor(fogColor: Vec3, brightness: Float): Vec3 =
        fogColor.multiply(
            (brightness * 0.94f + 0.06f).toDouble(),
            (brightness * 0.94f + 0.06f).toDouble(),
            (brightness * 0.91f + 0.09f).toDouble()
        );

    override fun isFoggyAt(x: Int, y: Int): Boolean = false

}