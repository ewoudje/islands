package org.mashed.islands.generation.layer

class CutoffLayer(val at: Float, val layer: HeightMapLayer) : HeightMapLayer {
    override fun getHeight(x: Float, z: Float): Float =
        layer.getHeight(x, z).let { if (it < at) 0f else it - at  }
}

// It doesn't rescale it so it does - at everything
fun HeightMapLayer.cutoff(at: Float): HeightMapLayer = CutoffLayer(at, this)