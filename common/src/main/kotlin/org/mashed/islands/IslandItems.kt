package org.mashed.islands
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.mashed.lasagna.api.registry.CreativeTabs
import org.mashed.lasagna.api.registry.DeferredRegister

@Suppress("unused")
object IslandItems {
    private val ITEMS = DeferredRegister(IslandMod.MOD_ID, Registry.ITEM_REGISTRY)
    val TAB: CreativeModeTab = CreativeTabs.create(
        ResourceLocation(
            IslandMod.MOD_ID,
            "island_mod"
        )
    ) { ItemStack(IslandBlocks.EWOUDJIUM.get()) }

    fun register() {
        IslandBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}
