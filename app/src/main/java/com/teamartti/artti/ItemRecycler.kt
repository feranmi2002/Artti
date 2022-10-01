package com.teamartti.artti

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamartti.artti.databinding.ImageItemBinding

class ItemRecycler(
    val data: MutableList<ItemModel>,
    val downloadCallback: (url: String?) -> Unit,
    val wallpaperCallback: (url: String?) -> Unit
) :
    RecyclerView.Adapter<ItemRecycler.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var item: ItemModel? = null
        val image = binding.image
        val menu = binding.menu

        init {
            image.setOnClickListener {
                // TODO show full image
            }

            menu.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, binding.menu)
                popupMenu.menuInflater.inflate(R.menu.item_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.download -> downloadCallback.invoke(item?.link)
                        R.id.wallpaper -> wallpaperCallback.invoke(item?.link)
                    }
                    true
                }
                popupMenu.show()
            }
        }

        fun bind(model: ItemModel) {
            item = model
            Glide.with(itemView.context)
                .load(model.link)
                .into(binding.image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = data[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = data.size
}