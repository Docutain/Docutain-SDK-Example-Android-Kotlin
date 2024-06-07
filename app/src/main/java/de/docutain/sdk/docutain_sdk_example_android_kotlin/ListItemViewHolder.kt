package de.docutain.sdk.docutain_sdk_example_android_kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class ListItemViewHolder(itemView: View) : ViewHolder(itemView) {
    val icon: ImageView = itemView.findViewById(R.id.list_item_icon)
    val title: TextView = itemView.findViewById(R.id.list_item_text)
    val secondary: TextView = itemView.findViewById(R.id.list_item_secondary_text)

    companion object{
        fun create(parent: ViewGroup): ListItemViewHolder {
            return ListItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
            )
        }
    }
}