package org.mashed.islands.generation

import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.LevelChunkSection
import net.minecraft.world.level.levelgen.WorldgenRandom
import org.valkyrienskies.core.impl.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.common.BlockStateInfo

class IslandBuilder(val level: ServerLevel, val random: WorldgenRandom, val chunkX: Int, val chunkZ: Int) {
    private val cache = Short2ObjectArrayMap<LevelChunkSection>()
    private val toBeUpdated = DenseBlockPosSet()

    private fun intoShort(x: Int, y: Int, z: Int): Short = (
                ((x shr 4) and 0b11111) shl 10 or
                (level.getSectionIndex(y) and 0b11111) shl 5 or
                ((z shr 4) and 0b11111)
        ).toShort()

    fun getSection(x: Int, y: Int, z: Int) : LevelChunkSection = cache.getOrPut(intoShort(x, y, z)) {
        val chunk = level.getChunk((x shr 4) + chunkX, (z shr 4) + chunkZ)
        val section = chunk.getSection(level.getSectionIndex(y + 128))
        section.acquire()
        section
    }

    fun setBlock(x: Int, y: Int, z: Int, state: BlockState) {
        val section = getSection(x, y, z)
        toBeUpdated.add(x, y, z)
        section.setBlockState(x and 0b1111, y and 0b1111, z and 0b1111, state, false)
    }

    fun getBlock(x: Int, y: Int, z: Int) : BlockState {
        val section = getSection(x, y, z)
        return section.getBlockState(x and 0b1111, y and 0b1111, z and 0b1111)
    }

    fun clean() {
        toBeUpdated.forEach { x, y, z ->
            BlockStateInfo.onSetBlock(level,
                (x + (chunkX shl 4)), y, (z + (chunkZ shl 4)),
                Blocks.AIR.defaultBlockState(),
                getBlock(x, y, z))
        }

        toBeUpdated.clear()

        cache.values.forEach { it.release() }
        cache.clear()
    }
}
