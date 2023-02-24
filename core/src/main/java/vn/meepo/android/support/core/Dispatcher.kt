package vn.meepo.android.support.core

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface Dispatcher

inline fun <reified T : FragmentActivity> Dispatcher.open(
    args: Bundle? = null,
    edit: Intent.() -> Unit = {}
): Dispatcher {
    when (this) {
        is AppCompatActivity -> startActivity(Intent(this, T::class.java).apply {
            args?.let { putExtras(it) }
            edit()
        })
        is Fragment -> startActivity(Intent(requireContext(), T::class.java).apply {
            args?.let { putExtras(it) }
            edit()
        })
    }
    return this
}

inline fun <reified T : FragmentActivity> Dispatcher.openForResult(
    args: Bundle? = null,
    crossinline edit: Intent.() -> Unit,
    crossinline callback: (ActivityResult) -> Unit
): Dispatcher {
    when (this) {
        is AppCompatActivity -> registerForActivityResult(
            object : ActivityResultContract<Intent, ActivityResult>() {
                override fun createIntent(context: Context, input: Intent): Intent {
                    return Intent(context, T::class.java).apply {
                        args?.let { putExtras(args) }
                        edit()
                    }
                }

                override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
                    return ActivityResult(resultCode, intent)
                }
            }) { callback(it) }
        is Fragment -> registerForActivityResult(
            object : ActivityResultContract<Intent, ActivityResult>() {
                override fun createIntent(context: Context, input: Intent): Intent {
                    return Intent(context, T::class.java).apply {
                        args?.let { putExtras(args) }
                        edit()
                    }
                }

                override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
                    return ActivityResult(resultCode, intent)
                }
            }) { callback(it) }
    }
    return this
}

fun Dispatcher.close() {
    when (this) {
        is AppCompatActivity -> finish()
        is Fragment -> requireActivity().finish()
    }
}

fun Dispatcher.clear() {
    when (this) {
        is AppCompatActivity -> finishAffinity()
        is Fragment -> requireActivity().finishAffinity()
    }
}