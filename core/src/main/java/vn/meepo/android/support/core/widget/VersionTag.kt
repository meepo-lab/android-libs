package vn.meepo.android.support.core.widget

import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import vn.meepo.android.support.core.extension.toPx


abstract class VersionTag {

    protected open val versionColor: Int get() = Color.RED
    protected open val shouldShow: Boolean get() = false
    protected open val versionText: String get() = ""


    private val FragmentActivity.findRoot: ViewGroup
        get() {
            var view: ViewGroup = findViewById(android.R.id.content) ?: error("Not found")
            while (true) {
                if (view.parent == null) return view
                if (view.parent !is ViewGroup) return view
                view = view.parent as ViewGroup
            }
        }

    fun applyTo(activity: FragmentActivity) {
        if (!shouldShow) return

        activity.findRoot.also { container ->
            container.addView(TagTextView(container.context).apply {
                layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.END
                }
                text = versionText
                color = versionColor
                setPadding(context.toPx(8f), context.toPx(5f), 0, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    translationZ = Float.MAX_VALUE
                }
            })
        }
    }

    companion object {
        operator fun get(debug: Boolean, flavor: String): VersionTag {
            val pro = "pro"
            val uat = "uat"
            return when {
                debug && flavor == pro -> ProDebugVersion()
                debug && flavor == uat -> UatDebugVersion()

                !debug && flavor == pro -> ProReleaseVersion()
                !debug && flavor == uat -> UatReleaseVersion()

                debug -> DevDebugVersion()
                else -> DevReleaseVersion()
            }
        }
    }
}

class DevDebugVersion : VersionTag() {
    override val shouldShow: Boolean = true
    override val versionText: String = "Dev Debug"
    override val versionColor: Int = Color.parseColor("#b71c1c")
}

class ProDebugVersion : VersionTag() {
    override val shouldShow: Boolean = true
    override val versionText: String = "Pro Debug"
    override val versionColor: Int = Color.parseColor("#283593")
}

class UatDebugVersion : VersionTag() {
    override val shouldShow: Boolean = true
    override val versionText: String = "UAT Debug"
    override val versionColor: Int = Color.parseColor("#283593")
}

class UatReleaseVersion : VersionTag() {
    override val shouldShow: Boolean = true
    override val versionText: String = "UAT Release"
    override val versionColor: Int = Color.parseColor("#283593")
}

class DevReleaseVersion : VersionTag() {
    override val shouldShow: Boolean = true
    override val versionText: String = "Dev Release"
    override val versionColor: Int = Color.parseColor("#00c853")
}

class ProReleaseVersion : VersionTag()