package org.mashed.islands.generation.layer

import org.mashed.islands.Noise

class PerlinLayer(seed: Int, frequency: Float, octaves: Int) : HeightMapLayer {
    val noise = Noise(seed, frequency, Noise.PERLIN_FRACTAL, octaves)
    override fun getHeight(x: Float, z: Float): Float =
        noise.getPerlinFractal(x, z) * 0.5f + 0.5f

}