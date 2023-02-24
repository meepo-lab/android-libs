package vn.meepo.android.support.core.extension

import android.content.Context
import android.content.res.TypedArray
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DimenRes
import vn.meepo.android.support.core.R
import java.util.*

fun EditText.focus() {
    requestFocus()
    setSelection(0, length())
}

fun View.setMarginTop(it: Float) {
    (layoutParams as ViewGroup.MarginLayoutParams).topMargin = it.toInt()
}

fun View.setMarginTop(@DimenRes dimen: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams).topMargin = if (dimen == 0) 0 else
        resources.getDimensionPixelSize(dimen)
}

@Suppress("unchecked_cast")
fun ViewGroup.setContentView(id: Int) {
    removeAllViews()
    if (id == 0) return
    var cache = tag as? HashMap<Int, View>
    if (cache == null) {
        cache = hashMapOf()
        tag = cache
    }
    val view = if (cache.containsKey(id)) cache[id] else {
        LayoutInflater.from(context).inflate(id, this, false).also {
            cache[id] = it
        }
    }
    addView(view)
}

fun ViewGroup.of(id: Int, function: ViewGroup.() -> Unit) {
    setContentView(id)
    function()
}

fun TextView.format(vararg format: Any): String {
    return text.toString().format(Locale.getDefault(), *format)
}

fun TextView.addSpan(
        spanValue: String,
        spanned: CharacterStyle,
        textValue: String = text.toString()
) {
    val span = SpannableString(textValue)
    val start = span.indexOf(spanValue)
    if (start == -1) return
    val end = start + spanValue.length
    span.setSpan(spanned, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (spanned is ClickableSpan) {
        movementMethod = LinkMovementMethod.getInstance()
    }
    setText(span, TextView.BufferType.SPANNABLE)
}

fun ViewGroup.inflate(id: Int): View {
    return LayoutInflater.from(context).inflate(id, this, false)
}

fun Context.with(
        attrs: AttributeSet?,
        type: IntArray,
        defStyleAttr: Int,
        function: (TypedArray) -> Unit
) {
    if (attrs != null) {
        val typed = obtainStyledAttributes(attrs, type, defStyleAttr, 0)
        function(typed)
        typed.recycle()
    }
}

fun View.dispatchEnabled(b: Boolean) {
    b enable this
    if (this is ViewGroup) (0 until childCount).forEach {
        getChildAt(it).dispatchEnabled(b)
    }
}

fun View.dispatchForceEnabled(b: Boolean) {
    b forceEnable this
    if (this is ViewGroup) (0 until childCount).forEach {
        getChildAt(it).dispatchForceEnabled(b)
    }
}

infix fun Boolean.enable(view: View) {
    val forceEnable = view.getTag(R.id.force_enable).let { toggle -> toggle == true || toggle == null }
    view.isEnabled = forceEnable && this
}

infix fun Boolean.enable(views: List<View>) {
    views.forEach { this enable it }
}

infix fun Boolean.forceEnable(views: List<View>) {
    views.forEach { this forceEnable it }
}

infix fun Boolean.forceEnable(view: View) {
    view.setTag(R.id.force_enable, this)
    view.isEnabled = this
}

infix fun Boolean.show(view: View) {
    view.visibility = if (this) View.VISIBLE else View.GONE
}

infix fun Boolean.show(views: List<View>) {
    views.forEach { this show it }
}

fun <T : View> T.show(b: Boolean, function: T.() -> Unit) {
    visibility = if (b) {
        function()
        View.VISIBLE
    } else View.GONE
}

fun List<View>.show(b: Boolean, callback: () -> Unit) {
    b show this
    if (b) callback()
}

fun View.showOrInvisible(b: Boolean, function: () -> Unit) {
    visibility = if (b) {
        function()
        View.VISIBLE
    } else View.INVISIBLE
}

fun List<View>.showOrGone(b: Boolean, function: () -> Unit) {
    forEach {
        it.visibility = if (b) {
            function()
            View.VISIBLE
        } else View.GONE
    }
}

infix fun Boolean.invisible(view: View) {
    view.visibility = if (this) View.INVISIBLE else View.VISIBLE
}

infix fun Boolean.showOrGone(view: View) {
    view.visibility = if (this) View.VISIBLE else View.INVISIBLE
}

infix fun Boolean.invisible(views: List<View>) {
    views.forEach { this invisible it }
}

operator fun View.plus(view: View): List<View> {
    return arrayListOf(this, view)
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun <T : View> T.showOrGone(b: Boolean, function: (T.() -> Unit)? = null) {
    if (b) function?.invoke(this)
    visibility = if (b) View.VISIBLE else View.GONE
}

fun Context.toPx(dp: Float): Int {
    return (dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun Context.toDp(px: Int): Float {
    return px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}