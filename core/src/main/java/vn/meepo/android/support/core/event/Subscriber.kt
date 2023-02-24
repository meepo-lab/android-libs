package vn.meepo.android.support.core.event

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import vn.meepo.android.support.core.isOnMainThread

interface PostAble<in T> {
    fun postValue(value: T?)
    fun setValue(value: T?)
    fun post(value: T? = null) {
        if (isOnMainThread) setValue(value)
        else postValue(value)
    }
}

interface PromisePostAble<in T> : PostAble<T> {
    fun postError(e: Throwable)
    val error: Subscriber<Throwable>
}

open class Subscriber<T> : MutableLiveData<T>(), PostAble<T> {
    private val LifecycleOwner.subscribeOwner: LifecycleOwner
        get() = when (this) {
            is Fragment -> viewLifecycleOwner
            else -> this
        }

    fun subscribe(owner: LifecycleOwner, function: (T?) -> Unit) {

        super.observe(owner.subscribeOwner, object : Observer<T> {
            override fun onChanged(t: T) {
                function(t)
                removeObserver(this)
            }
        })
    }

    fun then(owner: LifecycleOwner, function: (T) -> Unit) {

        super.observe(owner.subscribeOwner, object : Observer<T> {
            override fun onChanged(t: T) {
                if (t != null) function(t)
                removeObserver(this)
            }
        })
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner.subscribeOwner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}

class Promise<T> : Subscriber<T>(), PromisePostAble<T> {
    override val error = Subscriber<Throwable>()

    override fun postError(e: Throwable) {
        error.post(e)
    }

    fun catch(owner: LifecycleOwner, function: (Throwable) -> Unit): Subscriber<T> {
        error.then(owner, function)
        return this
    }
}

fun <T> subscriber(function: PostAble<T>.() -> Unit): Subscriber<T> {
    return Subscriber<T>().apply {
        try {
            function()
        } catch (t: Throwable) {
        }
    }
}

fun <T> promise(function: PromisePostAble<T>.() -> Unit): Promise<T> {
    return Promise<T>().apply {
        try {
            function()
        } catch (e: Throwable) {
            postError(e)
        }
    }
}