package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel

import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory

data class HomeScreenRecyclerviewDataModel(
    val dataCategory: String,
    val mediaCollection: List<DataCategory>
)