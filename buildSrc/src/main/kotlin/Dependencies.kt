@file:Suppress("SpellCheckingInspection")

/**
 * This file list down all dependencies using for project.
 *
 * WARNING: Please don't format this file by tool => MANUAL FORMAT FOR LINE SEPARATE
 */
object Dependencies {
    const val KOTLIN_VERSION = "1.5.10"
    const val KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$KOTLIN_VERSION"

    const val ANDROIDX_CORE_KTX   = "androidx.core:core-ktx:1.9.0"
    const val ANDROIDX_APP_COMPAT = "androidx.appcompat:appcompat:1.6.1"

    const val GOOGLE_MATERIAL = "com.google.android.material:material:1.3.0"
    const val GOOGLE_GSON     = "com.google.code.gson:gson:2.8.6"

    const val ANDROIDX_NAVIGATION_VERSION      = "2.3.5"
    const val ANDROIDX_NAVIGATION_FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:$ANDROIDX_NAVIGATION_VERSION"
    const val ANDROIDX_NAVIGATION_UI_KTX       = "androidx.navigation:navigation-ui-ktx:$ANDROIDX_NAVIGATION_VERSION"

    const val COIL_KTX = "io.coil-kt:coil:1.2.2"

    const val LOGGER_TIMBER = "com.jakewharton.timber:timber:4.7.1"

    const val ILT_GAMEPORTAL_ANDROID_DI                  = "io.inspirelab.defi.gameportal:android-di:1.0.0"
    const val ILT_GAMEPORTAL_ANDROID_CORE                = "io.inspirelab.defi.gameportal:android-core:1.0.1"

    // region NETWORKING
    private const val SQUAREUP_VERSION = "2.9.0"
    const val RETROFIT2                = "com.squareup.retrofit2:retrofit:$SQUAREUP_VERSION"
    const val RETROFIT2_CONVERTER_GSON = "com.squareup.retrofit2:converter-gson:$SQUAREUP_VERSION"

    private const val OKHTTP3_VERSION     = "4.9.0"
    const val OKHTTP3_OKHTTP              = "com.squareup.okhttp3:okhttp:$OKHTTP3_VERSION"
    const val OKHTTP3_LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:$OKHTTP3_VERSION"
    // endregion NETWORKING

    // region TESTING
    private const val JUNIT_VERSION = "4.13.2"
    const val JUNIT = "junit:junit:$JUNIT_VERSION"
    const val TESTING_ANDROIDX_TEST_EXT           = "androidx.test.ext:junit:1.1.2"
    const val TESTING_ANDROIDX_TEST_ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.3.0"
    // endregion TESTING
}