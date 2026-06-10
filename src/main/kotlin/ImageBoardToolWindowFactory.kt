package com.system32dev

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.Image
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*

class ImageBoardToolWindowFactory : ToolWindowFactory {

    override fun shouldBeAvailable(project: Project): Boolean = true

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {

        val settings = ImageBoardSettings.getInstance()

        settings.getImages().removeIf {
            !File(it).exists()
        }

        val panel = JPanel(BorderLayout())

        var currentIndex = 0

        val imageLabel = JLabel(
            "No Images",
            SwingConstants.CENTER
        )

        val previousButton = JButton("◀")
        val nextButton = JButton("▶")
        val addButton = JButton("+")
        val removeButton = JButton("-")

        fun refreshImage() {

            val images = settings.getImages()

            if (images.isEmpty()) {

                imageLabel.icon = null
                imageLabel.text = "No Images"

                previousButton.isEnabled = false
                nextButton.isEnabled = false
                removeButton.isEnabled = false

                return
            }

            removeButton.isEnabled = true

            if (currentIndex >= images.size) {
                currentIndex = images.lastIndex
            }

            val path = images[currentIndex]

            val file = File(path)

            if (!file.exists()) {

                settings.removeImage(path)

                refreshImage()

                return
            }

            val icon = ImageIcon(path)

            imageLabel.text = ""

            imageLabel.icon = ImageIcon(
                icon.image.getScaledInstance(
                    500,
                    500,
                    Image.SCALE_SMOOTH
                )
            )

            previousButton.isEnabled =
                currentIndex > 0

            nextButton.isEnabled =
                currentIndex < images.lastIndex
        }

        previousButton.addActionListener {

            if (currentIndex > 0) {

                currentIndex--

                refreshImage()
            }
        }

        nextButton.addActionListener {

            if (currentIndex <
                settings.getImages().lastIndex
            ) {

                currentIndex++

                refreshImage()
            }
        }

        addButton.addActionListener {

            val descriptor = FileChooserDescriptor(
                true,
                false,
                false,
                false,
                false,
                false
            )

            descriptor.title = "Select Image"

            val file = FileChooser.chooseFile(
                descriptor,
                project,
                null
            ) ?: return@addActionListener

            settings.addImage(file.path)

            currentIndex =
                settings.getImages().lastIndex

            refreshImage()
        }

        removeButton.addActionListener {

            val images = settings.getImages()

            if (images.isEmpty()) {
                return@addActionListener
            }

            images.removeAt(currentIndex)

            if (currentIndex >= images.size) {
                currentIndex =
                    maxOf(0, images.size - 1)
            }

            refreshImage()
        }

        imageLabel.addMouseListener(
            object : MouseAdapter() {

                override fun mouseClicked(
                    e: MouseEvent
                ) {

                    if (e.clickCount != 2) {
                        return
                    }

                    val images =
                        settings.getImages()

                    if (images.isEmpty()) {
                        return
                    }

                    ImageViewerDialog(
                        images[currentIndex]
                    ).show()
                }
            }
        )

        val controls = JPanel()

        controls.add(previousButton)
        controls.add(addButton)
        controls.add(removeButton)
        controls.add(nextButton)

        panel.add(
            JScrollPane(imageLabel),
            BorderLayout.CENTER
        )

        panel.add(
            controls,
            BorderLayout.SOUTH
        )

        refreshImage()

        val content = ContentFactory
            .getInstance()
            .createContent(panel, "", false)

        toolWindow.contentManager.addContent(content)
    }
}