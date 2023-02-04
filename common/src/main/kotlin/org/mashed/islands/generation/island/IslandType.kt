package org.mashed.islands.generation.island

import org.mashed.islands.generation.IslandBuilder

interface IslandType {
    val sizeRange: IntRange

    fun generateShape(state: IslandState): IslandShape
    fun iterateShape(state: IslandState, shape: IslandShape)
    fun applyShape(state: IslandState, shape: IslandShape, stateSetter: IslandBuilder)
}