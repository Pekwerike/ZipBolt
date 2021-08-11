package com.salesground.zipbolt.utils


open class SingleLiveDataEventForUIState<out T>(private val content: T) {
    // list of all observers that have received the send button event
    private val mutableSet = mutableSetOf<String>()

    fun getEvent(fragmentName: String): T? {
        return if (mutableSet.contains(fragmentName)) {
            null
        } else {
            mutableSet.add(fragmentName)
            content
        }
    }
}