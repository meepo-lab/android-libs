package vn.meepo.android.support.core.funcational

interface Parser {
    fun <T> fromJson(string: String?, type: Class<T>, isArray: Boolean): T?
    fun <T> toJson(value: T?): String
}