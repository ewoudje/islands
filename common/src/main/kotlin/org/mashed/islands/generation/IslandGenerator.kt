package org.mashed.islands.generation

import com.mojang.logging.LogUtils
import net.minecraft.Util
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.LevelChunkSection
import net.minecraft.world.level.levelgen.LegacyRandomSource
import net.minecraft.world.level.levelgen.RandomSupport
import net.minecraft.world.level.levelgen.WorldgenRandom
import org.joml.Math.lerp
import org.joml.Vector2f
import org.joml.Vector3i
import org.mashed.islands.generation.island.IslandState
import org.mashed.islands.generation.island.IslandType
import org.mashed.islands.generation.island.PlainIsland
import org.mashed.islands.generation.layer.CircleLayer
import org.mashed.islands.generation.layer.FixedLayer
import org.mashed.islands.generation.layer.HeightMapLayer
import org.mashed.islands.generation.layer.PerlinLayer
import org.slf4j.event.Level
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.mod.common.BlockStateInfo
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.GameTickForceApplier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executor

class IslandGenerator(val seed: Long) {
    val random = WorldgenRandom(LegacyRandomSource(seed))

    fun makeIsland(region: WorldGenRegion, x: Int, y: Int, z: Int, exectuor: Executor) {
        val level = region.level
        exectuor.execute {
            try {



                /*
                CompletableFuture.completedFuture(IslandBuilder(level, random, shipChunkX, shipChunkZ)).thenApplyAsync({
                    Util.wrapThreadWithTaskName("wgen_make_island") {
                        try {
                            generateIsland(it)
                        } catch (t: Throwable) {
                            logger.error("Failed to generate island", t)
                        }
                    }; it
                }, Util.backgroundExecutor()).join().clean(level)*/

                val builder = startIsland(level, Vector3i(x, y, z))


                val type = PlainIsland
                val size = lerp(type.sizeRange.first.toFloat(), type.sizeRange.last.toFloat(), random.nextFloat())
                val island = IslandState(size, random.nextInt(), type)

                generateIsland(builder, island)
                builder.clean()

            } catch (t: Throwable) {
                logger.error("Failed to prepare island", t)
            }
        }
    }

    fun startIsland(level: ServerLevel, at: Vector3i): IslandBuilder {
        val ship = level.shipObjectWorld.createNewShipAtBlock(
            at,
            false,
            random.nextDouble(0.97, 1.08),
            level.dimensionId
        )

        val shipChunkX = ship.chunkClaim.xMiddle
        val shipChunkZ = ship.chunkClaim.zMiddle

        val builder = IslandBuilder(level, random, shipChunkX, shipChunkZ)

        (ship.getAttachment<GameTickForceApplier>()
            ?: GameTickForceApplier().apply { ship.saveAttachment(this) }).setStatic(true)

        return builder
    }

    fun generateIsland(islandBuilder: IslandBuilder, island: IslandState) {
        buildSurface(islandBuilder, island)
        buildRock(islandBuilder, island)
    }

    private fun buildSurface(islandBuilder: IslandBuilder, island: IslandState) {
        val layer = island.surfaceLayer
        val hsize = island.size / 2f
        val isize = island.size.toInt()
        val half = isize / 2

        repeat(isize) { x_ ->
            repeat(isize) { z_ ->
                val x = x_ - half
                val z = z_ - half

                val fHeight = layer.getHeight(x / hsize, z / hsize)
                if (fHeight > 0.1f) {
                    val height = (fHeight * island.topThickness).toInt() + 1

                    if (fHeight < 0.2f) {
                        generateStone(islandBuilder, x, z, height)
                    } else repeat(height) { y ->
                        if (height - 1 == y)
                            generateSurfaceGrass(islandBuilder, x, y, z)
                        else
                            generateSemiDeep(islandBuilder, x, y, z)
                    }
                }
            }
        }
    }

    private fun buildRock(islandBuilder: IslandBuilder, island: IslandState) {
        val layer = island.rockLayer
        val hsize = island.size / 2f
        val isize = island.size.toInt()
        val half = isize / 2

        repeat(isize) { x_ ->
            repeat(isize) { z_ ->
                val x = x_ - half
                val z = z_ - half

                val height = (layer.getHeight(x / hsize, z / hsize) * 6).toInt()

                repeat(height) { y ->
                    generateStone(islandBuilder, x, -y - 1, z)
                }
            }
        }
    }

    private fun generateSurfaceGrass(islandBuilder: IslandBuilder, x: Int, y: Int, z: Int) {
        val state = Blocks.GRASS_BLOCK.defaultBlockState()

        islandBuilder.setBlock(x, y, z, state)
    }

    private fun generateSemiDeep(islandBuilder: IslandBuilder, x: Int, y: Int, z: Int) {
        val state = Blocks.DIRT.defaultBlockState()

        islandBuilder.setBlock(x, y, z, state)
    }

    private fun generateStone(islandBuilder: IslandBuilder, x: Int, y: Int, z: Int) {
        val state = Blocks.STONE.defaultBlockState()

        islandBuilder.setBlock(x, y, z, state)
    }

    companion object {
        private val logger = LogUtils.getLogger()
    }
}