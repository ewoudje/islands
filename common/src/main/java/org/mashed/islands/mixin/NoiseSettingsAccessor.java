package org.mashed.islands.mixin;

import net.minecraft.world.level.levelgen.NoiseSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NoiseSettings.class)
public interface NoiseSettingsAccessor {

    @Accessor("FLOATING_ISLANDS_NOISE_SETTINGS")
    static NoiseSettings getIslandsNoise() {
        throw new AssertionError();
    }

}
