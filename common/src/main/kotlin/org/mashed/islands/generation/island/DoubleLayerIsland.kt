package org.mashed.islands.generation.island

import net.minecraft.world.level.block.Blocks
import org.mashed.islands.generation.IslandBuilder
import org.mashed.islands.generation.layer.HeightMapLayer

abstract class DoubleLayerIsland(override val sizeRange: IntRange) : IslandType {

    override fun generateShape(island: GeneratingIsland): IslandShape {
        val surfaceLayer = makeIslandSurfaceLayer(island)
        val rockLayer = makeIslandRockLayer(island)
        val shape = IslandShape()

        sample(island, shape, surfaceLayer)
        sample(island, shape, rockLayer) { -it - 1 }

        return shape
    }

    override fun iterateShape(island: GeneratingIsland, shape: IslandShape) {
        //NOTHING
    }

    override fun applyShape(island: GeneratingIsland, shape: IslandShape, builder: IslandBuilder) {
        shape.forEach { x, y, z -> builder.setBlock(x,y,z, Blocks.STONE.defaultBlockState()) }
        shape.forEachSurface { x, y, z ->
            builder.setBlock(x, y, z, Blocks.GRASS_BLOCK.defaultBlockState())

            repeat(2) {
                val nY = y - it - 1
                if (shape.contains(x, nY -1, z))
                    builder.setBlock(x, nY, z, Blocks.DIRT.defaultBlockState())
            }

            val grassy = builder.random.nextFloat()
            if (grassy < 0.3f) {
                if (grassy < 0.05f)
                    builder.setBlock(x, y + 1, z, Blocks.POPPY.defaultBlockState())
                else
                    builder.setBlock(x, y + 1, z, Blocks.GRASS.defaultBlockState())
            }
        }
    }

    protected fun sample(state: GeneratingIsland, shape: IslandShape, layer: HeightMapLayer, yMan: (Int) -> Int = { it }) {
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


    abstract fun makeIslandSurfaceLayer(island: GeneratingIsland): HeightMapLayer
    abstract fun makeIslandRockLayer(island: GeneratingIsland): HeightMapLayer
}