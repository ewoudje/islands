package org.mashed.islands.generation.island

import org.joml.Vector2f
import org.mashed.islands.generation.layer.CircleLayer
import org.mashed.islands.generation.layer.FixedLayer
import org.mashed.islands.generation.layer.HeightMapLayer
import org.mashed.islands.generation.layer.PerlinLayer
import org.mashed.islands.generation.layer.erode

object PlainIsland : IslandType {
    override val sizeRange: IntRange = 5..50
    override fun makeIslandSurfaceLayer(island: IslandState): HeightMapLayer =
        PerlinLayer(island.seed, 4f, 2)
            .mask(CircleLayer(0.95f, 0.5f, 1f, Vector2f(0f, 0f)))
            .erode(1, 0.01f)

    override fun makeIslandRockLayer(island: IslandState): HeightMapLayer =
        makeIslandSurfaceLayer(island).mask(FixedLayer(island.size * 0.6f))
            .mask(PerlinLayer(island.seed, 3f, 4))
            .erode(1, 0.02f)

    override fun getTopThickness(island: IslandState): Int =
        (island.size * 0.2f).toInt() + 1
}