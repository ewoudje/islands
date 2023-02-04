package org.mashed.islands.generation.island

import org.mashed.islands.generation.IslandBuilder

data class IslandState(
    val size: Float,
    val seed: Int,
    val type: IslandType
) {
    fun generateShape() = type.generateShape(this)
    fun iterateShape(shape: IslandShape) = type.iterateShape(this, shape)
    fun applyShape(shape: IslandShape, builder: IslandBuilder) = type.applyShape(this, shape, builder)
}
