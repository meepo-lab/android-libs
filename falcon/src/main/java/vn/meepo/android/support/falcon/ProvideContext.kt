package vn.meepo.android.support.falcon

abstract class ProvideContext {
    abstract fun getBean(clazz: Class<*>): IBean<*>?
    abstract fun modules(vararg module: ModuleContext)

    abstract fun <T> factory(override: Boolean = false, clazz: Class<T>, function: () -> T)
    abstract fun <T> single(override: Boolean = false, clazz: Class<T>, function: () -> T)
    abstract fun <T> scope(scopeId: String, clazz: Class<T>, function: () -> T)

    inline fun <reified T> factory(override: Boolean = false, noinline function: () -> T) {
        return factory(override, T::class.java, function)
    }

    inline fun <reified T> single(override: Boolean = false, noinline function: () -> T) {
        return single(override, T::class.java, function)
    }

    inline fun <reified T> scope(scopeId: String, noinline function: () -> T) {
        return scope(scopeId, T::class.java, function)
    }

    abstract fun <T> getOrNull(clazz: Class<T>): T?
    abstract fun <T> getOrNull(scopeId: String, clazz: Class<T>): T?

    fun <T> get(clazz: Class<T>): T {
        return getOrNull(clazz) ?: error("Not found bean ${clazz.simpleName}")
    }

    fun <T> get(scopeId: String, clazz: Class<T>): T {
        return getOrNull(scopeId, clazz) ?: error("Not found bean ${clazz.simpleName}")
    }

    inline fun <reified T> get(scopeId: String): T {
        return get(scopeId, T::class.java)
    }

    inline fun <reified T> get(): T {
        return get(T::class.java)
    }
}
