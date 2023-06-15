package org.mashed.islands.generation

import com.mojang.datafixers.util.Either
import com.mojang.logging.LogUtils
import net.minecraft.core.Direction
import net.minecraft.server.level.ChunkHolder
import net.minecraft.server.level.ChunkHolder.ChunkLoadingFailure
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ThreadedLevelLightEngine
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.chunk.ChunkStatus
import net.minecraft.world.level.chunk.ImposterProtoChunk
import net.minecraft.world.level.chunk.ProtoChunk
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager
import org.mashed.islands.generation.island.AppliedGeneratedIsland
import org.mashed.islands.generation.island.GeneratingIsland
import org.mashed.islands.generation.island.IslandStatus
import org.mashed.islands.generation.island.WrappingIsland
import org.mashed.islands.mixin.accessors.ChunkStatusAccessor
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Function

object MyChunkStatuses {
    // Chunk statuses of a ship chunk
    // Rn quite wacky, maybe rework later
    val logger = LogUtils.getLogger()

    val EMPTY = ChunkStatus.EMPTY
    val SHAPE = register("shape", EMPTY, 0, ChunkStatus.ChunkType.PROTOCHUNK, task { level, _, chunk, status ->
        if (chunk is ImposterProtoChunk) return@task CompletableFuture.completedFuture(chunk)
        chunk as IslandProtoChunk

        chunk.island.next?.thenApplyAsync { island ->
            logger.trace("Generating SHAPED Chunk")
            if (island.status == IslandStatus.SHAPED)
                chunk.status = status
            else throw IllegalAccessException("Island not shaped")

            chunk
        } ?: helper(chunk)
    })

    val ITERATED = register("iterated", SHAPE, 0, ChunkStatus.ChunkType.PROTOCHUNK, task { level, _, chunk, status ->
        if (chunk is ImposterProtoChunk) return@task CompletableFuture.completedFuture(chunk)
        chunk as IslandProtoChunk

        if (chunk.island.status != IslandStatus.SHAPED) return@task helper(chunk)

        chunk.island.next?.thenApplyAsync { island ->
            logger.trace("Generating ITERATED Chunk")
            if (island.status == IslandStatus.ITERATED)
                chunk.status = status
            else throw IllegalAccessException("Island not iterated")

            chunk
        } ?: helper(chunk)
    })

    val APPLIED = register("applied", ITERATED, 0, ChunkStatus.ChunkType.PROTOCHUNK, task { level, _, chunk, status ->
        if (chunk is ImposterProtoChunk) return@task CompletableFuture.completedFuture(chunk)
        chunk as IslandProtoChunk

        if (chunk.island.status != IslandStatus.ITERATED) return@task helper(chunk)

        chunk.island.next?.thenApplyAsync { island ->
            logger.trace("Generating APPLIED Chunk")
            if (island.status == IslandStatus.DONE)
                chunk.status = status
            else throw IllegalAccessException("Island not done")

            chunk
        } ?: helper(chunk)
    })

    val FULL_ISLAND = register("full_island", APPLIED, 0, ChunkStatus.ChunkType.LEVELCHUNK, task { level, _, chunk, status ->
        if (chunk is ImposterProtoChunk) return@task CompletableFuture.completedFuture(chunk.wrapped)

        CompletableFuture.supplyAsync {
            logger.trace("Generating FULL_ISLAND Chunk")
            var island = (chunk as IslandProtoChunk).island
            if (island.status != IslandStatus.DONE) {
                throw IllegalAccessException("Island not done")
            }

            if (island is WrappingIsland) {
                island = island.island!!
            }

            if (island is AppliedGeneratedIsland) {
                island.builder
            } else throw IllegalAccessException("Island not AppliedGeneratedIsland")
        }.thenComposeAsync {
            it.makeChunk(chunk as ProtoChunk)
        }
    })


    private fun register(name: String,
                         parent: ChunkStatus?,
                         taskRange: Int,
                         type: ChunkStatus.ChunkType,
                         task: ChunkStatus.GenerationTask
    ) = ChunkStatusAccessor.register(name, parent, taskRange, EnumSet.noneOf(Heightmap.Types::class.java), type, task)

    private fun task(task: (ServerLevel, ChunkGenerator, ChunkAccess, ChunkStatus) -> CompletableFuture<ChunkAccess>): ChunkStatus.GenerationTask =
        ChunkStatus.GenerationTask { chunkStatus,
                                     executor,
                                     serverLevel,
                                     chunkGenerator,
                                     structureManager,
                                     threadedLevelLightEngine,
                                     function,
                                     chunkAccesses,
                                     chunkAccess, b ->
            val completableFuture = task(serverLevel, chunkGenerator, chunkAccess, chunkStatus)
            completableFuture.handleAsync { chunk, throwable ->
                if (throwable is ChunkLoadingFailure) {
                    Either.right(throwable)
                } else if (throwable != null) {
                    throw throwable
                } else {
                    Either.left(chunk)
                }
            }
        }

    // Exists for debugging reasons
    private fun helper(chunk: IslandProtoChunk): CompletableFuture<ChunkAccess> =
        CompletableFuture.supplyAsync { chunk.status = APPLIED; chunk }

    fun register() {
        // cinit
    }
}