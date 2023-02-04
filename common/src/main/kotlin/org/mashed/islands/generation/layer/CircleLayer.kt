package org.mashed.islands.generation.layer

import org.joml.Math.lerp
import org.joml.Vector2f
import kotlin.math.max

class CircleLayer(dropOffEnd: Float, dropOffStart: Float, val height: Float, val center: Vector2f) : HeightMapLayer {
    private val dropOffEnd = dropOffEnd * dropOffEnd
    private val dropOffStart = dropOffStart * dropOffStart
    private val diff = (dropOffEnd - dropOffStart) * (dropOffEnd - dropOffStart)

    override fun getHeight(x: Float, z: Float): Float =
        center.distanceSquared(x, z).let {
            if (it < dropOffStart) height
            else if (it >= dropOffEnd) 0f
            else max(lerp(height, 0f, (it - dropOffStart) / diff), 0f)
        }
}