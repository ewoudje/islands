package org.mashed.islands.generation

import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap
import net.minecraft.core.Registry
import net.minecraft.server.level.ChunkMap
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.LevelChunkSection
import net.minecraft.world.level.chunk.ProtoChunk
import net.minecraft.world.level.levelgen.WorldgenRandom
import org.mashed.islands.generation.island.GeneratingIsland
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.common.BlockStateInfo
import java.util.concurrent.CompletableFuture

class IslandBuilder(val level: ServerLevel, val random: WorldgenRandom, val chunkX: Int, val chunkZ: Int, val ship: ServerShip) {
    private val cache = Short2ObjectArrayMap<LevelChunkSection>()
    private val toBeUpdated = DenseBlockPosSet()

    private fun intoShort(chunkX: Int, sectionIndex: Int, chunkZ: Int): Short = (
                (chunkX and 0b11111) shl 10 or
                (sectionIndex and 0b11111) shl 5 or
                (chunkZ and 0b11111)
        ).toShort()

    fun getSection(x: Int, y: Int, z: Int) : LevelChunkSection {
        val sectionIndex = level.getSectionIndex(y + 128)
        return cache.getOrPut(intoShort(x shr 4, sectionIndex, z shr 4)) {
            val section = LevelChunkSection(
                level.getSectionYFromSectionIndex(sectionIndex),
                level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
            )
            section.acquire()
            section
        }
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

    fun makeChunk(protoChunk: ProtoChunk): CompletableFuture<ChunkAccess> =
        CompletableFuture.supplyAsync {
            val protoX = protoChunk.pos.x - chunkX + 0b1111
            val protoZ = protoChunk.pos.z - chunkZ + 0b1111

            if (protoX < 0 || protoX > 0b11111 || protoZ < 0 || protoZ > 0b11111)
                return@supplyAsync LevelChunk(level, protoChunk) {  }

            repeat(protoChunk.sectionsCount) { i ->
                val result = cache.get(intoShort(protoX, i, protoZ))
                if (result != null)
                    protoChunk.sections[i] = result
            }

            LevelChunk(level, protoChunk) { chunk ->
                //ChunkMap.postLoadProtoChunk(level, protoChunk.entities)
            }
        }

    fun finish() {
        toBeUpdated.forEach { x, y, z ->
            BlockStateInfo.onSetBlock(level,
                (x + (chunkX shl 4)), y, (z + (chunkZ shl 4)),
                Blocks.AIR.defaultBlockState(),
                getBlock(x, y, z))
        }

        cache.values.forEach { it.release() }

        ship.saveAttachment<GeneratingIsland>(null) // TODO this makes it impossible to partially generate islands
    }

    fun clean() {
        toBeUpdated.clear()
        cache.clear()
    }
}
