package org.mashed.islands.generation.island

import org.mashed.islands.generation.IslandBuilder
import org.mashed.lasagna.api.registry.RegistryItem

interface IslandType: RegistryItem<IslandType> {
    val sizeRange: IntRange

    fun generateShape(island: GeneratingIsland): IslandShape
    fun iterateShape(island: GeneratingIsland, shape: IslandShape)
    fun applyShape(island: GeneratingIsland, shape: IslandShape, stateSetter: IslandBuilder)
}