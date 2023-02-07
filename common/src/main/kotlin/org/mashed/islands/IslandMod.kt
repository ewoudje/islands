package org.valkyrienskies.eureka

import net.fabricmc.api.Environment
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.mashed.islands.IslandChunkGenerators
import org.mashed.islands.IslandCommands
import org.mashed.islands.IslandWorldPresets
import org.mashed.islands.generation.island.IslandType
import org.mashed.lasagna.api.events.RegistryEvents
import org.valkyrienskies.core.impl.config.VSConfigClass


object IslandMod {
    const val MOD_ID = "vs_islands"

    @JvmStatic
    fun init() {
        IslandBlocks.register()
        IslandBlockEntities.register()
        IslandItems.register()
        IslandWorldPresets.register()
        IslandChunkGenerators.register()
        VSConfigClass.registerConfig("vs_islands", IslandConfig::class.java)

        RegistryEvents.onServerCommandRegister.register {
            IslandCommands.register(it)
        }
    }

    @Environment(net.fabricmc.api.EnvType.CLIENT)
    @JvmStatic
    fun initClient() {

    }

    fun String.id() = ResourceLocation(MOD_ID, this)
}
