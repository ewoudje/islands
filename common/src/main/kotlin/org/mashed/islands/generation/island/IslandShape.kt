package org.mashed.islands.generation.island

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.world.level.ChunkPos
import org.joml.Vector3ic
import org.valkyrienskies.core.impl.datastructures.SingleChunkDenseBlockPosSet
import org.valkyrienskies.core.impl.util.x
import org.valkyrienskies.core.impl.util.y
import org.valkyrienskies.core.impl.util.z
import java.util.function.IntFunction
import java.util.function.LongFunction

class IslandShape {
    private val columns = Long2ObjectOpenHashMap<SectionColumn>()

    fun add(pos: Vector3ic): Unit = add(pos.x, pos.y, pos.z)

    fun add(x: Int, y: Int, z: Int): Unit =
        getOrCreateColumn(x shr 4, z shr 4)
            .add((x and 0xF), y, (z and 0xF))

    fun remove(pos: Vector3ic): Unit =
        getOrCreateColumn(pos.x shr 4, pos.z shr 4)
            .remove((pos.x and 0xF), pos.y, (pos.z and 0xF))

    fun contains(pos: Vector3ic): Boolean = contains(pos.x, pos.y, pos.z)

    fun contains(x: Int, y: Int, z: Int): Boolean =
        getOrCreateColumn(x shr 4, z shr 4)
            .contains((x and 0xF), y, (z and 0xF))

    fun forEach(fn: (Int, Int, Int) -> Unit) =
        columns.forEach { (key, value) ->
           val chunkPos = ChunkPos(key.toLong())
           value.forEach { x, y, z -> fn((chunkPos.x shl 4) + x, y, (chunkPos.z shl 4) + z)} }

    fun forEachSurface(fn: (Int, Int, Int) -> Unit) =
        columns.forEach { (key, value) ->
            val chunkPos = ChunkPos(key.toLong())
            value.forEachSurface { x, y, z -> fn((chunkPos.x shl 4) + x, y, (chunkPos.z shl 4) + z)} }

    private fun getOrCreateColumn(x: Int, z: Int): SectionColumn =
        columns.computeIfAbsent(ChunkPos.asLong(x, z), LongFunction { SectionColumn() })

    private class SectionColumn {
        private val sections = arrayOfNulls<SingleChunkDenseBlockPosSet>(16)
        private val surface = IntArray(16 * 16) { Int.MIN_VALUE }

        fun add(x: Int, y: Int, z: Int) {
            val section = sections[sectionIndex(y)] ?: SingleChunkDenseBlockPosSet().also { sections[sectionIndex(y)] = it }
            section.add(x, y and 0xF, z)

            if (y > surface[z * 16 + x]) {
                surface[z * 16 + x] = y
            }
        }

        fun remove(x: Int, y: Int, z: Int) {
            val section = sections[sectionIndex(y)] ?: return
            section.remove(x, y and 0xF, z)

            if (y == surface[z * 16 + x]) {
                for (i in y downTo 0) {
                    if (section.contains(x, i, z)) {
                        surface[z * 16 + x] = i
                        return
                    }
                }

                surface[z * 16 + x] = Int.MIN_VALUE
            }
        }

        fun contains(x: Int, y: Int, z: Int): Boolean {
            val section = sections[sectionIndex(y)] ?: return false
            return section.contains(x, y and 0xF, z)
        }

        inline fun forEach(fn: (Int, Int, Int) -> Unit) {
            sections.forEachIndexed { index, section ->
                section?.forEach { x, y, z ->
                    fn(x, y + (index - 8) * 16, z)
                }
            }
        }

        inline fun forEachSurface(fn: (Int, Int, Int) -> Unit) {
            surface.forEachIndexed { index, depth ->
                if (depth != Int.MIN_VALUE)
                    fn(index % 16, depth, index / 16)
            }
        }

        private fun sectionIndex(y: Int): Int = (y shr 4) + 8
    }
}