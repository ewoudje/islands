package org.mashed.islands.generation.layer

import kotlin.math.cos
import kotlin.math.sin

class ErodeLayer(val strength: Int, val resolution: Float, val layer: HeightMapLayer): HeightMapLayer {

    override fun getHeight(x: Float, z: Float): Float {
        var height = layer.getHeight(x, z)

       repeat(strength) { i ->
            repeat(8) { circle ->
                val angle = circle * (Math.PI / 4.0)

                val nextHeight = layer.getHeight(
                    (cos(angle).toFloat() * resolution * (i + 1)) + x,
                    (sin(angle).toFloat() * resolution * (i + 1)) + z
                )

                if (nextHeight < height) {
                    height = nextHeight
                }
            }
        }

        return height
    }
}

fun HeightMapLayer.erode(strength: Int, resolution: Float): HeightMapLayer = ErodeLayer(strength, resolution, this)