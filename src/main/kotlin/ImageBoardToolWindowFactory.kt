package com.system32dev

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.io.File
import javax.swing.*

class ImageBoardToolWindowFactory : ToolWindowFactory {

    override fun shouldBeAvailable(project: Project) = true

    var internalChange = false

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {

        val settings = ImageBoardSettings.getInstance()

        settings.getImages().removeIf { !File(it).exists() }

        val panel = JPanel(BorderLayout())

        val imagePanel = ZoomableImagePanel()

        var currentIndex = 0

        val prev = JButton("◀")
        val next = JButton("▶")
        val add = JButton("+")
        val remove = JButton("-")

        val zoomSlider = JSlider(10, 300, 100)

        imagePanel.setZoomListener { zoom ->
            internalChange = true
            zoomSlider.value = (zoom * 100).toInt()
            internalChange = false
        }

        fun refresh() {

            val list = settings.getImages()

            if (list.isEmpty()) {
                imagePanel.resetZoom()
                imagePanel.clearImage()

                prev.isEnabled = false
                next.isEnabled = false
                remove.isEnabled = false

                currentIndex = 0
                return
            }

            if (currentIndex >= list.size) {
                currentIndex = list.lastIndex
            }

            val path = list[currentIndex]

            if (!File(path).exists()) {
                settings.removeImage(path)
                refresh()
                return
            }

            imagePanel.setImage(path)

            prev.isEnabled = currentIndex > 0
            next.isEnabled = currentIndex < list.lastIndex
            remove.isEnabled = true
        }

        prev.addActionListener {
            if (currentIndex > 0) {
                currentIndex--
                refresh()
            }
        }

        next.addActionListener {
            if (currentIndex < settings.getImages().lastIndex) {
                currentIndex++
                refresh()
            }
        }

        add.addActionListener {

            val desc = FileChooserDescriptor(
                true, false, false,
                false, false, false
            )

            val file = FileChooser.chooseFile(desc, project, null)
                ?: return@addActionListener

            settings.addImage(file.path)

            currentIndex = settings.getImages().lastIndex

            refresh()
        }

        remove.addActionListener {

            val list = settings.getImages()

            if (list.isEmpty()) return@addActionListener

            list.removeAt(currentIndex)

            if (list.isEmpty()) {
                currentIndex = 0
                imagePanel.clearImage()
                refresh()
                return@addActionListener
            }

            if (currentIndex >= list.size) {
                currentIndex = list.lastIndex
            }

            refresh()
        }

        zoomSlider.addChangeListener {
            if (internalChange) return@addChangeListener

            val zoom = zoomSlider.value / 100.0
            imagePanel.setZoom(zoom)
        }

        val navControls = JPanel()
        navControls.add(prev)
        navControls.add(next)
        navControls.add(add)
        navControls.add(remove)

        val zoomControls = JPanel()
        zoomControls.add(JLabel("Zoom"))
        zoomControls.add(zoomSlider)

        val bottom = JPanel()
        bottom.layout = BoxLayout(bottom, BoxLayout.Y_AXIS)
        bottom.add(navControls)
        bottom.add(zoomControls)

        panel.add(imagePanel, BorderLayout.CENTER)
        panel.add(bottom, BorderLayout.SOUTH)

        refresh()

        val content = ContentFactory.getInstance()
            .createContent(panel, "", false)

        toolWindow.contentManager.addContent(content)
    }
}