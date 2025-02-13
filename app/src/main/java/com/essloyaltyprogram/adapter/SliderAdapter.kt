package com.essloyaltyprogram.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.essloyaltyprogram.dataClasses.BannerItem
import com.essloyaltyprogram.databinding.ViewpagerItemsBinding


class SliderAdapter(val context : Context) : ListAdapter<BannerItem,SliderAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<BannerItem>(){
        override fun areItemsTheSame(oldItem: BannerItem, newItem: BannerItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: BannerItem, newItem: BannerItem): Boolean {
            return oldItem == newItem
        }

    }

    class ViewHolder(val binding : ViewpagerItemsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewpagerItemsBinding.inflate(LayoutInflater.from(context),null,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.progressBar.visibility = View.VISIBLE
        Glide.with(context)
            .load(item.image)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    holder.binding.banner.setImageDrawable(resource)
                    holder.binding.progressBar.visibility = View.GONE
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    holder.binding.progressBar.visibility = View.GONE
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    holder.binding.progressBar.visibility = View.GONE
                }
            })
    }


}
