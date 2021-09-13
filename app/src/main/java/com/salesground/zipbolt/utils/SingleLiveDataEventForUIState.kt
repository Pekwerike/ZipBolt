package com.salesground.zipbolt.utils

import androidx.lifecycle.Observer


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

open class Event<out T>(private val content: T) {
    var isHandled = false
    fun getEventIfNotHandled(): T? {
        return if (!isHandled) {
            null
        } else {
            isHandled = true
            content
        }
    }
}
