package com.teamartti.artti

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView


class ItemRecycler(
    private val data: List<ItemModel>,
    val downloadCallback: (bitmap: Bitmap?) -> Unit,
    val wallpaperCallback: (bitmap: Bitmap?) -> Unit
) :
    RecyclerView.Adapter<ItemRecycler.ItemViewHolder>() {

    inner class ItemViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {

        var item: ItemModel? = null
        val image = view.findViewById<ShapeableImageView>(R.id.image)
        val menu = view.findViewById<ShapeableImageView>(R.id.menu)

        init {
            image.setOnClickListener {
//               do later
            }

            menu.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, menu)
                popupMenu.menuInflater.inflate(R.menu.item_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.download -> downloadCallback.invoke(item?.image)
                        R.id.wallpaper -> wallpaperCallback.invoke(item?.image)
                    }
                    true
                }
                popupMenu.show()
            }
        }

        fun bind(model: ItemModel) {
            item = model
            Glide.with(itemView.context)
                .load(model.image)
                .into(image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = data[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = data.size
}