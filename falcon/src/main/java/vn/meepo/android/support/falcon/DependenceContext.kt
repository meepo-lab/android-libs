package vn.meepo.android.support.falcon

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel

class DependenceContext : ProvideContext() {
    companion object {
        private val TAG = DependenceContext::class.java.simpleName
    }

    private val beans = hashMapOf<Class<*>, IBean<*>>()
    private val scopeBeans = hashMapOf<String, Scope>()
    private lateinit var application: ApplicationBean

    override fun getBean(clazz: Class<*>): IBean<*>? {
        if (beans.containsKey(clazz)) return beans[clazz]
        error("${clazz.javaClass.name} not found in Beans")
    }

    override fun modules(vararg module: ModuleContext) {
        module.forEach { it.provide() }
    }

    override fun <T> factory(override: Boolean, clazz: Class<T>, function: () -> T) {
        if (beans.containsKey(clazz) && !override) error("Can not override ${clazz.name} bean when override config=false")
        beans[clazz] = Bean(false, function)
    }

    override fun <T> single(override: Boolean, clazz: Class<T>, function: () -> T) {
        if (beans.containsKey(clazz) && !override) error("Can not override ${clazz.name} bean when override config=false")
        beans[clazz] = Bean(true, function)
    }

    override fun <T> scope(scopeId: String, clazz: Class<T>, function: () -> T) {
        val scope = getScope(scopeId)
        if (scope.contains(clazz)) error("Class ${clazz.simpleName} existed is scope $scopeId")
        scope.factory(clazz, function)
    }

    override fun <T> getOrNull(clazz: Class<T>): T? {
        return (lookup(clazz) as? IBean<T>)?.value
    }

    override fun <T> getOrNull(scopeId: String, clazz: Class<T>): T? {
        return (lookup(scopeId, clazz) as? IBean<T>)?.value
    }

    private fun getScope(scopeId: String): IScope {
        return if (scopeBeans.containsKey(scopeId)) {
            scopeBeans[scopeId]!!
        } else {
            Scope(this).also { scopeBeans[scopeId] = it }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> lookup(clazz: Class<T>): IBean<T> {
        if (beans.containsKey(clazz).not()) {
            if (clazz.isAssignableFrom(Application::class.java) || clazz.isAssignableFrom(Context::class.java)) {
                return application as IBean<T>
            }
            reflectProvideIfNeed(clazz)
        }
        return getBean(clazz) as IBean<T>
    }

    private fun <T> lookup(scopeId: String, clazz: Class<T>): IBean<T> {
        return getScope(scopeId).lookup(clazz)
    }

    private fun <T> provideByInject(clazz: Class<T>) {
        val annotation = clazz.getAnnotation(Inject::class.java)
        var shouldProvide = false
        var singleton = false

        when {
            annotation != null -> {
                shouldProvide = true
                singleton = annotation.singleton
            }
            Injectable::class.java.isAssignableFrom(clazz) -> {
                shouldProvide = true
                singleton = SingleInjectable::class.java.isAssignableFrom(clazz)
            }
        }
        require(shouldProvide) { "Not found declaration for ${clazz.simpleName}" }

        if (singleton) {
            single(clazz = clazz) { create(clazz) }
        } else {
            factory(clazz = clazz) { create(clazz) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> provideByInjectBy(clazz: Class<T>) {
        val annotation = clazz.getAnnotation(InjectBy::class.java)
            ?: error("Not found provider for ${clazz.simpleName}")
        val byClazz = annotation.clazz.java
        if (annotation.singleton) {
            single(clazz = clazz) { create(byClazz) as T }
        } else {
            factory(clazz = clazz) { create(byClazz) as T }
        }
    }

    private fun <T> reflectProvideIfNeed(clazz: Class<T>) {
        when {
            ViewModel::class.java.isAssignableFrom(clazz) -> factory(clazz = clazz) { create(clazz) }
            clazz.isInterface -> provideByInjectBy(clazz)
            else -> provideByInject(clazz)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(clazz: Class<T>): T {
        val constructor = clazz.constructors.firstOrNull()
            ?: clazz.declaredConstructors.firstOrNull()
            ?: error("Constructor not found to create ${clazz.simpleName}")

        val paramTypes = constructor.genericParameterTypes

        return try {
            constructor.newInstance(*paramTypes.map { lookup(it as Class<*>).value }
                .toTypedArray()) as T
        } catch (e: Throwable) {
            Log.e(TAG, "Error create ${clazz.name} coz ")
            throw e
        }
    }

    internal fun set(application: Application) {
        this.application = ApplicationBean(application)
    }
}