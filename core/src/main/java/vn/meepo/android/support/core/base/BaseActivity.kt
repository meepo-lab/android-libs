package vn.meepo.android.support.core.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import vn.meepo.android.support.core.Dispatcher
import vn.meepo.android.support.core.LocalStore
import vn.meepo.android.support.core.LocalStoreOwner
import vn.meepo.android.support.core.extension.lazyNone

abstract class BaseActivity(
    @LayoutRes layoutId: Int
) : AppCompatActivity(layoutId), Dispatcher, LocalStoreOwner {

    override val localStore: LocalStore by lazyNone { LocalStore() }

    override fun onDestroy() {
        super.onDestroy()
        localStore.clear()
    }

}