package org.mashed.islands.generation.island

import org.joml.Vector2f
import org.mashed.islands.generation.layer.CircleLayer
import org.mashed.islands.generation.layer.FixedLayer
import org.mashed.islands.generation.layer.HeightMapLayer
import org.mashed.islands.generation.layer.PerlinLayer
import org.mashed.islands.generation.layer.cutoff
import org.mashed.islands.generation.layer.erode

object TestIsland : DoubleLayerIsland( 5..50) {
    override fun makeIslandSurfaceLayer(island: IslandState): HeightMapLayer =
        PerlinLayer(island.seed, 1.5f, 2)
            .mask(FixedLayer(2f))
            .mask(CircleLayer(0.95f, 0.5f, 1f, Vector2f(0f, 0f)))
            .cutoff(0.7f)
            //.erode(2, 0.01f)
            .mask(FixedLayer(island.size * 0.2f))



    override fun makeIslandRockLayer(island: IslandState): HeightMapLayer =
        makeIslandSurfaceLayer(island)
            .mask(FixedLayer(1.5f + island.size * 0.3f))
            .mask(CircleLayer(0.9f, 0.1f, 1f, Vector2f(0f, 0f)))
            .mask(PerlinLayer(island.seed xor 0xDEADBEEF.toInt(), 8f, 4))
            .erode(1, 0.02f)
}