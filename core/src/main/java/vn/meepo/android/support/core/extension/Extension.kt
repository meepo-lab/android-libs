package vn.meepo.android.support.core.extension

import vn.meepo.android.support.core.Disposable

fun <T> block(any: T?, function: T.() -> Unit) {
    if (any != null) function(any)
}

fun String?.safe(def: String = ""): String {
    return this ?: def
}

fun String?.or(def: String = ""): String {
    if (this != null) {
        if (this.isBlank()) return def
        return this
    }
    return def
}

fun Int?.safe(def: Int = 0): Int {
    return this ?: def
}

fun Double?.safe(def: Double = 0.0): Double {
    return this ?: def
}

fun Float?.safe(def: Float = 0f): Float {
    return this ?: def
}

fun Boolean?.safe(def: Boolean = false): Boolean {
    return this ?: def
}

fun String.sub(start: Int, end: Int): String {
    return substring(start, if (length < end) length else end)
}

fun <T> lazyNone(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

fun <T> tryCall(function: () -> T): Pair<T?, Throwable?> {
    return try {
        function() to null
    } catch (t: Throwable) {
        null to t
    }
}

fun catching(function: () -> Unit): Throwable? {
    return try {
        function()
        null
    } catch (e: Throwable) {
        e
    }
}

class Refer<T> : Disposable {
    private var mData: T? = null

    fun get(function: () -> T): T {
        if (mData == null) mData = function()
        return mData!!
    }

    override fun dispose() {
        mData = null
    }
}