package de.docutain.sdk.docutain_sdk_example_android_kotlin

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ListItem(var title: Int, var subtitle: Int, var icon: Int, var type: ListAdapter.ItemType)

class ListAdapter(private val onItemClicked: (ListItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ItemType {
        NONE,
        DOCUMENT_SCAN,
        DATA_EXTRACTION,
        TEXT_RECOGNITION,
        PDF_GENERATING,
        SETTINGS
    }

    private val items = arrayOf(
        ListItem(
            R.string.title_document_scan,
            R.string.subtitle_document_scan,
            R.drawable.document_scanner,
            ItemType.DOCUMENT_SCAN
        ),
        ListItem(
            R.string.title_data_extraction,
            R.string.subtitle_data_extraction,
            R.drawable.data_extraction,
            ItemType.DATA_EXTRACTION
        ),
        ListItem(
            R.string.title_text_recognition,
            R.string.subtitle_text_recognition,
            R.drawable.ocr,
            ItemType.TEXT_RECOGNITION
        ),
        ListItem(
            R.string.title_PDF_generating,
            R.string.subtitle_PDF_generating,
            R.drawable.pdf,
            ItemType.PDF_GENERATING
        ),
        ListItem(
            R.string.title_settings,
            R.string.subtitle_settings,
            R.drawable.settings,
            ItemType.SETTINGS
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListItemViewHolder).title.setText(items[position].title)
        holder.secondary.setText(items[position].subtitle)
        holder.icon.setImageResource(items[position].icon)
        holder.itemView.setOnClickListener { onItemClicked(items[position]) }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}