package org.mashed.islands.generation.layer

import kotlin.random.Random

object RandomLayer : HeightMapLayer {
    override fun getHeight(x: Float, z: Float): Float {
        return Random.nextFloat()
    }
}