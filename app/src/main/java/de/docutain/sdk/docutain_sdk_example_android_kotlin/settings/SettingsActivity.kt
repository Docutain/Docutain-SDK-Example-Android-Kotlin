package de.docutain.sdk.docutain_sdk_example_android_kotlin.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.docutain.sdk.docutain_sdk_example_android_kotlin.R
import de.docutain.sdk.docutain_sdk_example_android_kotlin.utils.*

class SettingsActivity : AppCompatActivity() {
    private val settingsSharedPreferences = SettingsSharedPreferences(this)
    private lateinit var settingsAdapter: SettingsMultiViewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initToolbar()
        initRestButton()

        settingsAdapter = SettingsMultiViewsAdapter(preparingData(), settingsSharedPreferences)
        val recyclerView: RecyclerView = findViewById(R.id.settings_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = settingsAdapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun preparingData(): MutableList<SettingsMultiItems> {
        val colorPrimaryItem =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorPrimary)
        val colorSecondaryItem =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorSecondary)
        val colorOnSecondaryItem =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorOnSecondary)
        val colorScanButtonsLayoutBackground =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorScanButtonsLayoutBackground)
        val colorScanButtonsForeground =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorScanButtonsForeground)
        val colorScanPolygon =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorScanPolygon)
        val colorBottomBarBackground =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorBottomBarBackground)
        val colorBottomBarForeground =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorBottomBarForeground)
        val colorTopBarBackground =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorTopBarBackground)
        val colorTopBarForeground =
            settingsSharedPreferences.getColorItem(ColorSettings.ColorTopBarForeground)

        val allowCaptureModeSetting =
            settingsSharedPreferences.getScanItem(ScanSettings.AllowCaptureModeSetting).checkValue
        val autoCapture =
            settingsSharedPreferences.getScanItem(ScanSettings.AutoCapture).checkValue
        val autoCrop =
            settingsSharedPreferences.getScanItem(ScanSettings.AutoCrop).checkValue
        val multiPage =
            settingsSharedPreferences.getScanItem(ScanSettings.MultiPage).checkValue
        val preCaptureFocus =
            settingsSharedPreferences.getScanItem(ScanSettings.PreCaptureFocus).checkValue
        val defaultScanFilter =
            settingsSharedPreferences.getScanFilterItem(ScanSettings.DefaultScanFilter).scanValue

        val allowPageFilter =
            settingsSharedPreferences.getEditItem(EditSettings.AllowPageFilter).editValue
        val allowPageRotation =
            settingsSharedPreferences.getEditItem(EditSettings.AllowPageRotation).editValue
        val allowPageArrangement =
            settingsSharedPreferences.getEditItem(EditSettings.AllowPageArrangement).editValue
        val allowPageCropping =
            settingsSharedPreferences.getEditItem(EditSettings.AllowPageCropping).editValue
        val pageArrangementShowDeleteButton =
            settingsSharedPreferences.getEditItem(EditSettings.PageArrangementShowDeleteButton).editValue
        val pageArrangementShowPageNumber =
            settingsSharedPreferences.getEditItem(EditSettings.PageArrangementShowPageNumber).editValue

        val items = mutableListOf(
            SettingsMultiItems.TitleItem(
                R.string.color_settings
            ),
            SettingsMultiItems.ColorItem(
                R.string.color_primary_title,
                R.string.color_primary_subtitle,
                colorPrimaryItem.lightCircle, colorPrimaryItem.darkCircle,
                ColorSettings.ColorPrimary
            ),
            SettingsMultiItems.ColorItem(
                R.string.color_secondary_title,
                R.string.color_secondary_subtitle,
                colorSecondaryItem.lightCircle, colorSecondaryItem.darkCircle,
                ColorSettings.ColorSecondary
            ),
            SettingsMultiItems.ColorItem(
                R.string.color_on_secondary_title,
                R.string.color_secondary_subtitle,
                colorOnSecondaryItem.lightCircle, colorOnSecondaryItem.darkCircle,
                ColorSettings.ColorOnSecondary

            ),
            SettingsMultiItems.ColorItem(
                R.string.color_scan_layout_title,
                R.string.color_scan_layout_subtitle,
                colorScanButtonsLayoutBackground.lightCircle,
                colorScanButtonsLayoutBackground.darkCircle,
                ColorSettings.ColorScanButtonsLayoutBackground
            ),
            SettingsMultiItems.ColorItem(
                R.string.color_scan_foreground_title,
                R.string.color_scan_foreground_subtitle,
                colorScanButtonsForeground.lightCircle, colorScanButtonsForeground.darkCircle,
                ColorSettings.ColorScanButtonsForeground
            ),
            SettingsMultiItems.ColorItem(
                R.string.color_scan_polygon_title,
                R.string.color_scan_polygon_subtitle,
                colorScanPolygon.lightCircle, colorScanPolygon.darkCircle,
                ColorSettings.ColorScanPolygon
            ),
            SettingsMultiItems.ColorItem(
                R.string.color_bottom_bar_background_title,
                R.string.color_bottom_bar_background_subtitle,
                colorBottomBarBackground.lightCircle, colorBottomBarBackground.darkCircle,
                ColorSettings.ColorBottomBarBackground
            ),
            SettingsMultiItems.ColorItem(
                R.string.color_bottom_bar_forground_title,
                R.string.color_bottom_bar_forground_subtitle,
                colorBottomBarForeground.lightCircle, colorBottomBarForeground.darkCircle,
                ColorSettings.ColorBottomBarForeground
            ),
            SettingsMultiItems.ColorItem(
                R.string.color_top_bar_background_title,
                R.string.color_top_bar_background_subtitle,
                colorTopBarBackground.lightCircle, colorTopBarBackground.darkCircle,
                ColorSettings.ColorTopBarBackground

            ),
            SettingsMultiItems.ColorItem(
                R.string.color_top_bar_forground_title,
                R.string.color_top_bar_forground_subtitle,
                colorTopBarForeground.lightCircle, colorTopBarForeground.darkCircle,
                ColorSettings.ColorTopBarForeground
            ),
            SettingsMultiItems.TitleItem(
                R.string.scan_settings
            ),
            SettingsMultiItems.ScanSettingsItem(
                R.string.capture_mode_setting_title,
                R.string.capture_mode_setting_subtitle,
                allowCaptureModeSetting,
                ScanSettings.AllowCaptureModeSetting
            ),
            SettingsMultiItems.ScanSettingsItem(
                R.string.auto_capture_setting_title,
                R.string.auto_capture_setting_subtitle,
                autoCapture,
                ScanSettings.AutoCapture
            ),
            SettingsMultiItems.ScanSettingsItem(
                R.string.auto_crop_setting_title,
                R.string.auto_crop_setting_subtitle,
                autoCrop,
                ScanSettings.AutoCrop
            ),
            SettingsMultiItems.ScanSettingsItem(
                R.string.multi_page_setting_title,
                R.string.multi_page_setting_subtitle,
                multiPage,
                ScanSettings.MultiPage
            ),
            SettingsMultiItems.ScanSettingsItem(
                R.string.pre_capture_setting_title,
                R.string.pre_capture_setting_subtitle,
                preCaptureFocus,
                ScanSettings.PreCaptureFocus
            ),
            SettingsMultiItems.ScanFilterItem(
                R.string.default_scan_setting_title,
                R.string.default_scan_setting_subtitle,
                defaultScanFilter,
                ScanSettings.DefaultScanFilter
            ),
            SettingsMultiItems.TitleItem(
                R.string.edit_settings
            ),
            SettingsMultiItems.EditItem(
                R.string.allow_page_filter_setting_title,
                R.string.allow_page_filter_setting_subtitle,
                allowPageFilter,
                EditSettings.AllowPageFilter
            ),
            SettingsMultiItems.EditItem(
                R.string.allow_page_rotation_setting_title,
                R.string.allow_page_rotation_setting_subtitle,
                allowPageRotation,
                EditSettings.AllowPageRotation
            ),
            SettingsMultiItems.EditItem(
                R.string.allow_page_arrangement_setting_title,
                R.string.allow_page_arrangement_setting_subtitle,
                allowPageArrangement,
                EditSettings.AllowPageArrangement
            ),
            SettingsMultiItems.EditItem(
                R.string.allow_page_cropping_setting_title,
                R.string.allow_page_cropping_setting_subtitle,
                allowPageCropping,
                EditSettings.AllowPageCropping
            ),
            SettingsMultiItems.EditItem(
                R.string.page_arrangement_delete_setting_title,
                R.string.page_arrangement_delete_setting_subtitle,
                pageArrangementShowDeleteButton,
                EditSettings.PageArrangementShowDeleteButton
            ),
            SettingsMultiItems.EditItem(
                R.string.page_arrangement_number_setting_title,
                R.string.page_arrangement_number_setting_subtitle,
                pageArrangementShowPageNumber,
                EditSettings.PageArrangementShowPageNumber
            )
        )
        return items
    }

    private fun initRestButton() {
        findViewById<AppCompatButton>(R.id.rest_button_settings).setOnClickListener {
            settingsSharedPreferences.defaultSettings()
            settingsAdapter.refresh(preparingData())
        }
    }

    private fun initToolbar() {
        supportActionBar?.title = getString(R.string.title_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}