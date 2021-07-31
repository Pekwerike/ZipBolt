package com.salesground.zipbolt.ui.recyclerview


class DataToTransferRecyclerViewItemClickListener<T>(
    private val clicked: (T) -> Unit
) {
    fun onClick(clickedItem: T) {
        return clicked(clickedItem)
    }
}



