package com.salesground.zipbolt.ui.recyclerview


class RecyclerViewItemClickedListener<T>(
    private val clicked: (T) -> Unit
) {
    fun onClick(clickedItem: T) {
        return clicked(clickedItem)
    }
}



