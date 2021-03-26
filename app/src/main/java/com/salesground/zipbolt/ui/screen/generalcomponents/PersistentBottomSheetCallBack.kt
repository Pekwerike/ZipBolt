package com.salesground.zipbolt.ui.screen.generalcomponents

import android.view.View
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PersistentBottomSheetCallBack(
    var
    persistentBottomSheetBehavior: BottomSheetBehavior<LinearLayout>,
    var persistentBottomSheetStateChanged: (Int) -> Unit) :
    BottomSheetBehavior.BottomSheetCallback(){

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        // for each state change, update the bottomSheet mutableState variable
        when (newState) {
            BottomSheetBehavior.STATE_COLLAPSED -> {
                persistentBottomSheetStateChanged(BottomSheetBehavior.STATE_COLLAPSED)
                persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            BottomSheetBehavior.STATE_EXPANDED -> {
                persistentBottomSheetStateChanged(BottomSheetBehavior.STATE_EXPANDED)
                persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            BottomSheetBehavior.STATE_HIDDEN -> {
                persistentBottomSheetStateChanged(BottomSheetBehavior.STATE_HIDDEN)
                persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                persistentBottomSheetStateChanged(BottomSheetBehavior.STATE_HALF_EXPANDED)
                persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
            BottomSheetBehavior.STATE_DRAGGING -> {
                persistentBottomSheetStateChanged(BottomSheetBehavior.STATE_DRAGGING)
                persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
            }
            BottomSheetBehavior.STATE_SETTLING -> {
                persistentBottomSheetStateChanged(BottomSheetBehavior.STATE_SETTLING)
                persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_SETTLING
            }
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {

    }
}

