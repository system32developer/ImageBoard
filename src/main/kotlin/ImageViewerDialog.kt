package com.system32dev

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import javax.swing.*

class ImageViewerDialog(path: String) : DialogWrapper(true) {

    private val imagePanel = ZoomableImagePanel()

    init {
        title = "Image Preview"

        val root = JPanel(BorderLayout())

        imagePanel.setImage(path)

        val controls = JPanel()

        val zoomIn = JButton("+")
        val zoomOut = JButton("-")
        val reset = JButton("100%")

        zoomIn.addActionListener { imagePanel.zoomIn() }
        zoomOut.addActionListener { imagePanel.zoomOut() }
        reset.addActionListener { imagePanel.resetZoom() }

        controls.add(zoomOut)
        controls.add(reset)
        controls.add(zoomIn)

        root.add(imagePanel, BorderLayout.CENTER)
        root.add(controls, BorderLayout.SOUTH)

        init()

        contentPane.add(root)
    }

    override fun createCenterPanel(): JComponent? {
        return null
    }
}