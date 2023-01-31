import org.joml.Vector2f
import org.mashed.islands.generation.layer.CircleLayer
import org.mashed.islands.generation.layer.PerlinLayer
import org.mashed.islands.generation.layer.erode
import java.awt.Color
import java.awt.Graphics
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.min


object VisualizeWorldGen : JPanel() {
    val layer = PerlinLayer(0, 4f, 2)
        .mask(CircleLayer(1f, 0.01f, 1f, Vector2f(0f, 0f)))
        .erode(3, 0.01f)

    val resolution = 400

    @JvmStatic
    fun main(args: Array<String>) {
        val f = JFrame() //creating instance of JFrame

        f.add(this)
        this.setSize(resolution, resolution)
        f.setSize(resolution, resolution) //400 width and 500 height
        f.layout = null //using no layout managers
        f.isVisible = true //making the frame visible
        f.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }

    override fun paint(g: Graphics) {
        g.color = Color.BLACK
        g.fillRect(0, 0, resolution, resolution)
        repeat(resolution) { x ->
            repeat(resolution) { y ->
                val height = layer.getHeight((x.toFloat() / (resolution / 2f)) - 1f, (y.toFloat() / (resolution / 2f)) - 1f)
                if (height > 1) {
                    g.color = Color.GREEN
                    g.fillRect(x, y, 1, 1)
                } else if (height > 0) {
                    g.color = Color(height, height, height)
                    g.fillRect(x, y, 1, 1)
                }
            }
        }
    }
}