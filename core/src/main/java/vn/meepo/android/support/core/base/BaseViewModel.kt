package vn.meepo.android.support.core.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import vn.meepo.android.support.core.*
import vn.meepo.android.support.core.event.LoadingEvent
import vn.meepo.android.support.core.event.PostAble
import vn.meepo.android.support.core.event.SingleLiveEvent
import vn.meepo.android.support.core.extension.LoadCacheLiveData
import vn.meepo.android.support.core.extension.doAsync
import vn.meepo.android.support.core.extension.post
import vn.meepo.android.support.core.factory.SavableViewModelFactory
import vn.meepo.android.support.core.factory.ViewModelFactory
import vn.meepo.android.support.core.funcational.Form
import vn.meepo.android.support.core.funcational.SavedStateCreatable

abstract class BaseViewModel : ViewModel() {
    private val concurrent = ConcurrentContext()

    val refresh = MutableLiveData<Any>()
    val error = SingleLiveEvent<Throwable>()
    val loading = LoadingEvent()
    val viewLoading = LoadingEvent()

    override fun onCleared() {
        super.onCleared()
        concurrent.cancel()
    }

    fun <T, V> LiveData<T>.async(
        loadingEvent: LoadingEvent? = loading,
        errorEvent: SingleLiveEvent<Throwable>? = error,
        function: ConcurrentScope.(T) -> V?
    ): LiveData<V> {
        val next = MediatorLiveData<V>()
        next.addSource(this) {
            next.doAsync(it, concurrent, loadingEvent, errorEvent, function)
        }
        return next
    }

    fun <T, V> LoadCacheLiveData<T, V>.orAsync(
        loadingEvent: LoadingEvent? = loading,
        errorEvent: SingleLiveEvent<Throwable>? = error,
        function: ConcurrentScope.(T) -> V?
    ): LiveData<V> {
        val next = MediatorLiveData<V>()
        next.addSource(this) {
            if (it.second != null) {
                next.value = it.second
                return@addSource
            }
            next.doAsync(it.first, concurrent, loadingEvent, errorEvent, function)
        }
        return next
    }

    fun <T, V> LoadCacheLiveData<T, V>.thenAsync(
        loadingEvent: LoadingEvent? = loading,
        errorEvent: SingleLiveEvent<Throwable>? = error,
        function: ConcurrentScope.(T) -> V?
    ): LiveData<V> {
        val next = MediatorLiveData<V>()
        next.addSource(this) {
            if (it.second != null) next.value = it.second
            next.doAsync(it.first, concurrent, loadingEvent, errorEvent, function)
        }
        return next
    }

    fun async(
        loadingEvent: LoadingEvent? = loading,
        errorEvent: PostAble<Throwable>? = error,
        function: ConcurrentScope.() -> Unit
    ) {
        loadingEvent?.post(true)

        concurrent.launch {
            try {
                function()
            } catch (t: Throwable) {
                errorEvent?.postValue(t)
                t.printStackTrace()
            } finally {
                loadingEvent?.postValue(false)
            }
        }
    }

    fun diskIO(showError: Boolean = false, function: () -> Unit) {
        val callable = {
            try {
                function()
            } catch (t: Throwable) {
                t.printStackTrace()
                if (showError) error.postValue(t)
            }
        }

        if (isOnMainThread) AppExecutors.diskIO.execute(callable)
        else callable()
    }

    fun <T> LiveData<T>.validate(function: (T) -> Unit): LiveData<T> {
        val next = MediatorLiveData<T>()
        next.addSource(this) {
            try {
                function(it)
                next.value = it
            } catch (t: Throwable) {
                error.value = t
            }
        }
        return next
    }

    fun validate(form: Form): Any? = validate(form::validate)

    fun validate(function: () -> Unit): Any? {
        return try {
            function()
            null
        } catch (t: Throwable) {
            error.value = t
            this
        }
    }
}

class EmptyViewModel : BaseViewModel()


inline fun <reified T : ViewModel> LocalStoreOwner.getViewModel(owner: ViewModelStoreOwner): T {
    return localStore.get("vm:${javaClass.simpleName}:${owner.javaClass.simpleName}") {
        owner.getViewModel()
    }
}

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(): T {
    val viewModelClass = T::class.java
    val factory =
        if (SavedStateCreatable::class.java.isAssignableFrom(viewModelClass) && this is SavedStateRegistryOwner) {
            SavableViewModelFactory(this)
        } else {
            ViewModelFactory()
        }

    return ViewModelProvider(this, factory).get(viewModelClass).also {
        if (it is BaseViewModel && this is ViewModelRegistry) register(it)
    }
}

inline fun <reified T : ViewModel> AppCompatActivity.viewModel(): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { getViewModel() }

inline fun <reified T : ViewModel> Fragment.viewModel(): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { getViewModel() }

inline fun <reified T : ViewModel> Fragment.shareViewModel(): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { requireActivity().getViewModel() }

inline fun <reified T : ViewModel> viewModel(crossinline function: () -> ViewModelStoreOwner): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { function().getViewModel() }