package vn.meepo.android.support.core.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vn.meepo.android.support.core.funcational.Creatable
import vn.meepo.android.support.falcon.dependenceContext

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = dependenceContext.get(modelClass)
        if (viewModel is Creatable) viewModel.onCreate()
        return viewModel
    }
}