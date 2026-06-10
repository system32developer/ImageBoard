package com.system32dev

import com.intellij.ui.JBColor
import java.awt.*
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel

class ZoomableImagePanel : JPanel() {

    private var image: BufferedImage? = null
    private var zoom = 1.0
    private var onZoomChanged: ((Double) -> Unit)? = null

    init {
        addMouseWheelListener(object : MouseWheelListener {
            override fun mouseWheelMoved(e: MouseWheelEvent) {
                if (e.wheelRotation < 0) zoomIn() else zoomOut()
            }
        })
    }

    fun setImage(path: String) {
        image = ImageIO.read(File(path))
        repaint()
    }

    fun zoomIn() {
        zoom = (zoom * 1.15).coerceAtMost(10.0)
        onZoomChanged?.invoke(zoom)
        repaint()
    }

    fun zoomOut() {
        zoom = (zoom / 1.15).coerceAtLeast(0.1)
        onZoomChanged?.invoke(zoom)
        repaint()
    }

    fun setZoom(value: Double) {
        zoom = value.coerceIn(0.1, 10.0)
        onZoomChanged?.invoke(zoom)
        repaint()
    }

    fun getZoom(): Double {
        return zoom
    }

    fun clearImage() {
        image = null
        zoom = 1.0
        repaint()
    }

    fun setZoomListener(listener: (Double) -> Unit) {
        onZoomChanged = listener
    }

    fun resetZoom() {
        zoom = 1.0
        onZoomChanged?.invoke(zoom)
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2 = g as Graphics2D

        if (image == null) {
            g2.color = JBColor.GRAY
            g2.font = Font("Arial", Font.BOLD, 18)

            val text = "No image selected"
            val fm = g2.fontMetrics

            val x = (width - fm.stringWidth(text)) / 2
            val y = height / 2

            g2.drawString(text, x, y)
            return
        }

        val img = image!!

        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )

        val scaleX = width.toDouble() / img.width
        val scaleY = height.toDouble() / img.height

        val baseScale = minOf(scaleX, scaleY)
        val scale = baseScale * zoom

        val w = (img.width * scale).toInt()
        val h = (img.height * scale).toInt()

        val x = (width - w) / 2
        val y = (height - h) / 2

        g2.drawImage(img, x, y, w, h, null)
    }
}