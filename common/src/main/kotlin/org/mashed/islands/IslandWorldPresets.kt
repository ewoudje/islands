package org.mashed.islands

import net.minecraft.client.gui.screens.worldselection.WorldPreset
import net.minecraft.core.Registry
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.FixedBiomeSource
import net.minecraft.world.level.levelgen.FlatLevelSource
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings
import org.mashed.islands.level.VoidLevelSource
import org.mashed.lasagna.api.registry.DeferredRegister
import org.mashed.lasagna.api.registry.SpecialRegistries
import org.mashed.lasagna.createWorldPreset
import org.valkyrienskies.eureka.IslandMod
import java.util.*

object IslandWorldPresets {
    private val PRESETS = DeferredRegister.create(IslandMod.MOD_ID, SpecialRegistries.WORLD_PRESETS)

    val VOID_ISLANDS = register("void_islands", createWorldPreset("void_islands") { registry, seed ->
        val biomes = registry.registryOrThrow(Registry.BIOME_REGISTRY)
        val structures = registry.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY)

        VoidLevelSource(structures, FixedBiomeSource(biomes.getHolderOrThrow(Biomes.PLAINS)), seed)
    })

    private fun register(name: String, preset: WorldPreset): WorldPreset {
        PRESETS.register(name) { preset }
        return preset
    }

    fun register() {
        PRESETS.applyAll()
    }
}