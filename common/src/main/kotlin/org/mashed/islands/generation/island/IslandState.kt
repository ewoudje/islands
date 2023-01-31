package org.mashed.islands.generation.island

import org.mashed.islands.generation.layer.HeightMapLayer

data class IslandState(
    val size: Float,
    val seed: Int,
    val type: IslandType
) {
    val surfaceLayer: HeightMapLayer = type.makeIslandSurfaceLayer(this)
    val rockLayer: HeightMapLayer = type.makeIslandRockLayer(this)
    val topThickness: Int = type.getTopThickness(this)
}
