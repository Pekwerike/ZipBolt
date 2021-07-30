package com.salesground.zipbolt.ui.recyclerview


class DataToTransferRecyclerViewItemClickListener<T>(
    private val clicked: (T) -> Unit
) {
    fun onClick(clickedItem: T) {
        return clicked(clickedItem)
    }
}

class FolderClickedListener(
    private val clicked: (String) -> Unit
) {
    fun onClick(folderPath: String) {
        return clicked(folderPath)
    }
}

