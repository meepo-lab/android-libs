package vn.meepo.android.support.falcon

import android.app.Application
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Inject(val singleton: Boolean = false)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectBy(val clazz: KClass<out Injectable>, val singleton: Boolean = false)

interface Injectable
interface SingleInjectable : Injectable

interface IBean<T> {
    val value: T
}

interface IScope {
    fun contains(clazz: Class<*>): Boolean
    fun <T> factory(clazz: Class<T>, function: () -> T)
    fun <T> lookup(clazz: Class<T>): IBean<T>
    fun dispose()
}

internal open class Bean<T>(private val singleton: Boolean, val function: () -> T) : IBean<T> {
    private var _value: T? = null

    override val value: T
        get() {
            return if (singleton) {
                if (_value == null) synchronized(this) {
                    if (_value == null) _value = function()
                }
                _value!!
            } else {
                function()
            }
        }
}

internal class Scope(private val context: DependenceContext) : IScope {
    private val beans = hashMapOf<Class<*>, ScopeBean<*>>()

    override fun contains(clazz: Class<*>): Boolean {
        return beans.containsKey(clazz)
    }

    override fun <T> factory(clazz: Class<T>, function: () -> T) {
        beans[clazz] = ScopeBean(function)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> lookup(clazz: Class<T>): IBean<T> {
        if (!beans.containsKey(clazz)) {
            factory(clazz) { context.create(clazz) }
        }
        return beans[clazz] as IBean<T>
    }

    override fun dispose() {
        beans.values.forEach { it.dispose() }
        beans.clear()
    }
}

internal class ScopeBean<T>(private val function: () -> T) : IBean<T> {
    private var _value: T? = null

    override val value: T
        get() {
            if (_value == null) _value = function()
            return _value!!
        }

    fun dispose() {
        _value = null
    }
}

internal class ApplicationBean(application: Application) : IBean<Application> {
    override val value: Application = application
}
