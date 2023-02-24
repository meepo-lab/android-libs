package vn.meepo.android.support.core.funcational

import android.os.Bundle

interface SavedStateCallback {
    fun onSavedState(): Bundle
    fun onRestoreState(savedState: Bundle)
}