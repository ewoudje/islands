package org.mashed.islands

import net.fabricmc.api.Environment
import net.minecraft.resources.ResourceLocation
import org.mashed.islands.generation.MyChunkStatuses
import org.mashed.lasagna.LasagnaMod
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
        MyChunkStatuses.register()
        VSConfigClass.registerConfig("vs_islands", IslandConfig::class.java)

        RegistryEvents.onServerCommandRegister.register {
            IslandCommands.register(it)
        }
    }

    @Environment(net.fabricmc.api.EnvType.CLIENT)
    @JvmStatic
    fun initClient() {

    }

    internal val String.resource: ResourceLocation get() = ResourceLocation(LasagnaMod.MOD_ID, this)
}
