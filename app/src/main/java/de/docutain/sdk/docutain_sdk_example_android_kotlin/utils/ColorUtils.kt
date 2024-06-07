package de.docutain.sdk.docutain_sdk_example_android_kotlin.utils

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.getModeColor(@ColorRes colorResId: Int, isNight: Boolean): String {
    val newConfig = Configuration(resources.configuration).apply {
        uiMode = if (isNight) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
    }
    val nightModeContext = createConfigurationContext(newConfig)
    val color = ContextCompat.getColor(nightModeContext, colorResId)
    return "#" + Integer.toHexString(color).uppercase()
}