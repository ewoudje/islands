package org.mashed.islands.generation

import com.mojang.logging.LogUtils
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.LegacyRandomSource
import net.minecraft.world.level.levelgen.WorldgenRandom
import org.joml.Math.lerp
import org.joml.Vector3i
import org.mashed.islands.generation.island.EmptyGeneratedIsland
import org.mashed.islands.generation.island.GeneratingIsland
import org.mashed.islands.generation.island.IslandType
import org.mashed.islands.generation.island.PlainIsland
import org.mashed.islands.generation.island.WrappingIsland
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.util.x
import org.valkyrienskies.core.impl.util.y
import org.valkyrienskies.core.impl.util.z
import org.valkyrienskies.mod.common.BlockStateInfo
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.GameTickForceApplier
import org.valkyrienskies.mod.common.util.ShipSettings
import java.util.concurrent.Executor

class IslandGenerator(val seed: Long) {
    val random = WorldgenRandom(LegacyRandomSource(seed))

    fun makeIsland(region: WorldGenRegion, x: Int, y: Int, z: Int, exectuor: Executor) {
        val level = region.level
        exectuor.execute {
            try {
                val type = PlainIsland
                val size = lerp(type.sizeRange.first.toFloat(), type.sizeRange.last.toFloat(), random.nextFloat())
                makeIsland(level, Vector3i(x, y, z), type, size)
            } catch (t: Throwable) {
                logger.error("Failed to prepare island", t)
            }
        }
    }

    fun makeIsland(level: ServerLevel, at: Vector3i, type: IslandType, size: Float): ServerShip {
        val ship = level.shipObjectWorld.createNewShipAtBlock(
            at,
            false,
            random.nextDouble(0.97, 1.08),
            level.dimensionId
        )

        val shipChunkX = ship.chunkClaim.xMiddle
        val shipChunkZ = ship.chunkClaim.zMiddle

        val builder = IslandBuilder(level, random, shipChunkX, shipChunkZ, ship)

        (ship.getAttachment<GameTickForceApplier>()
            ?: GameTickForceApplier().apply { ship.saveAttachment(this) }).setStatic(true)

        val owner = WrappingIsland(null)
        owner.island = EmptyGeneratedIsland(size, random.nextInt(), type, builder, owner)
        ship.saveAttachment<GeneratingIsland>(owner)
        ship.saveAttachment(ShipSettings(true))

        val center = ship.transform.positionInShip
        BlockStateInfo.onSetBlock(level,
            center.x.toInt(),
            center.y.toInt(),
            center.z.toInt(),
            Blocks.AIR.defaultBlockState(),
            Blocks.GOLD_BLOCK.defaultBlockState()
        )

        return ship
    }

    companion object {
        private val logger = LogUtils.getLogger()
    }
}