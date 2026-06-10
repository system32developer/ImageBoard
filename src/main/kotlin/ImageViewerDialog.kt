package com.system32dev

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

class ImageViewerDialog(
    path: String
) : DialogWrapper(true) {

    private val panel = JPanel(BorderLayout())

    init {

        title = "Image Preview"

        val imageLabel = JLabel(
            ImageIcon(path)
        )

        panel.add(
            JScrollPane(imageLabel),
            BorderLayout.CENTER
        )

        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}