package vn.meepo.android.support.falcon

import android.app.Application

val dependenceContext = DependenceContext()

fun Application.dependencies(function: DependenceContext.() -> Unit) {
    dependenceContext.set(this)
    function(dependenceContext)
}

fun module(function: ProvideContext.() -> Unit): ModuleContext {
    return ModuleContext(dependenceContext, function)
}

inline fun <reified T> inject() = lazy(LazyThreadSafetyMode.NONE) {
    dependenceContext.get(T::class.java)
}

inline fun <reified T> inject(scopeId: String) = lazy(LazyThreadSafetyMode.NONE) {
    dependenceContext.get(scopeId, T::class.java)
}