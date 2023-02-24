package vn.meepo.android.support.core.event

import androidx.lifecycle.MutableLiveData

open class LoadingEvent : MutableLiveData<Boolean>() {
    private var numberOfLoading = 0

    override fun postValue(value: Boolean?) {
        synchronized(this) {
            if (value!!) {
                numberOfLoading++
                if (shouldPost(true)) super.postValue(true)
            } else {
                numberOfLoading--
                if (numberOfLoading < 0) numberOfLoading = 0
                if (numberOfLoading == 0) super.postValue(false)
            }
        }
    }

    protected open fun shouldPost(b: Boolean) = this.value != b
}