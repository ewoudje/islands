package org.mashed.islands

import net.minecraft.client.gui.screens.worldselection.WorldPreset
import net.minecraft.client.renderer.DimensionSpecialEffects
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.FixedBiomeSource
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.levelgen.WorldGenSettings
import org.mashed.islands.IslandMod.resource
import org.mashed.islands.level.VoidLevelSource
import org.mashed.islands.level.VoidSpecialEffects
import org.mashed.lasagna.api.registry.DeferredRegister
import org.mashed.lasagna.api.registry.RegistrySupplier
import org.mashed.lasagna.api.registry.SpecialClientRegistries
import org.mashed.lasagna.createWorldPreset

object IslandWorldPresets {
    private val PRESETS = DeferredRegister(IslandMod.MOD_ID, SpecialClientRegistries.WORLD_PRESETS)
    private val DIMENSION_EFFECTS = DeferredRegister(IslandMod.MOD_ID, SpecialClientRegistries.DIMENSION_SPECIAL_EFFECTS)

    val VOID_DIMENSION_EFFECTS = dimensionEffects("void_overworld") { VoidSpecialEffects }

    val VOID_DIMENSION_TYPE = dimensionType("void_overworld")

    val VOID_ISLANDS = preset("void_islands") { createWorldPreset("void_islands".resource, { registry, seed ->
        val biomes = registry.registryOrThrow(Registry.BIOME_REGISTRY)
        val structures = registry.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY)

        VoidLevelSource(structures, FixedBiomeSource(biomes.getHolderOrThrow(Biomes.THE_VOID)), seed)
    }, { registries, seed, generateFeatures, generateBonusChest, generator ->
        WorldGenSettings(seed, generateFeatures, generateBonusChest, WorldGenSettings.withOverworld(
            DimensionType.defaultDimensions(registries, seed),
            registries
                .registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY)
                .getHolderOrThrow(VOID_DIMENSION_TYPE),
            generator
        ))
    }) }

    private fun preset(name: String, preset: () -> WorldPreset): RegistrySupplier<WorldPreset> =
        PRESETS.register(name, preset)

    private fun dimensionType(name: String): ResourceKey<DimensionType> =
        ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, name.resource)

    private fun dimensionEffects(name: String, effects: () -> DimensionSpecialEffects): RegistrySupplier<DimensionSpecialEffects> =
        DIMENSION_EFFECTS.register(name, effects)

    fun register() {
        PRESETS.applyAll()
        DIMENSION_EFFECTS.applyAll()
    }
}