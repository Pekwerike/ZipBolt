package com.salesground.zipbolt.ui.recyclerview

import com.salesground.zipbolt.model.DataToTransfer

class DataToTransferRecyclerViewItemClickListener(val clicked: (DataToTransfer) -> Unit) {

    fun onClick(dataToTransfer: DataToTransfer) {
        return clicked(dataToTransfer)
    }
}