package de.docutain.sdk.docutain_sdk_example_android_kotlin.settings

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import de.docutain.sdk.docutain_sdk_example_android_kotlin.R
import de.docutain.sdk.docutain_sdk_example_android_kotlin.utils.*
import de.docutain.sdk.ui.ScanFilter
import java.util.*

sealed class SettingsMultiItems {
    data class TitleItem(
        var title: Int
    ) : SettingsMultiItems()

    data class ColorItem(
        var title: Int,
        var subtitle: Int,
        var lightCircle: String,
        var darkCircle: String,
        var colorKey: ColorSettings
    ) : SettingsMultiItems()

    data class ScanSettingsItem(
        var title: Int,
        var subtitle: Int,
        var checkValue: Boolean,
        var scanKey: ScanSettings
    ) : SettingsMultiItems()

    data class ScanFilterItem(
        var title: Int,
        var subtitle: Int,
        var scanValue: ScanFilter,
        var filterKey: ScanSettings
    ) : SettingsMultiItems()

    data class EditItem(
        var title: Int,
        var subtitle: Int,
        var editValue: Boolean,
        var editKey: EditSettings
    ) : SettingsMultiItems()
}

class SettingsMultiViewsAdapter(
    private val items: MutableList<SettingsMultiItems>,
    private val settingsSharedPreferences: SettingsSharedPreferences
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: SettingsMultiItems.TitleItem) {
            val textView = itemView.findViewById<TextView>(R.id.title_view_settings)
            textView.setText(item.title)
        }
    }

    inner class ColorSettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: SettingsMultiItems.ColorItem) {
            val title: TextView = itemView.findViewById(R.id.title_settings_item)
            val subtitle: TextView = itemView.findViewById(R.id.subtitle_settings_item)
            val lightView: View = itemView.findViewById(R.id.light_circle_view)
            val darkView: View = itemView.findViewById(R.id.dark_circle_view)

            val cachedItem = settingsSharedPreferences.getColorItem(item.colorKey)

            circleView(lightView, cachedItem.lightCircle)
            circleView(darkView, cachedItem.darkCircle)

            lightView.setOnClickListener {
                colorPickerDialog(itemView.context, lightView, item, ColorType.Light,cachedItem.lightCircle)
            }
            darkView.setOnClickListener {
                colorPickerDialog(itemView.context, darkView, item, ColorType.Dark,cachedItem.darkCircle)
            }
            title.setText(item.title)
            subtitle.setText(item.subtitle)
        }
    }

    inner class ScanSettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: SettingsMultiItems.ScanSettingsItem) {
            val cachedItem = settingsSharedPreferences.getScanItem(item.scanKey)

            val title: TextView = itemView.findViewById(R.id.title_scan_settings_item)
            val subtitle: TextView = itemView.findViewById(R.id.subtitle_scan_settings_item)
            val switchButton: SwitchCompat = itemView.findViewById(R.id.switch_scan_settings_item)

            title.setText(item.title)
            subtitle.setText(item.subtitle)

            switchButton.setOnCheckedChangeListener(null)
            switchButton.isChecked =  cachedItem.checkValue

            switchButton.setOnCheckedChangeListener { _, isChecked ->
                settingsSharedPreferences.saveScanItem(item.scanKey, isChecked)
            }
        }
    }

    inner class ScanFilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: SettingsMultiItems.ScanFilterItem) {
            val cachedItem = settingsSharedPreferences.getScanFilterItem(item.filterKey)

            val title: TextView = itemView.findViewById(R.id.title_filter_settings_item)
            val subtitle: TextView = itemView.findViewById(R.id.subtitle_filter_settings_item)
            val inputScanFilter: TextInputEditText = itemView.findViewById(R.id.input_filter_filter_dialog)

            title.setText(item.title)
            subtitle.setText(item.subtitle)

            inputScanFilter.setText(cachedItem.scanValue.toString())
            inputScanFilter.setOnClickListener {
                scanFilterDialog(itemView.context, inputScanFilter, item)
            }
        }
    }


    inner class EditSettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: SettingsMultiItems.EditItem) {
            val cachedItem = settingsSharedPreferences.getEditItem(item.editKey)

            val title: TextView = itemView.findViewById(R.id.title_edit_settings_item)
            val subtitle: TextView = itemView.findViewById(R.id.subtitle_edit_settings_item)
            val switchButton: SwitchCompat = itemView.findViewById(R.id.switch_edit_settings_item)

            title.setText(item.title)
            subtitle.setText(item.subtitle)

            switchButton.setOnCheckedChangeListener(null)
            switchButton.isChecked = cachedItem.editValue

            switchButton.setOnCheckedChangeListener { _, isChecked ->
                settingsSharedPreferences.saveEditItem(item.editKey, isChecked)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SettingsMultiItems.TitleItem -> TYPE_TITLE
            is SettingsMultiItems.ColorItem -> TYPE_COLOR
            is SettingsMultiItems.ScanSettingsItem -> TYPE_SCAN_SETTINGS
            is SettingsMultiItems.ScanFilterItem -> TYPE_SCAN_FILTER
            is SettingsMultiItems.EditItem -> TYPE_EDIT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TITLE -> TitleViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.title_view_settings_item, parent, false)
            )
            TYPE_COLOR -> ColorSettingsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.color_settings_item, parent, false)
            )
            TYPE_SCAN_SETTINGS -> ScanSettingsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.scan_view_settings_item, parent, false)
            )
            TYPE_SCAN_FILTER -> ScanFilterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.scan_filter_settings, parent, false)
            )
            TYPE_EDIT -> EditSettingsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.edit_view_settings_item, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SettingsMultiItems.TitleItem -> (viewHolder as TitleViewHolder).bind(item)
            is SettingsMultiItems.ColorItem -> (viewHolder as ColorSettingsViewHolder).bind(item)
            is SettingsMultiItems.ScanSettingsItem -> (viewHolder as ScanSettingsViewHolder).bind(item)
            is SettingsMultiItems.ScanFilterItem -> (viewHolder as ScanFilterViewHolder).bind(item)
            is SettingsMultiItems.EditItem -> (viewHolder as EditSettingsViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    private fun scanFilterDialog(
        context: Context,
        inputText: TextInputEditText,
        item: SettingsMultiItems.ScanFilterItem
    ) {
        val options = arrayOf(
            context.getString(R.string.auto_option),
            context.getString(R.string.gray_option),
            context.getString(R.string.black_and_white_option),
            context.getString(R.string.original_option),
            context.getString(R.string.text_option),
            context.getString(R.string.auto_2_option),
            context.getString(R.string.illustration_option),
        )
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(R.string.title_scan_dialog).setItems(options) { _, which ->
            val selectedOption = options[which]
            inputText.setText(selectedOption)
            settingsSharedPreferences.saveScanFilterItem(item.filterKey, which)
        }.setNegativeButton(context.getString(R.string.cancel_scan_dialog)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun colorPickerDialog(
        context: Context,
        view: View,
        colorItem: SettingsMultiItems.ColorItem,
        colorType: ColorType,
        defaultColor:String
    ) {
        ColorPickerDialog
            .Builder(context)
            .setTitle("Pick Theme")
            .setDefaultColor(defaultColor)
            .setColorShape(ColorShape.SQAURE).setColorListener { _, colorHex ->
                circleView(view, colorHex)
                when (colorType) {
                    ColorType.Light -> {
                        settingsSharedPreferences.saveColorItemLight(
                            colorItem.colorKey.toString(),
                            colorHex
                        )
                    }
                    ColorType.Dark -> {
                        settingsSharedPreferences.saveColorItemDark(
                            colorItem.colorKey.toString(),
                            colorHex
                        )
                    }
                }
                this.notifyDataSetChanged()
            }.show()
    }

    private fun circleView(view: View, colorHex: String) {
        val color = Color.parseColor(colorHex)
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.OVAL
        gradientDrawable.setStroke(2, Color.GRAY)
        gradientDrawable.setColor(color)
        view.background = gradientDrawable
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refresh(newData: List<SettingsMultiItems>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged() // Notify adapter about the changes
    }
    companion object {
        const val TYPE_TITLE = 0
        const val TYPE_COLOR = 1
        const val TYPE_SCAN_SETTINGS = 2
        const val TYPE_SCAN_FILTER = 3
        const val TYPE_EDIT = 4
    }
}