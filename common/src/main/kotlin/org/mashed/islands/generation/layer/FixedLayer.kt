package org.mashed.islands.generation.layer

class FixedLayer(val height: Float) : HeightMapLayer {
    override fun getHeight(x: Float, z: Float): Float = height
}