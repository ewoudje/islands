package org.mashed.islands.generation.island

import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector2f
import org.mashed.islands.generation.IslandBuilder
import org.mashed.islands.generation.layer.CircleLayer
import org.mashed.islands.generation.layer.HeightMapLayer
import org.mashed.islands.generation.layer.PerlinLayer
import org.mashed.islands.generation.layer.erode

abstract class DoubleLayerIsland(override val sizeRange: IntRange) : IslandType {

    override fun generateShape(state: IslandState): IslandShape {
        val surfaceLayer = makeIslandSurfaceLayer(state)
        val rockLayer = makeIslandRockLayer(state)
        val shape = IslandShape()

        sample(state, shape, surfaceLayer)
        sample(state, shape, rockLayer) { -it - 1 }

        return shape
    }

    override fun iterateShape(state: IslandState, shape: IslandShape) {
        //NOTHING
    }

    override fun applyShape(state: IslandState, shape: IslandShape, builder: IslandBuilder) {
        shape.forEach { x, y, z -> builder.setBlock(x,y,z, Blocks.STONE.defaultBlockState()) }
        shape.forEachSurface { x, y, z ->
            builder.setBlock(x, y, z, Blocks.GRASS_BLOCK.defaultBlockState())

            repeat(2) {
                val nY = y - it - 1
                if (shape.contains(x, nY -1, z))
                    builder.setBlock(x, nY, z, Blocks.DIRT.defaultBlockState())
            }
        }
    }

    protected fun sample(state: IslandState, shape: IslandShape, layer: HeightMapLayer, yMan: (Int) -> Int = { it }) {
        val hsize = state.size / 2f
        val isize = state.size.toInt()
        val half = isize / 2

        repeat(isize) { x_ ->
            repeat(isize) { z_ ->
                val x = x_ - half
                val z = z_ - half

                val height = (layer.getHeight(x / hsize, z / hsize)).toInt()

                repeat(height) {
                    shape.add(x, yMan(it), z)
                }
            }
        }
    }


    abstract fun makeIslandSurfaceLayer(island: IslandState): HeightMapLayer
    abstract fun makeIslandRockLayer(island: IslandState): HeightMapLayer
}