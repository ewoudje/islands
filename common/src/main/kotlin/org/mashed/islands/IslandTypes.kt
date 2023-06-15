package org.mashed.islands

import net.minecraft.resources.ResourceKey
import org.mashed.islands.IslandMod.resource
import org.mashed.islands.generation.island.IslandType
import org.mashed.islands.generation.island.PlainIsland
import org.mashed.islands.generation.island.TestIsland
import org.mashed.lasagna.api.registry.createUserRegistry

object IslandTypes {

    val ISLAND_TYPE_REGISTRY = ResourceKey.createRegistryKey<IslandType>("island_type".resource)
    val ISLAND_TYPES = createUserRegistry(ISLAND_TYPE_REGISTRY)

    val PLAIN_ISLAND = register("plain", PlainIsland)
    val TEST_ISLAND = register("test", TestIsland)

    fun register(name: String, islandType: IslandType) =
        ISLAND_TYPES.register(name.resource) { islandType }

    fun register() {
        ISLAND_TYPES.applyAll()
    }

}