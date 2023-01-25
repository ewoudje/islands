package org.valkyrienskies.eureka

import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import org.mashed.lasagna.api.registry.DeferredRegister

@Suppress("unused")
object IslandBlocks {
    private val BLOCKS = DeferredRegister.create(IslandMod.MOD_ID, Registry.BLOCK_REGISTRY)

    val EWOUDJIUM = BLOCKS.register("ewoudjium") { Block(BlockBehaviour.Properties.of(Material.AMETHYST)) }

    // Blocks should also be registered as items, if you want them to be able to be held
    // aka all blocks
    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.forEach {
            items.register(it.name) { BlockItem(it.get(), Item.Properties().tab(IslandItems.TAB)) }
        }
    }

    fun register() {
        BLOCKS.applyAll()
    }
}
