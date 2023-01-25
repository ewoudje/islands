package org.mashed.islands.level

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.NoiseColumn
import net.minecraft.world.level.StructureFeatureManager
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.BiomeSource
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.blending.Blender
import net.minecraft.world.level.levelgen.structure.StructureSet
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class VoidLevelSource(structureSets: Registry<StructureSet>, biomeSource: BiomeSource, val seed: Long) :
    ChunkGenerator(structureSets, Optional.empty(), biomeSource) {

    override fun codec(): Codec<out ChunkGenerator> = CODEC

    override fun withSeed(seed: Long): ChunkGenerator = VoidLevelSource(this.structureSets, biomeSource, seed)

    override fun climateSampler(): Climate.Sampler = Climate.empty();

    override fun applyCarvers(
        level: WorldGenRegion,
        seed: Long,
        biomeManager: BiomeManager,
        structureFeatureManager: StructureFeatureManager,
        chunk: ChunkAccess,
        step: GenerationStep.Carving
    ) {}

    override fun buildSurface(
        level: WorldGenRegion,
        structureFeatureManager: StructureFeatureManager,
        chunk: ChunkAccess
    ) {}

    override fun spawnOriginalMobs(level: WorldGenRegion) {}

    override fun getGenDepth(): Int = minY

    override fun fillFromNoise(
        executor: Executor,
        blender: Blender,
        structureFeatureManager: StructureFeatureManager,
        chunk: ChunkAccess
    ): CompletableFuture<ChunkAccess> = CompletableFuture.completedFuture(chunk)

    override fun getSeaLevel(): Int = minY

    override fun getMinY(): Int = -64

    override fun getBaseHeight(x: Int, z: Int, type: Heightmap.Types, level: LevelHeightAccessor): Int = -128
    override fun getBaseColumn(x: Int, z: Int, level: LevelHeightAccessor): NoiseColumn =
        NoiseColumn(0, arrayOfNulls(0));

    override fun addDebugScreenInfo(info: MutableList<String>, pos: BlockPos) {}

    companion object {
        val CODEC: Codec<VoidLevelSource> =
            RecordCodecBuilder.create { instance ->
                commonCodec(instance)
                    .and(
                        instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter { it.biomeSource },
                            Codec.LONG.fieldOf("seed").forGetter { it.seed }
                        )
                    ).apply(instance, ::VoidLevelSource)
            }
    }
}