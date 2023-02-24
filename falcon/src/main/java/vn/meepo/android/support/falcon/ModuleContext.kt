package vn.meepo.android.support.falcon

class ModuleContext(
    private val context: DependenceContext,
    private val provide: (DependenceContext) -> Unit
) : ProvideContext() {
    private var modules: Array<out ModuleContext>? = null

    override fun getBean(clazz: Class<*>): IBean<*>? = context.getBean(clazz)

    override fun modules(vararg module: ModuleContext) {
        this.modules = module
    }

    override fun <T> factory(override: Boolean, clazz: Class<T>, function: () -> T) =
        context.factory(override, clazz, function)

    override fun <T> single(override: Boolean, clazz: Class<T>, function: () -> T) =
        context.single(override, clazz, function)

    override fun <T> scope(scopeId: String, clazz: Class<T>, function: () -> T) =
        context.scope(scopeId, clazz, function)

    override fun <T> getOrNull(clazz: Class<T>): T? = context.getOrNull(clazz)

    override fun <T> getOrNull(scopeId: String, clazz: Class<T>): T? =
        context.getOrNull(scopeId, clazz)

    fun provide() {
        modules?.forEach { it.provide() }
        this.provide(context)
    }
}