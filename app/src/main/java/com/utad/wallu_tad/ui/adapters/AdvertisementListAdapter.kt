package com.utad.wallu_tad.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.AdvertisementItemBinding
import com.utad.wallu_tad.model.Advertisement
import java.util.zip.Inflater

class AdvertisementListAdapter(val action: (advertisement: Advertisement) -> Unit) :
    ListAdapter<Advertisement, AdvertisementListAdapter.AdvertisementViewHolder>(
        AdvertisementItemCallBack
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertisementViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdvertisementItemBinding.inflate(inflater, parent, false)
        return AdvertisementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdvertisementViewHolder, position: Int) {
        val advertisement = getItem(position)
        holder.binding.root.setOnClickListener { action(advertisement) }
        holder.binding.tvAdvertisementTitle.text = advertisement.title
        holder.binding.tvPrice.text = "${advertisement.price}€"

        Glide.with(holder.binding.root).load(advertisement.image)
            .centerCrop()
            .placeholder(R.drawable.bg_divider)
            .into(holder.binding.ivAdvertisement)


    }

    inner class AdvertisementViewHolder(val binding: AdvertisementItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

object AdvertisementItemCallBack : DiffUtil.ItemCallback<Advertisement>() {
    override fun areItemsTheSame(oldItem: Advertisement, newItem: Advertisement): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Advertisement, newItem: Advertisement): Boolean {
        return oldItem == newItem
    }
}