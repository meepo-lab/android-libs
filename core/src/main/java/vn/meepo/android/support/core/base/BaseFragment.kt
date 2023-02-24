package vn.meepo.android.support.core.base

import androidx.fragment.app.Fragment
import vn.meepo.android.support.core.Dispatcher
import vn.meepo.android.support.core.LocalStore
import vn.meepo.android.support.core.LocalStoreOwner

abstract class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId), LocalStoreOwner,
    Dispatcher {
    private var _localStore: LocalStore? = null
    private var _viewLocalStoreOwner: LocalStoreOwner? = null

    override val localStore: LocalStore
        get() {
            if (_localStore == null) _localStore = LocalStore()
            return _localStore!!
        }

    private val viewLocalStoreOwner: LocalStoreOwner
        get() {
            if (_viewLocalStoreOwner == null) _viewLocalStoreOwner = object : LocalStoreOwner {
                override val localStore: LocalStore = LocalStore()
            }
            return _viewLocalStoreOwner!!
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewLocalStoreOwner?.localStore?.clear()
        _viewLocalStoreOwner = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _localStore?.clear()
    }
}

val Fragment.isVisibleOnScreen: Boolean
    get() = !isHidden && isAdded && (parentFragment?.isVisibleOnScreen ?: true)