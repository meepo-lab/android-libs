package vn.meepo.android.support.core.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.isShared
import vn.meepo.android.support.core.LocalStoreOwner

interface ViewModelRegistry : LocalStoreOwner {

    @CallSuper
    fun register(viewModel: BaseViewModel) {
        var viewModelId = "registry:vm:${viewModel.javaClass.name}"
        if (this is ViewModelStoreOwner) {
            viewModelId = "$viewModelId:shared:${viewModel.isShared(this)}"
        }

        if (!localStore.get(viewModelId) { false }) {
            onRegistryViewModel(viewModel)
            localStore[viewModelId] = true
        }
    }

    fun onRegistryViewModel(viewModel: BaseViewModel)
}
