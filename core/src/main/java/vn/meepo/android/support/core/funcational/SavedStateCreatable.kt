package vn.meepo.android.support.core.funcational

import android.os.Bundle

interface SavedStateCreatable {
    fun onCreate(savedState: Bundle?)

    fun onSavedState(): Bundle = Bundle()
}