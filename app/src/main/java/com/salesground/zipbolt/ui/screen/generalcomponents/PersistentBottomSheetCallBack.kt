package com.salesground.zipbolt.ui.screen.generalcomponents

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PersistentBottomSheetCallBack(
    var actionCallback: OnActionCallback
) :
    BottomSheetBehavior.BottomSheetCallback() {

    interface OnActionCallback{
        fun bottomSheetStateChanged(state : Int)
        fun bottomSheetSlide(slideValue : Float)
    }
    override fun onStateChanged(bottomSheet: View, newState: Int) {
        // for each state change, update the bottomSheet mutableState variable
        when (newState) {
            BottomSheetBehavior.STATE_COLLAPSED -> {
                actionCallback.bottomSheetStateChanged(BottomSheetBehavior.STATE_COLLAPSED)
            }
            BottomSheetBehavior.STATE_EXPANDED -> {
                actionCallback.bottomSheetStateChanged(BottomSheetBehavior.STATE_EXPANDED)
            }
            BottomSheetBehavior.STATE_HIDDEN -> {
               actionCallback.bottomSheetStateChanged(BottomSheetBehavior.STATE_HIDDEN)
            }
            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                actionCallback.bottomSheetStateChanged(BottomSheetBehavior.STATE_HALF_EXPANDED)
            }
            BottomSheetBehavior.STATE_DRAGGING -> {
                actionCallback.bottomSheetStateChanged(BottomSheetBehavior.STATE_DRAGGING)
            }
            BottomSheetBehavior.STATE_SETTLING -> {
                actionCallback.bottomSheetStateChanged(BottomSheetBehavior.STATE_SETTLING)
            }
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        actionCallback.bottomSheetSlide(slideOffset)
    }
}

