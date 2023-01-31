package org.mashed.islands.generation.layer

interface HeightMapLayer {

    fun getHeight(x: Float, z: Float): Float

    fun mask(layer: HeightMapLayer): HeightMapLayer = MaskedHeightMapLayer(this, layer)
}

private class MaskedHeightMapLayer(val layer1: HeightMapLayer, val layer2: HeightMapLayer) : HeightMapLayer {
    override fun getHeight(x: Float, z: Float): Float = layer1.getHeight(x, z) * layer2.getHeight(x, z)
}