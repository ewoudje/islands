package org.mashed.islands.generation

import net.minecraft.core.Registry
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.ProtoChunk
import net.minecraft.world.level.chunk.UpgradeData
import net.minecraft.world.level.levelgen.blending.BlendingData
import org.mashed.islands.generation.island.GeneratingIsland
import org.mashed.islands.generation.island.IslandShape

class IslandProtoChunk(
    chunkPos: ChunkPos,
    upgradeData: UpgradeData,
    lhAccessor: LevelHeightAccessor,
    biomes: Registry<Biome>,
    blendingData: BlendingData?,
    val island: GeneratingIsland
) : ProtoChunk(chunkPos, upgradeData, lhAccessor, biomes, blendingData)