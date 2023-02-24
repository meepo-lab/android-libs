package vn.meepo.android.support.core

import android.app.Dialog
import android.widget.PopupMenu
import android.widget.PopupWindow
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LocalStore {
    private val caches = hashMapOf<String, Any>()

    operator fun set(key: String, value: Any) {
        caches[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: String, function: () -> T): T {
        return if (caches.containsKey(key)) {
            caches[key] as T
        } else {
            function().apply { caches[key] = this }
        }
    }

    inline fun <reified T : Any> get(noinline function: () -> T): T {
        return get(T::class.java.name, function)
    }

    fun clear() {
        caches.values.forEach {
            when (it) {
                is Dialog -> it.dismiss()
                is PopupWindow -> it.dismiss()
                is PopupMenu -> it.dismiss()
                is BottomSheetDialogFragment -> it.dismiss()
            }
        }
        caches.clear()
    }
}

interface Disposable {
    fun dispose()
}

interface LocalStoreOwner {
    val localStore: LocalStore
}