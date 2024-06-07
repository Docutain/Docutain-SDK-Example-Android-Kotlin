package de.docutain.sdk.docutain_sdk_example_android_kotlin.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import de.docutain.sdk.docutain_sdk_example_android_kotlin.R
import de.docutain.sdk.docutain_sdk_example_android_kotlin.utils.getModeColor
import de.docutain.sdk.ui.ScanFilter

enum class ColorSettings {
    ColorPrimary,
    ColorSecondary,
    ColorOnSecondary,
    ColorScanButtonsLayoutBackground,
    ColorScanButtonsForeground,
    ColorScanPolygon,
    ColorBottomBarBackground,
    ColorBottomBarForeground,
    ColorTopBarBackground,
    ColorTopBarForeground
}

enum class ColorType {
    Light,
    Dark
}

enum class ScanSettings {
    AllowCaptureModeSetting,
    AutoCapture,
    AutoCrop,
    MultiPage,
    PreCaptureFocus,
    DefaultScanFilter
}

enum class EditSettings {
    AllowPageFilter,
    AllowPageRotation,
    AllowPageArrangement,
    AllowPageCropping,
    PageArrangementShowDeleteButton,
    PageArrangementShowPageNumber
}

class SettingsSharedPreferences(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    private fun saveColorItem(key: ColorSettings, lightColor: String, darkColor: String) {
        sharedPreferences.edit().apply {
            putString(key.toString().lowercase() + LIGHT_COLOR_KEY, lightColor)
            putString(key.toString().lowercase() + DARK_COLOR_KEY, darkColor)
            apply()
        }
    }

    fun saveColorItemLight(key: String, color: String) {
        sharedPreferences.edit().apply {
            putString(key.lowercase() + LIGHT_COLOR_KEY, color)
            apply()
        }
    }

    fun saveColorItemDark(key: String, color: String) {
        sharedPreferences.edit().apply {
            putString(key.lowercase() + DARK_COLOR_KEY, color)
            apply()
        }
    }

    fun saveEditItem(key: EditSettings, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(key.toString().lowercase() + EDIT_VALUE_KEY, value)
            apply()
        }
    }

    fun saveScanItem(key: ScanSettings, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(key.toString().lowercase() + SCAN_VALUE_KEY, value)
            apply()
        }
    }

    fun saveScanFilterItem(key: ScanSettings, value: Int) {
        sharedPreferences.edit().apply {
            putInt(key.toString().lowercase() + FILTER_VALUE_KEY, value).apply()
            apply()
        }
    }

    fun getColorItem(key: ColorSettings): SettingsMultiItems.ColorItem {
        val color1 =
            sharedPreferences.getString(key.toString().lowercase() + LIGHT_COLOR_KEY, "").toString()
        val color2 =
            sharedPreferences.getString(key.toString().lowercase() + DARK_COLOR_KEY, "").toString()
        return SettingsMultiItems.ColorItem(0, 0, color1, color2, key)
    }

    fun getEditItem(key: EditSettings): SettingsMultiItems.EditItem {
        val checkValue =
            sharedPreferences.getBoolean(key.toString().lowercase() + EDIT_VALUE_KEY, false)
        return SettingsMultiItems.EditItem(0, 0, checkValue, key)
    }

    fun getScanItem(key: ScanSettings): SettingsMultiItems.ScanSettingsItem {
        val scanValue =
            sharedPreferences.getBoolean(key.toString().lowercase() + SCAN_VALUE_KEY, false)
        return SettingsMultiItems.ScanSettingsItem(0, 0, scanValue, key)
    }

    fun getScanFilterItem(key: ScanSettings): SettingsMultiItems.ScanFilterItem {
        val scanValue = sharedPreferences.getInt(key.toString().lowercase() + FILTER_VALUE_KEY, ScanFilter.ILLUSTRATION.ordinal)
        return SettingsMultiItems.ScanFilterItem(0, 0, ScanFilter.values()[scanValue], key)
    }

    fun isEmpty(): Boolean {
        return sharedPreferences.all.isEmpty()

    }

    private fun defaultColorPrimary(
        lightColor: String = context.getModeColor(R.color.docutain_colorPrimary,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorPrimary,true)
    ) {
        saveColorItem(
            ColorSettings.ColorPrimary,
            lightColor,
            darkColor
        )
    }

    private fun defaultColorSecondary(
        lightColor: String = context.getModeColor(R.color.docutain_colorPrimary,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorPrimary,true)
    ) {
        saveColorItem(
            ColorSettings.ColorSecondary,
            lightColor,
            darkColor
        )
    }

    private fun defaultColorOnSecondary(
        lightColor: String = context.getModeColor(R.color.docutain_colorOnSecondary,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorOnSecondary,true)
    ) {
        saveColorItem(
            ColorSettings.ColorOnSecondary,
            lightColor,
            darkColor
        )
    }

    private fun defaultColorScanButtonsLayoutBackground(
        lightColor: String = context.getModeColor(R.color.docutain_colorScanButtonsLayoutBackground,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorScanButtonsLayoutBackground,true)
    ) {
        saveColorItem(
            ColorSettings.ColorScanButtonsLayoutBackground,
            lightColor,
            darkColor
        )
    }

    private fun defaultColorScanButtonsForeground(
        lightColor: String = context.getModeColor(R.color.docutain_colorScanButtonsForeground,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorScanButtonsForeground,true)
    ) {
        saveColorItem(
            ColorSettings.ColorScanButtonsForeground,
            lightColor,
            darkColor
        )
    }

    private fun defaultColorScanPolygon(
        lightColor: String = context.getModeColor(R.color.docutain_colorScanPolygon,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorScanPolygon,true)
    ) {
        saveColorItem(
            ColorSettings.ColorScanPolygon,
            lightColor,
            darkColor
        )
    }

    private fun defaultColorBottomBarBackground(
        lightColor: String = context.getModeColor(R.color.docutain_colorBottomBarBackground,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorBottomBarBackground,true)
    ) {
        saveColorItem(
            ColorSettings.ColorBottomBarBackground,
            lightColor,
            darkColor
        )
    }

    private fun defaultColorBottomBarForeground(
        lightColor: String = context.getModeColor(R.color.docutain_colorBottomBarForeground,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorBottomBarForeground,true)
    ) {
        saveColorItem(
            ColorSettings.ColorBottomBarForeground,
            lightColor,
            darkColor
        )
    }

    private fun defaultColorTopBarBackground(
        lightColor: String = context.getModeColor(R.color.docutain_colorTopBarBackground,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorTopBarBackground,true)
    ) {
        saveColorItem(
            ColorSettings.ColorTopBarBackground,
            lightColor,
            darkColor
        )
    }

    private fun defaultColorTopBarForeground(
        lightColor: String = context.getModeColor(R.color.docutain_colorTopBarForeground,false),
        darkColor: String = context.getModeColor(R.color.docutain_colorTopBarForeground,true)
    ) {
        saveColorItem(
            ColorSettings.ColorTopBarForeground,
            lightColor,
            darkColor
        )
    }

    fun defaultSettings() {

        defaultColorPrimary()
        defaultColorOnSecondary()
        defaultColorSecondary()
        defaultColorScanButtonsLayoutBackground()
        defaultColorScanButtonsForeground()
        defaultColorScanPolygon()
        defaultColorBottomBarForeground()
        defaultColorTopBarBackground()
        defaultColorTopBarForeground()
        defaultColorBottomBarBackground()

        saveScanItem(ScanSettings.AllowCaptureModeSetting, false)
        saveScanItem(ScanSettings.AutoCapture, true)
        saveScanItem(ScanSettings.AutoCrop, true)
        saveScanItem(ScanSettings.MultiPage, true)
        saveScanItem(ScanSettings.PreCaptureFocus, true)
        saveScanFilterItem(ScanSettings.DefaultScanFilter, ScanFilter.ILLUSTRATION.ordinal)

        saveEditItem(EditSettings.AllowPageFilter, true)
        saveEditItem(EditSettings.AllowPageRotation, true)
        saveEditItem(EditSettings.AllowPageArrangement, true)
        saveEditItem(EditSettings.AllowPageCropping, true)
        saveEditItem(EditSettings.PageArrangementShowDeleteButton, false)
        saveEditItem(EditSettings.PageArrangementShowPageNumber, true)
    }

    private fun getColor(color: Int): String =
        "#" + Integer.toHexString(ContextCompat.getColor(context, color)).uppercase()

    companion object {
        private const val PREF_FILE_NAME = "settings_prefs"
        const val LIGHT_COLOR_KEY = "_light_color"
        const val DARK_COLOR_KEY = "_dark_color"
        const val EDIT_VALUE_KEY = "_edit_value"
        const val SCAN_VALUE_KEY = "_scan_value"
        const val FILTER_VALUE_KEY = "_filter_value"
    }
}
