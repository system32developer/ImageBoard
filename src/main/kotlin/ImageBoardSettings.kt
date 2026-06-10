package com.system32dev

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import java.util.*

@Service(Service.Level.APP)
@State(
    name = "ImageBoardSettings",
    storages = [Storage("ImageBoard.xml")]
)
class ImageBoardSettings :
    PersistentStateComponent<ImageBoardSettings.State> {

    class State {
        var imagePaths: MutableList<String> = mutableListOf()
    }

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    fun getImages(): MutableList<String> {
        return state.imagePaths
    }

    fun addImage(path: String) {
        if (!state.imagePaths.contains(path)) {
            state.imagePaths.add(path)
        }
    }

    fun removeImage(path: String) {
        state.imagePaths.remove(path)
    }

    companion object {
        fun getInstance(): ImageBoardSettings =
            ApplicationManager.getApplication()
                .getService(ImageBoardSettings::class.java)
    }
}