package com.essloyaltyprogram.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.essloyaltyprogram.dataClasses.BannerItem
import com.essloyaltyprogram.databinding.ViewpagerItemsBinding

class SliderAdapter(private val bannerList: List<BannerItem>) :
    RecyclerView.Adapter<SliderAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ViewpagerItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ViewpagerItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val realPosition = position % bannerList.size
        val item = bannerList[realPosition]

        holder.binding.progressBar.visibility = View.VISIBLE
        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.binding.banner)
        holder.binding.progressBar.visibility = View.GONE
        holder.binding.root.setOnClickListener {
            if (item.url != null && item.url.contains("http")) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(item.url))
                holder.itemView.context.startActivity(intent)
            }else {
                Toast.makeText(holder.itemView.context, "Invalid Url", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return if (bannerList.isNotEmpty()) Int.MAX_VALUE else 0
    }
}

