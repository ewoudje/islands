package org.mashed.islands

import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.world.level.chunk.ChunkGenerator
import org.mashed.islands.level.VoidLevelSource
import org.mashed.lasagna.api.registry.DeferredRegister
import org.valkyrienskies.eureka.IslandMod

object IslandChunkGenerators {
    private val GENERATORS = DeferredRegister.create(IslandMod.MOD_ID, Registry.CHUNK_GENERATOR_REGISTRY)

    val VOID_WORLD = register("void_islands", VoidLevelSource.CODEC)
    private fun register(name: String, codec: Codec<out ChunkGenerator>) =
        GENERATORS.register(name) { codec }

    fun register() {
        GENERATORS.applyAll()
    }
}