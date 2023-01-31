package org.mashed.islands.generation.island

import org.mashed.islands.generation.layer.HeightMapLayer

interface IslandType {
    val sizeRange: IntRange
    fun makeIslandSurfaceLayer(island: IslandState): HeightMapLayer
    fun makeIslandRockLayer(island: IslandState): HeightMapLayer
    fun getTopThickness(island: IslandState): Int = 4
}