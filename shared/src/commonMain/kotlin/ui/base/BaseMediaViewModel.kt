package ui.base

import data.model.media.BaseMediaNode
import data.model.media.BaseMyListStatus
import data.model.media.MediaType

abstract class BaseMediaViewModel : BaseViewModel() {
    abstract val mediaType: MediaType
    open var mediaInfo: BaseMediaNode? = null
    open var myListStatus: BaseMyListStatus? = null
}